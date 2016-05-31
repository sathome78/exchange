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
    <title><loc:message code="mywallets.title"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='https://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value='/client/js/function.js'/>"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/download.js'/>"></script>
    <%----------%>
</head>

<body>

<%@include file='header_new.jsp' %>

<main class="container orders_new transaction my_orders orders .container_footer_bottom my_wallets">
    <%@include file='exchange_info_new.jsp' %>
    <div class="row">
        <%@include file='usermenu_new.jsp' %>
        <div class="col-sm-9">
            <button id="upload-users-wallets" class="simple-button" type="submit"><loc:message
                    code="wallets.download"/></button>
            </form>
        </div>
        <div class="col-sm-9 content">
            <c:forEach var="wallet" items="${usersWalletsSummaryList}">
                <div class="block">
                    <div class="currency">${wallet.currencyName}</div>
                    <p class="info-item info-item-title col-sm-12">
                        <loc:message code="wallets.amount"/>:
                            ${wallet.walletsAmount}
                    </p>

                    <p class="info-item col-sm-4">
                        <loc:message code="wallets.balance"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.balance}"/>
                    </p>

                    <p class="info-item next_item">
                        <loc:message code="wallets.average"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.balancePerWallet}"/>
                    </p>
                    <br/>

                    <p class="info-item col-sm-4">
                        <loc:message code="wallets.abalance"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.activeBalance}"/>
                    </p>

                    <p class="info-item next_item">
                        <loc:message code="wallets.average"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.activeBalancePerWallet}"/>
                    </p>
                    <br/>

                    <p class="info-item  col-sm-4">
                        <loc:message code="wallets.rbalance"/>:
                            ${wallet.reservedBalance}
                    </p>

                    <p class="info-item next_item">
                        <loc:message code="wallets.average"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9"
                                          value="${wallet.reservedBalancePerWallet}"/>
                    </p>
                </div>
            </c:forEach>
        </div>
    </div>
    <hr>
</main>
<%@include file='footer_new.jsp' %>
<%@include file='admin/datePicker.jsp' %>
</body>
</html>

