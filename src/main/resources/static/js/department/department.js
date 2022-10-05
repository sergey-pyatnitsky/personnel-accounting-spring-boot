$(document).ready(function () {
  hide_preloader();
  hideAllContent();
});

function hideAllContent() {
  $("#content-add-department").hide();
  $("#content-edit-department").hide();
  $("#content-activate-department").hide();
  $("#content-view-department").hide();
  $("#content-assign-user").hide();
}