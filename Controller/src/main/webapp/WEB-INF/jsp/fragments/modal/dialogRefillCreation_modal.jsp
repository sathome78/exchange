<div class="modal fade merchant-input" id="dialog-refill-creation">
    <div style="font-size: 14px; overflow:auto"
         class="modal-dialog modal-lg">
        <div class="modal-content" style="overflow:auto">
            <div class="modal-header">
                <button type="button" class="close" id="assertInputPayment" data-dismiss="modal"
                        aria-hidden="true">&times;
                </button>
                <h4 class="modal-title"><loc:message code="merchants.inputTitle"/></h4>
            </div>
            <div class="modal-body">
                <div id="amount-info-wrapper">
                    <label class="alert-danger merchantError"><loc:message
                            code="merchants.notAvaliablePayment"/></label>
                    <div class="paymentInfo">
                        <p><loc:message code="merchants.modalInputHeader"/></p>
                        <p id="payment-info-commission-merchant"><loc:message
                                code="merchants.modalInputCommissionMerchant"/></p>
                        <p><loc:message code="merchants.modalInputCommission"/></p>
                        <p><loc:message code="merchants.modalInputFinalSum"/></p>
                    </div>
                    <div id="merchant-commission-warning">
                        <hr>
                        <div class="red">
                            <span id="merchant-warnings"></span>
                        </div>
                        <hr>
                    </div>
                </div>
                <div class="timeoutWarning">
                    <c:forEach var="warningCode" items="${warningCodeList}">
                        <div><strong><loc:message code="${warningCode}"/></strong></div>
                    </c:forEach>
                </div>

                <div id="message"
                     style="overflow:auto; border-radius: 5px; display: block; padding: 5px; background: rgba(111, 111, 111, 0.83); color: white">
                </div>
                <div>
                    <img id="payment-qr" src="" />
                </div>
                <div></div>
            </div>
            <div class="modal-footer">
                <div id='request-money-operation-btns-wrapper'
                     class="add__money__btns">
                    <button id="continue-btn"
                            class="btn btn-primary btn-md" type="button"><loc:message
                            code="merchants.continue"/></button>
                    <button class="btn btn-danger btn-md" type="button" data-dismiss="modal"><loc:message
                            code="merchants.dismiss"/></button>
                </div>
                <div id='simple-invoice-btns-wrapper' class="add__money__btns" style="display: none">
                    <div class="pull-left">
                        <button id="request-confirm-btn" class="btn btn-primary btn-md" type="button"><loc:message
                                code="refill.invoice.paid"/></button>
                        <button id="request-revoke-btn" class="btn btn-danger btn-md" type="button"><loc:message
                                code="merchants.invoice.revoke"/></button>
                    </div>
                </div>

                <div id="response-money-operation-btns-wrapper">
                    <button id="dialog-refill-creation-close" class="btn btn-danger btn-md" type="button"
                            data-dismiss="modal"><loc:message
                            code="merchants.close"/></button>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>
<!-- /.modal -->
