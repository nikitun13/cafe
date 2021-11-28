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

    function isNotEmpty(element) {
        let body = element.val();
        if (body.trim() === "") {
            element.addClass('is-invalid');
            return false;
        } else {
            element.removeClass('is-invalid');
            return true;
        }
    }

    function validatePrice(element) {
        let str = element.val();
        let price = Number(str);
        if (str.trim() === '' || isNaN(price) || price < 0) {
            element.addClass('is-invalid');
            return false;
        } else {
            element.removeClass('is-invalid');
            return true;
        }
    }

    $('.dish-price').on('input', function () {
        validatePrice($(this))
    })

    $('.dish-name').on('keyup', function () {
        isNotEmpty($(this))
    })

    $('.updateDishForm').submit(function () {
        let id = $(this).data('id')
        let name = $('#dish-name-' + id);
        let price = $('#dish-price-' + id);

        return isNotEmpty(name)
            && validatePrice(price);
    })

    $('.description-area').on('keyup', function () {
        isNotEmpty($(this))
    })

    $('.dish-description-form').submit(function () {
        let id = $(this).data('id')
        let body = $('#description-area-' + id);

        return isNotEmpty(body);
    })

    let newDishNameElement = $('#create-name');
    let newDishPriceElement = $('#create-price');
    let newDishDescriptionElement = $('#create-description');

    newDishNameElement.on('keyup', function () {
        isNotEmpty($(this))
    });

    newDishDescriptionElement.on('keyup', function () {
        isNotEmpty($(this))
    });

    newDishPriceElement.on('input', function () {
        validatePrice($(this))
    })

    var input = document.querySelector('#create-image');

    function isValidImage(file) {
        return validFileType(file)
            && validImageSize(file)
    }

    function isValidInputImage(element) {
        let files = element.files;
        if (files.length !== 0 && isValidImage(files[0])) {
            element.classList.remove('is-invalid');
            return true;
        } else {
            element.classList.add('is-invalid');
            return false;
        }
    }

    $('#create-dish-form').submit(function () {
        return isNotEmpty(newDishNameElement)
            && validatePrice(newDishPriceElement)
            && isNotEmpty(newDishDescriptionElement)
            && isValidInputImage(input);
    })

    var preview = document.querySelector('.preview');

    input.addEventListener('change', updateImageDisplay);

    function updateImageDisplay() {
        while (preview.firstChild) {
            preview.removeChild(preview.firstChild);
        }
        isValidInputImage(input);
        var curFiles = input.files;
        if (curFiles.length !== 0) {
            var list = document.createElement('ul');
            list.style.listStyleType = 'none';
            list.style.padding = '0';
            list.style.margin = '0';
            preview.appendChild(list);
            for (var i = 0; i < curFiles.length; i++) {
                var listItem = document.createElement('li');
                var para = document.createElement('p');
                if (isValidImage(curFiles[i])) {
                    var image = document.createElement('img');
                    image.style.width = '200px';
                    image.style.maxHeight = '200px';
                    image.src = window.URL.createObjectURL(curFiles[i]);

                    listItem.appendChild(image);
                    listItem.appendChild(para);
                } else {
                    para.textContent = 'File name ' + curFiles[i].name
                        + ': Not a valid file type or size. Max size is 1MB. Update your selection.';
                    listItem.appendChild(para);
                }

                list.appendChild(listItem);
            }
        }
    }

    var fileTypes = [
        'image/png'
    ]

    function validFileType(file) {
        for (var i = 0; i < fileTypes.length; i++) {
            if (file.type === fileTypes[i]) {
                return true;
            }
        }

        return false;
    }

    var maxSize = parseInt($('#create-image').data('max-size'), 10)

    function validImageSize(file) {
        if (typeof file !== 'undefined') {
            return maxSize > file.size;
        }
    }
});
