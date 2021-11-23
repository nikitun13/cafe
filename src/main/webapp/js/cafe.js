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

    $('#signInBtn').click(function () {
        return validateSignInEmail()
            && validateSignInPassword();
    })

    let signInEmailInput = $('#signInEmailInput');
    let signInPasswordInput = $('#signInPasswordInput');

    signInEmailInput.keyup(function () {
        validateSignInEmail();
    });

    signInPasswordInput.keyup(function () {
        validateSignInPassword();
    });

    function validateSignInEmail() {
        if (signInEmailInput.val().trim() === "") {
            signInEmailInput.addClass('is-invalid');
            return false;
        } else {
            signInEmailInput.removeClass('is-invalid');
            return true;
        }
    }

    function validateSignInPassword() {
        if (signInPasswordInput.val().trim() === "") {
            signInPasswordInput.addClass('is-invalid');
            return false;
        } else {
            signInPasswordInput.removeClass('is-invalid');
            return true;
        }
    }

    let email = $('#email');
    let firstName = $('#firstName');
    let lastName = $('#lastName');
    let password = $('#password');
    let repeatPassword = $('#repeatPassword');
    let phone = $('#phone');

    email.on('keyup', validateEmail)
    firstName.on('keyup', validateFirstName)
    lastName.on('keyup', validateLastName)
    password.on('keyup', validatePassword)
    repeatPassword.on('keyup', validateRepeatPassword)
    phone.on('keyup', validatePhone)

    $('#signUpBtn').click(function () {
        return validateEmail()
            && validateFirstName()
            && validateLastName()
            && validatePhone()
            && validatePassword(password)
            && validateRepeatPassword()
    })

    let emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
        + "[^-.][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    function validateEmail() {
        let val = email.val();
        if (val.trim() === "" || !val.match(emailRegex)) {
            email.addClass('is-invalid');
            return false;
        } else {
            email.removeClass('is-invalid');
            return true;
        }
    }

    function validateFirstName() {
        return validateString(firstName)
    }

    function validateLastName() {
        return validateString(lastName)
    }

    function validateString(element) {
        let val = element.val();
        if (val.trim() === "") {
            element.addClass('is-invalid');
            return false;
        } else {
            element.removeClass('is-invalid');
            return true;
        }

    }

    let phoneRegex = "^\\d{10,15}$";

    function validatePhone() {
        let val = phone.val();
        if (val.trim() === "" || !val.match(phoneRegex)) {
            phone.addClass('is-invalid');
            return false;
        } else {
            phone.removeClass('is-invalid');
            return true;
        }
    }

    let passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}";

    function validateRepeatPassword() {
        let repeatPasswordVal = repeatPassword.val();
        let passwordVal = password.val();
        if (passwordVal === repeatPasswordVal) {
            repeatPassword.removeClass('is-invalid');
            return true;
        } else {
            repeatPassword.addClass('is-invalid');
            return false;
        }
    }

    function clearCartIfOrderCreated() {
        if ($('#createdOrder').length) {
            localStorage.removeItem('shoppingCart');
        }
    }

    clearCartIfOrderCreated();

    let oldPassword = $('#oldPassword');
    let newPassword = $('#newPassword');
    let repeatNewPassword = $('#repeatNewPassword');

    oldPassword.on('keyup', function () {
        validatePassword(oldPassword)
    })
    newPassword.on('keyup', function () {
        validatePassword(newPassword)
    })
    repeatNewPassword.on('keyup', validateRepeatNewPassword)

    function validatePassword(passwordElement) {
        let val = passwordElement.val();
        if (val.trim() === "" || !val.match(passwordRegex)) {
            passwordElement.addClass('is-invalid');
            return false;
        } else {
            passwordElement.removeClass('is-invalid');
            return true;
        }
    }

    function validateRepeatNewPassword() {
        let repeatPasswordVal = repeatNewPassword.val();
        let passwordVal = newPassword.val();
        if (passwordVal === repeatPasswordVal) {
            repeatNewPassword.removeClass('is-invalid');
            return true;
        } else {
            repeatNewPassword.addClass('is-invalid');
            return false;
        }
    }

    $('#userInfo').submit(function () {
        return validateEmail()
            && validateFirstName()
            && validateLastName()
            && validatePhone()
    })

    $('#userPass').submit(function () {
        return validatePassword(oldPassword)
            && validatePassword(newPassword)
            && validateRepeatNewPassword()
    })
});
