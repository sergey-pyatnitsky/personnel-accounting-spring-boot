let view_table = null;

$(document).ready(function () {
  $("#view-user").click(function (event) {
    event.preventDefault();
    $('.alert').replaceWith(`<div class="alert"></div>`);
    $("#content-view-user .nav-link").removeClass('active');
    $("#content-view-user #employee_btn").addClass('active');
    hideAllContent();
    $("#content-view-user").show();
    if (view_table != null) view_table.destroy();
    loadViewTable("#content-view-user #view_data_table", "/api/employee/get_all");

    $("#content-view-user #employee_btn").click(function () {
      $("#content-view-user .nav-link").removeClass('active');
      $("#content-view-user #employee_btn").addClass('active');
      view_table.destroy();
      loadViewTable("#content-view-user #view_data_table", "/api/employee/get_all");
    });

    $("#content-view-user #admin_btn").click(function () {
      $("#content-view-user .nav-link").removeClass('active');
      $("#content-view-user #admin_btn").addClass('active');
      view_table.destroy();
      loadViewTable("#content-view-user #view_data_table", "/api/employee/get_all/admins");
    });

    $("#content-view-user #dismissed_btn").click(function () {
      $("#content-view-user .nav-link").removeClass('active');
      $("#content-view-user #dismissed_btn").addClass('active');
      view_table.destroy();
      current_url_for_edit_table = "/api/employee/get_all/dismissed";
      loadViewTable("#content-view-user #view_data_table", current_url_for_edit_table);
    });
  });
});

function loadViewTable(table_id, req_url) {
  view_table = $(table_id).DataTable({
    "processing": true,
    "serverSide": true,
    "pagingType": "full_numbers",
    "destroy": true,
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