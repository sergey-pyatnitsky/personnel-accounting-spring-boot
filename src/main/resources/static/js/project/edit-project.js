let edit_table = null, department_table = null, departments = null;

$(document).ready(function () {
  $("#edit-project").click(function (event) {
    event.preventDefault();
    $('.alert').replaceWith(`<div class="alert"></div>`);
    hideAllContent();
    $("#content-edit-project").show();
    if (edit_table != null) edit_table.destroy();
    loadEditTable("#content-edit-project #edit_project_table", "/api/project/get_all/open");

    let current_row = null;
    $("body").on("click", "#editProjectBtn", function (event) {
      event.stopImmediatePropagation();
      event.preventDefault();
      current_row = $(this);
      $('#projectEditModal').modal('toggle');
    });

    $("body").on('show.bs.modal', "#projectEditModal", function (event) {
      if (department_table != null) department_table.destroy();
      loadDeartmentsTable("#content-edit-project #departments_table", "/api/department/get_all/open");
      current_row = current_row.closest('tr');
      let modal = $(this);

      modal.find('#project_modal_name').val(current_row.find('.project_name').text());
      modal.find('#modal_department').val(current_row.find('.selected_department').text());

      $("body").on("click", "#content-edit-project #save_project_department_modal_btn", function () {
        edit_project(current_row.find('.project_id').text(), $("#project_modal_name").val(),
          $(this).val());
      });

      $("body").on("click", "#content-edit-project #save_project_modal_btn", function () {
        edit_project(current_row.find('.project_id').text(), $("#project_modal_name").val());
      });
    });

    $("body").on("click", "#content-edit-project #remove_project_btn", function () {
      close_project($(this).val());
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
        console.log(JSON.stringify(d));
        return JSON.stringify(d);
      }
    },
    "columns": [
      { "data": "id", "sClass": "project_id" },
      { "data": "name", "sClass": "project_name" },
      {
        "data": "department", render: function (data) {
          return data.id + "-" + data.name;
        }, "sClass": "selected_department"
      },
      { "data": "startDate" },
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
          let message1 = get_message(localStorage.getItem("lang"),
            "project.button.text.edit");
          let message2 = get_message(localStorage.getItem("lang"),
            "project.button.text.remove");
          return '<button type="button" class="btn btn-warning btn-rounded btn-sm my-0 mr-2" data-toggle="modal"'
            + ' id="editProjectBtn" data-target="#projectEditModal">' + message1 + '</button>'
            + '<button type="button" class="btn btn-danger btn-rounded btn-sm my-0" id="remove_project_btn"'
            + ' value="' + data.id + '|' + data.name + '">' + message2 + '</button>';
        }
      }
    ],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}

function loadDeartmentsTable(table_id, req_url) {
  department_table = $(table_id).DataTable({
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
            "project.button.text.select");
          return '<button type="button" class="btn btn-primary" id="save_project_department_modal_btn" ' +
            'data-dismiss="modal" value="' + data.id + '">' + message + '</button>'
        }
      }
    ],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}

function edit_project(project_id, project_name, department_id) {
  let project = {}, department = {};
  Object.assign(project, { department });
  project.name = project_name;
  project.id = project_id;
  if (department_id != null)
    project.department.name = 'qwerty';
    project.department.id = department_id;
  $.ajax({
    type: "PUT",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/project/edit/" + project.id,
    data: JSON.stringify(project),
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert').empty();
      if (data == "") {
        let message = get_message(localStorage.getItem("lang"),
          "project.alert.edit").replace("0", project.name);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        edit_table.destroy();
        loadEditTable("#content-edit-project #edit_project_table", "/api/project/get_all/open");
      } else $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">` + data.error + `</div>`);
    },
    error: function (error) {
      console.log(error);
      $('.alert').empty();
      $('.alert').replaceWith(`<div class="alert alert-danger" role = "alert">500 Error</div>`);
    }
  });
}

function close_project(project_str) {
  let project = {};
  project.id = project_str.split('|')[0];
  $.ajax({
    type: "DELETE",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/project/close/" + project.id,
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert').empty();
      if (data == "") {
        let message = get_message(localStorage.getItem("lang"),
          "project.alert.close").replace("0", project_str.split('|')[1]);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        edit_table.destroy();
        loadEditTable("#content-edit-project #edit_project_table", "/api/project/get_all/open");
      } else $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">` + data.error + `</div>`);
    },
    error: function (error) {
      console.log(error);
      $('.alert').empty();
      $('.alert').replaceWith(`<div class="alert alert-danger" role = "alert">500 Error</div>`);
    }
  });
}