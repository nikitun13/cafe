$(document).ready(function () {

    $('.updateForm').submit(function () {
        let id = $(this).data('id')
        let currentElement = $('#points-' + id);
        return validatePoints(currentElement);
    })

    $('.points').on('input', function () {
        validatePoints($(this));
    })

    function validatePoints(element) {
        let points = Number(element.val());
        if (isNaN(points) || !Number.isInteger(points) || points < 0) {
            element.addClass('is-invalid');
            return false;
        } else {
            element.removeClass('is-invalid');
            return true;
        }
    }
});
