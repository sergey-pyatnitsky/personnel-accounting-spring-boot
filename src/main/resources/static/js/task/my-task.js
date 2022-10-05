let my_task_table = null, project_my_task_table = null;
let selected_project = null, current_status= null;

$(document).ready(function () {
  hide_preloader();
  hideAllContent();

  $("#my-task").click(function (event) {
    event.preventDefault();
    hideAllContent();
    $("#content-my-task").show();

    $("#content-my-task .alert").addClass('d-none');

    $("#content-my-task #div_department_my_task_task_table").hide();
    $("#content-my-task #div_project_my_task_task_table").hide();
    $("#content-my-task #view_my_tasks_table").show();

    show_view_task("OPEN");

    $("body").on("click", "#content-my-task #open_tasks_btn", function () {
      if (my_task_table != null) my_task_table.destroy();
      loadViewTable("#content-my-task #view_my_tasks_table", getUrl("OPEN"));
    });

    $("body").on("click", "#content-my-task #in_progress_tasks_btn", function () {
      if (my_task_table != null) my_task_table.destroy();
      loadViewTable("#content-my-task #view_my_tasks_table", getUrl("IN_PROGRESS"));
    });

    $("body").on("click", "#content-my-task #done_tasks_btn", function () {
      if (my_task_table != null) my_task_table.destroy();
      loadViewTable("#content-my-task #view_my_tasks_table", getUrl("DONE"));
    });

    $("body").on("click", "#content-my-task #closed_tasks_btn", function () {
      if (my_task_table != null) my_task_table.destroy();
      loadViewTable("#content-my-task #view_my_tasks_table", getUrl("CLOSED"));
    });

    $("body").on("click", "#content-my-task #edit_task_status_btn", function () {
      edit_task_status($(this).attr("value"));
    });

    $("body").on("click", "#content-my-task #resetBtn", function () {
      selected_project = null;
      selected_department = null;
      reloadSelectedItemsInNav();
      show_view_task("OPEN");
    });

    $("body").on("click", "#content-my-task #projectBtnToMyTask", function () {
      $("#content-my-task #div_my_task_tasks_table").hide();
      $("#content-my-task #div_department_my_task_task_table").hide();
      $("#content-my-task #div_project_my_task_task_table").show();
      if (project_my_task_table != null) project_my_task_table.destroy();
      $("#content-my-task .nav-link").removeClass('active');
      $(this).addClass('active');
      loadProjectViewTable("#content-my-task #project_my_task_task_table",
        "/api/project/by_employee/open/0");

      $("body").on("click", "#content-my-task #select_project_view", function () {
        selected_project = $(this).val();
        reloadSelectedItemsInNav();
        $("#content-my-task .nav-link").removeClass('active');
        show_view_task("OPEN");
      });
    });
  });
});

function reloadSelectedItemsInNav() {
  $("#selectedItems").empty();
  if (selected_project != null)
    $("#selectedItems").append(`
      <li class="nav-item">` + selected_project.split("|")[1] + `</li>`);
}

function show_view_task(status) {
  $("#content-my-task #div_department_my_task_task_table").hide();
  $("#content-my-task #div_project_my_task_task_table").hide();
  $("#content-my-task #div_my_task_tasks_table").show();
  if (my_task_table != null) my_task_table.destroy();
  loadViewTable("#content-my-task #view_my_tasks_table", getUrl(status));
}

function loadDepartmentViewTable(table_id, req_url) {
  department_my_task_table = $(table_id).DataTable({
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
  project_my_task_table = $(table_id).DataTable({
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
  my_task_table = $(table_id).DataTable({
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


          let content = ``;
          if (data.status == "DONE") {
            let message = get_message(localStorage.getItem("lang"), "task.time");
            content = `
                <input type="time" id="time_input" class="form-control"/>
                <button type="button" class="btn btn-danger btn-sm waves-light mb-0 mt-2" id="edit_task_status_btn" 
                        value="` + data.id + '|' + data.name + `">` + message + `</button>`;
          }
          else if (data.status != "CLOSED"){
            let message = get_message(localStorage.getItem("lang"), "task.alert.button.edit.status");
            content = `
                <button type="button" class="btn btn-danger btn-sm waves-light mb-0 mt-2" id="edit_task_status_btn" 
                        value="` + data.id + '|' + data.name + `">` + message + `</button></div>`;
          }


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
                    ` + content + `
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
  if(selected_project == null)
    return "/api/task/get_all/by_status/" + status + "/employee";
  else return "/api/task/get_all/project/" + selected_project.split("|")[0] + "/by_status/" + status + "/employee";
}

function edit_task_status(task) {
  let time = null;
  if ($("#time_input").length != 0) time = $("#time_input").val();
  $.ajax({
    type: "PUT",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/task/edit/status/" + task.split('|')[0],
    data: JSON.stringify(time),
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert').empty();
      if (data == "") {
        let message = get_message(localStorage.getItem("lang"),
          "task.alert.edit.status").replace("0", task.split('|')[1]);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        if (my_task_table != null) my_task_table.destroy();
        loadViewTable("#content-my-task #view_my_tasks_table", getUrl(current_status));
      }
      else $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">` + data.error + `</div>`);
    },
    error: function (error) {
      console.log(error);
      $('.alert').empty();
      $('.alert').replaceWith(`<div class="alert alert-danger" role = "alert">500 Error</div>`);
    }
  });
}