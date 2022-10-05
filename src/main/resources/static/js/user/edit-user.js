let edit_table = null;
let current_url_for_edit_table = "/api/employee/get_all";

$(document).ready(function () {
  $("#edit-user").click(function (event) {
    event.preventDefault();
    $('.alert').replaceWith(`<div class="alert"></div>`);
    $("#content-edit-user #toRemoveTab").remove();
    $("#content-edit-user .nav-link").removeClass('active');
    $("#content-edit-user #employee_btn").addClass('active');
    hideAllContent();
    $("#content-edit-user").show();
    if (edit_table != null) edit_table.destroy();
    current_url_for_edit_table = "/api/employee/get_all";
    loadEditTable("#content-edit-user #edit_users_table", current_url_for_edit_table);

    $("#content-edit-user #employee_btn").click(function () {
      $("#content-edit-user .nav-link").removeClass('active');
      $("#content-edit-user #employee_btn").addClass('active');
      edit_table.destroy();
      current_url_for_edit_table = "/api/employee/get_all";
      loadEditTable("#content-edit-user #edit_users_table", current_url_for_edit_table);
    });

    $("#content-edit-user #admin_btn").click(function () {
      $("#content-edit-user .nav-link").removeClass('active');
      $("#content-edit-user #admin_btn").addClass('active');
      edit_table.destroy();
      current_url_for_edit_table = "/api/employee/get_all/admins";
      loadEditTable("#content-edit-user #edit_users_table", current_url_for_edit_table);
    });

    let current_row = null;
    $("body").on("click", "#editUserBtn", function (event) {
      event.stopImmediatePropagation();
      event.preventDefault();
      current_row = $(this);
      $('#editUserModal').modal("toggle");
    });

    $("body").on('show.bs.modal', "#editUserModal", function (event) {
      current_row = current_row.closest('tr');
      let modal = $(this);

      modal.find('#inputNameEditModal').val(current_row.find('.employee_name').text());
      modal.find('#roleSelectEditModal').val(current_row.find('.employee_role').text());

      $("#modal_save").click(function () {
        edit_user(current_row.find('.employee_id').text(),
          modal.find('#inputNameEditModal').val(), modal.find('#roleSelectEditModal').val());
      })
    });

    $("body").on("click", "#content-edit-user #removeUserBtn", function () {
      delete_employee($(this).val());
    });
  });
});

function loadEditTable(table_id, req_url) {
  edit_table = $(table_id).DataTable({
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
      { "data": "id", "sClass": "employee_id" },
      { "data": "user.username" },
      { "data": "name", "sClass": "employee_name" },
      { "data": "user.authority.role", "sClass": "employee_role" },
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
          let message1 = get_message(localStorage.getItem("lang"),
            "user.alert.button.edit");
          let message2 = get_message(localStorage.getItem("lang"),
            "user.alert.button.remove");
          return '<button type="button" class="btn btn-warning btn-rounded btn-sm my-0 mr-2" data-toggle="modal"'
            + ' data-target="#editUserModal" id="editUserBtn" value="' + data.user.username + '">' + message1 + '</button>'
            + '<button type="button" class="btn btn-danger btn-rounded btn-sm my-0" id="removeUserBtn"'
            + ' value="' + data.id + '|' + data.name + '">' + message2 + '</button>';
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

function edit_user(id, name, emp_role) {
  show_preloader();
  let employee = {}, user = {}, authority = {};
  Object.assign(employee, { user });
  Object.assign(user, { authority });
  employee.name = name;
  employee.user.authority.role = emp_role;
  employee.password='456';
  employee.id = id;
  $.ajax({
    type: "PUT",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/employee/edit",
    data: JSON.stringify(employee),
    async: false,
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert').empty();
      if (data == "") {
        let message = get_message(localStorage.getItem("lang"),
          "user.alert.edit").replace("0", employee.name);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        edit_table.destroy();
        loadEditTable("#content-edit-user #edit_users_table", current_url_for_edit_table);
      } else $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">` + data.error + `</div>`);
    },
    error: function (error) {
      console.log(error);
      $('.alert').empty();
      $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">
        Error!</div>`);
    }
  });
  hide_preloader();
}

function delete_employee(employeeStr) {
  show_preloader();
  let employee = {};
  employee.id = employeeStr.split('|')[0];
  $.ajax({
    type: "DELETE",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/employee/remove/" + employee.id,
    data: JSON.stringify(employee),
    async: false,
    cache: false,
    timeout: 600000,
    success: function () {
      $('.alert').empty();
      let message = get_message(localStorage.getItem("lang"),
        "user.alert.remove").replace("0", employeeStr.split('|')[1]);
      $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
      edit_table.destroy();
      loadEditTable("#content-edit-user #edit_users_table", current_url_for_edit_table);
    },
    error: function (error) {
      console.log(error);
      $('.alert').empty();
      $('.alert').replaceWith(`<div class="alert alert-danger" role = "alert">500 Error</div>`);
    }
  });
  hide_preloader();
}