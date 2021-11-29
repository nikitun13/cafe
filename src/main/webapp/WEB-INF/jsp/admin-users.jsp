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
                            <fmt:message key="admin.statistics"/>
                        </a>
                        <a href="<c:url value="/admin/orders"/>" class="list-group-item list-group-item-action">
                            <fmt:message key="cafe.profile.orders"/>
                        </a>
                        <a href="<c:url value="/admin/dishes"/>" class="list-group-item list-group-item-action">
                            <fmt:message key="admin.dishes"/>
                        </a>
                        <a href="<c:url value="/admin/users"/>" class="list-group-item list-group-item-action active">
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
                    <c:if test="${not empty users}">
                        <table class="table table-striped align-middle text-center">
                            <caption class="caption-top">
                                <fmt:message key="admin.users"/>
                            </caption>
                            <thead>
                            <tr class="align-middle">
                                <th scope="col">Id</th>
                                <th scope="col">
                                    <fmt:message key="admin.orders.userName"/>
                                </th>
                                <th scope="col">
                                    <fmt:message key="input.email"/>
                                </th>
                                <th scope="col">
                                    <fmt:message key="admin.users.role"/>
                                </th>
                                <th scope="col">
                                    <fmt:message key="input.phone"/>
                                </th>
                                <th scope="col">
                                    <fmt:message key="admin.users.points"/>
                                </th>
                                <th scope="col">
                                    <fmt:message key="admin.users.isBlocked"/>
                                </th>
                                <th scope="col">
                                    <fmt:message key="admin.users.totalCompleted"/>
                                </th>
                                <th scope="col">
                                    <fmt:message key="admin.users.totalSpent"/>
                                </th>
                                <th scope="col">
                                    <fmt:message key="admin.users.action"/>
                                </th>
                            </tr>
                            </thead>
                            <c:set var="totalCompletedOrdersMap" value="${requestScope.totalCompletedOrders}"/>
                            <c:set var="totalSpentMap" value="${requestScope.totalSpentMap}"/>
                            <c:set var="admin" value="${sessionScope.user}"/>
                            <c:forEach var="user" items="${users}">
                                <form method="post" class="updateForm" id="updateForm-${user.id}"
                                      action="<c:url value="/admin/updateuser"/>" data-id="${user.id}">
                                    <input type="hidden" name="userId" value="${user.id}">
                                </form>
                            </c:forEach>

                            <tbody>
                            <c:forEach var="user" items="${users}">
                                <c:if test="${not user.id.equals(admin.id)}">
                                    <tr>
                                        <th scope="row">
                                            <c:out value="${user.id}"/>
                                        </th>
                                        <td>
                                            <c:out value="${user.firstName}"/> <c:out
                                                value="${user.lastName}"/>
                                        </td>
                                        <td>
                                            <c:out value="${user.email}"/>
                                        </td>
                                        <td style="width: 10%">
                                            <select class="form-select w-100" name="role" aria-label="role"
                                                    form="updateForm-${user.id}">
                                                <c:choose>
                                                    <c:when test="${user.role.name().equals('CLIENT')}">
                                                        <option class="w-100" selected value="CLIENT">CLIENT</option>
                                                        <option class="w-100" value="ADMIN">ADMIN</option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <option value="CLIENT">CLIENT</option>
                                                        <option selected value="ADMIN">ADMIN</option>
                                                    </c:otherwise>
                                                </c:choose>
                                            </select>
                                        </td>
                                        <td>
                                            +<c:out value="${user.phone}"/>
                                        </td>
                                        <td style="width: 10%">
                                            <input class="form-control text-center points" type="number"
                                                   aria-label="points"
                                                   name="points" required min="0" value="${user.points}"
                                                   form="updateForm-${user.id}" id="points-${user.id}">
                                        </td>
                                        <td style="width: 8%">
                                            <select class="form-select w-100" name="isBlocked" aria-label="role"
                                                    form="updateForm-${user.id}">
                                                <c:choose>
                                                    <c:when test="${user.isBlocked()}">
                                                        <option class="w-100" value="false">false</option>
                                                        <option selected class="w-100" value="true">true</option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <option selected class="w-100" value="false">false</option>
                                                        <option class="w-100" value="true">true</option>
                                                    </c:otherwise>
                                                </c:choose>
                                            </select>
                                        </td>
                                        <td>
                                            <c:out value="${totalCompletedOrdersMap[user]}"/>
                                        </td>
                                        <td>
                                            <fmt:formatNumber maxFractionDigits="2"
                                                              minFractionDigits="2"
                                                              value="${totalSpentMap[user] / 100}"/>
                                        </td>
                                        <td>
                                            <div class="d-flex flex-row">
                                                <input type="reset" aria-label="reset"
                                                       class="btn btn-outline-danger btn-sm me-1"
                                                       value="<fmt:message key="admin.users.reset"/>"
                                                       form="updateForm-${user.id}">
                                                <button type="submit" class="btn btn-outline-success btn-sm"
                                                        form="updateForm-${user.id}">
                                                    <fmt:message key="input.update"/>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                            </tbody>
                        </table>
                        <c:set var="currentPage" value="${requestScope.currentPage}"/>
                        <c:set var="pageCount" value="${requestScope.pageCount}"/>
                        <nav aria-label="Page navigation">
                            <ul class="pagination">
                                <li class="page-item <c:if test="${currentPage == 1}">disabled</c:if>">
                                    <a class="page-link"
                                       href="<c:url value="/admin/users?page=1"/>"
                                       aria-label="first">
                                        <span aria-hidden="true">&laquo;</span>
                                    </a>
                                </li>
                                <li class="page-item <c:if test="${currentPage == 1}">disabled</c:if>">
                                    <a class="page-link"
                                       href="<c:url value="/admin/users?page=${currentPage - 1}"/>">
                                        <fmt:message key="pagination.previous"/>
                                    </a>
                                </li>
                                <c:forEach var="i" begin="${requestScope.startPage}"
                                           end="${currentPage - 1}">
                                    <li class="page-item">
                                        <a class="page-link"
                                           href="<c:url value="/admin/users?page=${i}"/>">
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
                                           href="<c:url value="/admin/users?page=${i}"/>">
                                            <c:out value="${i}"/>
                                        </a>
                                    </li>
                                </c:forEach>
                                <li class="page-item <c:if test="${currentPage == pageCount}">disabled</c:if>">
                                    <a class="page-link"
                                       href="<c:url value="/admin/users?page=${currentPage + 1}"/>">
                                        <fmt:message key="pagination.next"/>
                                    </a>
                                </li>
                                <li class="page-item <c:if test="${currentPage == pageCount}">disabled</c:if>">
                                    <a class="page-link"
                                       href="<c:url value="/admin/users?page=${pageCount}"/>"
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
<script type="text/javascript" src="<c:url value="/js/admin.js"/>"></script>
</body>
</html>
