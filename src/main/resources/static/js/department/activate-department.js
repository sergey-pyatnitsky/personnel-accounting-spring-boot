let activate_table = null;
let current_url_for_activate_table = "/api/department/get_all/open";

$(document).ready(function () {
  $("#activate-department").click(function (event) {
    event.preventDefault();
    $('.alert').replaceWith(`<div class="alert"></div>`);
    hideAllContent();
    $("#content-activate-department").show();
    if (activate_table != null) activate_table.destroy();
    current_url_for_activate_table = "/api/department/get_all/open";
    loadActivateTable("#content-activate-department #activate_departments_table", current_url_for_activate_table);

    $("#content-activate-department #getOpenDepartmentsBtn").click(function () {
      edit_table.destroy();
      current_url_for_activate_table = "/api/department/get_all/open";
      loadActivateTable("#content-activate-department #activate_departments_table", current_url_for_activate_table);
    });

    $("#content-activate-department #getClosedDepartmentsBtn").click(function () {
      activate_table.destroy();
      current_url_for_activate_table = "/api/department/get_all/closed";
      loadActivateTable("#content-activate-department #activate_departments_table", current_url_for_activate_table);
    });

    $("body").on('click', "#content-activate-department #activate_department_btn", function () {
      activate_department($(this).val(), $(this).text());
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
      { "data": "id", "sClass": "department_id" },
      { "data": "name", "sClass": "department_name" },
      {
        "data": "createDate", render: function (data) {
          return data != null
            ? '<p class="text-success">' + data + '</p>'
            : '<p class="text-danger">-</p>';
        }
      },
      {
        "data": "active", render: function (data) {
          return data
            ? '<p class="text-success">+</p>'
            : '<p class="text-danger">-</p>';
        }
      },
      {
        "mData": null,
        "bSortable": false,
        "mRender": function (data) {
          let content = '<button type="button" class="btn btn-danger btn-rounded btn-sm my-0" '
            + 'id="activate_department_btn" value="' + data.id + '">';
          if (!data.active) {
            let message = get_message(localStorage.getItem("lang"),
              "department.button.text.activate");
            content += message + '</button>';
          }
          else {
            let message = get_message(localStorage.getItem("lang"),
              "department.button.text.deactivate");
            content += message + '</button>';
          }
          return content;
        }
      }
    ],
    "aoColumnDefs": [
      { 'bSortable': false, 'aTargets': [ 3 ] }
    ],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}

function activate_department(department_id, action) {
  show_preloader();
  if (action === "Активировать" || action === "Activate") {
    $.ajax({
      type: "PUT",
      headers: {"Authorization": sessionStorage.getItem('tokenData')},
      contentType: "application/json",
      url: "/api/department/activate/" + department_id,
      async: false,
      cache: false,
      timeout: 600000,
      success: function (data) {
        $('.alert').empty();
        let message = get_message(localStorage.getItem("lang"), "department.alert.activate").replace("0", data.name);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        activate_table.destroy();
        loadActivateTable("#content-activate-department #activate_departments_table", current_url_for_activate_table);
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
      url: "/api/department/inactivate/" + department_id,
      async: false,
      cache: false,
      timeout: 600000,
      success: function (data) {
        $('.alert').empty();
        let message = get_message(localStorage.getItem("lang"), "department.alert.deactivate").replace("0", data.name);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        activate_table.destroy();
        loadActivateTable("#content-activate-department #activate_departments_table", current_url_for_activate_table);
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