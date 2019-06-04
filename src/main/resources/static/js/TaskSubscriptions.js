function completeTaskSubscription(id) {
    var url = '/web/subscriptions/complete/' + id;
    $.ajax({
        url: url,
        type: 'POST',
        success: function () {
            console.log('request sent!')
        }
    });
    document.getElementById('subscription' + id).style.display = "none";
}

function completeOneTime(id) {
    var url = '/web/oneTimes/complete/' + id;
    $.ajax({
        url: url,
        type: 'POST',
        success: function () {
            console.log('request sent!')
        }
    });
    document.getElementById('oneTime' + id).style.display = "none";
}