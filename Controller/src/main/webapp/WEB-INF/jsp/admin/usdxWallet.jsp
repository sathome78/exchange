
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
                        <label for="accountNameUsdxWallet" class="input-block-wrapper__label">Account name</label>
                    </div>
                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                        <input name="accountName" class="form-control input-block-wrapper__input" id="accountNameUsdxWallet"/>
                    </div>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                        <label for="amountUsdxWallet" class="input-block-wrapper__label">Amount</label>
                    </div>
                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                        <input name="amount" class="form-control input-block-wrapper__input" id="amountUsdxWallet"/>
                    </div>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                        <label for="currencyUsdxWallet" class="input-block-wrapper__label">Сurrency</label>
                    </div>
                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                        <input name="currency" class="form-control input-block-wrapper__input" id="currencyUsdxWallet" value="LHT" readonly/>
                    </div>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                        <label for="memoUsdxWallet" class="input-block-wrapper__label">Memo (Optional)</label>
                    </div>
                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                    <input name="memo" class="form-control input-block-wrapper__input" id="memoUsdxWallet" placeholder="Optional field"/>
                    </div>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                        <label for="customDataUsdxWallet" class="input-block-wrapper__label">Custom data (Optional)</label>
                    </div>
                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                        <input name="customData" class="form-control input-block-wrapper__input" id="customDataUsdxWallet" placeholder="Optional field"/>
                    </div>
                </div>

                    <div class="col-md-1"></div>
                    <button id="button-reset-trans-fields" class="col-md-2 btn btn-warning" style="margin-top:15px; margin-bottom:15px;">
                        <loc:message code="admin.reset"/>
                    </button>

                    <div class="col-md-2"></div>

                    <button type="button" id="button-usdx-send-trans" class="col-md-6 btn btn-primary" style="margin-top:15px; margin-bottom:15px;" disabled>
                        <loc:message code="admin.submit"/>
                    </button>

                    <div class="col-md-1"></div>

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

<div id="usdx-password-modal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="btcWallet.password.title"/></h4>
            </div>
            <div class="modal-body">
                <p><loc:message code="btcWallet.password.prompt"/></p>
                <form id="usdx-password-form" class="form_full_width form_auto_height">
                    <div class="input-block-wrapper">
                        <div class="col-md-12 input-block-wrapper__input-wrapper">
                            <input name="password" class="input-block-wrapper__input admin-form-input" type="text" id="usdx-password">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <button id="button-send-usdx-wallet-transaction-pass" class="delete-order-info__button blue-box" type="button"><loc:message code="admin.submit"/></button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div id="usdx-transaction-info-modal" class="modal fade order-info__modal modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="btcWallet.txDetails"/></h4>
            </div>
            <div class="modal-body">
                <div class="well">
                    <table id="usdxTxInfoTable" class="table">
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
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
