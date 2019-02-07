<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
 <%----%>
<%--<!DOCTYPE html>--%>
<%--<html lang="en">--%>
<%--<head>--%>
    <%--<meta charset="utf-8">--%>
    <%--<meta http-equiv="X-UA-Compatible" content="IE=edge">--%>
    <%--<meta name="viewport" content="width=device-width, initial-scale=1.0">--%>
    <%--<meta name="description" content="">--%>
    <%--<meta name="author" content="">--%>
 <%----%>
    <%--<title></title>--%>
 <%----%>
    <%----%>
<%--</head>--%>
 <%----%>
<%--<body>--%>
 <%----%>

    <%--<div style="margin-top: 10px;">--%>
    <%--<sec:authorize access="!isAuthenticated()">--%>
            <%--<p><a href="<c:url value="/login" />" role="button">Login</a></p>--%>
            <%--<p><a href="<c:url value="/register" />" role="button">Registration</a></p>--%>
        <%--</sec:authorize>--%>
        <%--<sec:authorize access="isAuthenticated()">--%>
            <%--You are authorize as <sec:authentication property="principal.username" /><br/>--%>
          	<%--<c:url value="/logout" var="logoutUrl" />--%>
			<%--<form action="${logoutUrl}" method="post">--%>
         		 <%--<input type="submit" value="Logout" /> --%>
         		 <%--<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />--%>
			<%--</form>  --%>
         <%--</sec:authorize>--%>
    <%--</div>--%>

<%--</body>--%>
<%--</html>--%>

<header class="header">

    <!-- begin Right block -->
    <div class="header__right__box">

        <a href="#" class="mobile__menu__toggle glyphicon-align-justify"></a>

        <div class="header__flip">
            <span>Добрый день! <strong><sec:authentication property="principal.username" /></strong></span>
            <c:url value="/logout" var="logoutUrl" />
            <form action="${logoutUrl}" id="logoutForm" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                <button type="submit" class="btn btn-link">
                    <a>
                        <h5><strong>Выйти</strong></h5>
                    </a>
                </button>
            </form>
            <%--<a href="#" id="logout" class="login__link">--%>
                <%--<h5><strong>Выйти</strong></h5>--%>
            <%--</a>--%>
            <div class="dropdown lang__select">
                <a data-toggle="dropdown" href="#">ru</a><i class="glyphicon-chevron-down"></i>
                <ul class="dropdown-menu">
                    <li><a href="#">ru</a></li>
                    <li><a href="#">en</a></li>
                </ul>
            </div>
        </div>

        <!-- begin order__history -->
        <section id="" class="order__history">
            <div class="dropdown order__history__instrument">
                <a data-toggle="dropdown" class="btn btn-default" href="#">BTC/USD <span class="glyphicon-chevron-down"></span></a>
                <ul class="dropdown-menu">
                    <li><a href="#">BTC/USD</a></li>
                    <li><a href="#">BTC/USD</a></li>
                </ul>
            </div>
            <ul class="order__history__item">
                <li><span>Последняя сделка:</span> <span>456 USD</span></li>
                <li><span>Цена открытия:</span> <span>450 USD</span></li>
                <li><span>Цена закрытия:</span> <span>470 USD</span></li>
                <li><span>Объем:</span> <span>1000 BTC</span></li>
                <li><span>35000 USD</span></li>
            </ul>
        </section>
        <!-- end order__history -->

    </div>
    <!-- end Right block -->

</header>