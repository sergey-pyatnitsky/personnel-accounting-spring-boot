let view_departments_table = null;

$(document).ready(function () {
  hide_preloader();
  hideAllContent();

  $("#add-project").click(function (event) {
    event.preventDefault();
    $('.alert').replaceWith(`<div class="alert"></div>`);
    hideAllContent();
    show_add_project_content();

    $("body").on("click", "#project_add_btn", function () {
      add_project($('#projectNameInput').val(), $(this).val().split("-")[0], $(this).val().split("-")[1]);
    });
  });
});

function loadAddTable(table_id, req_url) {
  view_departments_table = $(table_id).DataTable({
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
        "data": "active", render: function (data) {
          return data
            ? '<p class="text-success">+</p>'
            : '<p class="text-danger">-</p>';
        }
      },
      { "data": "startDate" },
      {
        "mData": null,
        "bSortable": false,
        "mRender": function (data) {
          let message = get_message(localStorage.getItem("lang"),
            "project.button.text.select_department");
          return '<button type="button" class="btn btn-danger btn-rounded btn-sm my-0" id="project_add_btn"'
            + ' value="' + data.id + "-" + data.name + '">' + message + '</button>';
        }
      }
    ],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}

function show_add_project_content() {
  hideAllContent();
  $("#content-add-project").show();
  $('#projectNameInput').val('');
  if ($("#view_departments_table").length) {
    if (view_departments_table != null) view_departments_table.destroy();
    loadAddTable("#content-add-project #view_departments_table", "/api/department/get_all/open");
  }
  else $(".alert").replaceWith(`<div class="alert"></div>`);
}

function add_project(project_name, department_id, department_name) {
  show_preloader();
  let project = {}, department = {};
  Object.assign(project, { department });
  project.name = project_name;
  if ($("#view_departments_table").length) {
    project.department.id = department_id;
    project.department.name = department_name;
  }
  project.active = false;
  $.ajax({
    type: "POST",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/project/add",
    data: JSON.stringify(project),
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert').empty();
      if (data.id == null)
        $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">` + data.error + `</div>`);
      else {
        let message = get_message(localStorage.getItem("lang"),
          "project.alert.add").replace("0", data.name);
        message = message.replace("1", data.id);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        $('#projectNameInput').val('');
      }
    },
    error: function (error) {
      console.log(error);
      $('.alert').empty();
      $('.alert').replaceWith(`<div class="alert alert-danger" role = "alert">500 Error</div>`);
    }
  });
  hide_preloader();
}