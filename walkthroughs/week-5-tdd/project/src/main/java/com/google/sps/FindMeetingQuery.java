// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.*;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Stores the time ranges of the events whose attendees are mandatory for the meeting.
    ArrayList<TimeRange>  relevantEventsTimeRanges = new ArrayList();
    // Stores the time ranges of the events whose attendees are optional for the meeting.
    ArrayList<TimeRange> optionalEventsTimeRanges = new ArrayList();
    // For each time range defined ahead,
    // stores the maximum number of request's optional attendees which are busy in that interval
    HashMap<TimeRange, Integer> optionalAttendeesPerEvent = new HashMap<TimeRange, Integer>();

    // Populate the collections defined ahead.
    for (Event e : events) {
      if (!Collections.disjoint(e.getAttendees(), request.getAttendees())) {
        relevantEventsTimeRanges.add(e.getWhen());
      } else {
        Set<String> intersection = new HashSet<String>(e.getAttendees());
        intersection.retainAll(request.getOptionalAttendees());
        if (!intersection.isEmpty()) {
          optionalEventsTimeRanges.add(e.getWhen());
          if(optionalAttendeesPerEvent.containsKey(e.getWhen())) {
            optionalAttendeesPerEvent.replace(e.getWhen(), 
              Math.max(optionalAttendeesPerEvent.get(e.getWhen()), intersection.size()));
          } else {
          optionalAttendeesPerEvent.put(e.getWhen(), intersection.size());
          }
        }
      }
    }

    // If the duration is longer than a day, there should be no options.
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    } else {
      Collection<TimeRange> mandatoryAvailableSlots = new ArrayList();
      
      // If the mandatory attendees are free, all day should be returned.
      if (relevantEventsTimeRanges.isEmpty()) {
          mandatoryAvailableSlots = Arrays.asList(TimeRange.WHOLE_DAY);
      } else {
          mandatoryAvailableSlots = findTimeSlots(relevantEventsTimeRanges, request.getDuration());
      }

      // No slots available for all mandatory attendees.
      if (mandatoryAvailableSlots.isEmpty()) {
          return Arrays.asList();
      }
      
      // No optional attendees.
      if (optionalEventsTimeRanges.isEmpty()) {
        return mandatoryAvailableSlots;
      } else {
        ArrayList<TimeRange> bestSlots = new ArrayList();
        // Stores the minimum number of optional attendees who can't attend in a certain time range.
        int bestScore = optionalEventsTimeRanges.size();
        Collections.sort(optionalEventsTimeRanges, TimeRange.ORDER_BY_START);

        for (TimeRange t : mandatoryAvailableSlots) {
          // Split available slots in intervals of the duration of the requested meeting
          // and calculate the "score" for each of them.
          for (int k = t.start(); k < t.end(); k +=request.getDuration()) {
            TimeRange currentTimeRange = TimeRange.fromStartDuration(k, (int)request.getDuration());
            int score = getScore (currentTimeRange, optionalEventsTimeRanges,
              optionalAttendeesPerEvent);

            // Update score and resulted list of slots.
            if (score < bestScore) {
              bestScore = score;
              bestSlots.clear();
              bestSlots.add(currentTimeRange);
            } else {
              if (bestScore == score) {
                bestSlots.add(currentTimeRange);
              }
            }
          }
        }

        // Put all of the splitted slots back together if it is possible.
        bestSlots = combineSlots(bestSlots);
        return bestSlots;
      }
    }
  }

  // Computes the number of optional attendees who would miss the meeting when using current slot.
  private int getScore (TimeRange currentTimeRange, ArrayList<TimeRange> busySlots,
    HashMap<TimeRange, Integer> optionalAttendeesPerEvent) {
    int score = 0;
    for(TimeRange t : busySlots) {
      if (currentTimeRange.isPartOf(t)) {
        score += optionalAttendeesPerEvent.get(t);
      }
    }
    return score;
  }

  // Combine splitted slots if possible.
  // For example 09:00 - 10:00 and 10:00 - 11:00 become 09:00 - 11:00
  private ArrayList<TimeRange> combineSlots(ArrayList<TimeRange> separateSlots) {
      ArrayList<TimeRange> combinedSlots = new ArrayList();
      combinedSlots.add(separateSlots.get(0));

      for (int k = 1; k < separateSlots.size(); k++) {
        // Successive slots can be combined.
        if (separateSlots.get(k).start() == combinedSlots.get(combinedSlots.size() - 1).end()) {
          combinedSlots.add(TimeRange.fromStartEnd(combinedSlots.get(combinedSlots.size() - 1).start(),
          separateSlots.get(k).end(), false));
          combinedSlots.remove(combinedSlots.size() - 2);
        } else {
          combinedSlots.add(separateSlots.get(k));
        }
      }
    return combinedSlots;
  }

  // Computes all available slots given a list of busy slots and the duration of the meeting.
  private Collection<TimeRange> findTimeSlots (ArrayList<TimeRange> eventsTimeRanges, long meetingDuration) {
    int currentTime = TimeRange.START_OF_DAY;
    Collection<TimeRange> resultedSlots = new ArrayList();

    Collections.sort(eventsTimeRanges, TimeRange.ORDER_BY_START);

    while (!eventsTimeRanges.isEmpty()) {
      TimeRange currentTimeRange = eventsTimeRanges.get(0);
      // Check if the meeting could take place until the beggining of next event.
      if (currentTimeRange.start() - currentTime >= meetingDuration) {
        resultedSlots.add(TimeRange.fromStartEnd(currentTime, currentTimeRange.start(), false));
      }
      currentTime = Math.max(currentTime, currentTimeRange.end());
      eventsTimeRanges.remove(0);
    }
    // Check for a slot in the remaining part of the day.
    if (TimeRange.END_OF_DAY + 1 - currentTime >= meetingDuration) {
      resultedSlots.add(TimeRange.fromStartEnd(currentTime, TimeRange.END_OF_DAY, true));
    }

    return resultedSlots;
  }
}
