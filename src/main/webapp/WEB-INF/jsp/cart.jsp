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
    <link rel="stylesheet" href="<c:url value="/css/cart.css"/>"/>
    <link rel="shortcut icon" href="<c:url value="/img/favicon.ico"/>" type="image/x-icon"/>
    <title>Cafe - <fmt:message key="cafe.cart"/></title>
</head>
<body class="d-flex flex-column h-100">

<c:import url="header.jsp" charEncoding="utf-8"/>

<main class="d-flex flex-row flex-wrap justify-content-center my-auto">
    <div class="container card mb-4">
        <div class="row">
            <div class="col-md-7 cart">
                <div class="row border-bottom border-secondary">
                    <div class="col">
                        <h4>
                            <fmt:message key="cafe.cart"/>
                        </h4>
                    </div>
                </div>

                <ul class="list-group list-group-flush show-cart"></ul>

                <div id="emptyCart" class="mt-3 text-center" hidden>
                    <img src="<c:url value="/img/nothing-found.png"/>" alt="empty cart">
                    <span style="font-size: 30px">
                        <fmt:message key="cart.empty"/>
                    </span>
                </div>
            </div>

            <div class="col-md-5 summary">
                <div class="border-bottom border-secondary row">
                    <h4>
                        <fmt:message key="cart.summary"/>
                    </h4>
                </div>
                <form method="post" id="orderForm" class="fs-5" novalidate>
                    <div class="row my-2">
                        <div class="col-auto">
                            <span>
                                <fmt:message key="cart.totalItems"/>:
                            </span>
                            <span class="total-count"></span>
                        </div>
                        <div class="col d-flex justify-content-end">
                            <span class="total-cart" id="totalCart"></span>
                            <span class="ms-1">BYN</span>
                        </div>
                    </div>

                    <label for="expectedDate">
                        <fmt:message key="cart.expectedDate"/>
                        <input class="form-control" type="datetime-local" id="expectedDate"
                               required value="${requestScope.minDate}" min="${requestScope.minDate}"
                               max="${requestScope.maxDate}"/>
                        <div class="invalid-feedback">
                            <span class="me-1"><fmt:message key="cart.error.expectedDate"/>:</span>
                            <span class="me-1"><fmt:message key="cart.error.expectedDate.from"/></span>
                            <span class="me-1">
                                <fmt:formatDate value="${requestScope.minDateTimestamp}" type="both"
                                                timeStyle="medium" dateStyle="short"/>
                            </span>
                            <span class="me-1"><fmt:message key="cart.error.expectedDate.to"/></span>
                            <span>
                                <fmt:formatDate value="${requestScope.maxDateTimestamp}" type="both"
                                                timeStyle="medium" dateStyle="short"/>
                            </span>
                        </div>
                    </label>

                    <div class="row my-3">
                        <div class="d-flex flex-row">
                            <span class="me-2">
                                <fmt:message key="cart.points.available"/>:
                            </span>
                            <span id="availablePoints">
                                <c:out value="${user.points}"/>
                            </span>
                        </div>
                        <label for="debitedPoints">
                            <fmt:message key="cart.points.writeOff"/>
                        </label>
                        <div class="col-6" id="debitedPointsBlock">
                            <input class="form-control" type="number" id="debitedPoints" name="debitedPoints"
                                   required min="0" max="${user.points}" value="0">
                        </div>
                        <div class="invalid-feedback">
                            <fmt:message key="cart.error.points"/>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="d-flex flex-row">
                            <span class="me-2"><fmt:message key="cart.totalPrice"/>:</span>
                            <span id="totalPrice"></span>
                        </div>
                    </div>

                    <c:if test="${not empty requestScope.errorMessageKey}">
                        <div class="alert alert-danger alert-dismissible fade show mb-3">
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            <strong><fmt:message key="cafe.error"/>!</strong>
                            <fmt:message key="${requestScope.errorMessageKey}"/>
                        </div>
                    </c:if>

                    <div class="row mx-1" id="submitOrderButtonBlock">
                        <button type="submit" class="btn btn-success">
                            <fmt:message key="cart.makeOrder"/>
                        </button>
                    </div>
                    <div class="invalid-feedback">
                        <fmt:message key="cart.error.empty"/>
                    </div>
                </form>
            </div>
        </div>
    </div>
</main>

<c:import url="footer.jsp" charEncoding="utf-8"/>

<script type="text/javascript" src="<c:url value="/js/jquery-3.6.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bootstrap.bundle.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/cafe.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/cart.js"/>"></script>
</body>
</html>
