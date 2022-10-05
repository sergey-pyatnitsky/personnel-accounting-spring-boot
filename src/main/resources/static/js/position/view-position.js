let view_table = null;

$(document).ready(function () {
  $("#view-position").click(function (event) {
    event.preventDefault();
    $('.alert').replaceWith(`<div class="alert"></div>`);
    hideAllContent();
    $("#content-view-position").show();
    if (view_table != null) view_table.destroy();
    loadViewTable("#content-view-position #view_positions_table", "/api/position/get_all");
  });
});

function loadViewTable(table_id, req_url) {
  view_table = $(table_id).DataTable({
    processing: true,
    serverSide: true,
    paging: false,
    searching: false,
    info: false,
    ajax: {
      url: req_url,
      type: "POST",
      beforeSend : function(xhr) {
        xhr.setRequestHeader('Authorization', sessionStorage.getItem('tokenData'));
      },
      dataType: "json",
      contentType: "application/json",
      data: function (d) {
        return JSON.stringify(d);
      }
    },
    columns: [
      { data: "id" },
      { data: "name" }
    ],
    language: {
      url: language_url
    },
  });
  $(table_id).removeClass("no-footer");
}