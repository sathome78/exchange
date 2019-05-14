
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
    <title>${title}</title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/admin-usdxWallet/usdxWallet.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/tmpl.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<c:set var="admin_manageBtcWallet" value="<%=AdminAuthority.MANAGE_BTC_CORE_WALLET%>"/>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-md-offset-1 admin-container">
            <div class="text-center">
                <h4>${title}</h4>
            </div>
            <div class="row text-center" style="font-size: 1.4rem">
                <p class="green"><strong><loc:message code="admin.externalWallets.totalWalletBalance"/>
                    <span id="current-btc-balance">${usdxBalance.toPlainString()}</span> USDX</strong></p>
                <c:choose>
                    <c:when test="${usdxBalance != null}">
                        <p class="green"><strong><loc:message code="admin.externalWallets.totalWalletBalance"/>
                            <span id="current-btc-balance">${lhtBalance.toPlainString()}</span> LHT</strong></p>
                    </c:when>
                    <c:otherwise>
                        <p class="green"><strong>Error getting ${currency} balances</strong></p>
                    </c:otherwise>
                </c:choose>
            </div>

            <div>
                <form id="usdx-transaction">
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                        <label for="accountName" class="input-block-wrapper__label">Account name></label>
                    </div>
                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                        <input name="accountName" class="form-control input-block-wrapper__input" id="accountName"/>
                    </div>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                        <label for="amount" class="input-block-wrapper__label">Amount</label>
                    </div>
                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                        <input name="amount" class="form-control input-block-wrapper__input" id="amount"/>
                    </div>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                        <label for="currency" class="input-block-wrapper__label">Сurrency</label>
                    </div>
                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                        <input name="currency" class="form-control input-block-wrapper__input" id="currency" value="LHT" readonly/>
                    </div>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                        <label for="memo" class="input-block-wrapper__label">Memo</label>
                    </div>
                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                    <input name="memo" class="form-control input-block-wrapper__input" id="memo"/>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                        <label for="customData" class="input-block-wrapper__label">Custom data (Optional)</label>
                    </div>
                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                        <input name="customData" class="form-control input-block-wrapper__input" id="customData" placeholder="Optional field"/>
                    </div>
                </div>

                <button id="button-send-trans" class="btn btn-primary" style="margin-top:15px; margin-bottom:15px; width:100%;text-align: center;">
                    <loc:message code="admin.submit"/>
                </button>
                </form>
            </div>

            <div class="tab-content">
                <div id="panel1" class="tab-pane active">
                    <div class="text-center"><h4><loc:message code="btcWallet.history.title"/></h4></div>
                    <table id="txHistory">
                        <thead>
                        <tr>
                            <th><loc:message code="btcWallet.history.time"/></th>
                            <th><loc:message code="btcWallet.history.txid"/></th>
                            <th><loc:message code="btcWallet.history.type"/></th>
                            <th><loc:message code="transaction.currency"/></th>
                            <th>Memo</th>
                            <th>Custom data</th>
                            <th><loc:message code="btcWallet.history.amount"/></th>
                            <th><loc:message code="transaction.status"/></th>
                            <th></th>
                        </tr>
                        </thead>
                    </table>
                </div>

            </div>
        </div>
</main>


<div id="btc-tx-info-modal" class="modal fade order-info__modal modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="btcWallet.txDetails"/></h4>
            </div>
            <div class="modal-body">
                <div class="well">
                    <table id="btcTxInfoTable" class="table">
                        <tbody>
                        <tr>
                            <td><loc:message code="transaction.id"/></td>
                            <td id="info-id"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="myorders.datecreation"/></td>
                            <td id="info-dateCreation"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="refill.statusModificationDate"/></td>
                            <td id="info-status-date"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="admin.status"/></td>
                            <td id="info-status"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="refill.user"/></td>
                            <td id="info-user"></td>
                        </tr>
                        </tbody>
                    </table>
                    <div id="no-address">
                        <p class="red"><loc:message code="btcWallet.noAddress"/></p>
                    </div>
                </div>


                <div hidden>
                    <form id="createRefillForm">
                        <input name="txId">
                        <input name="address">
                    </form>
                </div>


                <div class="modal-footer">
                    <div class="order-info__button-wrapper">
                        <button id="create-refill" class="order-info__button" data-dismiss="modal">
                            <loc:message code="btcWallet.createRequest"/>
                        </button>
                        <button class="order-info__button" data-dismiss="modal">
                            <loc:message code="orderinfo.ok"/>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>




<span hidden id="confirmBtcMessage"><loc:message code="btcWallet.payment.prompt" arguments="${currency}"/></span>
<span hidden id="viewMessage"><loc:message code="merchants.invoice.viewConfirm"/></span>
<span hidden id="currencyName">${currency}</span>
<span hidden id="merchantName">${merchant}</span>
<%@include file='../fragments/modal/loading_modal.jsp' %>
<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
