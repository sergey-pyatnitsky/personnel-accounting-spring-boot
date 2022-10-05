let add_table = null, department_add_table = null,
  employee_add_table = null, project_add_table = null;
let selected_add_department = null, selected_add_project = null, selected_employee = null;

$(document).ready(function () {
  hide_preloader();
  hideAllContent();

  $("#add-task").click(function (event) {
    event.preventDefault();
    hideAllContent();
    $("#content-add-task").show();
    if ($("#content-add-task #department_add_task_table").length != 0) {
      $("#content-add-task #div_project_add_task_table").hide();
      $("#content-add-task #div_employee_add_task_table").hide();
      $("#content-add-task #div_add_tasks").hide();
      $("#content-add-task #div_department_add_task_table").show();
      if (department_add_table != null) department_add_table.destroy();
      loadDepartmentaddTable("#content-add-task #department_add_task_table", "/api/department/get_all/open");

      $("body").on("click", "#content-add-task #select_department_add", function () {
        selected_add_department = $(this).val();
        showProjectSelectAddTable($(this).val());
      });
    } else showProjectSelectAddTable();

    $("body").on("click", "#content-add-task #departmentBtnTask", function () {
      $("#content-add-task #div_add_tasks").hide();
      $("#content-add-task #div_project_add_task_table").hide();
      $("#content-add-task #div_employee_add_task_table").hide();
      $("#content-add-task #div_department_add_task_table").show();
      if (department_add_table != null) department_add_table.destroy();
      loadDepartmentaddTable("#content-add-task #department_add_task_table", "/api/department/get_all/open");

      $("body").on("click", "#content-add-task #select_department_add", function () {
        selected_add_department = $(this).val();
        showProjectSelectAddTable($(this).val());
      });
    });

    $("body").on("click", "#content-add-task #projectBtnTask", function () {
      $("#content-add-task #div_add_tasks").hide();
      $("#content-add-task #div_employee_add_task_table").hide();
      $("#content-add-task #div_project_add_task_table").show();
      $("#content-add-task #div_department_add_task_table").hide();
      showProjectSelectAddTable(selected_add_department);
    });

    $("body").on("click", "#content-add-task #employeeBtnTask", function () {
      if (selected_add_project != null) {
        $("#content-add-task #div_add_tasks").hide();
        $("#content-add-task #div_employee_add_task_table").show();
        $("#content-add-task #div_project_add_task_table").hide();
        $("#content-add-task #div_department_add_task_table").hide();
        showEmployeeSelectTable(selected_add_project);
      }
    });

    $("body").on("click", "#add_task_btn", function (event) {
      event.stopImmediatePropagation();
      event.preventDefault();
      add_task();
    });
  });
});

function showProjectSelectAddTable(department_id) {
  $("#content-add-task #div_department_add_task_table").hide();
  $("#content-add-task #div_employee_add_task_table").hide();
  $("#content-add-task #div_project_add_task_table").show();
  if (department_id == null) department_id = 0;
  if (project_add_table != null) project_add_table.destroy();
  loadProjectaddTable("#content-add-task #project_add_task_table",
    "/api/project/by_department/open/" + department_id);

  $("body").on("click", "#content-add-task #select_project_add", function () {
    selected_add_project = $(this).val();
    showEmployeeSelectTable($(this).val());
  });
}

function showEmployeeSelectTable(project_id) {
  $("#content-add-task #div_project_add_task_table").hide();
  $("#content-add-task #div_employee_add_task_table").show();
  if (employee_add_table != null) employee_add_table.destroy();
  loadEmployeeaddTable("#content-add-task #employee_add_task_table", "/api/employee/get_all/by_project/" + project_id);

  $("body").on("click", "#content-add-task #select_employee_add", function () {
    selected_employee = $(this).val();
    showAddTask();
  });
}

function showAddTask() {
  $("#content-add-task #div_employee_add_task_table").hide();
  $("#content-add-task #div_add_tasks").show();
}

function loadProjectaddTable(table_id, req_url) {
  project_add_table = $(table_id).DataTable({
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
            "task.alert.button.choice");
          return '<button type="button" class="btn btn-primary" id="select_project_add" ' +
            'value="' + data.id + '">' + message + '</button>'
        }
      }
    ],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}

function loadEmployeeaddTable(table_id, req_url) {
  employee_add_table = $(table_id).DataTable({
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
          let message = get_message(localStorage.getItem("lang"),
            "task.alert.button.choice");
          return '<button type="button" class="btn btn-primary" id="select_employee_add" ' +
            'value="' + data.id + '">' + message + '</button>'
        }
      }
    ],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}

function loadDepartmentaddTable(table_id, req_url) {
  department_add_table = $(table_id).DataTable({
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
            "task.alert.button.choice");
          return '<button type="button" class="btn btn-primary" id="select_department_add" ' +
            'value="' + data.id + '">' + message + '</button>'
        }
      }
    ],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}

function add_task() {
  let task = {}, project = {}, assignee = {};
  Object.assign(task, { project });
  Object.assign(task, { assignee });
  project.name = "name";
  assignee.name = "name";
  task.name = $('#content-add-task #taskNameInput').val();
  task.description = $('#content-add-task #taskDescriptionInput').val();
  task.project.id = selected_add_project;
  task.assignee.id = selected_employee;

  $.ajax({
    type: "POST",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/task/add",
    data: JSON.stringify(task),
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert').empty();
      if (data.status != undefined) {
        let message = get_message(localStorage.getItem("lang"),
          "task.alert.add").replace("0", task.name);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        $('#taskNameInput').val('');
        $('#taskDescriptionInput').val('');
      }
      else
        $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">` + data.error + `</div>`);
    },
    error: function (error) {
      console.log(error);
      $('.alert').empty();
      $('.alert').replaceWith(`<div class="alert alert-danger" role = "alert">500 Error</div>`);
    }
  });
}