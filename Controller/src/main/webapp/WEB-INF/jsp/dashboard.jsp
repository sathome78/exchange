<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<html>
<head>
    <title>Exrates</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='https://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value='/client/js/dashboard.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='https://www.google.com/jsapi'/>"></script>
    <script type="text/javascript">
        google.load("visualization", "1", {"packages": ["corechart"]});
    </script>

    <script type="text/javascript">
        window.$zopim||(function(d,s){var z=$zopim=function(c){z._.push(c)},$=z.s=
                d.createElement(s),e=d.getElementsByTagName(s)[0];z.set=function(o){z.set.
                _.push(o)};z._=[];z.set._=[];$.async=!0;$.setAttribute("charset","utf-8");
            $.src="//v2.zopim.com/?3n4rzwKe0WvQGt1TDMpL8gvMRIUvgCjX";z.t=+new Date;$.
                    type="text/javascript";e.parentNode.insertBefore($,e)})(document,"script");
    </script>

</head>

<body>

<header>
    <nav class="navbar">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                        aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#"><img src="/client/img/logo.png" alt="Logo"></a>
            </div>
            <div id="navbar" class="collapse navbar-collapse">
                <ul class="nav navbar-nav">
                    <li>
                        <%--ГЛАВНАЯ--%>
                        <a href="#" class="navabr__link active"><loc:message code="dashboard.general"/></a>
                    </li>
                    <li><a href="#">НОВОСТИ</a></li>
                    <li><a href="#">ОБУЧЕНИЕ</a></li>
                    <li>
                        <%--ЛИЧНЫЙ КАБИНЕТ--%>
                        <a href="<c:url value="/mywallets"/>" class="navabr__link"><loc:message
                                code="dashboard.personalArea"/></a
                    </li>


                    <li class="margin-left">
                        <%--ВОЙТИ--%>
                        <a href="#" data-toggle="modal" data-target="#myModal"><loc:message
                                code="dashboard.entrance"/></a></li>

                    <li>
                        <%--РЕГИСТРАЦИЯ--%>
                        <a href="<c:url value="/register" />"><loc:message code="dashboard.signUp"/></a>
                    </li>

                    <%--ПЕРЕКЛЮЧЕНИЕ ЯЗЫКОВ--%>
                    <li role="presentation" class="dropdown closed">
                        <a href="#" id="language" class="dropdown-toggle" data-toggle="dropdown"
                           aria-expanded="true">
                            ${pageContext.response.locale} <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu" id="languageUl">
                            <li><a class="lang__item" href="#">English</a></li>
                            <li><a class="lang__item" href="#">Русский</a></li>
                            <li><a class="lang__item" href="#">Chinese</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
            <!--/.nav-collapse -->
        </div>
    </nav>
</header>

<!-- Modal  SIGN IN -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Вход</h4>
            </div>
            <div class="modal-body">
                <div class="content">
                    <c:url value="/login" var="loginUrl"/>
                    <form action="${loginUrl}" method="post">
                        <%--логин--%>
                        <input type="text" name="username" placeholder=<loc:message code="dashboard.loginText"/>>
                        <%--пароль--%>
                        <input type="password" name="password" placeholder=<loc:message code="dashboard.passwordText"/>>
                        <%--csrf--%>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <%--войти--%>
                        <button type="submit" class="button_enter"><loc:message code="dashboard.entrance"/></button>
                        <%--Забыли пароль?--%>
                        <button type="button" class="button_forgot"><a href="<c:url value="/forgotPassword"/>"><loc:message code="dashboard.forgotPassword"/></a></button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<main class="container">
    <div class="exchange_data"> <!-- Exchange currencies and graphic -->
        <ul class="exchange">

            <c:forEach var="curr" items="${currencyPairs}" begin="0" end="3">
                <c:choose>
                    <c:when test="${curr.getName()==currencyPair.getName()}">
                        <li class="exchange__pair active" selected><a class="active"
                                                                      href="?name=${curr.getName()}">${curr.getName()}</a></li>
                    </c:when>
                    <c:otherwise>
                        <li class="exchange__pair"><a
                                href="?name=${curr.getName()}">${curr.getName()}</a></li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            <li id="other_pairs"><a href="#">Другие пары <span class="caret"></span></a>
                <ul>
                    <c:forEach var="curr" items="${currencyPairs}" begin="4">
                        <c:choose>
                            <c:when test="${curr.getName()==currencyPair.getName()}">
                                <li class="exchange__pair active" selected><a class="active"
                                        href="?name=${curr.getName()}">${curr.getName()}</a></li>
                            </c:when>
                            <c:otherwise>
                                <li class="exchange__pair"><a
                                        href="?name=${curr.getName()}">${curr.getName()}</a></li>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </ul>
            </li>
        </ul>
        <div class="graphic"> <!-- graphic -->
            <img src="/client/img/graphic.png" alt="Graphic">
        </div>
    </div>

    <%@include file='exchange_info_new.jsp' %>

    <!-- begin quotes__news__section -->
    <%--элемент отсутсвует в новом интерфейсе  //TODO --%>
    <%--отключен до выяснения функциональности--%>
    <section hidden class="quotes__news__section">
        <div class="container container_center">

            <!-- begin chart__section -->
            <div class="chart__section">
                <div class="chart__section__title"><a id="chartPair"></a></div>
                <span style="color:red">${msg}</span><br><br>
                <c:if test="${not empty sumAmountBuyClosed}">
                    <div id='chart_div'></div>
                </c:if>
            </div>
            <!-- end chart__section -->

        </div>
    </section>
    <!-- end quotes__news__section -->

    <div class="buy_sell row"> <!-- BUY or SELL BTC -->
        <div class="buy col-sm-4">
            <%--купить--%>
            <h3><loc:message code="dashboard.BUY"/> ${currencyPair.getCurrency1().getName()}</h3>
            <hr class="display_at_small_width">
            <div class="row add_margin">
                <%--ваши средства--%>
                <div class="col-xs-6"><loc:message code="dashboard.yourBalance"/><br>
                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${balanceCurrency1}"/>
                    ${currencyPair.getCurrency2().getName()}
                </div>
                <%--мин цена--%>
                <div class="col-xs-6"><loc:message code="dashboard.lowestPrice"/><br>
                    <%--${minPrice}--%>
                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${minPrice}"/>
                    ${currencyPair.getCurrency2().getName()}
                </div>
            </div>
            <form:form action="order/submit" method="post" modelAttribute="order" name="formBuy">
                <%--количество--%>
                <label class="col1"><loc:message
                        code="dashboard.amount"/> ${currencyPair.getCurrency1().getName()}:</label>
                <form:errors path="amountBuy" style="color:red"/>
                <form:input class="col2" path="amountBuy" type="text" id="amountBuyForm1" placeholder="0"/>
                <%--цена за--%>
                <label class="col1"><loc:message
                        code="dashboard.priceFor"/> ${currencyPair.getCurrency1().getName()}:</label>
                <form:errors path="amountSell" style="color:red"/>
                <input type="text" class="col2" id="amountSellForm1" placeholder="0">
                <form:input type="hidden" path="amountSell" class="form-control" id="sumSellForm1"
                            placeholder="0"/>
                <%--всего--%>
                <span class="col1"><loc:message code="dashboard.total"/></span>
                <span class="col2"><b id="sumBuyWithCommission"></b> ${currencyPair.getCurrency2().getName()}</span>
                <%--комисия--%>
                <span class="col1"><loc:message code="dashboard.fee"/></span>
                <span class="col2"><b id="buyCommission"></b> ${currencyPair.getCurrency1().getName()}</span>

                <div class="row">
                    <div class="col-xs-6">
                            <%--Подсчитать--%>
                        <button class="calculate" type="button" name="calculateBuy"><loc:message
                                code="dashboard.calculate"/></button>
                    </div>
                    <c:set var="BUY" value="<%=me.exrates.model.enums.OperationType.BUY%>"/>
                    <form:hidden path="operationType" value="${BUY}"/>
                    <form:hidden path="currencySell" value="${currencyPair.getCurrency2().getId()}"/>
                    <form:hidden path="currencyBuy" value="${currencyPair.getCurrency1().getId()}"/>
                    <div class="col-xs-6">
                            <%--Купить--%>
                        <button class="buy" type="submit"><loc:message
                                code="dashboard.buy"/> ${currencyPair.getCurrency1().getName()}</button>
                    </div>
                </div>
            </form:form>
        </div>
        <!-- End BUY BTC-->

        <div class="col-sm-4 big_logo"></div>

        <div class="sell col-sm-4">
            <%--Продать--%>
            <h3><loc:message code="dashboard.SELL"/> ${currencyPair.getCurrency1().getName()}</h3>
            <hr class="display_at_small_width">
            <div class="row add_margin">
                <%--Ваши средства--%>
                <div class="col-xs-6"><loc:message code="dashboard.yourBalance"/> <br>
                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${balanceCurrency2}"/>
                    ${currencyPair.getCurrency1().getName()}
                </div>
                <%--Мин. Цена--%>
                <div class="col-xs-6"><loc:message code="dashboard.highestPrice"/> <br>
                    <%--${maxPrice} --%>
                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${maxPrice}"/>
                    ${currencyPair.getCurrency2().getName()}
                </div>
            </div>
            <form:form action="order/submit" method="post" modelAttribute="order">
                <%--Количество--%>
                <form:errors path="amountSell" style="color:red"/>
                <form:input type="text" class="col2" path="amountSell" id="amountSellForm2" placeholder="0"/>
                <label class="col1"><loc:message
                        code="dashboard.amount"/> ${currencyPair.getCurrency1().getName()}:</label>
                <%--Цена за--%>
                <input type="text" class="col2" id="amountBuyForm2" placeholder="0">
                <form:input type="hidden" path="amountBuy" class="form-control" id="sumBuyForm2" placeholder="0"/>
                <label class="col1"><loc:message
                        code="dashboard.priceFor"/> ${currencyPair.getCurrency1().getName()}:</label>
                <%--ВСЕГО--%>
                <span class="col1"><loc:message code="dashboard.total"/></span>
                    <span class="col2"><b
                            id="sumSellWithCommission"></b> ${currencyPair.getCurrency2().getName()}</span>
                <%--Комиссия--%>
                <span class="col1"><loc:message code="dashboard.fee"/></span>
                <span class="col2"><b id="sellCommission"></b> ${currencyPair.getCurrency2().getName()}</span>

                <div class="row">
                    <div class="col-xs-6" type="button" name="calculateSell">
                            <%--Подсчитать--%>
                        <button class="calculate"><loc:message code="dashboard.calculate"/></button>
                    </div>
                    <c:set var="SELL" value="<%=me.exrates.model.enums.OperationType.SELL%>"/>
                    <form:hidden path="operationType" value="${SELL}"/>
                    <form:hidden path="currencySell" value="${currencyPair.getCurrency1().getId()}"/>
                    <form:hidden path="currencyBuy" value="${currencyPair.getCurrency2().getId()}"/>
                    <div class="col-xs-6">
                            <%--Купить--%>
                        <button class="buy" type="submit"><loc:message
                                code="dashboard.sell"/> ${currencyPair.getCurrency1().getName()}</button>
                    </div>
                </div>
            </form:form>
        </div>
        <!-- End SELL BTC -->
    </div>
    <!-- End BUY or SELL BTC -->

    <div class="row margin_top orders"> <!-- ORDERS -->
        <div class="col-sm-4">
            <%--ОРДЕРА НА ПРОДАЖУ--%>
            <h3 class=""><loc:message code="dashboard.buyOrders"/></h3>
            <hr class="display_at_small_width">
            <%--Всего--%>
            <p>
                Всего:
                <fmt:formatNumber type="number" maxFractionDigits="9" value="${sumAmountBuy}"/>
                ${currencyPair.getCurrency1().getName()}
            </p>
        </div>
        <div class="col-sm-4"></div>
        <div class="col-sm-4 align-right">
            <%--ОРДЕРА НА ПОКУПКУ--%>
            <h3 class=""><loc:message code="dashboard.sellOrders"/></h3>
            <hr class="display_at_small_width">
            <p>
                Всего:
                <fmt:formatNumber type="number" maxFractionDigits="9" value="${sumAmountSell}"/>
                ${currencyPair.getCurrency2().getName()}
            </p>
        </div>
    </div>
    <!-- end Orders -->
    <div class="row"> <!-- Tables -->
        <div class="col-xs-6 table1 mCustomScrollbar" data-mcs-theme="dark">
            <table>
                <tr>
                    <th>Цена</th>
                    <th>${currencyPair.getCurrency1().getName()}</th>
                    <th>${currencyPair.getCurrency2().getName()}</th>
                </tr>
                <c:forEach var="order" items="${ordersSell}">
                    <tr>
                        <td>
                            <fmt:formatNumber type="number" maxFractionDigits="9"
                                              value="${order.amountBuy/order.amountSell}"/>
                        </td>
                        <td>
                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountSell}"/>
                        </td>
                        <td>
                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountBuy}"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <div class="col-xs-6 table1 mCustomScrollbar" data-mcs-theme="dark">
            <table>
                <tr>
                    <th>Цена</th>
                    <th>${currencyPair.getCurrency1().getName()}</th>
                    <th>${currencyPair.getCurrency2().getName()}</th>
                </tr>
                <c:forEach var="order" items="${ordersBuy}">
                    <tr>
                        <td>
                            <fmt:formatNumber type="number" maxFractionDigits="9"
                                              value="${order.amountSell/order.amountBuy}"/>
                        </td>
                        <td>
                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountBuy}"/>
                        </td>
                        <td>
                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountSell}"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
    <!-- end Tables -->
</main>

<%@include file='footer_new.jsp' %>

<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
</body>
</html>