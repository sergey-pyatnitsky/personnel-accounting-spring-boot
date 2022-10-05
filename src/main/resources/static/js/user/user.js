$(document).ready(function () {
  hide_preloader();
  hideAllContent();
});

function hideAllContent() {
  $("#content-view-user").hide();
  $("#content-edit-user").hide();
  $("#content-activate-user").hide();
}