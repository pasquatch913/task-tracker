function incrementCompletions(instanceId) {
    //find the right element to increment in browser
    var elementToUpdate = document.getElementById("completions" + instanceId);
    var newCompletionsValue = parseInt(elementToUpdate.innerText) + 1;

    //base URL used when no custom time is provided
    var url = '/web/tasks/complete/' + instanceId;

    //if time is provided, use query param for time with request
    if (document.getElementById('customTimeCheckbox' + instanceId).checked
        && document.getElementById('datetimeinput' + instanceId).value !== '') {
        var customCompletionDate = document.getElementById('datetimeinput' + instanceId).value;

        url = url.concat('?time=', customCompletionDate);
    }

    $.post(url, function (data, status) {
        console.log('request sent!');
    });
    elementToUpdate.innerText = newCompletionsValue;
}

function decrementCompletions(instanceId) {
    //find the right element to increment in browser
    var elementToUpdate = document.getElementById("completions" + instanceId);
    var newCompletionsValue = parseInt(elementToUpdate.innerText) - 1;
    elementToUpdate.innerText = newCompletionsValue;

    //increment the correct taskInstance's counter
    var url = '/web/tasks/uncomplete/' + instanceId;
    $.post(url, function (data, status) {
        console.log('request sent!');
    });
}

$(document).ready(function () {

    console.log("ready!");

    $('.dateSection').toggle();
    $('.dateSection').datetimepicker({
        format: "YYYY-MM-DDTHH:mm"
    });

    function configureDateWidget(event) {
        if (event.target && event.target.name === 'customTimeCheckbox') {
            var checkboxId = event.target.id;
            var rowId = checkboxId.match(/\d+/g);
            var dateInputSection = document.getElementById('datetimepicker' + rowId);
            if (event.target.checked) {
                dateInputSection.style.removeProperty('display');
                dateInputSection.setAttribute('required', true);
            }
            else {
                dateInputSection.style.display = "none";
                dateInputSection.setAttribute('required', false);
                document.getElementById('datetimeinput' + rowId).value = '';
            }
        }
    }

    document.addEventListener('change', configureDateWidget, true);

});
