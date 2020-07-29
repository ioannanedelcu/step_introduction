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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//** Returns the login status of a user */
@WebServlet("/login")
public class LogInServlet extends HttpServlet {
  static class LoginStatus {
    boolean loggedIn;
    String actionURL;

    LoginStatus(boolean loggedIn, String actionURL) {
      this.loggedIn = loggedIn;
      this.actionURL = actionURL;
    }
    
    boolean getLoggedIn() {
      return loggedIn;
    }

    String getActionURL() {
      return actionURL;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    LoginStatus loginStatus ;

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String logoutUrl = userService.createLogoutURL("/");   
      loginStatus = new LoginStatus(true, logoutUrl);
    } else {
      String loginUrl = userService.createLoginURL("/");
      loginStatus = new LoginStatus(false, loginUrl);
    }

    response.setContentType("application/json;");
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(loginStatus));
  }
}
