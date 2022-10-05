$(document).ready(function () {
  $('#employeeTable').hide();
  $('#feedback').hide();
  $("#bth-search").click(function (event) {
    $('#feedback').hide();
    event.preventDefault();
    search_employee();
  });

});

function search_employee() {
  let value = $("#search_select option:selected").val();

  if (value == "email") search_by_email($("#searach_criteria").val());
  else if (value == "phone") search_by_phone($("#searach_criteria").val());
  else search_by_name($("#searach_criteria").val());
}

function search_by_email(search_value) {
  $.ajax({
    type: "POST",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/employee/telephone-directory/search/by_email/" + search_value,
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('#tbody_search').remove();
      $('#employeeTable').show();

      $('#employeeTable').append(`
        <tbody id='tbody_search'>${data.map(n => `
          <tr>
            <td>${n.id}</td>
            <td>${n.name}</td>
            <td>${n.profile.phone}</td>
            <td>${n.profile.email}</td>
          </tr>`).join('')}
        </tbody>
      `);
    },
    error: function (error) {
      console.log(error);
      $('#employeeTable').hide();
      $('#feedback').show();
    }
  });
}

function search_by_phone(search_value) {
  $.ajax({
    type: "POST",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/employee/telephone-directory/search/by_phone/" + search_value,
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('#tbody_search').remove();
      $('#employeeTable').show();

      $('#employeeTable').append(`
      <tbody id='tbody_search'>${data.map(n => `
        <tr>
          <td>${n.id}</td>
          <td>${n.name}</td>
          <td>${n.profile.phone}</td>
          <td>${n.profile.email}</td>
        </tr>`).join('')}
      </tbody>
    `);
    },
    error: function (error) {
      console.log(error);
      $('#employeeTable').hide();
      $('#feedback').show();
    }
  });
}

function search_by_name(search_value) {
  $.ajax({
    type: "POST",
    headers: {"Authorization": sessionStorage.getItem('tokenData')},
    contentType: "application/json",
    url: "/api/employee/telephone-directory/search/by_name/" + search_value,
    cache: false,
    timeout: 600000,
    success: function (data) {
      $('#tbody_search').remove();
      $('#employeeTable').show();

      $('#employeeTable').append(`
      <tbody id='tbody_search'>${data.map(n => `
        <tr>
          <td>${n.id}</td>
          <td>${n.name}</td>
          <td>${n.profile.phone}</td>
          <td>${n.profile.email}</td>
        </tr>`).join('')}
      </tbody>
    `);
    },
    error: function (error) {
      console.log(error);
      $('#employeeTable').hide();
      $('#feedback').show();
    }
  });
}