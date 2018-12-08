<%--<%@ page contentType="text/html; charset=UTF-8" language="java" %>--%>
<%--<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>--%>
<%--<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>--%>
 <%--&lt;%&ndash;&ndash;%&gt;--%>
<%--&lt;%&ndash;<!DOCTYPE html>&ndash;%&gt;--%>
<%--&lt;%&ndash;<html lang="en">&ndash;%&gt;--%>
<%--&lt;%&ndash;<head>&ndash;%&gt;--%>
    <%--&lt;%&ndash;<meta charset="utf-8">&ndash;%&gt;--%>
    <%--&lt;%&ndash;<meta http-equiv="X-UA-Compatible" content="IE=edge">&ndash;%&gt;--%>
    <%--&lt;%&ndash;<meta name="viewport" content="width=device-width, initial-scale=1.0">&ndash;%&gt;--%>
    <%--&lt;%&ndash;<meta name="description" content="">&ndash;%&gt;--%>
    <%--&lt;%&ndash;<meta name="author" content="">&ndash;%&gt;--%>
 <%--&lt;%&ndash;&ndash;%&gt;--%>
    <%--&lt;%&ndash;<title></title>&ndash;%&gt;--%>
 <%--&lt;%&ndash;&ndash;%&gt;--%>
    <%--&lt;%&ndash;&ndash;%&gt;--%>
<%--&lt;%&ndash;</head>&ndash;%&gt;--%>
 <%--&lt;%&ndash;&ndash;%&gt;--%>
<%--&lt;%&ndash;<body>&ndash;%&gt;--%>
 <%--&lt;%&ndash;&ndash;%&gt;--%>

    <%--&lt;%&ndash;<div style="margin-top: 10px;">&ndash;%&gt;--%>
    <%--&lt;%&ndash;<sec:authorize access="!isAuthenticated()">&ndash;%&gt;--%>
            <%--&lt;%&ndash;<p><a href="<c:url value="/login" />" role="button">Login</a></p>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<p><a href="<c:url value="/register" />" role="button">Registration</a></p>&ndash;%&gt;--%>
        <%--&lt;%&ndash;</sec:authorize>&ndash;%&gt;--%>
        <%--&lt;%&ndash;<sec:authorize access="isAuthenticated()">&ndash;%&gt;--%>
            <%--&lt;%&ndash;You are authorize as <sec:authentication property="principal.username" /><br/>&ndash;%&gt;--%>
          	<%--&lt;%&ndash;<c:url value="/logout" var="logoutUrl" />&ndash;%&gt;--%>
			<%--&lt;%&ndash;<form action="${logoutUrl}" method="post">&ndash;%&gt;--%>
         		 <%--&lt;%&ndash;<input type="submit" value="Logout" /> &ndash;%&gt;--%>
         		 <%--&lt;%&ndash;<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />&ndash;%&gt;--%>
			<%--&lt;%&ndash;</form>  &ndash;%&gt;--%>
         <%--&lt;%&ndash;</sec:authorize>&ndash;%&gt;--%>
    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>

<%--&lt;%&ndash;</body>&ndash;%&gt;--%>
<%--&lt;%&ndash;</html>&ndash;%&gt;--%>


<%--<header class="header">--%>

    <%--<!-- begin Right block -->--%>
    <%--<div class="header__right__box">--%>

        <%--<a href="#" class="mobile__menu__toggle glyphicon-align-justify"></a>--%>

        <%--<div class="header__flip">--%>
            <%--<span>Добрый день! <strong><sec:authentication property="principal.username" /></strong></span>--%>
            <%--<c:url value="/logout" var="logoutUrl" />--%>
            <%--<form action="${logoutUrl}" id="logoutForm" method="post">--%>
                <%--<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />--%>
                <%--<button type="submit" class="btn btn-link">--%>
                    <%--<a>--%>
                        <%--<h5><strong>Выйти</strong></h5>--%>
                    <%--</a>--%>
                <%--</button>--%>
            <%--</form>--%>
            <%--&lt;%&ndash;<a href="#" id="logout" class="login__link">&ndash;%&gt;--%>
                <%--&lt;%&ndash;<h5><strong>Выйти</strong></h5>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</a>&ndash;%&gt;--%>
            <%--<div class="dropdown lang__select">--%>
                <%--<a data-toggle="dropdown" href="#">ru</a><i class="glyphicon-chevron-down"></i>--%>
                <%--<ul class="dropdown-menu">--%>
                    <%--<li><a href="#">ru</a></li>--%>
                    <%--<li><a href="#">en</a></li>--%>
                <%--</ul>--%>
            <%--</div>--%>
        <%--</div>--%>

        <%--<!-- begin order__history -->--%>
        <%--<section id="" class="order__history">--%>
            <%--<div class="dropdown order__history__instrument">--%>
                <%--<a data-toggle="dropdown" class="btn btn-default" href="#">BTC/USD <span class="glyphicon-chevron-down"></span></a>--%>
                <%--<ul class="dropdown-menu">--%>
                    <%--<li><a href="#">BTC/USD</a></li>--%>
                    <%--<li><a href="#">BTC/USD</a></li>--%>
                <%--</ul>--%>
            <%--</div>--%>
            <%--<ul class="order__history__item">--%>
                <%--<li><span>Последняя сделка:</span> <span>456 USD</span></li>--%>
                <%--<li><span>Цена открытия:</span> <span>450 USD</span></li>--%>
                <%--<li><span>Цена закрытия:</span> <span>470 USD</span></li>--%>
                <%--<li><span>Объем:</span> <span>1000 BTC</span></li>--%>
                <%--<li><span>35000 USD</span></li>--%>
            <%--</ul>--%>
        <%--</section>--%>
        <%--<!-- end order__history -->--%>

    <%--</div>--%>
    <%--<!-- end Right block -->--%>

<%--</header>--%>