<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:import url="locale.jsp" charEncoding="utf-8"/>

<c:set var="dish" value="${requestScope.dish}"/>
<c:set var="user" value="${sessionScope.user}"/>

<!doctype html>
<html lang="en" class="h-100">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="stylesheet" href="<c:url value="/css/bootstrap.min.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/dish.css"/>"/>
    <link rel="shortcut icon" href="<c:url value="/img/favicon.ico"/>" type="image/x-icon"/>
    <title>Cafe - <c:out value="${dish.name}"/></title>
</head>
<body class="d-flex flex-column h-100">

<c:import url="header.jsp" charEncoding="utf-8"/>

<main class="d-flex flex-row flex-wrap justify-content-center">
    <div class="col-xxl-10 col-xl-9 col-lg-9 col-md-8 col-sm-6">
        <section class="item-details section">
            <div class="container me-5">
                <div class="top-area">
                    <div class="row">
                        <div class="col-lg-3 col-md-12 col-12 text-center">
                            <img class="img-fluid" src="<c:url value="/img/dishes/dish-${dish.id}.png"/>" id="current"
                                 alt="${dish.name} picture" style="width: 225px;height: auto">
                        </div>
                        <div class="col-lg-9 col-md-12 col-12">
                            <h2><c:out value="${dish.name}"/></h2>
                            <h4>
                                <fmt:formatNumber maxFractionDigits="2"
                                                  minFractionDigits="2"
                                                  value="${dish.price/100}"/> BYN
                            </h4>
                            <p class="mb-2 pb-2 border-bottom">
                                <c:out value="${dish.description}"/>
                            </p>
                            <div class="bottom-content mt-3">
                                <div class="row align-items-end">
                                    <div class="col-lg-4 col-md-4 col-12">
                                        <button class="btn btn-outline-success add-to-cart w-100"
                                                data-name="${dish.name}"
                                                data-id="${dish.id}"
                                                data-price="${dish.price/100}"
                                                data-img="<c:url value="/img/dishes/dish-${dish.id}.png"/>">
                                            <fmt:message key="cart.addToCart"/>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="product-details-info mb-3">
                    <div class="row">
                        <div class="col-lg-4 col-12">
                            <div class="single-block give-review pb-3">
                                <h4>
                                    <fmt:message key="dish.averageRating"/>:
                                    <c:choose>
                                        <c:when test="${not empty requestScope.averageRating}">
                                            <fmt:formatNumber maxFractionDigits="2"
                                                              minFractionDigits="2"
                                                              value="${requestScope.averageRating}"/>
                                        </c:when>
                                        <c:otherwise>
                                            -
                                        </c:otherwise>
                                    </c:choose>
                                </h4>
                                <ul class="p-0 align-items-center">
                                    <c:forEach var="group" items="${requestScope.countGroupedByRating}">
                                        <li class="d-flex flex-row">
                                            <c:forEach begin="1" end="${group.key}">
                                                <div class="text-warning me-1">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                                         fill="currentColor"
                                                         class="bi bi-star-fill" viewBox="0 0 16 16">
                                                        <path d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"></path>
                                                    </svg>
                                                </div>
                                            </c:forEach>
                                            <c:forEach begin="${group.key + 1}" end="5">
                                                <div class="text-warning me-1">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                                         fill="currentColor"
                                                         class="bi bi-star" viewBox="0 0 16 16">
                                                        <path d="M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.565.565 0 0 0-.163-.505L1.71 6.745l4.052-.576a.525.525 0 0 0 .393-.288L8 2.223l1.847 3.658a.525.525 0 0 0 .393.288l4.052.575-2.906 2.77a.565.565 0 0 0-.163.506l.694 3.957-3.686-1.894a.503.503 0 0 0-.461 0z"></path>
                                                    </svg>
                                                </div>
                                            </c:forEach>
                                            <span class="ms-1">
                                            - <c:out value="${group.value}"/>
                                            </span>
                                        </li>
                                    </c:forEach>
                                </ul>
                                <c:choose>
                                    <c:when test="${empty user}">
                                        <a href="<c:url value="/signin"/>" class="btn btn-warning mt-4 w-100 p-2">
                                            <fmt:message key="dish.leaveReview"/>
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-warning mt-4 w-100 p-2" data-bs-toggle="modal"
                                                data-bs-target="#exampleModal">
                                            <fmt:message key="dish.leaveReview"/>
                                        </button>
                                    </c:otherwise>
                                </c:choose>
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
                        <div class="col-lg-8 col-12">
                            <div class="single-block">
                                <div class="reviews">
                                    <h4 class="title">
                                        <fmt:message key="dish.reviews"/>
                                    </h4>
                                    <c:choose>
                                        <c:when test="${requestScope.pageCount != 0}">
                                            <c:forEach var="comment" items="${requestScope.comments}">
                                                <div class="single-review">
                                                <div class="review-info mb-4">
                                                    <div class="d-flex flex-wrap">
                                                        <h4 class="mb-0 my-1">
                                                            <c:out value="${comment.user.firstName}"/>
                                                            <c:out value="${comment.user.lastName}"/>
                                                        </h4>
                                                        <ul class="mb-0 text-warning">
                                                            <c:forEach begin="1" end="${comment.rating}">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                                     height="16"
                                                                     fill="currentColor"
                                                                     class="bi bi-star-fill" viewBox="0 0 16 16">
                                                                    <path d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"></path>
                                                                </svg>
                                                            </c:forEach>
                                                            <c:forEach begin="${comment.rating + 1}" end="5">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                                     height="16"
                                                                     fill="currentColor"
                                                                     class="bi bi-star" viewBox="0 0 16 16">
                                                                    <path d="M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.565.565 0 0 0-.163-.505L1.71 6.745l4.052-.576a.525.525 0 0 0 .393-.288L8 2.223l1.847 3.658a.525.525 0 0 0 .393.288l4.052.575-2.906 2.77a.565.565 0 0 0-.163.506l.694 3.957-3.686-1.894a.503.503 0 0 0-.461 0z"></path>
                                                                </svg>
                                                            </c:forEach>
                                                        </ul>
                                                    </div>
                                                    <span style="font-size: 14px;">
                                                <fmt:formatDate value="${comment.createdAt}" type="both"
                                                                timeStyle="medium" dateStyle="short"/>
                                            </span>
                                                    <p class="text-break"><c:out value="${comment.body}"/></p>
                                                </div>
                                            </c:forEach>
                                            <c:set var="currentPage" value="${requestScope.currentPage}"/>
                                            <nav aria-label="Page navigation">
                                                <ul class="pagination">
                                                    <li class="page-item <c:if test="${currentPage == 1}">disabled</c:if>">
                                                        <a class="page-link"
                                                           href="<c:url value="/dish?id=${dish.id}&page=${currentPage - 1}"/>">
                                                            <fmt:message key="pagination.previous"/>
                                                        </a>
                                                    </li>
                                                    <c:forEach var="i" begin="1" end="${currentPage - 1}">
                                                        <li class="page-item">
                                                            <a class="page-link"
                                                               href="<c:url value="/dish?id=${dish.id}&page=${i}"/>">
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
                                                               end="${requestScope.pageCount}">
                                                        <li class="page-item">
                                                            <a class="page-link"
                                                               href="<c:url value="/dish?id=${dish.id}&page=${i}"/>">
                                                                <c:out value="${i}"/>
                                                            </a>
                                                        </li>
                                                    </c:forEach>
                                                    <li class="page-item <c:if test="${currentPage == requestScope.pageCount}">disabled</c:if>">
                                                        <a class="page-link"
                                                           href="<c:url value="/dish?id=${dish.id}&page=${currentPage + 1}"/>">
                                                            <fmt:message key="pagination.next"/>
                                                        </a>
                                                    </li>
                                                </ul>
                                            </nav>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="d-flex flex-wrap justify-content-center mb-3">
                                                <img src="<c:url value="/img/nothing-found.png"/>"
                                                     class="img-fluid my-3" alt="nothing found img"/>
                                                <span class="display-6 col-12 text-center">
                                                     <fmt:message key="dish.noReviews"/>
                                                </span>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <aside class="col-xxl-2 col-xl-3 col-lg-3 col-md-4 col-sm-6">
        <div class="card me-xxl-3 me-xl-2 me-lg-1 me-md-0" id="cartBlock" style="width: 270px;">
            <div class="card-body d-flex justify-content-between">
                <h5 class="card-title">
                    <fmt:message key="cafe.cart"/>
                </h5>
                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger total-count"></span>
                <button class="border-0 bg-body text-danger mb-1 clear-cart" style="font-size: 14px;">
                    <fmt:message key="cart.clear"/>
                </button>
            </div>
            <ul class="list-group list-group-flush show-cart">
                <%--cart body--%>
            </ul>
            <div class="card-body">
                <div class="d-flex justify-content-between col-12 mb-3">
                        <span>
                            <fmt:message key="cart.totalPrice"/>:
                        </span>
                    <span><span class="total-cart"></span> BYN</span>
                </div>
                <a href="<c:url value="/cart"/>" class="btn btn-outline-success col-12 text-center">
                    <fmt:message key="cart.order"/>
                </a>
            </div>
        </div>
    </aside>
</main>

<c:if test="${not empty user}">
    <%-- start modal --%>
    <div class="modal fade review-modal" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel"
         aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">
                        <fmt:message key="dish.leaveReview"/>
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form method="post" action="<c:url value="/dish"/>">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-sm-6">
                                <div class="form-group">
                                    <label for="review-rating">
                                        <fmt:message key="dish.rating"/>
                                    </label>
                                    <div class="d-flex flex-row">
                                        <input type="range" name="rating" required value="5" class="form-range"
                                               id="review-rating" min="1" max="5"/>
                                        <output class="mt-2 mx-2" id="rating-output">5</output>
                                        <span class="mt-2">
                                            <fmt:message key="dish.stars"/>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="review-message">
                                <fmt:message key="dish.review"/>
                            </label>
                            <textarea name="body" class="form-control" required
                                      maxlength="1024" rows="8" id="review-message"></textarea>
                            <div class="d-flex flex-row text-muted mt-1">
                                <span class="me-1">
                                    <fmt:message key="dish.comment.left"/>:
                                </span>
                                <output id="leftChars">1024</output>
                            </div>
                            <div class="invalid-feedback">
                                <fmt:message key="dish.error.messageBody"/>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-outline-primary" id="sendReview">
                            <fmt:message key="dish.submit"/>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <%-- end modal --%>
</c:if>

<c:import url="footer.jsp" charEncoding="utf-8"/>

<script type="text/javascript" src="<c:url value="/js/jquery-3.6.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/bootstrap.bundle.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/cafe.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/cart.js"/>"></script>
</body>
</html>
