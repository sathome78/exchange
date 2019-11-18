<%@page language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8"/>
  <!--[if lt IE 9]>
  <%--script not found--%>
  <!--<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script-->><![endif]-->
  <title><loc:message code="admin.withdrawRequests"/></title>
  <meta name="keywords" content=""/>
  <meta name="description" content=""/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <%@include file='admin/links_scripts.jsp' %>
  <link rel="stylesheet" href="<c:url value="/client/css/jquery-ui.css"/>">
  <script type="text/javascript" src="<c:url value='/client/js/jquery-ui.js'/>"></script>
  <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
  <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
  <%--delete?--%>
  <script type="text/javascript" src="<c:url value='/client/js/reportAdmin.js'/>"></script>

  <script type="text/javascript" src="<c:url value='/client/js/inputOutput/withdrawal.js'/>"></script>

  <style>
    img {
      display: block;
      margin-left: auto;
      margin-right: auto;
    }
  </style>
</head>

<body id="withdraw-requests-admin">
<%@include file='fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='admin/left_side_menu.jsp' %>
        <div class="col-md-8 col-sm-offset-1 content admin-container">
            <div class="row text-right">
                <div style="float: left; display: inline-block">
                    <button id="withdraw-requests-manual" class="myorders__button blue-box margin-box">
                        <loc:message code="admin.withdraw.manual"/></button>
                    <button id="withdraw-requests-confirm" class="myorders__button blue-box margin-box">
                        <loc:message code="admin.withdraw.confirm"/></button>
                    <button id="withdraw-requests-auto" class="myorders__button blue-box margin-box">
                        <loc:message code="admin.withdraw.auto"/></button>
                    <button id="withdraw-requests-All" class="myorders__button blue-box margin-box">
                        <loc:message code="admin.withdraw.All"/></button>
                    <button id="withdraw-requests-accepted" class="myorders__button green-box margin-box">
                        <loc:message code="admin.withdraw.accepted"/></button>
                    <button id="withdraw-requests-declined" class="myorders__button red-box margin-box">
                        <loc:message code="admin.withdraw.declined"/></button>
                    <button id="withdraw-requests-checking" class="myorders__button yellow-box margin-box">
                        <loc:message code="admin.withdraw.checking"/></button>
                </div>
            </div>


      <div class="row text-center"><h4><loc:message code="admin.withdrawRequests"/></h4></div>
      <div class="col-md-10">
        <button class="blue-box" style="margin: 10px 0;"
                id="withdrawal-request-filter_button">
          <loc:message code="admin.user.transactions.extendedFilter"/></button>

        <button class="blue-box" style="margin: 10px 0;"
                id="withdrawal-statistic_button">
          <loc:message code="admin.withdrawal.statistic.filter"/></button>

        <div id="withdrawal-request-filter" style="display: none">
          <form id="withdrawal-request-search-form" class="form_full_height_width" method="get">
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
                  <loc:message code="transaction.currency"/>
                </label>
              </div>
              <div class="col-md-9 ">
                <ul class="checkbox-grid">
                  <c:forEach items="${currencies}" var="currency">
                    <li><input type="checkbox" name="currencyIds"
                               value="${currency.currencyId}"><span>${currency.currencyName}</span></li>
                  </c:forEach>
                </ul>
              </div>
            </div>
            <%--MERCHANT--%>
            <div class="input-block-wrapper">
              <div class="col-md-3 input-block-wrapper__label-wrapper">
                <label class="input-block-wrapper__label">
                  <loc:message code="withdrawal.merchant"/>
                </label>
              </div>
              <div class="col-md-9 ">
                <ul class="checkbox-grid">
                  <c:forEach items="${merchants}" var="merchant">
                    <li><input type="checkbox" name="merchantIds" value="${merchant.id}"><span>${merchant.name}</span>
                    </li>
                  </c:forEach>
                </ul>

              </div>

            </div>
            <%--TIME--%>
            <div class="input-block-wrapper">
              <div class="col-md-3 input-block-wrapper__label-wrapper">
                <label class="input-block-wrapper__label">
                  <loc:message code="ordersearch.date"/>
                </label>
              </div>
              <div class="col-md-9 input-block-wrapper__input-wrapper">
                <input id="filter-datetimepicker_start" type="text" name="startDate">
                <input id="filter-datetimepicker_end" type="text" name="endDate">
              </div>

            </div>
              <%--DATE_PROCESSING--%>
              <div class="input-block-wrapper">
                <div class="col-md-3 input-block-wrapper__label-wrapper">
                  <label class="input-block-wrapper__label">
                    <loc:message code="withdrawal.statusModificationDate"/>
                  </label>
                </div>
                <div class="col-md-9 input-block-wrapper__input-wrapper">
                  <input id="filter-dateProcessing-datetimepicker_start" type="text" name="startDateStatus">
                  <input id="filter-dateProcessing-datetimepicker_end" type="text" name="endDateStatus">
                </div>

              </div>
            <%--AMOUNT--%>
            <div class="input-block-wrapper">
              <div class="col-md-3 input-block-wrapper__label-wrapper">
                <label class="input-block-wrapper__label">
                  <loc:message code="orders.amount"/>
                </label>
              </div>
              <div class="col-md-9 input-block-wrapper__input-wrapper">
                <input type="number" id="filter-amountFrom" name="amountFrom">
                <input type="number" id="filter-amountTo" name="amountTo">
              </div>
            </div>
            <%--COMMISSION_AMOUNT--%>
            <div class="input-block-wrapper">
              <div class="col-md-3 input-block-wrapper__label-wrapper">
                <label class="input-block-wrapper__label">
                  <loc:message code="inputoutput.commissionAmount"/>
                </label>
              </div>
              <div class="col-md-9 input-block-wrapper__input-wrapper">
                <input type="number" id="filter-commission-amount-from" name="commissionAmountFrom">
                <input type="number" id="filter-commission-amount-to" name="commissionAmountTo">
              </div>
            </div>
            <%--USER EMAIL--%>
            <div class="input-block-wrapper">
              <div class="col-md-3 input-block-wrapper__label-wrapper">
                <label class="input-block-wrapper__label">
                  <loc:message code="transaction.initiatorEmail"/>
                </label>
              </div>
              <div class="col-md-9 input-block-wrapper__input-wrapper">
                <input id="filter-email" class="input-block-wrapper__input admin-form-input" name="email">
              </div>
            </div>
            <%--WALLET--%>
            <div class="input-block-wrapper">
              <div class="col-md-3 input-block-wrapper__label-wrapper">
                <label class="input-block-wrapper__label">
                  <loc:message code="merchants.withdrawDetails.recipientAccount"/>
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
                  <loc:message code="merchants.withdrawDetails.recipientBank"/>
                </label>
              </div>
              <div class="col-md-9 input-block-wrapper__input-wrapper">
                <input id="filter-bank-recipient" class="input-block-wrapper__input admin-form-input"
                       name="recipientBank">
              </div>
            </div>
            <%--full name--%>
            <div class="input-block-wrapper">
              <div class="col-md-3 input-block-wrapper__label-wrapper">
                <label class="input-block-wrapper__label">
                  <loc:message code="merchants.withdrawDetails.recipientFullName"/>
                </label>
              </div>
              <div class="col-md-9 input-block-wrapper__input-wrapper">
                <input id="filter-full-name" class="input-block-wrapper__input admin-form-input" name="fullName">
              </div>
            </div>

            <button id="filter-apply" class="blue-box"><loc:message
                    code="admin.user.transactions.applyFilter"/></button>
            <button id="filter-reset" class="blue-box"><loc:message
                    code="admin.user.transactions.resetFilter"/></button>
          </form>

        </div>

      <div class="col-md-12" id="withdrawal-statistic" style="display: none">
            <%--DATE--%>
            <div class="container">
              <div class="form-group">

                <div class="col-md-4 input-block-wrapper__label-wrapper">
                  <label class="input-block-wrapper__label">
                    <loc:message code="ordersearch.date"/>
                  </label>
                </div>
                <div class="col-md-5 input-block-wrapper__input-wrapper">
                  <input id="filter_statistic-datetimepicker_start" type="text" name="startDate">
                  <input id="filter_statistic-datetimepicker_end" type="text" name="endDate">
                </div>
                <button class="btn btn-md btn-default pull-left" style="margin-bottom: 10px"
                        id="filter_statistic_button">
                  <span class="glyphicon glyphicon-refresh"></span>
                </button>
              </div>
            </div>

            <div class="container">
              <div class="form-group">
                <label class="control-label col-sm-4" for="manual_withdrawals">
                  <loc:message code="admin.withdrawal.statistic.manual_withdrawals"/>
                </label>

                <div class="col-sm-8">
                  <input id="manual_withdrawals" type="text" name="manual_withdrawals"
                         style="text-align: center" disabled/>
                </div>
              </div>
            </div>
            <br/>
            <div class="container">
              <div class="form-group">
                <label class="control-label col-sm-4" for="auto_withdrawals">
                  <loc:message code="admin.withdrawal.statistic.auto_withdrawals"/>
                </label>

                <div class="col-sm-8">
                  <input id="auto_withdrawals" type="text" name="auto_withdrawals"
                         style="text-align: center" disabled/>
                </div>
              </div>
            </div>
      </div>

      <table id="withdrawalTable">
        <thead>
        <tr>
          <th><loc:message code="transaction.id"/></th>
          <th><loc:message code="withdrawal.requestDatetime"/></th>
          <th><loc:message code="withdrawal.user"/></th>
          <th><loc:message code="withdrawal.amount"/></th>
          <th><strong><loc:message code="withdrawal.amountToWithdraw"/></strong></th>
          <th><loc:message code="withdrawal.currency"/></th>
          <th><loc:message code="withdrawal.commission"/></th>
          <th><loc:message code="withdrawal.merchant"/></th>
          <th><loc:message code="merchants.withdrawDetails.recipientAccount"/></th>
          <th><loc:message code="merchants.withdrawDetails.txHash"/></th>
          <th><loc:message code="merchants.withdrawDetails.destinationTag"/></th>
          <th></th>
        </tr>
        </thead>

      </table>
          <div class="row text-1_5" style="margin-top: 20px">
            <strong><loc:message code="withdrawal.amountExplained"/><br/></strong>
            <strong><loc:message code="withdrawal.amountToWithdrawExplained"/></strong>
          </div>

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
  <loc:message code="merchants.withdrawRequestAccepted"/>
</div>
<div id="declined" style="display: none">
  <loc:message code="merchants.WithdrawRequestDecline"/>
</div>
<div id="prompt_acc_rqst" style="display: none">
  <loc:message code="merchants.promptWithdrawRequestAccept"/>
</div>
<div id="prompt_dec_rqst" style="display: none">
  <loc:message code="merchants.promptWithdrawRequestDecline"/>
</div>
<div id="prompt_send_message_rqst" style="display: none">
  <loc:message code="admin.promptSendMessageRequestAccept"/>
</div>

<%@include file='fragments/modal/withdraw_info_modal.jsp' %>
<%@include file="fragments/modal/confirm_with_info_modal.jsp" %>
<%@include file='fragments/modal/enter_note_before_decline_modal.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
