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
  <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
  <title><loc:message code="admin.transfers"/></title>
  <meta name="keywords" content=""/>
  <meta name="description" content=""/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <%@include file='links_scripts.jsp' %>
  <link rel="stylesheet" href="<c:url value="/client/css/jquery-ui.css"/>">
  <script type="text/javascript" src="<c:url value='/client/js/jquery-ui.js'/>"></script>
  <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
  <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/client/js/inputOutput/adminVoucher.js'/>"></script>

</head>

<body id="withdraw-requests-admin">
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-sm-offset-1 content admin-container">


      <div class="row text-center"><h4><loc:message code="admin.transfers"/></h4></div>
      <div class="col-md-11">
        <button data-toggle="collapse" class="blue-box" style="margin: 10px 0;"
                data-target="#withdrawal-request-filter">
          <loc:message code="admin.user.transactions.extendedFilter"/></button>
        <div id="withdrawal-request-filter" class="collapse">
          <form id="withdrawal-request-search-form" class="form_full_height_width" method="get">
            <%--ID--%>
            <div class="input-block-wrapper">
              <div class="col-md-3 input-block-wrapper__label-wrapper">
                <label class="input-block-wrapper__label">
                  <loc:message code="transaction.id"/>
                </label>
              </div>
              <div class="col-md-2 input-block-wrapper__input-wrapper">
                <input type="number" id="filter-id" name="voucherId">
              </div>
              <div class="col-md-7 input-block-wrapper__label-wrapper">
                <span id="errorValueForTransactionId" class="input-block-wrapper__label" style="color: #FF0000;" hidden>
                  <loc:message code="transaction.id.error.message.for.user"/>
                </span>
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
                    <li><input type="checkbox" name="merchantIds" value="${merchant.id}">
                      <span style="font-size: 10px">${merchant.name}</span>
                    </li>
                  </c:forEach>
                </ul>

              </div>

            </div>
              <%--STATUS--%>
              <div class="input-block-wrapper">
                <div class="col-md-3 input-block-wrapper__label-wrapper">
                  <label class="input-block-wrapper__label">
                    <loc:message code="admin.status"/>
                  </label>
                </div>
                <div class="col-md-9 ">
                  <ul class="checkbox-grid">
                    <c:forEach items="${statuses}" var="status">
                      <li><input type="checkbox" name="statuses" value="${status.code}">
                        <span  style="font-size: 8px">${status}</span>
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
                  <loc:message code="message.sender"/>
                </label>
              </div>
              <div class="col-md-9 input-block-wrapper__input-wrapper">
                <input id="filter-email" class="input-block-wrapper__input admin-form-input" name="creatorEmail">
              </div>
            </div>
              <%--Recipient EMAIL--%>
              <div class="input-block-wrapper">
                <div class="col-md-3 input-block-wrapper__label-wrapper">
                  <label class="input-block-wrapper__label">
                    <loc:message code="message.recipient"/>
                  </label>
                </div>
                <div class="col-md-9 input-block-wrapper__input-wrapper">
                  <input id="filter-recipient-email" class="input-block-wrapper__input admin-form-input" name="recipientEmail">
                </div>
              </div>
            <%--HASH--%>
            <div class="input-block-wrapper">
              <div class="col-md-3 input-block-wrapper__label-wrapper">
                <label class="input-block-wrapper__label">
                  <loc:message code="transaction.hash"/>
                </label>
              </div>
              <div class="col-md-9 input-block-wrapper__input-wrapper">
                <input id="filter-bank-recipient" class="input-block-wrapper__input admin-form-input"
                       name="hash">
              </div>
            </div>

            <button id="filter-apply" class="blue-box"><loc:message
                    code="admin.user.transactions.applyFilter"/></button>
            <button id="filter-reset" class="blue-box"><loc:message
                    code="admin.user.transactions.resetFilter"/></button>
          </form>

        </div>
      </div>

      <table id="voucherTable">
        <thead>
        <tr>
          <th><loc:message code="transaction.id"/></th>
          <th><loc:message code="orderinfo.createdate"/></th>
          <th><loc:message code="withdrawal.user"/></th>
          <th><loc:message code="withdrawal.amount"/></th>
          <th><loc:message code="withdrawal.currency"/></th>
          <th><loc:message code="withdrawal.commission"/></th>
          <th><loc:message code="withdrawal.merchant"/></th>
          <th><loc:message code="withdrawal.status"/></th>
          <th><loc:message code="merchants.withdrawDetails.recipientAccount"/></th>
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

<%--<%@include file='fragments/modal/withdraw_info_modal.jsp' %>--%>
<%@include file='order-modals.jsp' %>
<%@include file="../fragments/modal/confirm_with_info_modal.jsp" %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
