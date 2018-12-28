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
    <title><loc:message code="transactions.title"/></title>
    <%@include file="../jsp/tools/google_head.jsp"%>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value='/client/js/globalPages/dashboard-init.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/client/js/function.js'/>"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
    <%----------%>
</head>

<%--TODO DELETE--%>

<body>

<%@include file='header_new.jsp' %>
<%@include file="../jsp/tools/google_body.jsp"%>

<main class="container orders_new transaction my_orders orders .container_footer_bottom my_wallets">
    <div class="row">
        <%@include file='usermenu_new.jsp' %>

        <div class="col-sm-9 content">
            <c:choose>
                <c:when test="${fn:length(transactions)==0}">
                    <loc:message code="transactions.absent"/>
                </c:when>
                <c:otherwise>
                    <%--ИСТОРИЯ ОПЕРАЦИЙ--%>
                    <h4><loc:message code="transactions.title"/></h4>
                    <table>
                        <tbody>
                        <tr>
                                <%--Дата--%>
                            <th><loc:message code="transaction.datetime"/></th>
                                <%--Тип--%>
                            <th><loc:message code="transaction.operationType"/></th>
                                <%--Валюта--%>
                            <th><loc:message code="transaction.currency"/></th>
                                <%--Сумма--%>
                            <th><loc:message code="transaction.amount"/></th>
                                <%--Сумма <br> комиссии--%>
                            <th><loc:message code="transaction.commissionAmount"/></th>
                                <%--Платежная <br> система--%>
                            <th><loc:message code="transaction.merchant"/></th>
                            <th><loc:message code="transaction.order"/></th>
                            <th><loc:message code="transaction.status"/></th>
                        </tr>
                        <c:forEach var="transaction" items="${transactions}">
                            <tr>
                                <td style="white-space: nowrap;">
                                        ${transaction.datetime}
                                </td>
                                <td>
                                    <loc:message code="transaction.operationType${transaction.operationType}"/>
                                </td>
                                    <%--USD--%>
                                <td>
                                        ${transaction.currency}
                                </td>
                                    <%--Amount--%>
                                <td>
                                    <fmt:formatNumber value="${transaction.amount}" maxFractionDigits="9"/>
                                </td>
                                    <%--комиссия--%>
                                <td>
                                    <fmt:formatNumber value="${transaction.commissionAmount}" maxFractionDigits="9"/>
                                </td>
                                    <%--мерч имя--%>
                                <td>
                                    <c:if test="${transaction.merchant.getId() != null}">
                                        ${transaction.merchant.name}
                                    </c:if>
                                </td>
                                <td class="order-noty">
                                    <c:if test="${transaction.order.getId() != null && transaction.order.getId() >0}">
                                        ${transaction.order.getId()}
                                    </c:if>
                                    <input id="operationType" hidden type="text"
                                           value="${transaction.order.getOperationType()}"/>
                                    <input id="amountBase" hidden type="text"
                                           value="${transaction.order.getAmountBase()}"/>
                                    <input id="exRate" hidden type="text" value="${transaction.order.getExRate()}"/>
                                    <input id="amountConvert" hidden type="text"
                                           value="${transaction.order.getAmountConvert()}"/>
                                    <input id="dateCreation" hidden type="text"
                                           value="${transaction.order.getDateCreation()}"/>
                                    <input id="dateAcception" hidden type="text"
                                           value="${transaction.order.getDateAcception()}"/>
                                </td>
                                <td>
                                    <c:if test="${transaction.status != null}">
                                        ${transaction.status}
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <hr/>
</main>
<%@include file='fragments/footer.jsp' %>
</body>
</html>

