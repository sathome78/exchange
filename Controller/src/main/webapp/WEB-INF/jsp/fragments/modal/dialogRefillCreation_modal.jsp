<div class="modal fade merchant-input" id="dialog-refill_creation">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" id="assertInputPayment" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title"><loc:message code="merchants.inputTitle"/></h4>
            </div>
            <div class="modal-body">
                <label class="alert-danger merchantError"><loc:message code="merchants.notAvaliablePayment"/></label>
                <div class="paymentInfo">
                    <p><loc:message code="merchants.modalInputHeader"/></p>
                    <p><loc:message code="merchants.modalInputCommission"/></p>
                    <p><loc:message code="merchants.modalInputFinalSum"/></p>
                    <p><loc:message code="merchants.warn"/></p>
                </div>
                <c:if test="${not empty warningCodeTimeout}">
                    <div class="timeoutWarning">
                        <strong><loc:message code="${warningCodeTimeout}"/></strong>
                    </div>
                </c:if>
                <div class="timeoutWarning">
                    <c:if test="${not empty warningCodeTimeout}">
                            <strong><loc:message code="${warningCodeTimeout}"/></strong>
                    </c:if>
                </div>
                <div id="message" style="width: 100%; border-radius: 5px; display: block; padding: 5px; background: green; color: white"></div>
                <div id="paymentQR">
                </div>
            </div>
            <div class="modal-footer">
                <div id = 'request-money-operation-btns-wrapper'
                     class="add__money__btns">
                    <button class="modal-button" type="button" data-dismiss="modal"><loc:message code="merchants.dismiss"/></button>
                    <button class="modal-button" type="button" id="continue-btn" ><loc:message code="merchants.continue"/></button>
                </div>
                <div id="response-money-operation-btns-wrapper">
                    <button class="modal-button" type="button" data-dismiss="modal"><loc:message code="merchants.close"/></button>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
