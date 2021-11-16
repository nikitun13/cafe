<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fmd" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="locale.jsp"/>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="stylesheet" href="<c:url value="/css/bootstrap.min.css"/>"/>
    <link href="<c:url value="/css/sidebars.css"/>" rel="stylesheet">
    <link rel="shortcut icon" href="<c:url value="/img/favicon.ico"/>" type="image/x-icon"/>
    <title>Cafe - <fmt:message key="cafe.main"/></title>
</head>
<body class="d-flex flex-column h-100" data-bs-spy="scroll" data-bs-target=".navbar" data-bs-offset="50"
      style="position:relative;">
<jsp:include page="header.jsp"/>
<%--    <span><fmt:formatDate value="${requestScope.date}" type="both" timeStyle="medium" dateStyle="short"/></span>--%>
<div class="container text-center">
    <c:choose>
        <c:when test="${empty param.q}">
            <h1 class="h1">
                <fmt:message key="cafe.main"/>
            </h1>
        </c:when>
        <c:otherwise>
            <h1 class="h1">
                <fmt:message key="main.searchResult"/>
            </h1>
            <span style="font-size: 2rem">
                <fmt:message key="main.searchForRequest"/>: "<c:out value="${param.q}"/>"
            </span>
        </c:otherwise>
    </c:choose>
</div>

<div class="container-fluid sticky-top bg-body mb-3 pt-0 text-center shadow-sm">
    <nav class="nav navbar-expand-sm navbar-light d-flex flex-wrap
     align-items-center justify-content-end" aria-label="Secondary navigation">
        <div class="container text-end col-12 navbar-toggler border-0 d-flex justify-content-between text-center">
            <a class="navbar-toggler text-decoration-none col-6 me-2" href="#cartBlock">
                <fmt:message key="main.findCart"/>
            </a>
            <button class="navbar-toggler col-6" type="button" data-bs-toggle="collapse"
                    data-bs-target="#categoryContent">
                <span><fmt:message key="cafe.main"/></span>
                <span class="navbar-toggler-icon"></span>
            </button>
        </div>

        <div class="collapse navbar-collapse justify-content-center text-center col-12" id="categoryContent">
            <ul class="navbar-nav">
                <c:forEach var="category" items="${requestScope.groupedDishes.keySet()}">
                    <li class="nav-item" style="font-size: 1.5em">
                        <a class="nav-link" href="#${category}">
                            <c:out value="${category}"/>
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </nav>
</div>

<div class="container-fluid">
    <div class="row">
        <div class="col-xxl-2 col-xl-1 col-sm-12"></div>
        <div class="col-xxl-8 col-xl-8 col-lg-9 col-md-8 col-sm-6">
            <c:if test="${empty requestScope.groupedDishes}">
                <div class="text-center">
        <span class="display-6">
            <fmt:message key="main.nothingFound"/>
        </span>
                </div>
            </c:if>

            <c:forEach var="group" items="${requestScope.groupedDishes}">
                <div class="row mb-2">
                    <h2 class="h2" id="${group.key}">
                        <c:out value="${group.key}"/>
                    </h2>
                    <c:forEach var="dish" items="${group.value}">
                        <div class="col-xxl-3 col-xl-4 col-lg-6 col-md-12 text-center gy-2">
                            <div class="card pt-3 h-100 w-100 align-items-center">
                                <img src="<c:url value="/img/dishes/dish-${dish.id}.png"/>"
                                     class="card-img-top p-1"
                                     alt="${dish.name} picture" style="width: 150px; height: auto;"/>
                                <div class="card-body">
                                    <h5 class="card-title">
                                        <c:out value="${dish.name}"/>
                                    </h5>
                                    <p class="card-text">
                                        <c:out value="${dish.description}"/>
                                    </p>
                                </div>
                                <div class="card-footer bg-body">
                                    <p class="card-text mb-1">
                                        <fmt:formatNumber maxFractionDigits="2"
                                                          minFractionDigits="2"
                                                          value="${dish.price/100}"/> BYN
                                    </p>
                                    <button class="btn btn-outline-success add-to-cart"
                                            data-name="${dish.name}"
                                            data-id="${dish.id}"
                                            data-price="${dish.price/100}"
                                            data-img="<c:url value="/img/dishes/dish-${dish.id}.png"/>">
                                        <fmt:message key="main.addToCart"/>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:forEach>
        </div>

        <aside class="col-xxl-2 col-xl-3 col-lg-3 col-md-4 col-sm-6 mt-5">
            <div class="card me-xxl-3 me-xl-2 me-lg-1 me-md-0" id="cartBlock" style="width: 270px;">
                <div class="card-body d-flex justify-content-between">
                    <h5 class="card-title">
                        <fmd:message key="cafe.cart"/>
                    </h5>
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger total-count"></span>
                    <button class="border-0 bg-body text-danger mb-1 clear-cart" style="font-size: 14px;">
                        <fmd:message key="main.clear"/>
                    </button>
                </div>
                <%--cart body--%>
                <ul class="list-group list-group-flush show-cart"></ul>
                <div class="card-body">
                    <div class="d-flex justify-content-between col-12 mb-3">
                        <span>
                            <fmt:message key="main.totalPrice"/>:
                        </span>
                        <span><span class="total-cart"></span> BYN</span>
                    </div>
                    <a href="<c:url value="/cart"/>" class="btn btn-outline-success col-12 text-center">
                        <fmt:message key="main.order"/>
                    </a>
                </div>
            </div>
        </aside>
    </div>
</div>
<jsp:include page="footer.jsp"/>

<script type="text/javascript" src="<c:url value="/js/jquery-3.6.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/cart.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bootstrap.bundle.min.js"/>"></script>
</body>
</html>
