
$(document).ready(function () {
    var chNameCheckbox = $('#changeName'),
        chNameBlock = $('#changeNameInput'),
        chWeightCheckbox = $('#changeWeight'),
        chWeightBlock = $('#changeWeightInput'),
        chPeriodCheckbox = $('#changePeriod'),
        chPeriodBlock = $('#changePeriodInput'),
        chGoalCheckbox = $('#changeGoal'),
        chGoalBlock = $('#changeGoalInput'),
        chActiveCheckbox = $('#changeActive'),
        chActiveBlock = $('#changeActiveInput');

    chNameBlock.hide();
    chWeightBlock.hide();
    chPeriodBlock.hide();
    chGoalBlock.hide();
    chActiveBlock.hide();

    chNameCheckbox.on('click', function () {
        if ($(this).is(':checked')) {
            chNameBlock.show();
            chNameBlock.find('input').attr('required', true);
        } else {
            chNameBlock.hide();
            chNameBlock.find('input').attr('required', false);
        }
    });
    chWeightCheckbox.on('click', function () {
        if ($(this).is(':checked')) {
            chWeightBlock.show();
            chWeightBlock.find('input').attr('required', true);
        } else {
            chWeightBlock.hide();
            chWeightBlock.find('input').attr('required', false);
        }
    });
    chPeriodCheckbox.on('click', function () {
        if ($(this).is(':checked')) {
            chPeriodBlock.show();
            chPeriodBlock.find('input').attr('required', true);
        } else {
            chPeriodBlock.hide();
            chPeriodBlock.find('input').attr('required', false);
        }
    });
    chGoalCheckbox.on('click', function () {
        if ($(this).is(':checked')) {
            chGoalBlock.show();
            chGoalBlock.find('input').attr('required', true);
        } else {
            chGoalBlock.hide();
            chGoalBlock.find('input').attr('required', false);
        }
    });
    chActiveCheckbox.on('click', function () {
        if ($(this).is(':checked')) {
            chActiveBlock.show();
        } else {
            chActiveBlock.hide();
        }
    });

});



