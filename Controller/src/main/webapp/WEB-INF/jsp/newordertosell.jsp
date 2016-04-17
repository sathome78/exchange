<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="orders.title"/></title>

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
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/calculateCreateOrderFormField.js'/>"></script>
    <%----------%>

</head>


<body>

<%@include file='header_new.jsp' %>

<main class="container orders_new transaction my_orders orders">
    <%@include file='exchange_info_new.jsp' %>
    <div class="row">
        <%@include file='usermenu_new.jsp' %>
        <div class="col-sm-9 content">
            <c:set var="SELL" value="<%=me.exrates.model.enums.OperationType.SELL%>"/>
            <c:set var="BUY" value="<%=me.exrates.model.enums.OperationType.BUY%>"/>
            <c:set value="/order/submit" var="submitUrl" />
            <c:set value="/orders" var="cancelUrl"/>
            <%----%>
            <c:if test="${orderCreateDto.getOperationType()  eq BUY}">
                <div class="buy-sell-form">
                    <%@include file="orderForBuyForm.jsp" %>
                </div>
            </c:if>
            <c:if test="${orderCreateDto.getOperationType()  eq SELL}">
                <div class="buy-sell-form">
                    <%@include file="orderForSellForm.jsp" %>
                </div>
            </c:if>
            <%----%>
            <hr>
        </div>
    </div>
</main>
<%@include file='footer_new.jsp' %>
</body>
</html>

