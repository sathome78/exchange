<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title><loc:message code="admin.refillRequests"/></title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <%@include file='admin/links_scripts.jsp'%>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery-ui.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery-ui.js'/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/ekko-lightbox.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/inputOutput/valueInputControl.js"/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/inputOutput/refill.js'/>"></script>

</head>

<body id="refill-requests-admin">
<%@include file='fragments/header-simple.jsp'%>
<main class="container">
    <div class="row">
        <%@include file='admin/left_side_menu.jsp' %>
        <div class="col-md-8 col-sm-offset-1 content admin-container">
            <div class="row text-center">
                <div style="float: left; display: inline-block">
                    <button id="refill-requests-new" class="myorders__button blue-box margin-box">
                        <loc:message code="admin.refill.new"/></button>
                    <button id="refill-requests-on-pending" class="myorders__button blue-box margin-box">
                        <loc:message code="admin.refill.waitPayment"/></button>
                    <button id="refill-requests-on-bch-exam" class="myorders__button blue-box margin-box">
                        <loc:message code="admin.refill.confirmationCollecting"/></button>
                    <button id="refill-requests-All" class="myorders__button blue-box margin-box">
                        <loc:message code="admin.refill.All"/></button>
                    <button id="refill-requests-created-by-fact" class="myorders__button blue-box margin-box">
                        <loc:message code="admin.refill.createdByFact"/></button>
                    <button id="refill-requests-accepted" class="myorders__button green-box margin-box">
                        <loc:message code="admin.refill.accepted"/></button>
                    <button id="refill-requests-declined" class="myorders__button red-box margin-box">
                        <loc:message code="admin.refill.declined"/></button>
                    <a style="cursor: pointer; text-decoration: none" href="/2a8fy7b07dxe44/refillAddresses" class="myorders__button blue-box margin-box active">
                        <loc:message code="admin.refillAdresses"/></a>
                    <a id="create_refill_request" style="cursor: pointer; text-decoration: none" class="myorders__button blue-box margin-box active">
                        <loc:message code="message.create.refill.request"/></a>
                </div>
            </div>


            <div class="row text-center"><h4><loc:message code="admin.refillRequests"/></h4></div>
            <div class="col-md-8">
                <button data-toggle="collapse" class="blue-box" style="margin: 10px 0;" data-target="#refill-request-filter">
                    <loc:message code="admin.user.transactions.extendedFilter"/> </button>
                <div id="refill-request-filter" class="collapse">
                    <form id="refill-request-search-form" class="form_full_height_width" method="get">
                        <%--ID--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="transaction.id"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="number" id="filter-id" name="requestId">
                            </div>
                        </div>
                        <%--CURRENCY--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="transaction.currency" />
                                </label>
                            </div>
                            <div class="col-md-9 ">
                                <ul class="checkbox-grid">
                                    <c:forEach items="${currencies}" var="currency">
                                        <li><input type="checkbox" name="currencyIds" value="${currency.currencyId}"><span>${currency.currencyName}</span></li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </div>
                        <%--MERCHANT--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="refill.merchant" />
                                </label>
                            </div>
                            <div class="col-md-9 ">
                                <ul class="checkbox-grid">
                                     <c:forEach items="${merchants}" var="merchant">
                                         <li><input type="checkbox" name="merchantIds" value="${merchant.id}"><span>${merchant.name}</span></li>
                                     </c:forEach>
                                </ul>

                            </div>

                        </div>
                        <%--TIME--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ordersearch.date" />
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input id="filter-datetimepicker_start" type="text" name="startDate">
                                <input id="filter-datetimepicker_end" type="text" name="endDate">
                            </div>

                        </div>
                        <%--AMOUNT--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="orders.amount" />
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="number" id="filter-amountFrom" name="amountFrom">
                                <input type="number" id="filter-amountTo" name="amountTo">
                            </div>
                        </div>
                        <%--&lt;%&ndash;COMMISSION_AMOUNT&ndash;%&gt;
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="inputoutput.commissionAmount" />
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="number" id="filter-commission-amount-from" name="commissionAmountFrom">
                                <input type="number" id="filter-commission-amount-to" name="commissionAmountTo">
                            </div>
                        </div>--%>
                            <%--USER EMAIL--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="transaction.initiatorEmail" />
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input id="filter-email" class="input-block-wrapper__input admin-form-input" name="email">
                                </div>
                            </div>
                            <%--TX hash--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="transaction.hash" />
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input id="filter-hash" class="input-block-wrapper__input admin-form-input" name="hash">
                                </div>
                            </div>
                            <%--address--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="merchants.btc_invoice.addressToPay" />
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input id="filter-address" class="input-block-wrapper__input admin-form-input" name="address">
                                </div>
                            </div>
                        <%--WALLET--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="merchants.refillDetails.recipientAccount" />
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input id="filter-wallet" class="input-block-wrapper__input admin-form-input" name="wallet">
                            </div>
                        </div>
                        <%--RECIPIENT_BANK--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="merchants.refillDetails.recipientBank" />
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input id="filter-bank-recipient" class="input-block-wrapper__input admin-form-input" name="recipientBank">
                            </div>
                        </div>
                        <%--full name--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="merchants.refillDetails.recipientFullName" />
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input id="filter-full-name" class="input-block-wrapper__input admin-form-input" name="fullName">
                            </div>
                        </div>

                        <button id="filter-apply" class="blue-box"><loc:message code="admin.user.transactions.applyFilter" /></button>
                        <button id="filter-reset" class="blue-box"><loc:message code="admin.user.transactions.resetFilter" /></button>
                    </form>

                </div>
            </div>

            <table id="refillTable">
                <thead>
                <tr>
                    <th><loc:message code="transaction.id"/></th>
                    <th><loc:message code="refill.requestDatetime"/></th>
                    <th><loc:message code="refill.user"/></th>
                    <th><loc:message code="refill.amount"/></th>
                    <th><loc:message code="refill.currency"/></th>
                    <th><loc:message code="refill.receivedAmount"/></th>
                    <th><loc:message code="refill.commission"/></th>
                    <th><loc:message code="refill.enrolledAmount"/></th>
                    <th><loc:message code="refill.merchant"/></th>
                    <th><loc:message code="refill.status"/></th>
                    <th><loc:message code="refill.action"/></th>
                    <th></th>
                </tr>
                </thead>

            </table>
        </div>
    </div>

</main>
<div id="acceptRequestMessage" style="display: none">
    <loc:message code="merchants.withdrawRequestAccept"/>
</div>
<div id="declineRequestMessage" style="display: none">
    <loc:message code="merchants.withdrawRequestDecline"/>
</div>

<div id="accepted" style="display: none">
    <loc:message code="merchants.refillRequestAccepted"/>
</div>
<div id="declined" style="display: none">
    <loc:message code="merchants.refillRequestDecline"/>
</div>
<div id="prompt_acc_rqst" style="display: none">
    <loc:message code="merchants.promptRefillRequestAccept"/>
</div>
<div id="prompt_dec_rqst" style="display: none">
    <loc:message code="merchants.promptRefillRequestDecline"/>
</div>
<div id="prompt_send_message_rqst" style="display: none">
    <loc:message code="admin.promptSendMessageRequestAccept"/>
</div>

<%@include file='fragments/modal/dialogCreateRefill_modal.jsp' %>
<%@include file='fragments/modal/dialogRefillAccept_modal.jsp' %>
<%@include file='fragments/modal/dialogRefill_info_modal.jsp' %>
<%@include file="fragments/modal/confirm_with_info_modal.jsp" %>
<%@include file='fragments/modal/enter_note_before_decline_modal.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
