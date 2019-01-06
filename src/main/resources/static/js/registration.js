// works but user experience is kinda crappy -> revisit this
function check(input) {
    if (document.getElementById('confirmPassword').value != document.getElementById('password').value) {
        input.setCustomValidity('Password Must be Matching.');
    } else {
        // input is valid -- reset the error message
        input.setCustomValidity('');
    }
}