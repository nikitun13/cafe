<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:import url="locale.jsp" charEncoding="utf-8"/>

<c:set var="user" value="${sessionScope.user}"/>

<!doctype html>
<html lang="en" class="h-100">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="stylesheet" href="<c:url value="/css/bootstrap.min.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/signin.css"/>"/>
    <link rel="shortcut icon" href="<c:url value="/img/favicon.ico"/>" type="image/x-icon"/>
    <title>Cafe - <fmt:message key="cafe.profile.orders"/></title>
</head>
<body class="d-flex flex-column h-100">

<c:import url="header.jsp" charEncoding="utf-8"/>

<c:if test="${requestScope.orderCreated}">
    <div id="createdOrder" hidden></div>
</c:if>
<main class="d-flex flex-row justify-content-center h-75">
    <div class="container rounded h-100">
        <div class="row h-100">
            <div class="col-md-3 border-right">
                <div class="d-flex flex-column align-items-center text-center p-3 py-5">
                    <svg xmlns="http://www.w3.org/2000/svg" width="150" fill="currentColor" class="bi bi-person-circle"
                         viewBox="0 0 16 16">
                        <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0z"></path>
                        <path fill-rule="evenodd"
                              d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8zm8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1z"></path>
                    </svg>
                    <span class="mt-1"><c:out value="${user.firstName}"/></span>
                    <span class="text-black-50"><c:out value="${user.email}"/></span>
                    <div>
                        <span>
                            <fmt:message key="cart.points.available"/>:
                        </span>
                        <span><c:out value="${user.points}"/></span>
                    </div>
                    <div class="list-group w-100 mt-3">
                        <a href="<c:url value="/profile"/>" class="list-group-item list-group-item-action">
                            <fmt:message key="cafe.profile"/>
                        </a>
                        <a href="<c:url value="/profile/orders"/>"
                           class="list-group-item list-group-item-action active">
                            <fmt:message key="cafe.profile.orders"/>
                        </a>
                        <a href="<c:url value="/cart"/>" class="list-group-item list-group-item-action">
                            <fmt:message key="cafe.cart"/>
                        </a>
                    </div>
                    <c:if test="${not empty requestScope.errorMessageKey}">
                        <div class="alert alert-danger alert-dismissible fade show mt-3">
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            <strong><fmt:message key="cafe.error"/>!</strong>
                            <fmt:message key="${requestScope.errorMessageKey}"/>
                        </div>
                    </c:if>
                    <c:if test="${not empty requestScope.successMessageKey}">
                        <div class="alert alert-success alert-dismissible fade show mt-3">
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            <strong><fmt:message key="cafe.success"/>!</strong>
                            <fmt:message key="${requestScope.successMessageKey}"/>
                        </div>
                    </c:if>
                </div>
            </div>

            <c:set var="orders" value="${requestScope.orders}"/>

            <div class="col-md-4 h-100">
                <div class="p-3 py-5 h-100">
                    <c:choose>
                        <c:when test="${empty orders}">
                            <div class="d-flex flex-wrap justify-content-center mb-3">
                                <img src="<c:url value="/img/nothing-found.png"/>"
                                     class="img-fluid my-3" alt="nothing found img"/>
                                <span class="display-6 col-12 text-center">
                                    <fmt:message key="profile.order.noOrders"/>
                                </span>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <ol class="list-group list-group-numbered overflow-auto h-100" id="orderedDishes"
                                data-url="<c:url value="/async/ordereddishes"/>">
                                <c:forEach var="orderedDish" items="${requestScope.orderedDishes}">
                                    <li class="list-group-item d-flex justify-content-between align-items-start">
                                        <div class="ms-2 me-auto">
                                            <div class="fw-bold"><c:out value="${orderedDish.dish.name}"/></div>
                                            <span>
                                        <c:out value="${orderedDish.dishPrice / 100}"/> BYN x <c:out
                                                    value="${orderedDish.dishCount}"/>
                                    </span>
                                        </div>
                                        <span class="badge bg-primary rounded-pill">
                                    <c:out value="${orderedDish.totalPrice / 100}"/> BYN
                                </span>
                                    </li>
                                </c:forEach>
                            </ol>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="col-md-5 h-100">
                <div class="p-3 pt-5 h-100">
                    <div class="list-group list-group-flush overflow-auto h-100">
                        <c:forEach var="order" items="${orders}" varStatus="status">
                            <a class="order list-group-item list-group-item-action py-3 lh-tight
                                <c:if test="${status.first}">active</c:if>" style="cursor: pointer"
                               data-orderId="${order.id}">
                                <div class="d-flex w-100 align-items-center justify-content-between">
                                    <div>
                                        <strong class="mb-1"><fmt:message key="profile.order"/> №${order.id}</strong>
                                        <small class="ms-3">
                                            <fmt:formatDate value="${order.createdAt}" type="both"
                                                            timeStyle="medium" dateStyle="short"/>
                                        </small>
                                    </div>
                                    <div>
                                        <c:if test="${requestScope.isDeletableOrdersMap[order]}">
                                            <form method="post">
                                                <input type="hidden" name="orderId" value="${order.id}">
                                                <button class="btn btn-danger btn-sm">Delete</button>
                                            </form>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="col-12 mb-1">
                                    <div class="d-flex justify-content-between">
                                        <span><fmt:message key="profile.status"/>: ${order.status}</span>
                                        <span>
                                            <fmt:message key="cart.totalPrice"/>: <fmt:formatNumber
                                                maxFractionDigits="2"
                                                minFractionDigits="2"
                                                value="${order.totalPrice/100}"/>
                                        </span>
                                    </div>
                                </div>

                                <div class="col-12 mb-1">
                                    <div class="d-flex justify-content-between">
                                        <span>
                                            <fmt:message key="profile.accruedPoints"/>: ${order.accruedPoints}
                                        </span>
                                        <span>
                                            <fmt:message key="profile.debitedPoints"/>: ${order.debitedPoints}
                                        </span>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<c:import url="footer.jsp" charEncoding="utf-8"/>

<script type="text/javascript" src="<c:url value="/js/jquery-3.6.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bootstrap.bundle.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/cafe.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/order.js"/>"></script>
</body>
</html>
