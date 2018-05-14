<div class="modal fade merchant-input" id="dialog-transfer-creation">
  <div style="font-size: 14px"
       class="modal-dialog modal-md">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" id="assertInputPayment" data-dismiss="modal"
                aria-hidden="true">&times;</button>
        <h4 class="modal-title"><loc:message code="wallets.transferTitle"/></h4>
      </div>
      <div class="modal-body">
        <div id="amount-info-wrapper">
          <div class="transferInfo">
            <p><loc:message code="merchants.modalTransferHeader"/></p>
            <p><loc:message code="merchants.modalTransferCommission"/></p>
            <p><loc:message code="merchants.modalTransferFinalSum"/></p>
          </div>
        </div>
        <div id="recipient-input-wrapper"
             class="wallet_input">
          <label class="control-label" for="recipient">
            <loc:message code="merchants.modalTransferRecipient"/>
          </label>
          <input class="form-control" autofocus type="text" id="recipient" value="" autocomplete="off">
        </div>
        <div class="timeoutWarning">
          <c:forEach var="warningCode" items="${warningCodeList}">
            <div><strong><loc:message code="${warningCode}"/></strong></div>
          </c:forEach>
        </div>

        <div id="message"
             style="width: 100%; border-radius: 5px; display: block; padding: 5px; background: rgba(111, 111, 111, 0.83); color: white" hidden></div>
        <div id="hash"
             style="width: 100%; border-radius: 5px; display: block; padding: 5px; background: rgba(111, 111, 111, 0.83); color: white" hidden>
          Voucher code: <b id="hash_field"></b>
        </div>
        <div id="payment-qr">
        </div>
      </div>
      <div class="modal-footer">
        <div id='request-money-operation-btns-wrapper'
             class="add__money__btns">
          <button id="continue-btn"
                  class="btn btn-primary btn-md" type="button"><loc:message code="merchants.continue"/></button>
          <button class="btn btn-danger btn-md" type="button" data-dismiss="modal"><loc:message
                  code="merchants.dismiss"/></button>
        </div>
        <div id="response-money-operation-btns-wrapper">
          <button class="btn btn-danger btn-md" type="button" data-dismiss="modal"><loc:message
                  code="merchants.close"/></button>
        </div>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div>
<!-- /.modal -->
