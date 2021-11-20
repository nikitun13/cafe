$(document).ready(function () {
    let $review = $('#review-message');
    let $rating = $('#review-rating');

    $rating.on('input', function () {
        $('#rating-output').val($rating.val())
    });

    $review.on('keyup', function () {
        $('#leftChars').val(1024 - $review.val().length)
    });

    $review.keyup(function () {
        validateCommentBody();
    });

    function validateCommentBody() {
        let body = $review.val();
        if (body.trim() === "") {
            $review.addClass('is-invalid');
            return false;
        } else {
            $review.removeClass('is-invalid');
            return true;
        }
    }

    $('#sendReview').click(function () {
        return validateCommentBody();
    });

    function validateSearchBody() {
        let $searchInput = $('#searchInput');
        let body = $searchInput.val();
        if (body.trim() === "") {
            $searchInput.addClass('is-invalid');
            return false;
        } else {
            $searchInput.removeClass('is-invalid');
            return true;
        }
    }

    $('#search-form').submit(function () {
        return validateSearchBody();
    })
});
