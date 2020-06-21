$(function () {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});

$(document).ready(function () {
    $("#user-search-form").submit(function (event) {
        event.preventDefault();
        fire_search_user();
    });
});

function fire_search_user() {
    $("#btn-search").prop("disabled", true);
    var search = {};
    search["userEmail"] = $("#userEmail").val();

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/search/user",
        data: JSON.stringify(search),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (response) {
            if(response.isFound == true) {
                $('#searchUser').hide(500);
                $('#email').val(response.email);
                setTimeout(function(){
                    $('#shareRequest').show(500);
                }, 100);
                Materialize.updateTextFields();

            } else {
                Materialize.toast(response.message, 4000);
                $("#btn-search").prop("disabled", false);
            }
        },
        error: function (error) {
            $("#btn-search").prop("disabled", false);
            $('#shareRequest').hide();

            var errorResponse = JSON.parse(error.responseText);
            Materialize.toast(errorResponse.message, 4000);
        }
    });
}

function backToUserSearch() {
    $('#shareRequest').hide(500);
    $("#btn-search").prop("disabled", false);
    setTimeout(function(){
        $('#searchUser').show(500);
    }, 100);
}

$(document).ready(function () {
    $("#generate-link-form").submit(function (event) {
        event.preventDefault();
        fire_generate_link();
    });
});

function fire_generate_link() {
    $("#btn-link").prop("disabled", true);
    var search = {};
    search["cloudFileName"] = $('#cloudFileNameLink').val();
    search["linkTime"] = $('#linkTime').val();

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/file/link",
        data: JSON.stringify(search),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (response) {
            $('#generateLink').hide(1000);
            $('#link').val(response.link);
            Materialize.updateTextFields();
            $('#linkBox').show(1000);
        },
        error: function (error) {
            $("#btn-link").prop("disabled", false);
            $('#linkBox').hide();

            var errorResponse = JSON.parse(error.responseText);
            Materialize.toast(errorResponse.message, 4000);
        }
    });
}

function getContentType(contentType) {
    return 'text23';
}