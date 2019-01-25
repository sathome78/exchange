
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
    <script type="text/javascript" src="<c:url value='/client/js/admin-btcWallet/usdtWallet.js'/>"></script>
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
                    <span id="current-btc-balance">${btcBalance.toPlainString()}</span> BTC</strong></p>
                <c:choose>
                    <c:when test="${usdtBalances != null}">
                        <p class="green"><strong><loc:message code="admin.externalWallets.totalWalletBalance"/>
                            <span id="current-btc-balance">${usdtBalances.balance.toPlainString()}</span> USDT</strong></p>
                        <p class="green"><strong><loc:message code="admin.externalWallets.reservedWalletBalance"/>
                            <span id="current-btc-balance">${usdtBalances.reserved.toPlainString()}</span> USDT</strong></p>
                        <p class="green"><strong><loc:message code="admin.externalWallets.frozenBalance"/>
                            <span id="current-btc-balance">${usdtBalances.frozen.toPlainString()}</span> USDT</strong></p>
                    </c:when>
                    <c:otherwise>
                        <p class="green"><strong>Error getting ${currency} balances</strong></p>
                    </c:otherwise>
                </c:choose>
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
                            <th><loc:message code="btcWallet.addressFrom"/></th>
                            <th><loc:message code="btcWallet.recipientAddress"/></th>
                            <th><loc:message code="btcWallet.blockhash"/></th>
                            <th><loc:message code="btcWallet.history.amount"/></th>
                            <th><loc:message code="btcWallet.history.propertyId"/></th>
                            <th><loc:message code="btcWallet.history.fee"/></th>
                            <th><loc:message code="btcWallet.history.confirmations"/></th>
                            <th></th>
                            <th></th>
                            <th></th>
                        </tr>
                        </thead>
                    </table>

                    <br>
                    <div class="text-center"><h4><loc:message code="btcWallet.blockedAdresses"/></h4></div>
                    <table id="blockedAdresses">
                        <thead>
                        <tr>
                            <th><loc:message code="btcWallet.address"/></th>
                            <th><loc:message code="admin.email"/></th>
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
