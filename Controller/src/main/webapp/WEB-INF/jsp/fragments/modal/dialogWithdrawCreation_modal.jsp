<div class="modal fade merchant-output" id="dialog-withdraw-creation">
  <div style="font-size: 14px"
       class="modal-dialog modal-md">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title"><loc:message code="merchants.outputTitle"/></h4>
      </div>
      <div class="modal-body">
        <label class="alert-danger merchantError"><loc:message code="merchants.notAvaliablePayment"/></label>
        <div class="paymentInfo">
          <p><loc:message code="merchants.modalOutputHeader"/></p>
          <p><loc:message code="merchants.modalOutputCommission"/></p>
          <p><loc:message code="merchants.modalOutputCommissionMerchant"/></p>
          <p><loc:message code="merchants.modalOutputFinalSum"/></p>
          <p><loc:message code="merchants.modalOutputSumCommission"/></p>
        </div>
        <div id="merchant-commission-warning">
          <hr>
          <div class="red">
            <loc:message code="merchant.commission.warning"/>
          </div>
          <hr>
        </div>
        <div id="destination-input-wrapper"
             class="wallet_input">
          <label class="control-label" for="walletUid">
            <loc:message code="merchants.modalOutputWallet"/>
          </label>
          <input class="form-control" autofocus name="walletUid" type="text" id="walletUid">
          <label id="additional_field_name" class="control-label" for="address-tag">
           <%-- <loc:message code="merchants.modalOutputAddressTag"/>--%>
          </label>
          <input class="form-control" name="address-tag" type="text" id="address-tag">
        </div>
        <div class="timeoutWarning">
          <c:forEach var="warningCode" items="${warningCodeList}">
            <div><strong><loc:message code="${warningCode}"/></strong></div>
          </c:forEach>
        </div>
        <div id="message"
             style="width: 100%; border-radius: 5px; display: block; padding: 5px; background: green; color: white"></div>
      </div>
      <div class="modal-footer">
        <div id='request-money-operation-btns-wrapper'
             class="add__money__btns">
          <button id="continue-btn" disabled
                  class="btn btn-primary btn-md" type="button">
            <loc:message code="merchants.continue"/>
          </button>
          <button class="btn btn-danger btn-md" type="button" data-dismiss="modal">
            <loc:message code="merchants.dismiss"/>
          </button>
        </div>
        <div id="response-money-operation-btns-wrapper">
          <button class="btn btn-danger btn-md" type="button" data-dismiss="modal"><loc:message
                  code="merchants.close"/></button>
        </div>
      </div>
    </div>
    <!-- /.modal-content -->
  </div>
  <!-- /.modal-dialog -->
</div>