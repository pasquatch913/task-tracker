function updateCompletions (id, value) {
    //find the right element to increment in browser
    var elementToUpdate = document.getElementById("completions" + id);
    var newCompletionsValue = parseInt(elementToUpdate.innerText) + value;
    elementToUpdate.innerText =  newCompletionsValue;
    //increment the correct taskInstance's counter

    var url = '/tasks/instances/' + id + '/completions/' + newCompletionsValue;
    $.post(url, function (data, status) {
       console.log(`${data} and ${status}}`);
    });
}
