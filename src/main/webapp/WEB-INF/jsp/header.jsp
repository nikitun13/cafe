<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container-fluid bg-light mb-4">
    <header class="d-flex flex-wrap align-items-center justify-content-center
     py-3  border-bottom">

        <div class="container col-md-3 col-sm-12 d-inline-flex">

            <a class="navbar-brand d-inline-flex"
               href="<c:url value="/"/>">
                <img src="<c:url value="/img/logo.png"/>" alt="Cafe logo" width="32px" height="32px"
                     class="d-inline-block mb-1">
                <span class="mx-1" style="font-size: 1.5rem">Cafe</span>
            </a>

            <ul class="nav">
                <li><a href="<c:url value="/locale?lc=en-US"/>" class="nav-link px-2 mt-1 link-dark">
                    EN
                </a>
                </li>
                <li><a href="<c:url value="/locale?lc=ru-RU"/>" class="nav-link px-2 mt-1 link-dark">
                    RU
                </a>
                </li>
                <li><a href="<c:url value="/locale?lc=de-DE"/>" class="nav-link px-2 mt-1 link-dark">
                    DE
                </a>
                </li>
            </ul>
        </div>

        <form class="col-md-6 col-sm-12 mb-1 mt-2 mb-sm-2" id="search-form" action="<c:url value="/"/>">
            <input name="q" type="search" id="searchInput" class="form-control" required maxlength="64"
                   placeholder="<fmt:message key="header.search"/>..." aria-label="Search"
                    <c:if test="${not empty requestScope.searchString}">
                        value="<c:out value="${requestScope.searchString}"/>"
                    </c:if>
            />
        </form>

        <div class="col-md-3 col-sm-12 text-end">
            <c:choose>
                <c:when test="${empty sessionScope.user}">
                    <a href="<c:url value="/signin"/>" class="btn btn-outline-primary me-2">
                        <fmt:message key="cafe.signIn"/>
                    </a>
                    <a href="<c:url value="/signup"/>" class="btn btn-primary">
                        <fmt:message key="cafe.signUp"/>
                    </a>
                </c:when>
                <c:otherwise>
                    <div class="flex-shrink-0 dropdown d-flex justify-content-end me-2">
                        <button class="d-block link-dark text-decoration-none dropdown-toggle border-0 bg-light"
                                id="dropdownUser" data-bs-toggle="dropdown" aria-expanded="false">
                            <svg xmlns="http://www.w3.org/2000/svg" width="35" height="35" fill="currentColor"
                                 class="bi bi-person-circle" viewBox="0 0 16 16">
                                <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0z"></path>
                                <path fill-rule="evenodd"
                                      d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8zm8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1z"></path>
                            </svg>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end text-small shadow" aria-labelledby="dropdownUser"
                            style="z-index: 1025">
                            <li>
                                <a class="dropdown-item" href="<c:url value="/profile"/>">
                                    <fmt:message key="cafe.profile"/>
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="<c:url value="/profile/orders"/>">
                                    <fmt:message key="cafe.profile.orders"/>
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="<c:url value="/cart"/>">
                                    <fmt:message key="cafe.cart"/>
                                </a>
                            </li>
                            <li>
                                <hr class="dropdown-divider"/>
                            </li>
                            <li>
                                <a class="dropdown-item" href="<c:url value="/signout"/>">
                                    <fmt:message key="cafe.signOut"/>
                                </a>
                            </li>
                        </ul>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </header>
</div>
