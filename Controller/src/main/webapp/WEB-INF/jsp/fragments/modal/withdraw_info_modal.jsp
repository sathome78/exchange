<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 08.02.2017
  Time: 16:41
  To change this template use File | Settings | File Templates.
--%>
<div id="withdraw-info-modal" class="modal fade order-info__modal modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="withdraw.infoModal.title"/></h4>
            </div>
            <div class="modal-body">
                <div class="well">
                    <table id="withdrawInfoTable" class="table">
                        <tbody>
                        <tr>
                            <td><loc:message code="transaction.id"/></td>
                            <td id="info-transactionId"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="transaction.currency"/></td>
                            <td id="info-currency"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="myorders.amountwithcommission"/></td>
                            <td id="info-amount"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="transaction.commissionAmount"/></td>
                            <td id="info-commissionAmount"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="withdrawal.amountToWithdraw"/></td>
                            <td id="info-ammountToWithdraw"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="withdrawal.status"/></td>
                            <td id="info-status"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="withdrawal.statusModificationDate"/></td>
                            <td id="info-status-date"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="merchants.withdrawDetails.recipientBank"/></td>
                            <td id="info-bankRecipient"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="merchants.withdrawDetails.recipientAccount"/></td>
                            <td id="info-wallet"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="merchants.withdrawDetails.destinationTag"/></td>
                            <td id="info-destination-tag"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="merchants.withdrawDetails.recipientFullName"/></td>
                            <td id="info-userFullName"></td>
                        </tr>
                        <tr>
                            <td><loc:message code="merchants.invoiceDetails.remark"/></td>
                            <td id="info-remark"><textarea class="textarea non-resize" readonly></textarea></td>
                        </tr>
                        </tbody>
                    </table>
                    <div>
                        <img id="qr-code-id" src=""/><br>
                        <input id="qr-url-id" class="copyable" onclick="copyText()" style="width: 100%;" readonly>
                    </div>
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