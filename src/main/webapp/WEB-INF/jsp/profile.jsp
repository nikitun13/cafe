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
    <link rel="shortcut icon" href="<c:url value="/img/favicon.ico"/>" type="image/x-icon"/>
    <title>Cafe - <fmt:message key="cafe.profile"/></title>
</head>
<body class="d-flex flex-column h-100">

<c:import url="header.jsp" charEncoding="utf-8"/>

<main class="d-flex flex-row justify-content-center">
    <div class="container rounded bg-white mb-2">
        <div class="row">
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
                        <a href="<c:url value="/profile"/>" class="list-group-item list-group-item-action active">
                            <fmt:message key="cafe.profile"/>
                        </a>
                        <a href="<c:url value="/profile/orders"/>" class="list-group-item list-group-item-action">
                            <fmt:message key="cafe.profile.orders"/>
                        </a>
                        <a href="<c:url value="/profile/cart"/>" class="list-group-item list-group-item-action">
                            <fmt:message key="cafe.cart"/>
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-5 border-right">
                <form method="post" action="<c:url value="/profile/updateuser"/>" novalidate id="userInfo">
                    <div class="p-3 py-5">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h4 class="text-right">
                                <fmt:message key="profile.settings"/>
                            </h4>
                        </div>
                        <div class="row mt-2">
                            <div class="col-md-6">
                                <label for="firstName">
                                    <fmt:message key="input.firstName"/>
                                </label>
                                <input type="text" class="form-control" required name="firstName"
                                       id="firstName" value="<c:out value="${user.firstName}"/>" maxlength="128"/>
                            </div>
                            <div class="col-md-6">
                                <label for="lastName">
                                    <fmt:message key="input.lastName"/>
                                </label>
                                <input type="text" class="form-control" value="<c:out value="${user.lastName}"/>"
                                       id="lastName" required maxlength="128" name="lastName"/>
                            </div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-md-12 mb-2">
                                <label for="email">
                                    <fmt:message key="input.email"/>
                                </label>
                                <input type="email" class="form-control" id="email" name="email" maxlength="254"
                                       value="<c:out value="${user.email}"/>"/>
                                <div class="invalid-feedback">
                                    <fmt:message key="input.email.error"/>
                                </div>
                            </div>

                            <div class="col-md-12 mb-2">
                                <label for="phone">
                                    <fmt:message key="input.phone"/>
                                </label>
                                <div class="input-group">
                                    <span class="input-group-text">+</span>
                                    <input type="tel" class="form-control" id="phone" name="phone"
                                           minlength="10" maxlength="15" value="<c:out value="${user.phone}"/>"/>
                                </div>
                            </div>
                            <div class="mt-3 text-center">
                                <button class="btn btn-primary" type="submit">
                                    <fmt:message key="input.save"/>
                                </button>
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
                </form>
            </div>

            <div class="col-md-4">
                <form method="post" novalidate id="userPass">
                    <div class="p-3 py-5">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="fs-5">
                            <fmt:message key="input.password"/>
                        </span>
                        </div>
                        <div class="col-md-12 mt-4">
                            <label for="oldPassword">
                                <fmt:message key="input.password.old"/>
                            </label>
                            <input type="password" class="form-control" id="oldPassword"
                                   name="oldPassword" maxlength="128" minlength="8">
                        </div>
                        <div class="col-md-12 my-2">
                            <label for="newPassword">
                                <fmt:message key="input.password.new"/>
                            </label>
                            <input type="password" class="form-control" id="newPassword"
                                   name="newPassword" maxlength="128" minlength="8">
                            <div class="invalid-feedback">
                                <fmt:message key="input.password.error"/>
                            </div>
                        </div>
                        <div class="col-md-12">
                            <label for="repeatNewPassword">
                                <fmt:message key="input.repeatPassword"/>
                            </label>
                            <input type="password" class="form-control" id="repeatNewPassword"
                                   name="repeatPassword" maxlength="128" minlength="8">
                            <div class="invalid-feedback">
                                <fmt:message key="input.error.passwordMismatch"/>
                            </div>
                        </div>
                        <div class="mt-4 text-center">
                            <button class="btn btn-primary" type="submit">
                                <fmt:message key="profile.updatePassword"/>
                            </button>
                        </div>
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
</body>
</html>
