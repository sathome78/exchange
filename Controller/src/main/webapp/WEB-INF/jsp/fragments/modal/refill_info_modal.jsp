<div id="refill-info-modal" class="modal fade order-info__modal modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="refill.infoModal.title"/></h4>
            </div>
            <div class="modal-body">
                <div class="well">
                    <table id="refillInfoTable" class="table">
                        <tbody>
                        <tr>
                            <td><loc:message code="transaction.currency"/></td>
                            <td id="info-currency"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="transaction.amount"/></td>
                            <td id="info-amount"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="transaction.commissionAmount"/></td>
                            <td id="info-commissionAmount"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="refill.paymentAmount"/></td>
                            <td id="info-paymentAmount"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="refill.status"/></td>
                            <td id="info-status"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="refill.statusModificationDate"/></td>
                            <td id="info-status-date"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="merchants.refillDetails.recipientBank"/></td>
                            <td id="info-bankRecipient"></td>
                        </tr>
                        <tr><td></td><td></td></tr>
                        <tr>
                            <td><loc:message code="refill.payerData"/></td>
                            <td id="info-payer-data"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="refill.hash"/></td>
                            <td id="info-hash"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="refill.merchantTransactionId"/></td>
                            <td id="info-merchant-transaction-id"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="merchants.invoiceDetails.remark"/></td>
                            <td id="info-remark"><textarea class="textarea non-resize" readonly></textarea></td>
                        </tr>
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