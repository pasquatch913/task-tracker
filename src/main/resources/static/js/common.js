$(document).ready(function () {
    var token = $("meta[name='_csrf']").attr("content");

    $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
        jqXHR.setRequestHeader('X-CSRF-Token', token);
    });
});