$(document).ready(function () {
  $('#reg_button').click(function () {
    if ($("#password").val() != $("#repeat_password").val()) {
      $('.alert_reg').empty();
      let message = get_message(localStorage.getItem("lang"),
        "registration.alert.password");
      $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">` + message + `</div>`);
    }
    else {
      reg_user();
    }
  })
});

function reg_user() {
  let employee = {}, profile = {};
  employee.name = $('#name').val();
  Object.assign(employee, { profile });
  employee.profile.email = $('#email').val();
  Object.assign(employee, { user: { username: $('#username').val(), password: $("#password").val() } });

  $.ajax({
    type: "POST",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/registration",
    data: JSON.stringify(employee),
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert_reg').empty();
      if (data != "") {
        $('.alert_reg').replaceWith(`<div class="alert alert_reg alert-danger" role="alert">` + data.error + `</div>`)
      } else {
        let message = get_message(localStorage.getItem("lang"),
          "registration.alert.success");
        $('.alert_reg').replaceWith(`<div class="alert alert_reg alert-success" role="alert">` + message + `</div>`);
      }
    },
    error: function (error) {
      console.log(error);
      $('.alert_reg').empty();
      let message = get_message(localStorage.getItem("lang"),
        "registration.alert.exist");
      $('.alert_reg').replaceWith(`<div class="alert alert_reg alert-danger" role="alert">` + message + `</div>`);
    }
  });
}