// Copyright 2020 Google LLC

function change_grid_columns_number(columns_number) {
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
