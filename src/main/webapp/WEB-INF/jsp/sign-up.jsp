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
    <title>Cafe - <fmt:message key="cafe.signUp"/></title>
</head>
<body class="d-flex flex-column h-100">

<c:import url="header.jsp" charEncoding="utf-8"/>

<main class="d-flex flex-row justify-content-center my-auto">
    <div class="container my-4" style="max-width: 700px">
        <div class="text-center mb-3">
            <img class="mb-4" src="<c:url value="/img/logo.png"/>" alt="logo" width="72">
            <h1 class="h3 mb-3 fw-normal">
                <fmt:message key="signup.createAccount"/>
            </h1>
        </div>

        <c:set var="email" value="${requestScope.email}"/>
        <c:set var="firstName" value="${requestScope.firstName}"/>
        <c:set var="lastName" value="${requestScope.lastName}"/>
        <c:set var="phone" value="${requestScope.phone}"/>

        <form class="row g-3" method="post" novalidate>
            <div class="col-md-6">
                <label for="firstName" class="form-label">
                    <fmt:message key="input.firstName"/>
                </label>
                <input type="text" class="form-control" id="firstName" name="firstName" maxlength="128"
                <c:if test="${not empty firstName}">
                       value="<c:out value="${firstName}"/>"
                </c:if>>
            </div>
            <div class="col-md-6">
                <label for="lastName" class="form-label">
                    <fmt:message key="input.lastName"/>
                </label>
                <input type="text" class="form-control" id="lastName" name="lastName" maxlength="128"
                <c:if test="${not empty lastName}">
                       value="<c:out value="${lastName}"/>"
                </c:if>>
            </div>
            <div class="col-md-12">
                <label for="email" class="form-label">
                    <fmt:message key="input.email"/>
                </label>
                <input type="email" class="form-control" id="email" name="email" maxlength="254"
                <c:if test="${not empty email}">
                       value="<c:out value="${email}"/>"
                </c:if>>
                <div class="invalid-feedback">
                    <fmt:message key="input.email.error"/>
                </div>
            </div>
            <div class="col-md-6">
                <label for="password" class="form-label">
                    <fmt:message key="input.password"/>
                </label>
                <input type="password" class="form-control" id="password"
                       name="password" maxlength="128" minlength="8">
                <div class="invalid-feedback">
                    <fmt:message key="input.password.error"/>
                </div>
            </div>
            <div class="col-md-6">
                <label for="repeatPassword" class="form-label">
                    <fmt:message key="input.repeatPassword"/>
                </label>
                <input type="password" class="form-control" id="repeatPassword"
                       name="repeatPassword" maxlength="128" minlength="8">
            </div>
            <div class="col-md-6">
                <label for="phone" class="form-label">
                    <fmt:message key="input.phone"/>
                </label>
                <div class="input-group mb-3">
                    <span class="input-group-text" id="basic-addon1">+</span>
                    <input type="tel" class="form-control" id="phone" name="phone"
                           minlength="10" maxlength="15"
                    <c:if test="${not empty phone}">
                           value="<c:out value="${phone}"/>"
                    </c:if>>
                </div>
            </div>
            <div class="col-md-12 d-flex justify-content-center">
                <div class="col-md-4 col-auto">
                    <button type="submit" class="btn btn-primary w-100" id="signUpBtn">
                        <fmt:message key="cafe.signUp"/>
                    </button>
                </div>
            </div>
        </form>
        <c:if test="${not empty requestScope.errorMessageKey}">
            <div class="alert alert-danger alert-dismissible fade show mt-3">
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                <strong><fmt:message key="cafe.error"/>!</strong>
                <fmt:message key="${requestScope.errorMessageKey}"/>
            </div>
        </c:if>
    </div>
</main>

<c:import url="footer.jsp" charEncoding="utf-8"/>

<script type="text/javascript" src="<c:url value="/js/jquery-3.6.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bootstrap.bundle.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/cafe.js"/>"></script>
</body>
</html>
