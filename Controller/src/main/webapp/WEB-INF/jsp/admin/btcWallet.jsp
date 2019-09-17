<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 23.09.2016
  Time: 12:30
  To change this template use File | Settings | File Templates.
--%>
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
    <script type="text/javascript" src="<c:url value='/client/js/admin-btcWallet/btcWallet.js'/>"></script>
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
                <p class="green"><strong><loc:message code="btcWallet.balance"/>
                    <span id="current-btc-balance">${walletInfo.balance}</span> ${currency}</strong></p>
                <c:if test="${not empty walletInfo.confirmedNonSpendableBalance}">
                    <p><strong><loc:message code="btcWallet.confirmedNonSpendableBalance"/>
                        <span id="current-btc-balance">${walletInfo.confirmedNonSpendableBalance}</span> ${currency}</strong></p>
                </c:if>
                <c:if test="${not empty walletInfo.unconfirmedBalance}">
                    <p class="lightblue"><strong><loc:message code="btcWallet.unconfirmedBalance"/>
                        <span id="current-btc-unconfirmed-balance">${walletInfo.unconfirmedBalance}</span> ${currency}</strong></p>
                </c:if>

            </div>
            <sec:authorize access="hasAuthority('${admin_manageBtcWallet}')">
            <div id="walletMenu" class="buttons text-center">
                <button class="active adminForm-toggler blue-box">
                    <loc:message code="btcWallet.history.title"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="btcWallet.send.title"/> ${currency}
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="btcWallet.receive.title"/> ${currency}
                </button>

            </div>
            </sec:authorize>

            <div class="tab-content">
                <div id="panel1" class="tab-pane active">
                    <div class="text-center"><h4><loc:message code="btcWallet.history.title"/></h4></div>
                    <sec:authorize access="hasAuthority('${admin_manageBtcWallet}')">
                        <button id="check-payments-btn" class="blue-box"><loc:message code="btcWallet.checkPayment.title"/></button>
                    </sec:authorize>
                    <button class="btc_show_data blue-box">
                        <loc:message code="admin.datatable.showData"/> ${currency}
                    </button>
                    <button id="btc_get_transaction" class="blue-box">
                        <loc:message code="admin.datatable.getTransaction"/>
                    </button>
                    <table id="txHistory">
                        <thead>
                        <tr>
                            <th><loc:message code="btcWallet.history.time"/></th>
                            <th><loc:message code="btcWallet.history.txid"/></th>
                            <th><loc:message code="btcWallet.history.category"/></th>
                            <th><loc:message code="btcWallet.address"/></th>
                            <th><loc:message code="btcWallet.blockhash"/></th>
                            <th><loc:message code="btcWallet.history.amount"/></th>
                            <th><loc:message code="btcWallet.history.fee"/></th>
                            <th><loc:message code="btcWallet.history.confirmations"/></th>
                            <th></th>
                            <th></th>
                            <th></th>
                        </tr>
                        </thead>
                    </table>
                </div>
                <sec:authorize access="hasAuthority('${admin_manageBtcWallet}')">
                <div id="panel2" class="tab-pane">
                    <div class="text-center"><h4><loc:message code="btcWallet.send.title"/> ${currency}</h4></div>
                    <div class="col-md-8 col-md-offset-2">
                        <div class="col-md-11">
                            <div style="float: right; margin-bottom: 20px"><button id="addPayment" class="text-center btn btn-default">
                                <span style="font-size: 2rem; margin: 0" class="fa fa-plus"></span></button></div>
                        </div>

                        <form id="send-btc-form" class="form_full_width">

                            <div>
                                <div class="col-md-11">
                                    <div id="payments">
                                        <div id="payment_0" class="btcWalletPayment">
                                            <div class="input-block-wrapper">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class=" input-block-wrapper__label"><loc:message code="btcWallet.address"/></label>
                                                </div>
                                                <div class="col-md-6 input-block-wrapper__input-wrapper">
                                                    <input id="address_0" name="address" class="input-address input-block-wrapper__input admin-form-input"/>
                                                </div>
                                                <div style="padding-right: 0" class="rm-button-placeholder col-md-2 input-block-wrapper__input-wrapper"></div>
                                            </div>
                                            <div class="input-block-wrapper">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label"><loc:message code="btcWallet.amount" arguments="${currency}"/></label>
                                                </div>
                                                <div class="col-md-5 input-block-wrapper__input-wrapper">
                                                    <input id="amount_0" name="amount" type="number" class="input-amount input-block-wrapper__input admin-form-input"/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div id="fee-div" class="input-block-wrapper">
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label for="input-fee" class="input-block-wrapper__label"><loc:message code="btcWallet.fee" arguments="${currency}"/></label>
                                        </div>
                                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                                            <input id="input-fee" readonly disabled class="input-block-wrapper__input admin-form-input"/>
                                        </div>
                                    </div>
                                    <div class="input-block-wrapper">
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label for="input-fee-actual" class="input-block-wrapper__label"><loc:message code="btcWallet.actualFee" arguments="${currency}"/></label>
                                        </div>
                                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                                                <input id="input-fee-actual" type="number" step="any" class="input-block-wrapper__input admin-form-input"/>
                                        </div>
                                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                                            <button id="submitChangeFee" class="btn btn-sm btn-primary"><loc:message code="btcWallet.changeFee"/></button>
                                        </div>
                                    </div>
                                    <c:if test="${!rawTxEnabled}">
                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="subtract-fee-from-amount" class="input-block-wrapper__label">
                                                    <loc:message code="btcWallet.subtractFeeFromAmount"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <span id="subtract-fee-from-amount"></span>
                                            </div>
                                        </div>
                                    </c:if>



                                    <div id="btc-wallet-buttons">
                                        <div class="col-md-12">
                                            <button id="submit-btc" class="delete-order-info__button blue-box"
                                                    type="button"><loc:message code="admin.submit"/></button>
                                            <button id="reset-btc" class="delete-order-info__button blue-box"
                                                    type="button"><loc:message code="admin.reset"/></button>

                                        </div>
                                    </div>

                                </div>


                            </div>


                        </form>
                        <span hidden id="enable-raw-tx"><c:out value="${rawTxEnabled}"/></span>


                        <div hidden id="rm-button-template">
                            <div class="rm-button-container pull-right">
                                <button class="remove-payment text-center btn btn-default input-block-wrapper__input admin-form-input">
                                    <span style="font-size: 2rem" class="fa fa-minus"></span></button>
                            </div>

                        </div>

                    </div>
                </div>
                    <div hidden>
                        <form id="tx-fee-form">
                            <input name="fee" type="number" step="any" class="input-block-wrapper__input admin-form-input"/>
                        </form>
                    </div>
                    <div id="panel3" class="tab-pane">
                        <div class="text-center"><h4><loc:message code="btcWallet.receive.title"/> ${currency}</h4></div>
                        <div class="col-md-offset-2 col-md-2">
                            <button id="generate-address"><loc:message code="refill.generate"/> </button>
                            <button id="copy-address"><loc:message code="refill.copy"/> </button>
                        </div>
                        <div class="well text-center col-md-5">
                            <span id="refill-address"></span>
                            <div id = address-qr></div>
                        </div>

                    </div>
                </sec:authorize>
            </div>
        </div>
</main>

<sec:authorize access="hasAuthority('${admin_manageBtcWallet}')">

<div id="password-modal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="btcWallet.password.title"/></h4>
            </div>
            <div class="modal-body">
                <p><loc:message code="btcWallet.password.prompt"/></p>
                <form id="password-form" class="form_full_width form_auto_height">
                    <div class="input-block-wrapper">
                        <div class="col-md-12 input-block-wrapper__input-wrapper">
                            <input name="password" class="input-block-wrapper__input admin-form-input" type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <button id="submit-wallet-pass" class="delete-order-info__button blue-box" type="button"><loc:message code="admin.submit"/></button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div id="payment-confirm-modal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="btcWallet.payment.dialog.title" arguments="${currency}"/></h4>
            </div>
            <div class="modal-body">
                <p id="btc-confirm-prompt"><loc:message code="btcWallet.payment.prompt" arguments="${currency}"/></p>
            </div>
            <div class="modal-footer">
                <div class="delete-order-info__button-wrapper">
                    <button id="confirm-btc-submit" class="delete-order-info__button">
                        <loc:message code="admin.submit"/></button>
                    <button class="delete-order-info__button" data-dismiss="modal">
                        <loc:message code="admin.cancel"/></button>
                </div>
            </div>
        </div>
    </div>
</div>

    <div id="btc-send-result-modal" class="modal fade modal-form-dialog" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                </div>
                <div class="modal-body">
                    <div class="well">
                        <table id="btcResultInfoTable" class="table">
                            <tbody>
                            <script type="text/template" id="results-table_row">
                                <@var c = error === null ? "green" : "red";@>
                                <tr class="results-table__row <@=c@>">
                                    <td><@=address@></td>
                                    <td><@=amount@></td>
                                    <td>
                                        <@=
                                        (function(){
                                        if (txId !== null) {
                                            return txId;
                                        } else if (error !== null) {
                                            return error;
                                        }
                                        return '';
                                        })()
                                        @>
                                    </td>
                                </tr>
                            </script>
                            </tbody>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <div class="order-info__button-wrapper">
                            <button class="order-info__button" data-dismiss="modal">
                                <loc:message code="orderinfo.ok"/>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="btc-prepare-raw-modal" class="modal fade modal-form-dialog" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                </div>
                <div class="modal-body">
                    <div id="raw-tx-content" class="well">
                        <div class="row" id="raw-tx-payments">
                            <div class="col-md-12"><strong><loc:message code="btcWallet.raw.payments"/> </strong></div>
                            <div class="col-md-12"><ul></ul></div>

                        </div>
                        <div class="row">
                            <div class="col-md-3">
                                <strong><loc:message code="btcWallet.feeRate" arguments="${currency}"/>  </strong>
                            </div>
                            <div class="col-md-6">
                                <input class="form-control" id="fee-rate-raw">
                            </div>
                            <div class="col-md-2">
                                <button id="change-fee-raw" class="btn btn-sm btn-primary"><loc:message code="btcWallet.changeFee"/></button>
                            </div>
                        </div>
                        <div class="row red text-center">
                            <strong><loc:message code="btcWallet.totalFee"/> <span id="fee-amount-raw"></span> ${currency}</strong>
                        </div>

                    </div>
                    <div class="modal-footer">
                        <div class="order-info__button-wrapper">
                            <button id="submit-raw-tx">
                                <loc:message code="admin.submit"/>
                            </button>
                            <button class="order-info__button" data-dismiss="modal">
                                <loc:message code="admin.cancel"/>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="btc-check-payments-modal" class="modal fade modal-form-dialog" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <p style="font-size: 1.4rem"><loc:message code="btcWallet.checkPayment.modal.body"/></p>
                    </div>
                    <div class="row">
                        <input class="form-control" id="start-block-hash">
                    </div>

                </div>
                    <div class="modal-footer">
                        <div class="order-info__button-wrapper">
                            <button id="submit-check-payments-btn">
                                <loc:message code="admin.submit"/>
                            </button>
                            <button class="order-info__button" data-dismiss="modal">
                                <loc:message code="admin.cancel"/>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="btc-get-transaction-modal" class="modal fade modal-form-dialog" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <p style="font-size: 1.4rem">Type transaction id:</p>
                    </div>
                    <div class="row">
                        <input class="form-control" id="input-get-transaction-by-hash">
                    </div>

                </div>
                <div class="modal-footer">
                    <div class="order-info__button-wrapper">
                        <div class="row">
                            <button id="submit-get-transaction-btn">
                                <loc:message code="admin.submit"/>
                            </button>
                            <button class="order-info__button" data-dismiss="modal">
                                <loc:message code="admin.cancel"/>
                            </button>
                        </div>

                        <div class="row">
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label" id="label-btc_trans_time">Time</label>
                                </div>

                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input class="input-block-wrapper__input admin-form-input" id="btc_trans_time" readonly disabled/>
                                </div>
                            </div>

                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label" id="label-btc_trans_hash">Hash</label>
                                </div>

                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input class="input-block-wrapper__input admin-form-input" id="btc_trans_hash" readonly disabled/>
                                </div>
                            </div>

                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label" id="label-btc_trans_amount">Amount</label>
                                </div>

                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input class="input-block-wrapper__input admin-form-input" id="btc_trans_amount" readonly disabled/>
                                </div>
                            </div>

                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label" id="label-btc_trans_address">Address</label>
                                </div>

                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input class="input-block-wrapper__input admin-form-input" id="btc_trans_address" readonly disabled/>
                                </div>
                            </div>

                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label" id="label-btc_trans_fee_amount">Fee</label>
                                </div>

                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input class="input-block-wrapper__input admin-form-input" id="btc_trans_fee_amount" readonly disabled/>
                                </div>
                            </div>

                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label" id="label-btc_trans_confirmation">Confirmation</label>
                                </div>

                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input class="input-block-wrapper__input admin-form-input" id="btc_trans_confirmation" readonly disabled/>
                                </div>
                            </div>

                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label" id="label-btc_trans_comment">Comment</label>
                                </div>

                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input class="input-block-wrapper__input admin-form-input" id="btc_trans_comment" readonly disabled/>
                                </div>
                            </div>

                            <table id="btcTxDetailInfoTable" class="table">
                                <thead class="thead-light">
                                <tr>
                                    <th scope="col">Address</th>
                                    <th scope="col">Category</th>
                                    <th scope="col">Amount</th>
                                    <th scope="col">Fee</th>
                                </tr>
                                </thead>
                                <tbody>

                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </div>

</sec:authorize>


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
