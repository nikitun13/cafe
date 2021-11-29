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
                        <a href="<c:url value="/admin/dishes"/>" class="list-group-item list-group-item-action active">
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

            <c:set var="dishes" value="${requestScope.dishes}"/>

            <div class="col py-3">
                <div class="container" style="margin-left:25px">

                    <div class="d-flex flex-row justify-content-between">
                        <ul class="nav nav-tabs">
                            <c:forEach var="category" items="${requestScope.categories}">
                                <li class="nav-item">
                                    <a class="nav-link <c:if test="${requestScope.currentCategory.equals(category.name())}">
                                      active</c:if>" href="<c:url value="/admin/dishes?category=${category}"/>">
                                        <c:out value="${category}"/>
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                        <button class="btn btn-outline-primary" data-bs-toggle="modal"
                                data-bs-target="#create-dish-modal">
                            <fmt:message key="admin.dishes.create"/>
                        </button>
                    </div>

                    <c:choose>
                        <c:when test="${not empty dishes}">
                            <table class="table table-striped align-middle text-center">
                                <caption class="caption-top">
                                    <fmt:message key="admin.dishes"/>
                                </caption>
                                <thead>
                                <tr class="align-middle">
                                    <th scope="col">Id</th>
                                    <th scope="col">
                                        <fmt:message key="admin.dishes.image"/>
                                    </th>
                                    <th scope="col">
                                        <fmt:message key="admin.dishes.name"/>
                                    </th>
                                    <th scope="col">
                                        <fmt:message key="admin.dishes.category"/>
                                    </th>
                                    <th scope="col">
                                        <fmt:message key="admin.dishes.price"/>
                                    </th>
                                    <th scope="col">
                                        <fmt:message key="admin.dishes.description"/>
                                    </th>
                                    <th scope="col">
                                        <fmt:message key="admin.users.action"/>
                                    </th>
                                </tr>
                                </thead>
                                <c:forEach var="dish" items="${dishes}">
                                    <form method="post" class="updateDishForm" id="updateDishForm-${dish.id}"
                                          action="<c:url value="/admin/updatedish"/>" data-id="${dish.id}" novalidate>
                                        <input type="hidden" name="dishId" value="${dish.id}">
                                    </form>
                                </c:forEach>

                                <tbody>
                                <c:forEach var="dish" items="${dishes}">
                                    <tr>
                                        <th scope="row">
                                            <c:out value="${dish.id}"/>
                                        </th>
                                        <td>
                                            <img class="img-fluid"
                                                 src="<c:url value="/images/dishes/dish-${dish.id}.png"/>" id="current"
                                                 alt="${dish.name} picture" style="width: 64px;height: auto">
                                        </td>
                                        <td style="width: 26%">
                                            <input class="form-control w-100 dish-name" type="text" name="name"
                                                   maxlength="128" value="${dish.name}" aria-label="Dish name" required
                                                   form="updateDishForm-${dish.id}" id="dish-name-${dish.id}">
                                        </td>
                                        <td style="width: 12%">
                                            <select class="form-select w-100" name="category" aria-label="category"
                                                    form="updateDishForm-${dish.id}">
                                                <c:forEach var="category" items="${requestScope.categories}">
                                                    <option value="${category}"
                                                            <c:if test="${dish.category.toUpperCase().equals(category.name())}">
                                                                selected
                                                            </c:if>>
                                                        <c:out value="${category}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </td>
                                        <td style="width: 14%">
                                            <div class="input-group">
                                                <input class="form-control text-end dish-price" type="number"
                                                       aria-label="price" id="dish-price-${dish.id}"
                                                       name="price" required min="0" value="${dish.price / 100}"
                                                       form="updateDishForm-${dish.id}">
                                                <span class="input-group-text">BYN</span>
                                            </div>

                                        </td>
                                        <td>
                                            <button class="btn btn-warning" data-bs-toggle="modal"
                                                    data-bs-target="#description-modal-${dish.id}">
                                                <fmt:message key="admin.dishes.updateDescription"/>
                                            </button>
                                        </td>
                                        <td>
                                            <div class="d-flex flex-row justify-content-center">
                                                <input type="reset" aria-label="reset"
                                                       class="btn btn-outline-danger me-2"
                                                       value="<fmt:message key="admin.users.reset"/>"
                                                       form="updateDishForm-${dish.id}">
                                                <button type="submit" class="btn btn-outline-success"
                                                        form="updateDishForm-${dish.id}">
                                                    <fmt:message key="input.update"/>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div class="d-flex flex-wrap justify-content-center mb-3">
                                <img src="<c:url value="/img/nothing-found.png"/>"
                                     class="img-fluid my-3" alt="nothing found img"/>
                                <span class="display-6 col-12 text-center">
                                    <fmt:message key="admin.dishes.empty"/>
                                </span>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</main>

<div class="modal fade" id="create-dish-modal" tabindex="-1"
     aria-labelledby="exampleModalLabel2" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel2">
                    <fmt:message key="admin.dishes.creation"/>
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form method="post" novalidate action="<c:url value="/admin/createdish"/>"
                  id="create-dish-form" enctype="multipart/form-data">
                <div class="modal-body">
                    <div>
                        <label for="create-name">
                            <fmt:message key="admin.dishes.name"/>
                        </label>
                        <input class="form-control w-100" type="text" name="name" maxlength="128"
                               required id="create-name">
                    </div>
                    <div class="row gx-5 mt-2">
                        <div class="col-6">
                            <label for="create-category">
                                <fmt:message key="admin.dishes.category"/>
                            </label>
                            <select class="form-select" name="category" id="create-category">
                                <c:forEach var="category" items="${requestScope.categories}">
                                    <option value="${category}">
                                        <c:out value="${category}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-6">
                            <label for="create-price">
                                <fmt:message key="admin.dishes.price"/>
                            </label>
                            <div class="input-group">
                                <input class="form-control" type="number" id="create-price" name="price" required
                                       min="0">
                                <span class="input-group-text">BYN</span>
                            </div>
                        </div>
                    </div>
                    <div class="form-group my-2">
                        <label>
                            <fmt:message key="admin.dishes.description"/>
                        </label>
                        <textarea name="description" class="form-control"
                                  id="create-description" required maxlength="1024" rows="6"
                                  aria-label="description"></textarea>
                    </div>
                    <div class="row gx-5">
                        <div class="col-6">
                            <label for="create-image">
                                <fmt:message key="admin.dishes.image"/>
                            </label>
                            <input class="form-control" name="image" type="file" id="create-image" accept="image/png"
                                   required data-max-size="1048576">
                        </div>
                        <div class="preview text-center col-6"></div>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="d-flex justify-content-end">
                        <button type="submit" class="btn btn-outline-primary">
                            <fmt:message key="input.create"/>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>


<c:forEach items="${dishes}" var="dish">
    <div class="modal fade review-modal" id="description-modal-${dish.id}" tabindex="-1"
         aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">
                        <fmt:message key="admin.dishes.descriptionFor"/> <c:out value="${dish.name}"/>
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form method="post" action="<c:url value="/admin/updatedescription"/>" data-id="${dish.id}"
                      class="dish-description-form" id="dish-description-form-${dish.id}">
                    <input type="hidden" name="dishId" value="${dish.id}">
                    <div class="modal-body">
                        <div class="form-group">
                            <label><fmt:message key="admin.dishes.description"/></label>
                            <textarea name="description" class="form-control description-area"
                                      id="description-area-${dish.id}" required maxlength="1024" rows="8"
                                      aria-label="description"><c:out
                                    value="${dish.description}"/></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <div class="d-flex justify-content-end">
                            <input type="reset" aria-label="reset"
                                   class="btn btn-outline-danger me-3"
                                   value="<fmt:message key="admin.users.reset"/>">
                            <button type="submit" class="btn btn-outline-primary">
                                <fmt:message key="input.update"/>
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:forEach>

<c:import url="footer.jsp" charEncoding="utf-8"/>

<script type="text/javascript" src="<c:url value="/js/jquery-3.6.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bootstrap.bundle.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/cafe.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/admin.js"/>"></script>
</body>
</html>
