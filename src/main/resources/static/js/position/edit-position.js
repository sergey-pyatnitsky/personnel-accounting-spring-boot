let edit_table = null;
let current_url_for_edit_table = "/api/position/get_all/search-sorting";

$(document).ready(function () {
  $("#edit-position").click(function (event) {
    event.preventDefault();
    $('.alert').replaceWith(`<div class="alert"></div>`);
    hideAllContent();
    $("#content-edit-position").show();
    if (edit_table != null) edit_table.destroy();
    current_url_for_edit_table = "/api/position/get_all/search-sorting";
    loadEditTable("#content-edit-position #edit_positions_table", current_url_for_edit_table);

    let current_row = null;
    $("body").on("click", "#content-edit-position #editPositionBtn", function (event) {
      event.stopImmediatePropagation();
      event.preventDefault();
      current_row = $(this);
      $('#positionEditModal').modal('toggle');
    });

    $("body").on('show.bs.modal', "#content-edit-position #positionEditModal", function (event) {
      current_row = current_row.closest('tr');
      let modal = $(this);

      modal.find('#position_modal_name').val(current_row.find('.position_name').text());

      $("#save_position_modal_btn").click(function () {
        edit_position(current_row.find('.position_id').text(),
          modal.find('#position_modal_name').val());
      })
    });

    $("body").on("click", "#content-edit-position #close_position_btn", function () {
      remove_position($(this).val());
    });
  });
});

function loadEditTable(table_id, req_url) {
  edit_table = $(table_id).DataTable({
    "processing": true,
    "serverSide": true,
    "paging": false,
    "info": false,
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
      { "data": "id", "sClass": "position_id" },
      { "data": "name", "sClass": "position_name" },
      {
        "mData": null,
        "bSortable": false,
        "mRender": function (data) {
          let message1 = get_message(localStorage.getItem("lang"),
            "position.button.text.edit");
          let message2 = get_message(localStorage.getItem("lang"),
            "position.button.text.remove");
          return '<button type="button" class="btn btn-warning btn-rounded btn-sm my-0 mr-2" data-toggle="modal"'
            + ' id = "editPositionBtn" data-target="#positionEditModal">' + message1 + '</button>'
            + '<button type="button" class="btn btn-danger btn-rounded btn-sm my-0" id="close_position_btn"'
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

function edit_position(position_id, position_name) {
  let position = {};
  position.name = position_name;
  $.ajax({
    type: "PUT",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/position/edit/" + position_id,
    data: JSON.stringify(position),
    async: false,
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert').empty();
      if (data.id == null)
        $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">` + data.error + `</div>`);
      else {
        let message = get_message(localStorage.getItem("lang"),
          "position.alert.edit.success").replace("0", position.name);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        edit_table.destroy();
        loadEditTable("#content-edit-position #edit_positions_table", current_url_for_edit_table);
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

function remove_position(position_str) {
  let position = {};
  position.id = position_str.split('|')[0];
  $.ajax({
    type: "DELETE",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/position/remove/" + position.id,
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('.alert').empty();
      if (data == "") {
        let message = get_message(localStorage.getItem("lang"),
          "position.alert.remove.success").replace("0", position_str.split('|')[1]);
        $('.alert').replaceWith(`<div class="alert alert-success" role="alert">` + message + `</div>`);
        edit_table.destroy();
        loadEditTable("#content-edit-position #edit_positions_table", current_url_for_edit_table);
      } else $('.alert').replaceWith(`<div class="alert alert-danger" role="alert">` + data.error + `</div>`);
    },
    error: function (error) {
      console.log(error);
      $('.alert').empty();
      $('.alert').replaceWith(`<div class="alert alert-danger" role = "alert">500 Error</div>`);
    }
  });
}