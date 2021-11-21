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
    <link rel="stylesheet" href="<c:url value="/css/signin.css"/>"/>
    <link rel="shortcut icon" href="<c:url value="/img/favicon.ico"/>" type="image/x-icon"/>
    <title>Cafe - <fmt:message key="cafe.signIn"/></title>
</head>
<body class="d-flex flex-column h-100">

<c:import url="header.jsp" charEncoding="utf-8"/>

<main class="form-signin text-center">
    <form method="post" novalidate>
        <img class="mb-4" src="<c:url value="/img/logo.png"/>" alt="logo" width="72">
        <h1 class="h3 mb-3 fw-normal">
            <fmt:message key="signin.signInTo"/>
        </h1>
        <c:set var="currentEmail" value="${requestScope.email}"/>
        <div class="form-floating">
            <input type="email" name="email" class="form-control" id="signInEmailInput"
                   placeholder="<fmt:message key="input.email"/>" required
            <c:if test="${not empty currentEmail}">
                   value="<c:out value="${currentEmail}"/>"
            </c:if>>
            <label for="signInEmailInput">
                <fmt:message key="input.email"/>
            </label>
        </div>
        <div class="form-floating">
            <input type="password" name="password" class="form-control" id="signInPasswordInput"
                   placeholder="<fmt:message key="input.password"/>" required>
            <label for="signInPasswordInput">
                <fmt:message key="input.password"/>
            </label>
        </div>

        <button class="w-100 btn btn-lg btn-primary" type="submit" id="signInBtn">
            <fmt:message key="cafe.signIn"/>
        </button>
    </form>
    <div class="d-flex flex-row mt-3">
        <span class="me-1">
            <fmt:message key="signin.newUser"/>?
        </span>
        <a href="<c:url value="/signup"/>" class="text-decoration-none">
            <fmt:message key="signup.createAccount"/>
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
</main>

<c:import url="footer.jsp" charEncoding="utf-8"/>

<script type="text/javascript" src="<c:url value="/js/jquery-3.6.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bootstrap.bundle.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/cafe.js"/>"></script>
</body>
</html>
