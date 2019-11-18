<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="admin.companyWallet"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>
    <%@include file='admin/links_scripts.jsp' %>

    <script type="text/javascript" src="<c:url value='/client/js/function.js'/>"></script>

    <%----------%>
</head>


<body>

<%@include file='fragments/header-simple.jsp' %>

<main class="container my_wallets">
    <div class="row">
        <%@include file='admin/left_side_menu.jsp' %>

        <div class="col-md-8 col-md-offset-1 content admin-container">
            <div class="text-center">
                <h4>
                    <b><loc:message code="admin.companyWallet"/></b>
                </h4>
            </div>
            <%--список счетов--%>

            <c:forEach var="wallet" items="${companyWalletList}">
                <div class="block">
                        <%--RUB--%>
                    <div class="currency">${wallet.currency.name}</div>
                        <%--баланс:   0--%>
                    <p class="margin_top">
                        <loc:message code="mywallets.balance"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.balance}"/>
                    </p>
                        <%--Зарезервировано: 100--%>
                    <p>
                        <loc:message code="mywallets.commissionbalance"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9"
                                          value="${wallet.commissionBalance}"/>
                    </p>
                </div>
            </c:forEach>
        </div>
    </div>
    <hr>
</main>
</body>
</html>

