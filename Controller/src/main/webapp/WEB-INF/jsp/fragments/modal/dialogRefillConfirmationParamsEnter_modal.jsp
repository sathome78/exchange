<div hidden>
  <div id="bank-not-selected"><loc:message code="merchants.notSelected"/></div>
  <div id="enter-other-bank-phrase"><loc:message code="merchants.invoice.otherBank"/></div>
</div>

<div id="dialog-refill-confirmation-params-enter"
     class="modal fade merchant-output"
     style="margin-top: 20px">
  <c:choose>
    <c:when test="${not empty error}">
      <label class="alert-danger has-error">
        <loc:message code="${error}"/>
      </label>
    </c:when>
    <c:otherwise>
      <div class="row">
        <div id="credits-operation-info"
             class="credits-operation-info well col-md-6 col-md-offset-3 ">
          <div class="text-center">
            <h4><loc:message code="merchants.invoiceDetails.paymentDetails"/></h4>
          </div>
          <div class="row">
            <div class="col-md-6 col-md-offset-3">
              <div class="row">
                <div class="credits-operation-info__item clearfix">
                  <div class="col-md-6"><loc:message code="transaction.amount"/></div>
                  <div class="col-md-6 right"><span id="amount"></span> <span class="currency"></span></div>
                </div>
                <div class="credits-operation-info__item clearfix">
                  <div class="col-md-6"><loc:message code="merchants.invoiceDetails.bankName"/></div>
                  <div class="col-md-6" id="bank-name"></div>
                  <div hidden id="bank-id"></div>
                </div>
                <div class="credits-operation-info__item clearfix">
                  <div class="col-md-6"><loc:message code="merchants.invoiceDetails.bankAccount"/></div>
                  <div class="col-md-6" id="bank-account"></div>
                </div>
                <div class="credits-operation-info__item clearfix">
                  <div class="col-md-6"><loc:message code="merchants.invoiceDetails.bankRecipient"/></div>
                  <div class="col-md-6" id="bank-recipient"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div id="credits-operation-enter"
             class="credits-operation-info well col-md-6 col-md-offset-3 ">
          <div class="text-center">
            <h4><loc:message code="merchants.invoiceDetails.title"/></h4>
          </div>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="bank-data-list-confirm" class="input-block-wrapper__label">
                <loc:message code="merchants.invoiceConfirm.bankFrom"/>*</label>
            </div>
            <div class="col-md-5 ">
              <select id="bank-data-list-confirm"
                      class="credits-operation-enter__item form-control input-block-wrapper__input"
                      onchange="onSelectNewValueConfirm(this)">
              </select>
            </div>
            <div class="col-md-2 right input-block-wrapper__label-wrapper">
              <label for="bank-code" class="input-block-wrapper__label">
                <loc:message code="invoice.bankCode"/></label>
            </div>
            <div class="col-md-2 ">
              <input id="bank-code"
                     class="credits-operation-enter__item  form-control input-block-wrapper__input"
                     type="text"/>
            </div>
          </div>
          <div id="other-bank-wrapper"
               style="display: none"
               class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="other-bank" class="input-block-wrapper__label"></label>
            </div>
            <div class="col-md-5 ">
              <input id="other-bank"
                     class="credits-operation-enter__item  form-control input-block-wrapper__input"
                     type="text"/>
            </div>
            <div id="bankNameError"
                 class="col-md-4 left input-block-wrapper__error-wrapper" hidden>
              <p class="red"><loc:message code="merchants.error.bankNameInLatin"/></p>
            </div>
          </div>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="user-account" class="input-block-wrapper__label">
                <loc:message code="merchants.invoiceConfirm.userAccount"/>*</label>
            </div>
            <div class="col-md-5 ">
              <input id="user-account"
                     class="credits-operation-enter__item form-control input-block-wrapper__input"
                     type="text"/>
            </div>
            <div id="userAccountError"
                 class="col-md-4 left input-block-wrapper__error-wrapper" hidden>
              <p class="red"><loc:message code="merchants.error.accountDigitsOnly"/></p>
            </div>
          </div>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="user-full-name" class="input-block-wrapper__label">
                <loc:message code="merchants.invoiceDetails.userFullName"/>*</label>
            </div>
            <div class="col-md-5 ">
              <input id="user-full-name"
                     class="credits-operation-enter__item form-control input-block-wrapper__input"
                     type="text">
            </div>
            <div id="userFullNameConfirmError"
                 class="col-md-4 left input-block-wrapper__error-wrapper" hidden>
              <p class="red"><loc:message code="merchants.error.fullNameInLatin"/></p>
            </div>
          </div>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="remark" class="input-block-wrapper__label">
                <loc:message code="merchants.invoiceDetails.remark"/></label>
            </div>
            <div class="col-md-9">
              <textarea id="remark"
                        class="credits-operation-enter__item form-control textarea non-resize" name="remark">
              </textarea>
            </div>
          </div>


          <div class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="receipt-scan" class="input-block-wrapper__label">
                <loc:message code="merchants.invoiceConfirm.receiptScan"/>*</label>
            </div>
            <div id="receipt-scan-container" class="col-md-8">
              <a href="" class="col-sm-4">
                <img src="" class="img-responsive">
              </a>
              <input id="receipt-scan"
                     class="credits-operation-enter__item"
                     type="file">
            </div>
          </div>


          <div class="col-md-4 input-block-wrapper">
            <button id="invoiceSubmitConfirm"
                    class="btn btn-primary btn-md"
                    onmouseover="checkAllConfirmFields()"><loc:message code="admin.submit"/></button>
            <button id="invoiceCancelConfirm" class="btn btn-danger btn-md" data-dismiss="modal"><loc:message
                    code="admin.cancel"/></button>
          </div>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
</div>