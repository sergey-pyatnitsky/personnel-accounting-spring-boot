$(document).ready(function () {
  hide_preloader();
  hideAllContent();

  $("#add-position").click(function (event) {
    event.preventDefault();
    show_add_position_content();

    $("body").on("click", "#position_save_btn", function (event) {
      event.stopImmediatePropagation();
      event.preventDefault();
      add_position();
    });
  });
});

function show_add_position_content() {
  hideAllContent();
  $("#content-add-position").show();
  $('#positionNameInput').val('');
  $(".alert").replaceWith(`<div class="alert"></div>`);
}

function add_position() {
  let position = {};
  position.name = $('#positionNameInput').val();
  $.ajax({
    type: "POST",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/position/add",
    data: JSON.stringify(position),
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert').empty();
      if (data.id == null)
        $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">` + data.error + `</div>`);
      else {
        let message = get_message(localStorage.getItem("lang"), "position.alert.add.success").replace("0", data.name);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        $('#positionNameInput').val('');
      }
    },
    error: function (error) {
      console.log(error);
      $('.alert').empty();
      $('.alert').replaceWith(`<div class="alert alert-danger" role = "alert">500 Error</div>`);
    }
  });
}