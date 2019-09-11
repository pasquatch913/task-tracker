function incrementCompletions(instanceId) {
    //find the right element to increment in browser
    var elementToUpdate = document.getElementById("completions" + instanceId);
    var newCompletionsValue = parseInt(elementToUpdate.innerText) + 1;
    elementToUpdate.innerText =  newCompletionsValue;

    //increment the correct taskInstance's counter
    var url = '/web/tasks/complete/' + instanceId;
    $.post(url, function (data, status) {
        console.log('request sent!');
    });
}

function decrementCompletions(instanceId) {
    //find the right element to increment in browser
    var elementToUpdate = document.getElementById("completions" + instanceId);
    var newCompletionsValue = parseInt(elementToUpdate.innerText) - 1;
    elementToUpdate.innerText = newCompletionsValue;

    //increment the correct taskInstance's counter
    var url = '/web/tasks/complete/' + instanceId;
    $.post(url, function (data, status) {
        console.log('request sent!');
    });
}
