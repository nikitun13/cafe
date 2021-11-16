var shoppingCart = (function () {

    cart = [];

    // Constructor
    function Item(id, name, price, count, img) {
        this.id = id
        this.name = name;
        this.price = price;
        this.count = count;
        this.img = img
    }

    // Save cart
    function saveCart() {
        localStorage.setItem('shoppingCart', JSON.stringify(cart));
    }

    // Load cart
    function loadCart() {
        cart = JSON.parse(localStorage.getItem('shoppingCart'));
    }

    if (localStorage.getItem("shoppingCart") != null) {
        loadCart();
    }


    var obj = {};

    // Add to cart
    obj.addItemToCart = function (id, name, price, count, img) {
        for (var current in cart) {
            if (cart[current].id === id) {
                cart[current].count++;
                saveCart();
                return;
            }
        }
        var item = new Item(id, name, price, count, img);
        cart.push(item);
        saveCart();
    }
    // Set count from item
    obj.setCountForItem = function (id, count) {
        for (var i in cart) {
            if (cart[i].id === id) {
                cart[i].count = count;
                break;
            }
        }
    };
    // Remove item from cart
    obj.removeItemFromCart = function (id) {
        for (var item in cart) {
            if (cart[item].id === id) {
                cart[item].count--;
                if (cart[item].count === 0) {
                    cart.splice(item, 1);
                }
                break;
            }
        }
        saveCart();
    }

    // Remove all items from cart
    obj.removeItemFromCartAll = function (id) {
        for (var item in cart) {
            if (cart[item].id === id) {
                cart.splice(item, 1);
                break;
            }
        }
        saveCart();
    }

    // Clear cart
    obj.clearCart = function () {
        cart = [];
        saveCart();
    }

    // Count cart
    obj.totalCount = function () {
        var totalCount = 0;
        for (var item in cart) {
            totalCount += cart[item].count;
        }
        return totalCount;
    }

    // Total cart
    obj.totalCart = function () {
        var totalCart = 0;
        for (var item in cart) {
            totalCart += cart[item].price * cart[item].count;
        }
        return Number(totalCart.toFixed(2));
    }

    // List cart
    obj.listCart = function () {
        var cartCopy = [];
        for (i in cart) {
            item = cart[i];
            itemCopy = {};
            for (p in item) {
                itemCopy[p] = item[p];

            }
            itemCopy.total = Number(item.price * item.count).toFixed(2);
            cartCopy.push(itemCopy)
        }
        return cartCopy;
    }

    // cart : Array
    // Item : Object/Class
    // addItemToCart : Function
    // removeItemFromCart : Function
    // removeItemFromCartAll : Function
    // clearCart : Function
    // countCart : Function
    // totalCart : Function
    // listCart : Function
    // saveCart : Function
    // loadCart : Function
    return obj;
})();


// Add item
$('.add-to-cart').click(function (event) {
    event.preventDefault();
    var name = $(this).data('name');
    var price = Number($(this).data('price'));
    var id = Number($(this).data('id'));
    var img = $(this).data('img');
    shoppingCart.addItemToCart(id, name, price, 1, img);
    displayCart();
});

// Clear items
$('.clear-cart').click(function () {
    shoppingCart.clearCart();
    displayCart();
});


function displayCart() {
    var cartArray = shoppingCart.listCart();
    var output = "";
    for (var i in cartArray) {
        output +=
            " <li class=\"list-group-item\">\n" +
            "                        <div class=\"d-flex justify-content-between\">\n" +
            "                             <div class=\"col-12 my-1\">\n" +
            "                                <img src='/img/dishes/dish-" + cartArray[i].id + ".png' alt='" + cartArray[i].name + " picture'" +
            "                                 style=\"width: 32px; height: auto;\"/>\n" +
            "                                <span>" + cartArray[i].name + "</span>\n" +
            "                             </div>\n" +
            "                             <button type=\"button\" class=\"btn-close mt-1 delete-item\" data-id='" + cartArray[i].id + "' aria-label=\"Close\"></button>\n" +
            "                        </div>" +
            "                        <div class=\"col-12 d-flex justify-content-between\">\n" +
            "                            <div class=\"btn-group\" role=\"group\">\n" +
            "                                <button type=\"button\" class=\"btn btn-outline-danger py-0 px-1 minus-item\" data-id='" + cartArray[i].id + "'>\n" +
            "                                    <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\"\n" +
            "                                         class=\"bi bi-dash-lg\" viewBox=\"0 0 16 16\">\n" +
            "                                        <path fill-rule=\"evenodd\"\n" +
            "                                              d=\"M2 8a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11A.5.5 0 0 1 2 8Z\"></path>\n" +
            "                                    </svg>\n" +
            "                                </button>\n" +
            "                                <span class=\"mx-2\">" + cartArray[i].count + "</span>\n" +
            "                                <button type=\"button\" class=\"btn btn-outline-success py-0 px-1 plus-item\" data-id='" + cartArray[i].id + "'>\n" +
            "                                    <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\"\n" +
            "                                         class=\"bi bi-plus-lg\" viewBox=\"0 0 16 16\">\n" +
            "                                        <path fill-rule=\"evenodd\"\n" +
            "                                              d=\"M8 2a.5.5 0 0 1 .5.5v5h5a.5.5 0 0 1 0 1h-5v5a.5.5 0 0 1-1 0v-5h-5a.5.5 0 0 1 0-1h5v-5A.5.5 0 0 1 8 2Z\"></path>\n" +
            "                                    </svg>\n" +
            "                                </button>\n" +
            "                            </div>\n" +
            "                            <span>" + cartArray[i].price + " BYN</span>\n" +
            "                        </div>\n" +
            "                    </li>";
    }
    $('.show-cart').html(output);
    $('.total-cart').html(shoppingCart.totalCart());
    $('.total-count').html(shoppingCart.totalCount());
}

// Delete item button
$('.show-cart').on("click", ".delete-item", function (event) {
    var id = Number($(this).data('id'));
    shoppingCart.removeItemFromCartAll(id);
    displayCart();
}).on("click", ".minus-item", function (event) { // -1 button
    var id = Number($(this).data('id'));
    shoppingCart.removeItemFromCart(id);
    displayCart();
}).on("click", ".plus-item", function (event) { // +1 button
    var id = Number($(this).data('id'));
    shoppingCart.addItemToCart(id);
    displayCart();
}).on("change", ".item-count", function (event) { // Item count input
    var id = Number($(this).data('id'));
    var count = Number($(this).val());
    shoppingCart.setCountForItem(id, count);
    displayCart();
});

displayCart();
