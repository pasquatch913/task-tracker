function updateCompletions(instanceId, value) {
    //find the right element to increment in browser
    var elementToUpdate = document.getElementById("completions" + instanceId);
    var newCompletionsValue = parseInt(elementToUpdate.innerText) + value;
    elementToUpdate.innerText =  newCompletionsValue;

    //increment the correct taskInstance's counter
    var url = '/web/subscriptions/instances/' + instanceId + '/completions/' + newCompletionsValue;
    $.post(url, function (data, status) {
        console.log('request sent!');
    });
}

function updateOneTimeCompletions(id, value) {
    //find the right element to increment in browser
    var elementToUpdate = document.getElementById("completions" + id);
    var newCompletionsValue = parseInt(elementToUpdate.innerText) + value;
    elementToUpdate.innerText = newCompletionsValue;

    //increment the correct taskInstance's counter
    var url = '/web/oneTimes/' + id + '/completions/' + newCompletionsValue;
    $.post(url, function (data, status) {
        console.log('request sent!');
    });
}
