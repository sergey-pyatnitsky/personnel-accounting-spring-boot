let view_table = null, department_view_table = null, project_view_table = null;
let selected_department = null, selected_project = null, current_status= null;

$(document).ready(function () {
  hide_preloader();
  hideAllContent();
  $("#view-task").click(function (event) {
    event.preventDefault();
    hideAllContent();
    $("#content-view-task").show();

    $("#content-view-task .alert").addClass('d-none');

    $("#content-view-task #div_department_view_task_table").hide();
    $("#content-view-task #div_project_view_task_table").hide();
    $("#content-view-task #div_view_tasks_table").show();

    show_view_task("OPEN");

    $("body").on("click", "#content-view-task #open_tasks_btn", function () {
      if (view_table != null) view_table.destroy();
      loadViewTable("#content-view-task #view_tasks_table", getUrl("OPEN"));
    });

    $("body").on("click", "#content-view-task #in_progress_tasks_btn", function () {
      if (view_table != null) view_table.destroy();
      loadViewTable("#content-view-task #view_tasks_table", getUrl("IN_PROGRESS"));
    });

    $("body").on("click", "#content-view-task #done_tasks_btn", function () {
      if (view_table != null) view_table.destroy();
      loadViewTable("#content-view-task #view_tasks_table", getUrl("DONE"));
    });

    $("body").on("click", "#content-view-task #closed_tasks_btn", function () {
      if (view_table != null) view_table.destroy();
      loadViewTable("#content-view-task #view_tasks_table", getUrl("CLOSED"));
    });

    $("body").on("click", "#content-view-task #resetBtn", function () {
      selected_project = null;
      selected_department = null;
      reloadSelectedItemsInNav();
      show_view_task("OPEN");
    });

    $("body").on("click", "#content-view-task #departmentBtnToAddTask", function () {
      $("#content-view-task #div_view_tasks_table").hide();
      $("#content-view-task #div_project_view_task_table").hide();
      $("#content-view-task #div_department_view_task_table").show();
      if (department_view_table != null) department_view_table.destroy();
      $("#content-view-task .nav-link").removeClass('active');
      $(this).addClass('active');
      loadDepartmentViewTable("#content-view-task #department_view_task_table", "/api/department/get_all/open");

      $("body").on("click", "#content-view-task #select_department_view", function () {
        selected_department = $(this).val();
        selected_project = null
        reloadSelectedItemsInNav();
        $("#content-view-task .nav-link").removeClass('active');
        show_view_task("OPEN");
      });
    });

    $("body").on("click", "#content-view-task #projectBtnToAddTask", function () {
      $("#content-view-task #div_view_tasks_table").hide();
      $("#content-view-task #div_department_view_task_table").hide();
      $("#content-view-task #div_project_view_task_table").show();
      if (project_view_table != null) project_view_table.destroy();
      $("#content-view-task .nav-link").removeClass('active');
      $(this).addClass('active');
      if ($("#content-view-task #department_view_task_table").length != 0 && selected_department == null)
        loadProjectViewTable("#content-view-task #project_view_task_table",
          "/api/project/get_all/open");
      else if (selected_department == null)
        loadProjectViewTable("#content-view-task #project_view_task_table",
          "/api/project/by_department/open/" + 0);
      else
        loadProjectViewTable("#content-view-task #project_view_task_table",
          "/api/project/by_department/open/" + selected_department.split("|")[0]);

      $("body").on("click", "#content-view-task #select_project_view", function () {
        selected_project = $(this).val();
        reloadSelectedItemsInNav();
        $("#content-view-task .nav-link").removeClass('active');
        show_view_task("OPEN");
      });
    });

    let current_row = null;
    $("body").on("click", "#editTaskBtn", function (event) {
      event.stopImmediatePropagation();
      event.preventDefault();
      current_row = $(this);
      $('#task_edit_modal').modal('toggle');
    });

    $("body").on('show.bs.modal', "#task_edit_modal", function (event) {
      current_row = current_row.closest('tr');
      let modal = $(this);

      let task_id = current_row.find('.task_id').text();
      modal.find('#modal_input_name').val(current_row.find('.task_name').text());
      modal.find('#modal_input_description').val(current_row.find('.task_description').text());
      modal.find('#modal_input_status').val(current_row.find('.task_status').text());

      $("#modal_task_save").click(function () {
        edit_task(task_id, $("#modal_input_name").val(), $("#modal_input_description").val(),
          $("#modal_input_status option:selected").text());
      })
    });
  });
});

function reloadSelectedItemsInNav() {
  $("#selectedItems").empty();
  if (selected_department != null)
    $("#selectedItems").append(`
      <li class="nav-item">` + selected_department.split("|")[1] + `</li>`);
  if (selected_project != null)
    $("#selectedItems").append(`
      <li class="nav-item">` + selected_project.split("|")[1] + `</li>`);
}

function show_view_task(status) {
  $("#content-view-task #div_department_view_task_table").hide();
  $("#content-view-task #div_project_view_task_table").hide();
  $("#content-view-task #div_view_tasks_table").show();
  if (view_table != null) view_table.destroy();
  loadViewTable("#content-view-task #view_tasks_table", getUrl(status));
}

function loadDepartmentViewTable(table_id, req_url) {
  department_view_table = $(table_id).DataTable({
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
      {"data": "id"},
      {"data": "name"},
      {
        "mData": null,
        "bSortable": false,
        "mRender": function (data) {
          let message = get_message(localStorage.getItem("lang"),
            "task.alert.button.choice");
          return '<button type="button" class="btn btn-primary" id="select_department_view" ' +
            'value="' + data.id + '|' + data.name + '">' + message + '</button>'
        }
      }
    ],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}

function loadProjectViewTable(table_id, req_url) {
  project_view_table = $(table_id).DataTable({
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
      {"data": "id"},
      {"data": "name"},
      {
        "mData": null,
        "bSortable": false,
        "mRender": function (data) {
          let message = get_message(localStorage.getItem("lang"),
            "task.alert.button.choice");
          return '<button type="button" class="btn btn-primary" id="select_project_view" ' +
            'value="' + data.id + '|' + data.name + '">' + message + '</button>'
        }
      }
    ],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}

function loadViewTable(table_id, req_url) {
  view_table = $(table_id).DataTable({
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
      {"data": "id", "sClass": "task_id" },
      {"data": "name", "sClass": "task_name" },
      {"data": "description", "sClass": "task_description" },
      {"data": "status", "sClass": "task_status" },
      {"data": "createDate"},
      {
        "data": "project", render: function (data) {
          return data.id + "-" + data.name;
        }
      },
      {
        "data": "assignee", render: function (data) {
          return data.id + "-" + data.name;
        }
      },
      {
        "data": null,
        render: function (data) {
          let message = get_message(localStorage.getItem("lang"),
            "task.alert.button.edit");
          return `
            <div class="row mt-1">
              <div class="card p-0">
                <div class="card-header">
                  <h6>#` + data.id + ` ` + data.name + `
                    <span class="badge rounded-pill bg-info">` + data.status + `</span>
                  </h6>
                </div>
                <div class="card-body row m-1">
                  <div class="col-2 text-center">
                    <a href="#" class="text-muted">
                      <img class="d-flex mr-3 rounded-circle mx-auto"
                        src="http://localhost:8080/api/downloadFile/` + data.assignee.profile.imageId.toString() +`" style="width: 48px; height: 48px;" alt="">
                      <h5 class="media-heading mb-0 mt-0">` + data.assignee.name +`</h5>
                    </a>
                  </div>
                  <div class="col-9">
                    <a href="#1"><span class="badge rounded-pill bg-primary">` + data.department.name +`</span></a>
                    <a href="#2"><span class="badge rounded-pill bg-info text-dark">` + data.project.name + `</span></a>
                    <p>` + data.description + `</p>
                    <div class="text-end">` + data.createDate + `</div>
                  </div>
                  <div class="col">
                    <button type="button" class="btn btn-danger btn-sm waves-light mb-0 mt-2" ` +
                      `data-toggle="modal" data-target="#task_edit_modal" id = "editTaskBtn" value="` + data.id +`">` + message +`</button>
                  </div>
                </div>
              </div>
            </div>`;
        }
      }
    ],
    "columnDefs": [{
      "targets": 7,
      "createdCell": function (td) {
        td.setAttribute('colspan', '8');
      }
    },
      {
        "targets": [0, 1, 2, 3, 4, 5, 6],
        'createdCell': function (td) {
          $(td).addClass('d-none');
        }
      }],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}

function getUrl(status) {
  current_status = status;
  if (selected_department == null && selected_project == null)
    return "/api/task/get_all/by_status/" + status;
  else if (selected_department != null && selected_project == null)
    return "/api/task/get_all/by_department/" + selected_department.split("|")[0] + "/by_status/" + status;
  else return "/api/task/get_all/project/" + selected_project.split("|")[0] + "/by_status/" + status;
}

function edit_task(task_id, task_name, task_description, task_status) {
  let task = {};
  task.id = task_id
  task.name = task_name;
  task.description = task_description;
  task.status = task_status;
  $.ajax({
    type: "PUT",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/task/edit/" + task.id,
    data: JSON.stringify(task),
    cache: false,
    timeout: 600000,
    success: function (data) {
      $("#content-view-task .alert").removeClass('d-none');
      $("#content-view-task .alert").empty();
      if (data == "") {
        let message = get_message(localStorage.getItem("lang"),
          "task.alert.edit").replace("0", task.name);
        $("#content-view-task .alert").replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        view_table.destroy();
        loadViewTable("#content-view-task #view_tasks_table", getUrl(current_status));
      } else $("#content-view-task .alert").replaceWith(`<div class="alert alert-danger" role="alert">` + data.error + `</div>`);
    },
    error: function (error) {
      console.log(error);
      $("#content-view-task .alert").removeClass('d-none');
      $("#content-view-task .alert").empty();
      $("#content-view-task .alert").replaceWith(`<div class="alert alert-danger" role = "alert">500 Error</div>`);
    }
  });
}