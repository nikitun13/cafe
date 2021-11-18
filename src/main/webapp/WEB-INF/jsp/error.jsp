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
    <title>Cafe - <fmt:message key="cafe.error"/></title>
</head>
<body class="d-flex flex-column h-100">

<c:import url="header.jsp" charEncoding="utf-8"/>

<main class="d-flex flex-wrap justify-content-center">
    <h1 class="text-center col-12">
        <fmt:message key="error.oops"/>
    </h1>
    <c:if test="${not empty requestScope.errorStatus}">
    <span class="display-6 text-center col-12 my-1">
        <fmt:message key="error.code"/>: <c:out value="${requestScope.errorStatus}"/>
    </span>
    </c:if>
    <c:if test="${not empty requestScope.errorMessageKey}">
    <span class="display-6 text-center col-12 my-1">
        <fmt:message key="${requestScope.errorMessageKey}"/>
    </span>
    </c:if>
    <img src="<c:url value="/img/error.png"/>" class="img-fluid my-3" alt="error img"/>
</main>

<c:import url="footer.jsp" charEncoding="utf-8"/>

<script type="text/javascript" src="<c:url value="/js/bootstrap.bundle.min.js"/>"></script>
</body>
</html>
