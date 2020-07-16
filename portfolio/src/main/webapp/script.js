// Get the pictures in the grid in pictures variable
var pictures = document.getElementsByClassName("grid-column");
var i; // contor

// One column images view
function one() {
  for (i = 0; i < pictures.length; i++) {
    pictures[i].style.msFlex = "100%";
    pictures[i].style.flex = "100%";
  }
}

// Two columns images view
function two() {
  for (i = 0; i < pictures.length; i++) {
    pictures[i].style.msFlex = "50%";
    pictures[i].style.flex = "50%";
  }
}

// Four columns images view
function four() {
  for (i = 0; i < pictures.length; i++) {
    pictures[i].style.msFlex = "25%";
    pictures[i].style.flex = "25%";
  }
}
