let employee = null, file = null, image_id = "1", name = null;

$(document).ready(function () {
  $("#input_file_button").val();
  get_profile_data();
  $("#profile_image").attr("src", "http://localhost:8080/api/downloadFile/" + image_id);
  localStorage.setItem("imageUrl", "http://localhost:8080/api/downloadFile/" + image_id);
  $("#profile_name").text(localStorage.getItem("name"));

  $('#edit_profile').click(function () {
    edit_profile_data();
  })

  $('#edit_pass').click(function () {
    $('.alert_pass').empty();
  })

  $('input[type=file]').on('change', function () {
    file = this.files[0];
    $("#labelForImageInput").text(file.name.toString());
  });

  $('#input_file').click(function () {
    setImage();
  })

  $("#modal_save").click(function () {
    if ($("#modal_input_new_pass").val() != $("#modal_input_repeat").val()) {
      $('.alert_pass').empty();
      let message = get_message(localStorage.getItem("lang"),
        "profile.alert.password");
      $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
    } else {
      check_old_password();
    }
  })
});

function setImage() {
  var inputFile = new FormData();
  inputFile.append("file", file);
  $.ajax({
    type: "POST",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: false,
    processData: false,
    url: "/api/uploadFile",
    data: inputFile,
    success: function (data) {
      $("#profile_image").attr("src", "http://localhost:8080/api/downloadFile/" + data);
      $("#navbar_image").attr("src", "http://localhost:8080/api/downloadFile/" + data);
      localStorage.setItem("imageUrl", "http://localhost:8080/api/downloadFile/" + data);
    },
    error: function (error) {
      console.log(error);
      let message = get_message(localStorage.getItem("lang"),
        "image.error.size");
      alert(message);
    }
  });
}

function edit_profile_data() {
  employee.name = $('#name').val();
  employee.profile.phone = $('#phone').val();
  employee.profile.address = $('#address').val();
  employee.profile.email = $('#email').val();
  employee.profile.education = $('#education').val();
  employee.profile.skills = $('#experience').val();

  $.ajax({
    type: "POST",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/employee/profile/edit",
    data: JSON.stringify(employee),
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert').empty();
      if (data != "") {
        $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">` + data.error + `</div>`)
      } else {
        let message = get_message(localStorage.getItem("lang"),
          "profile.alert.done");
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
      }
      localStorage.setItem("name", employee.name);
    },
    error: function (error) {
      console.log(error);
      $('.alert').empty();
      $('.alert').append(`<div class="alert alert-danger"role="alert">Ошибка!</div>`);
    }
  });
}

function get_profile_data() {
  $.ajax({
    type: "GET",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/employee/profile/get_profile_data",
    cache: false,
    async: false,
    timeout: 600000,
    success: function (data) {
      employee = data;
      $('#name').val(data.name);
      $('#phone').val(data.profile.phone);
      $('#address').val(data.profile.address);
      $('#email').val(data.profile.email);
      $('#education').val(data.profile.education);
      $('#experience').val(data.profile.skills);
      image_id = data.profile.imageId.toString();
    },
    error: function (error) {
      console.log(error);
      $('.alert').empty();
      $('.alert').replaceWith(`<div class="alert alert-danger"role="alert">500 Error</div>`);
    }
  });
}

function check_old_password() {
  let entity = {};
  Object.assign(entity, { password: $("#modal_input_old_pass").val() });

  $.ajax({
    type: "POST",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/employee/profile/check_old_password",
    data: JSON.stringify(entity),
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert_pass').empty();
      data != ""
        ? $('.alert_pass').replaceWith(`<div class="alert alert-danger" role="alert">` + data.error + `</div>`)
        : save_password();
    },
    error: function (error) {
      console.log(error);
      $('.alert_pass').empty();
      $('.alert_pass').replaceWith(`<div class="alert alert-danger"role="alert">500 Error</div>`);
    }
  });
}

function save_password() {
  let entity = {};
  Object.assign(entity, { password: $("#modal_input_new_pass").val() });

  $.ajax({
    type: "POST",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/employee/profile/edit_password",
    data: JSON.stringify(entity),
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert_pass').empty();
      if (data != "") $('.alert_pass').replaceWith(`<div class="alert alert_pass alert-danger" role="alert">` + data.error + `</div>`)
      else {
        $('#modal_input_old_pass').val('');
        $('#modal_input_new_pass').val('');
        $('#modal_input_repeat').val('');
        $('.alert_pass').empty();

        let message = get_message(localStorage.getItem("lang"),
          "profile.alert.password.done");
        $('.alert_pass').replaceWith(`<div class="alert alert_pass alert-success" role="alert">` + message + `</div>`);
      }
    },
    error: function (error) {
      console.log(error);
      $('.alert_pass').empty();
      $('.alert_pass').replaceWith(`<div class="alert alert_pass alert-danger"role="alert">500 Error</div>`);
    }
  });
}