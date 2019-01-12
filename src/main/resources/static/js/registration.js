// works but user experience is kinda crappy -> revisit this
// currently requires that user blur a check box to get it to validate if it fails once
// better user experience:
// 1. don't check until at least 1 character is present in the 2nd text box
// 2. check on keystroke? afterwards as long as the keystroke is within either pw textbox
function check(input) {
    if (document.getElementById('confirmPassword').value != document.getElementById('password').value) {
        input.setCustomValidity('Password Must be Matching.');
    } else {
        // input is valid -- reset the error message
        input.setCustomValidity('');
    }
}