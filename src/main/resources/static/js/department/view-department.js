let view_table = null;

$(document).ready(function () {
  $("#view-department").click(function (event) {
    $("#content-view-department .nav-link").removeClass('active');
    $("#content-view-department #getOpenDepartmentsBtn").addClass('active');

    event.preventDefault();
    $('.alert').replaceWith(`<div class="alert"></div>`);
    hideAllContent();
    $("#content-view-department").show();
    if (view_table != null) view_table.destroy();
    loadViewTable("#content-view-department #view_departments_table", "/api/department/get_all/open");
    showColumn("#end_date_column", false);

    $("#content-view-department #getOpenDepartmentsBtn").click(function () {
      view_table.destroy();
      loadViewTable("#content-view-department #view_departments_table", "/api/department/get_all/open");
      showColumn("#end_date_column", false);

      $("#content-view-department .nav-link").removeClass('active');
      $(this).addClass('active');
    });

    $("#content-view-department #getClosedDepartmentsBtn").click(function () {
      view_table.destroy();
      loadViewTable("#content-view-department #view_departments_table", "/api/department/get_all/closed");
      showColumn("#end_date_column", true);

      $("#content-view-department .nav-link").removeClass('active');
      $(this).addClass('active');
    });
  });
});

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
      { "data": "id" },
      { "data": "name" },
      {
        "data": "active", render: function (data) {
          return data
            ? '<p class="text-success">+</p>'
            : '<p class="text-danger">-</p>';
        }
      },
      {
        "data": "startDate", render: function (data) {
          return data != null
              ? '<p class="text-success">' + data + '</p>'
              : '<p class="text-danger">-</p>';
        }
      },
      {
        "data": "endDate", render: function (data) {
          return data != null
              ? '<p class="text-success">' + data + '</p>'
              : '<p class="text-danger">-</p>';
        }
      },
    ],
    "aoColumnDefs": [
      { 'bSortable': false, 'aTargets': [ 2 ] }
    ],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}

function showColumn(column_id, mode) {
  if (mode == true) {
    $(column_id).show();
    view_table.column(4).visible(true);
  } else {
    $(column_id).hide();
    view_table.column(4).visible(false);
  }
}