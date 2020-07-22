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

function changeGridColumnsNumber(columns_number) {
    var flex_value;
    var pictures = document.getElementsByClassName("grid-column");

    if (columns_number === 1) {
      flex_value = "100%";
    } else {
      if (columns_number === 2) {
        flex_value = "50%";
      } else {
        flex_value = "25%";
      }
    }

    for (var i = 0; i < pictures.length; i++) {
        pictures[i].style.msFlex = flex_value;
        pictures[i].style.flex = flex_value;
  }
}

/** Fetches comments from the server and adds them to the DOM. */
function loadComments() {
  fetch('/list-comments').then(response => response.json()).then((comments) => {
    const commentsContainer = document.getElementById('container');
    comments.forEach((comment) => {
      commentsContainer.appendChild(createCommentElement(comment));
    })
  });
}

/** Creates an element that represents a comment. */
function createCommentElement(comment) {
  const commentElement = document.createElement('div');
  commentElement.className = 'comment-box';

  const textElement = document.createElement('span');
  textElement.innerText = comment.text;
  
  // Create a separation bar after a comment.
  const separationBar = document.createElement('hr');

  // Add text and bar to the comment element.
  commentElement.appendChild(textElement);
  commentElement.appendChild(separationBar);

  return commentElement;
}


