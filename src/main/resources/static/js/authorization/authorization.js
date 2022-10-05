$(document).ready(function () {
  $("#loginBtn").click(function (event) {
    event.preventDefault();
    sendLoginRequest($("#username").val(), $("#password").val());
  });
});

function sendLoginRequest(username, password) {
  let user = {};
  user.username = username;
  user.password = password;
  $.ajax({
    type: "POST",
    contentType: "application/json",
    url: "/authenticate",
    data: JSON.stringify(user),
    cache: false,
    timeout: 600000,
    success: function (data) {
      localStorage.clear();
      localStorage.setItem("lang", "en");
      saveToken(data);
      if (window.location.href.indexOf("login") <= -1)
        window.location.href = window.location.href + "/main";
      else window.location.href = window.location.href.split("login")[0] + "page/main";
    },
    error: function (error) {
      console.log(error);
      var responseObj = JSON.parse(error.responseText);
      $('.alert').removeClass('d-none');
      $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">` + responseObj.error + `</div>`);
    }
  });
}

function saveToken(token) {
  sessionStorage.setItem('tokenData', 'Bearer ' + JSON.stringify(token).replaceAll("\"", ""));
}