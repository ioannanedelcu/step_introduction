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

package com.google.sps.data;

/** Represents a comment added on the page. */
public final class Comment {

  private final long id;
  private final String text;
  private final String email;
  private final String nickname;
  private final String imageUrl;

  public Comment(long id, String text, String email,
   String nickname, String imageUrl) {
    this.id = id;
    this.text = text;
    this.email = email;
    this.nickname = nickname;
    this.imageUrl = imageUrl;
  }

  public long getId() {
    return id;
  }

  public String getText() {
    return text;
  }

  public String getEmail() {
    return email;
  }

  public String getNickname() {
    return nickname;
  }

  public String getImageUrl() {
      return imageUrl;
  }
}
