let activate_table = null;
let current_url_for_activate_table = "/api/employee/get_all";

$(document).ready(function () {
  $("#activate-user").click(function (event) {
    event.preventDefault();
    $('.alert').replaceWith(`<div class="alert"></div>`);
    $("#content-activate-user #toRemoveTab").remove();
    $("#content-activate-user .nav-link").removeClass('active');
    $("#content-activate-user #employee_btn").addClass('active');
    hideAllContent();
    $("#content-activate-user").show();
    if (activate_table != null) activate_table.destroy();
    current_url_for_activate_table = "/api/employee/get_all";
    loadActivateTable("#content-activate-user #activate_users_table", current_url_for_activate_table);

    $("#content-activate-user #employee_btn").click(function () {
      $("#content-activate-user .nav-link").removeClass('active');
      $("#content-activate-user #employee_btn").addClass('active');
      activate_table.destroy();
      current_url_for_activate_table = "/api/employee/get_all";
      loadActivateTable("#content-activate-user #activate_users_table", current_url_for_activate_table);
    });

    $("#content-activate-user #admin_btn").click(function () {
      $("#content-activate-user .nav-link").removeClass('active');
      $("#content-activate-user #admin_btn").addClass('active');
      activate_table.destroy();
      current_url_for_activate_table = "/api/employee/get_all/admins";
      loadActivateTable("#content-activate-user #activate_users_table", current_url_for_activate_table);
    });

    $("body").on('click', "#content-activate-user #activate_user_btn", function () {
      activate_user($(this).val(), $(this).text());
    });
  });
});

function loadActivateTable(table_id, req_url) {
  activate_table = $(table_id).DataTable({
    "processing": true,
    "serverSide": true,
    "pagingType": "full_numbers",
    "ajax": {
      "url": req_url,
      "type": "POST",
      "beforeSend" : function(xhr) {
        xhr.setRequestHeader('Authorization', sessionStorage.getItem('tokenData'));
      },
      "dataType": "json",
      "contentType": "application/json",
      "data": function (d) {
        return JSON.stringify(d);
      }
    },
    "columns": [
      { "data": "id" },
      { "data": "user.username" },
      { "data": "name" },
      { "data": "user.authority.role" },
      {
        "data": "user.active", render: function (data) {
          return data
            ? '<p class="text-success">+</p>'
            : '<p class="text-danger">-</p>';
        }
      },
      {
        "mData": null,
        "bSortable": false,
        "mRender": function (data) {
          let content = '<button type="button" class="btn btn-danger btn-rounded btn-sm my-0" data-toggle="modal" '
            + 'data-target="#activateUserModal" id="activate_user_btn" value="'
            + data.user.username + '">';
          if (data.user.active == false) {
            let message = get_message(localStorage.getItem("lang"),
              "user.alert.button.activate");
            content += message + '</button>';
          } else {
            let message = get_message(localStorage.getItem("lang"),
              "user.alert.button.deactivate");
            content += message + '</button>';
          }
          return content;
        }
      }
    ],
    "aoColumnDefs": [
      { 'bSortable': false, 'aTargets': [ 3, 4 ] }
    ],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}

function activate_user(username, action) {
  show_preloader();
  if (action === "Активировать" || action === "Activate") {
    $.ajax({
      type: "PUT",
      headers: {"Authorization": sessionStorage.getItem('tokenData')},
      contentType: "application/json",
      url: "/api/employee/activate/" + username,
      async: false,
      cache: false,
      timeout: 600000,
      success: function () {
        let message = get_message(localStorage.getItem("lang"),
          "user.alert.activate").replace("0", username);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        activate_table.destroy();
        loadActivateTable("#content-activate-user #activate_users_table", current_url_for_activate_table);
      },
      error: function (error) {
        console.log(error);
        $('.alert').empty();
        $('.alert').replaceWith(`<div class="alert alert-danger" role = "alert">500 Error</div>`);
      }
    });
  }
  else if (action === "Деактивировать" || action === "Deactivate") {
    $.ajax({
      type: "PUT",
      headers: {"Authorization": sessionStorage.getItem('tokenData')},
      contentType: "application/json",
      url: "/api/employee/inactivate/" + username,
      cache: false,
      timeout: 600000,
      success: function () {
        let message = get_message(localStorage.getItem("lang"),
          "user.alert.deactivate").replace("0", username);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        activate_table.destroy();
        loadActivateTable("#content-activate-user #activate_users_table", current_url_for_activate_table);
      },
      error: function (error) {
        console.log(error);
        $('.alert').empty();
        $('.alert').replaceWith(`<div class="alert alert-danger" role = "alert">500 Error</div>`);
      }
    });
  }
  hide_preloader();
}