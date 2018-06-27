<script>
  var bankId = -1;
  var $dialog;
  $(function () {
    $dialog = $("#dialog-refill-detailed-params-enter");
    $($dialog).find(".credits-operation-enter__item").on("change input", function (event) {
      var $elem = $(event.currentTarget);
      var checkResult = checkField($elem);
      if (checkResult) {
        $('#invoiceSubmit').prop('disabled', false);
      } else {
        $('#invoiceSubmit').prop('disabled', true);
      }
    });
  });

  function resetForm() {
    [].forEach.call($dialog.find(".credits-operation-enter__item"), function (item) {
      $(item).val("");
    });
    $dialog.find("#bank-data-list").val(-1);
    $dialog.find("#bank-data-list").change();
    checkAllFields();
  }

  function onSelectNewValue(select) {
    bankId = $('#bank-data-list').val();
    var $bankInfoOption = $(select).find("option[value=" + bankId + "]");
    var $infoWrapper = $dialog.find("#credits-operation-info");
    $infoWrapper.find("#bank-id").html($bankInfoOption.data("bank-id"));
    $infoWrapper.find("#bank-name").html($bankInfoOption.data("bank-name"));
    $infoWrapper.find("#bank-code").html($bankInfoOption.data("bank-code"));
    $infoWrapper.find("#bank-account").html($bankInfoOption.data("bank-account"));
    $infoWrapper.find("#bank-recipient").html($bankInfoOption.data("bank-recipient"));
    $infoWrapper.find("#bank-details").html($bankInfoOption.data("bank-details"));
  }

  function checkAllFields() {
    var result = true;
    [].forEach.call($dialog.find(".credits-operation-enter__item"), function (item) {
      result = checkField($(item)) && result;
    });
    if (result) {
      $('#invoiceSubmit').prop('disabled', false);
    } else {
      $('#invoiceSubmit').prop('disabled', true);
    }
  }

  function checkField($elem) {
    const NAME_REGEX = /^[a-zA-Z]([-']?[a-zA-Z]+)*( [a-zA-Z]([-']?[a-zA-Z]+)*)*$/;

    var result = true;
    var elemId = $elem.attr("id");

    if ($elem.closest(".input-block-wrapper").css("display") == 'none') {
      result = true;
    } else if (elemId == "bank-data-list") {
      if (bankId == -1) {
        result = false;
      }
    } else if (elemId == "user-full-name") {
      result = validateString($elem, NAME_REGEX, $('#userFullNameError'), false);
    }
    return result;
  }

</script>

<div id="dialog-refill-detailed-params-enter"
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
                <div id="additional-wrapper"
                     class="credits-operation-info__item clearfix">
                  <div class="col-md-6"><loc:message code="merchants.addition"/></div>
                  <div class="col-md-6 right"><span id="additional"></span> <span class="currency"></span></div>
                </div>
                <div class="credits-operation-info__item clearfix">
                  <div class="col-md-6"><loc:message code="transaction.amount"/></div>
                  <div class="col-md-6 right"><span id="amount"></span> <span class="currency"></span></div>
                </div>
                <div class="credits-operation-info__item clearfix">
                  <div class="col-md-6"><loc:message code="transaction.commission"/></div>
                  <div class="col-md-6 right"><span id="commission-percent"></span></div>
                </div>
                <div class="credits-operation-info__item clearfix">
                  <div class="col-md-6"><loc:message code="transaction.commissionAmount"/></div>
                  <div class="col-md-6 right"><span id="commission-amount"></span> <span class="currency"></span></div>
                </div>
                <div class="credits-operation-info__item clearfix">
                  <div class="col-md-6"><loc:message code="dashboard.amountwithcommission"/></div>
                  <div class="col-md-6 right"><span id="total-amount"></span> <span class="currency"></span></div>
                </div>
                <div class="credits-operation-info__item clearfix">
                  <div class="col-md-6"><loc:message code="merchants.invoiceDetails.bankName"/></div>
                  <div class="col-md-6" id="bank-name">&nbsp;<span class="col-md-9 normal" id="bank-code"></span></div>
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
                <div class="credits-operation-info__item clearfix">
                  <div class="col-md-6"><loc:message code="merchants.invoiceDetails.additional"/></div>
                  <div class="col-md-6" id="bank-details"></div>
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
              <label for="bank-data-list" class="input-block-wrapper__label">
                <loc:message code="merchants.invoiceDetails.bank"/>*</label>
            </div>
            <div class="col-md-5 ">
              <select id="bank-data-list"
                      class="credits-operation-enter__item form-control input-block-wrapper__input"
                      onchange="onSelectNewValue(this)">
              </select>
            </div>
          </div>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="user-full-name" class="input-block-wrapper__label">
                <loc:message code="merchants.withdrawDetails.recipientFullName"/>*</label>
            </div>
            <div class="col-md-5 ">
              <input id="user-full-name"
                     class="credits-operation-enter__item form-control input-block-wrapper__input"
                     type="text">
            </div>
            <div id="userFullNameError"
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
          <div class="col-md-4 input-block-wrapper">
            <button id="invoiceSubmit"
                    class="btn btn-primary btn-md"
                    onmouseover="checkAllFields()"><loc:message code="admin.submit"/></button>
            <button id="invoiceCancel" class="btn btn-danger btn-md" data-dismiss="modal"><loc:message
                    code="admin.cancel"/></button>
          </div>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
</div>