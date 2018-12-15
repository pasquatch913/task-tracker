function deleteTaskSubscription(id) {
    var url = '/tasks/' + id;
    $.ajax({
        url: url,
        type: 'DELETE',
        success: function () {
            console.log('request sent!')
        }
    });
    document.getElementById('subscription' + id).style.display = "none";
}