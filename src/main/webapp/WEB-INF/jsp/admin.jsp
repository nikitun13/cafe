<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:import url="locale.jsp" charEncoding="utf-8"/>

<!doctype html>
<html lang="en" class="h-100">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="stylesheet" href="<c:url value="/css/bootstrap.min.css"/>"/>
    <link rel="shortcut icon" href="<c:url value="/img/favicon.ico"/>" type="image/x-icon"/>
    <title>Cafe - <fmt:message key="cafe.admin"/></title>
</head>
<body class="d-flex flex-column h-100">

<c:import url="header.jsp" charEncoding="utf-8"/>

<main>
    <div class="container-fluid">
        <div class="row flex-nowrap">
            <div class="col-auto col-md-3 col-xl-2 px-sm-2 px-0 ">
                <div class="d-flex flex-column align-items-center align-items-sm-start px-3 pt-2">
                    <div class="list-group w-100 mt-3">
                        <a href="<c:url value="/admin"/>" class="list-group-item list-group-item-action active">
                            <fmt:message key="admin.statistics"/>
                        </a>
                        <a href="<c:url value="/admin/orders"/>" class="list-group-item list-group-item-action">
                            <fmt:message key="cafe.profile.orders"/>
                        </a>
                        <a href="<c:url value="/admin/dishes"/>" class="list-group-item list-group-item-action">
                            <fmt:message key="admin.dishes"/>
                        </a>
                        <a href="<c:url value="/admin/users"/>" class="list-group-item list-group-item-action">
                            <fmt:message key="admin.users"/>
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

            <c:set var="users" value="${requestScope.users}"/>

            <div class="col py-3">
                <div class="container" style="margin-left:25px">
                    <h1 class="text-center mb-3"><fmt:message key="admin.statistics"/></h1>
                    <div class="row row-cols-3">
                        <div class="col">
                            <div class="card h-100 text-center">
                                <div class="card-header h-100">
                                    <h5 class="card-title">
                                        <fmt:message key="admin.statistics.earnedLastMonth"/>
                                    </h5>
                                    <span>
                                        (<fmt:formatDate value="${requestScope.startDateOfPreviousMonth}"
                                                         dateStyle="short"/> — <fmt:formatDate
                                            value="${requestScope.endDateOfPreviousMonth}"
                                            dateStyle="short"/>)
                                    </span>
                                </div>
                                <div class="card-body">
                                    <span class="display-6">
                                        <fmt:formatNumber value="${requestScope.earnedLastMonth / 100}"
                                                          maxFractionDigits="2" minFractionDigits="2"/> BYN
                                    </span>
                                </div>
                            </div>
                        </div>
                        <div class="col">
                            <div class="card h-100 text-center">
                                <div class="card-header h-100">
                                    <h5 class="card-title">
                                        <fmt:message key="admin.statistics.earnedThisMonth"/>
                                    </h5>
                                    <span>
                                        (<fmt:formatDate value="${requestScope.startDateOfThisMonth}"
                                                         dateStyle="short"/> — <fmt:formatDate
                                            value="${requestScope.endDateOfThisMonth}"
                                            dateStyle="short"/>)
                                    </span>
                                </div>
                                <div class="card-body">
                                    <span class="display-6">
                                        <fmt:formatNumber value="${requestScope.earnedThisMonth / 100}"
                                                          maxFractionDigits="2" minFractionDigits="2"/> BYN
                                    </span>
                                </div>
                            </div>
                        </div>
                        <div class="col">
                            <div class="card h-100 text-center">
                                <div class="card-header h-100">
                                    <h5 class="card-title">
                                        <fmt:message key="admin.statistics.earnedTotal"/>
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <span class="display-6">
                                        <fmt:formatNumber value="${requestScope.earnedTotal / 100}"
                                                          maxFractionDigits="2" minFractionDigits="2"/> BYN
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <h2 class="text-center mt-3">
                        <fmt:message key="admin.statistics.topDishes"/>
                    </h2>
                    <div class="row row-cols-3">
                        <c:forEach var="topDish" items="${requestScope.topDishes}">
                            <div class="col">
                                <div class="card h-100 text-center align-items-center">
                                    <div>
                                        <img src="<c:url value="/images/dishes/dish-${topDish.key.id}.png"/>"
                                             class="card-img-top pt-2" alt="${topDish.key.name} img"
                                             style="max-width: 150px; max-height: 160px;">
                                    </div>
                                    <div class="card-body">
                                        <h5 class="card-title fs-3"><c:out value="${topDish.key.name}"/></h5>
                                        <div class="row">
                                            <span class="fs-5">
                                                <fmt:message key="admin.statistics.totalOrdered"/>: <c:out
                                                    value="${topDish.value.value}"/>
                                            </span>
                                        </div>
                                        <span class="fs-3"><fmt:message
                                                key="admin.statistics.totalSum"/>: <fmt:formatNumber
                                                value="${topDish.value.key / 100}" minFractionDigits="2"
                                                maxFractionDigits="2"/> BYN</span>
                                    </div>
                                </div>
                            </div>
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
<script type="text/javascript" src="<c:url value="/js/admin.js"/>"></script>
</body>
</html>
