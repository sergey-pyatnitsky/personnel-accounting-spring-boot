let assign_table = null, open_departments = null, department_assign_table = null;
let current_url_for_assign_table = "/api/employee/get_all/free";

$(document).ready(function () {
  $("#assign-user").click(function (event) {
    event.preventDefault();
    $("#content-assign-user .nav-link").removeClass('active');
    $("#content-assign-user #assignEmployeeBtn").addClass('active');
    $('.alert').replaceWith(`<div class="alert"></div>`);
    hideAllContent();
    $("#content-assign-user").show();
    if (assign_table != null) assign_table.destroy();
    current_url_for_assign_table = "/api/employee/get_all/free";
    loadAssignTable("#content-assign-user #assign_users_table", current_url_for_assign_table);

    $("#content-assign-user #assignEmployeeBtn").click(function () {
      assign_table.destroy();
      current_url_for_assign_table = "/api/employee/get_all/free";
      loadAssignTable("#content-assign-user #assign_users_table", current_url_for_assign_table);

      $("#content-assign-user .nav-link").removeClass('active');
      $(this).addClass('active');
    });

    $("#content-assign-user #transferEmployeeBtn").click(function () {
      assign_table.destroy();
      current_url_for_assign_table = "/api/employee/get_all/assigned";
      loadAssignTable("#content-assign-user #assign_users_table", current_url_for_assign_table);

      $("#content-assign-user .nav-link").removeClass('active');
      $(this).addClass('active');
    });

    let current_row = null;
    $("#content-assign-user #assignUserModal").on('show.bs.modal', function (event) {
      current_row = $(document.activeElement).closest('tr');
      if (department_assign_table != null) department_assign_table.destroy();
      loadDeartmentsAssignTable("#departmentSelectModalTable", "/api/department/get_all/open");

      let employee = current_row.find(".employee_id").text();
      let employee_name = current_row.find(".employee_name").text();

      $("body").on("click", "#content-assign-user #save_assign_department_modal_btn", function () {
        assign_user(employee, employee_name, $(this).val());
      });
    });
  });
});

function loadDeartmentsAssignTable(table_id, req_url) {
  department_assign_table = $(table_id).DataTable({
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
      { "data": "name" },
      {
        "mData": null,
        "bSortable": false,
        "mRender": function (data) {
          let message = get_message(localStorage.getItem("lang"),
            "department.button.text.select");
          return '<button type="button" class="btn btn-primary" id="save_assign_department_modal_btn" ' +
            'data-dismiss="modal" value="' + data.id + '|' + data.name + '">' + message + '</button>'
        }
      }
    ],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}

function loadAssignTable(table_id, req_url) {
  assign_table = $(table_id).DataTable({
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
        console.log(JSON.stringify(d));
        return JSON.stringify(d);
      }
    },
    "columns": [
      { "data": "id", "sClass": "employee_id" },
      { "data": "user.username" },
      { "data": "name", "sClass": "employee_name" },
      { "data": "user.authority.role" },
      {
        "data": "department", render: function (data) {
          return data != null
            ? data.id + "-" + data.name
            : '<p class="text-danger">-</p>';
        }, "sClass": "department_column"
      },
      {
        "mData": null,
        "bSortable": false,
        "mRender": function (data) {
          if (data.department != null) {
            let message = get_message(localStorage.getItem("lang"),
              "department.button.text.transfer");
            return '<button type="button" class="btn btn-danger btn-rounded btn-sm my-0" data-toggle="modal" ' +
              'data-target="#assignUserModal" id="transfer_user_btn" value="' + data.id + '">' + message + '</button>'
          } else {
            let message = get_message(localStorage.getItem("lang"),
              "department.button.text.assign");
            return '<button type="button" class="btn btn-danger btn-rounded btn-sm my-0" data-toggle="modal" ' +
              'data-target="#assignUserModal" id="transfer_user_btn" value="' + data.id + '">' + message + '</button>';
          }
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

function get_open_department() {
  show_preloader();
  $.ajax({
    type: "GET",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/department/get_all/open",
    async: false,
    cache: false,
    timeout: 600000,
    success: function (data) {
      open_departments = data;
    },
    error: function (error) {
      console.log(error);
    }
  });
  hide_preloader();
}

function assign_user(employee_id, employee_name, department_str) {
  let employee = {}, department = {};
  Object.assign(employee, { department });
  employee.id = employee_id;
  employee.department.id = department_str.split('|')[0];
  $.ajax({
    type: "POST",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/employee/assign/department",
    data: JSON.stringify(employee),
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert').empty();
      if (data == "") {
        let message = get_message(localStorage.getItem("lang"),
          "department.alert.assign_user").replace("0", employee_name);
        message = message.replace("1", department_str.split('|')[1]);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        assign_table.destroy();
        loadAssignTable("#content-assign-user #assign_users_table", current_url_for_assign_table);
      } else $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">` + data.error + `</div>`);
    },
    error: function (error) {
      console.log(error);
      $('.alert').empty();
      $('.alert').replaceWith(`<div class="alert alert-danger" role = "alert">500 Error</div>`);
    }
  });
}