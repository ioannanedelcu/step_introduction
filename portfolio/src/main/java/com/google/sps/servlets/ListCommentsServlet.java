// Copyright 2020 Google LLC
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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Responsible for listing comments. */
@WebServlet("/list-comments")
public class ListCommentsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment");
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

  /**
    * Extract query string parameter if it is specified.
    * Otherwise, use a default value(2).
  */
    int commentsLimit;
    try {
      commentsLimit = Integer.parseInt(request.getParameter("commentsNumber"));
      // Show all.
      if (commentsLimit == -1) {
        commentsLimit = results.countEntities();
      }
    } catch (Exception e) {
        commentsLimit = 2;
    }

    List<Comment> comments = new ArrayList<>();
    int displayedComments = 0;

    // Create Comment instances from entities and add them to the list.
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String text = (String) entity.getProperty("text");
      String email = (String) entity.getProperty("email");
      String nickname = (String) entity.getProperty("nickname");
      String imageUrl = (String) entity.getProperty("imageUrl");

      Comment comment = new Comment(id, text, email, nickname, imageUrl);
      comments.add(comment);
      
      displayedComments++;
      if (displayedComments >= commentsLimit) {
        break;
      }
    }

    Gson gson = new Gson();

    // Respond with the resulted list.
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }
}
