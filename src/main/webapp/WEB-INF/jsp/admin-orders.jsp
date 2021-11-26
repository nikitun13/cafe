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
                        <a href="<c:url value="/admin"/>" class="list-group-item list-group-item-action">
                            <fmt:message key="admin.home"/>
                        </a>
                        <a href="<c:url value="/admin/orders"/>" class="list-group-item list-group-item-action active">
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

            <c:set var="orders" value="${requestScope.orders}"/>

            <div class="col py-3">
                <div class="container" style="margin-left:25px">
                    <c:if test="${not empty orders}">
                        <form method="post">
                            <div class="d-flex flex-row flex-wrap">
                                <button type="submit" class="btn btn-outline-success orders-submit"
                                        formaction="<c:url value="/admin/completeorders"/>">
                                    <fmt:message key="admin.orders.complete"/>
                                </button>
                                <button type="submit" class="btn btn-outline-secondary mx-3 orders-submit"
                                        formaction="<c:url value="/admin/pendingorders"/>">
                                    <fmt:message key="admin.orders.pending"/>
                                </button>
                                <button type="submit" class="btn btn-outline-warning me-3 orders-submit"
                                        formaction="<c:url value="/admin/notcollectedorders"/>">
                                    <fmt:message key="admin.orders.notCollected"/>
                                </button>
                                <button type="submit" class="btn btn-outline-danger orders-submit"
                                        formaction="<c:url value="/admin/cancelorders"/>">
                                    <fmt:message key="admin.orders.cancel"/>
                                </button>
                            </div>
                            <table class="table table-striped align-middle text-center">
                                <caption class="caption-top">
                                    <fmt:message key="cafe.profile.orders"/>
                                </caption>
                                <thead>
                                <tr class="align-middle">
                                    <th scope="col"></th>
                                    <th scope="col">№</th>
                                    <th scope="col">
                                        <fmt:message key="admin.orders.userName"/>
                                    </th>
                                    <th scope="col">
                                        <fmt:message key="admin.orders.userId"/>
                                    </th>
                                    <th scope="col">
                                        <fmt:message key="admin.orders.createdAt"/>
                                    </th>
                                    <th scope="col">
                                        <fmt:message key="admin.orders.expectedRetrieveDate"/>
                                    </th>
                                    <th scope="col">
                                        <fmt:message key="admin.orders.actualRetrieveDate"/>
                                    </th>
                                    <th scope="col">
                                        <fmt:message key="profile.status"/>
                                    </th>
                                    <th scope="col">
                                        <fmt:message key="profile.debitedPoints"/>
                                    </th>
                                    <th scope="col">
                                        <fmt:message key="profile.accruedPoints"/>
                                    </th>
                                    <th scope="col">
                                        <fmt:message key="cart.totalPrice"/>
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="order" items="${orders}">
                                    <tr>
                                        <td>
                                            <div>
                                                <input class="form-check-input" type="checkbox" value="${order.id}"
                                                       aria-label="select" name="orders"/>
                                            </div>
                                        </td>
                                        <th scope="row"><c:out value="${order.id}"/></th>
                                        <td>
                                            <c:out value="${order.user.firstName}"/> <c:out
                                                value="${order.user.lastName}"/>
                                        </td>
                                        <td>
                                            <c:out value="${order.user.id}"/>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${order.createdAt}" type="both"
                                                            timeStyle="medium" dateStyle="short"/>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${order.expectedRetrieveDate}" type="both"
                                                            timeStyle="medium" dateStyle="short"/>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty order.actualRetrieveDate}">
                                                    <fmt:formatDate value="${order.actualRetrieveDate}" type="both"
                                                                    timeStyle="medium" dateStyle="short"/>
                                                </c:when>
                                                <c:otherwise>
                                                    —
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:out value="${order.status}"/>
                                        </td>
                                        <td>
                                            <c:out value="${order.debitedPoints}"/>
                                        </td>
                                        <td>
                                            <c:out value="${order.accruedPoints}"/>
                                        </td>
                                        <td>
                                            <fmt:formatNumber maxFractionDigits="2"
                                                              minFractionDigits="2"
                                                              value="${order.totalPrice / 100}"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </form>
                        <c:set var="currentPage" value="${requestScope.currentPage}"/>
                        <c:set var="pageCount" value="${requestScope.pageCount}"/>
                        <nav aria-label="Page navigation">
                            <ul class="pagination">
                                <li class="page-item <c:if test="${currentPage == 1}">disabled</c:if>">
                                    <a class="page-link"
                                       href="<c:url value="/admin/orders?page=1"/>"
                                       aria-label="first">
                                        <span aria-hidden="true">&laquo;</span>
                                    </a>
                                </li>
                                <li class="page-item <c:if test="${currentPage == 1}">disabled</c:if>">
                                    <a class="page-link"
                                       href="<c:url value="/admin/orders?page=${currentPage - 1}"/>">
                                        <fmt:message key="pagination.previous"/>
                                    </a>
                                </li>
                                <c:forEach var="i" begin="${requestScope.startPage}"
                                           end="${currentPage - 1}">
                                    <li class="page-item">
                                        <a class="page-link"
                                           href="<c:url value="/admin/orders?page=${i}"/>">
                                            <c:out value="${i}"/>
                                        </a>
                                    </li>
                                </c:forEach>
                                <li class="page-item active" aria-current="page">
                                                        <span class="page-link">
                                                            <c:out value="${currentPage}"/>
                                                        </span>
                                </li>
                                <c:forEach var="i" begin="${currentPage + 1}"
                                           end="${requestScope.endPage}">
                                    <li class="page-item">
                                        <a class="page-link"
                                           href="<c:url value="/admin/orders?page=${i}"/>">
                                            <c:out value="${i}"/>
                                        </a>
                                    </li>
                                </c:forEach>
                                <li class="page-item <c:if test="${currentPage == pageCount}">disabled</c:if>">
                                    <a class="page-link"
                                       href="<c:url value="/admin/orders?page=${currentPage + 1}"/>">
                                        <fmt:message key="pagination.next"/>
                                    </a>
                                </li>
                                <li class="page-item <c:if test="${currentPage == pageCount}">disabled</c:if>">
                                    <a class="page-link"
                                       href="<c:url value="/admin/orders?page=${pageCount}"/>"
                                       aria-label="Last">
                                        <span aria-hidden="true">&raquo;</span>
                                    </a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</main>

<c:import url="footer.jsp" charEncoding="utf-8"/>

<script type="text/javascript" src="<c:url value="/js/jquery-3.6.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bootstrap.bundle.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/cafe.js"/>"></script>
</body>
</html>
