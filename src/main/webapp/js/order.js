let url = $('#orderedDishes').data('url');

jQuery("a.order").click(function () {
    let currentElement = this;
    let orderId = $(currentElement).attr('data-orderId');
    $.ajax({
        type: 'POST',
        url: url,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        contentType: 'application/x-www-form-urlencoded; charset=utf-8',
        data: jQuery.param({orderId: orderId}),
        success: function (response) {
            showOrderedDishes(response)
            $('a.order.active').removeClass('active')
            $(currentElement).addClass('active')
        }
    })
})

function showOrderedDishes(jsObjects) {
    let output = "";
    for (var i = 0; i < jsObjects.length; i++) {
        var orderedDish = jsObjects[i];
        output += "<li class=\"list-group-item d-flex justify-content-between align-items-start\">\n" +
            "                                <div class=\"ms-2 me-auto\">\n" +
            "                                    <div class=\"fw-bold\">" + orderedDish.dish.name + " </div>\n" +
            "                                    <span>\n" + orderedDish.dishPrice / 100 + " BYN x " + orderedDish.dishCount + "\n" +
            "                                    </span>\n" +
            "                                </div>\n" +
            "                                <span class=\"badge bg-primary rounded-pill\">\n" + orderedDish.totalPrice / 100 + " BYN\n" +
            "                                </span>\n" +
            "                            </li>"
    }
    $('#orderedDishes').html(output);
}
