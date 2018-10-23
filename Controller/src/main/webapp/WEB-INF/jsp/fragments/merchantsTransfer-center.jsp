<div id="merchants-transfer-center">
  <h4><loc:message code="merchants.transferTitle"/></h4>
  <c:if test="${error!=null}">
    <label class="alert-danger has-error">
      <loc:message code="${error}"/>
    </label>
  </c:if>
  <c:choose>
    <c:when test="${empty merchantCurrencyData || accessToOperationForUser eq false}">
      <p class="red noMerchants"><loc:message code="merchant.operationNotAvailable"/></p>
    </c:when>
    <c:otherwise>
      <div class="row">
        <div hidden id="operationType">${payment.operationType}</div>
        <div class="form-horizontal withdraw__money">
          <div class="input-block-wrapper clearfix">
            <div class="col-md-4 input-block-wrapper__label-wrapper" style="width:225px; padding-left: 0">
              <label style="font-size: 15px" for="currencyFull" class="input-block-wrapper__label"><loc:message
                      code="merchants.currencyforoutput"/></label>
            </div>
            <div class="col-md-8 input-block-wrapper__input-wrapper">
              <input id="currencyFull"
                     style="float: left; width: auto"
                     class="form-control input-block-wrapper__input"
                     readonly
                     value="<c:out value='${wallet.name} ${balance}'/>"/>
            </div>
            <div style="float: left; height: 12px;  width: 408px; text-align: left; margin-right: 10px; padding-left: 240px">
              <span id="allSum" style="cursor: pointer; text-decoration: underline; size: 6px;">
                <loc:message code="merchants.addAll"/></span>
            </div>
          </div>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-4 input-block-wrapper__label-wrapper" style="width:225px; padding-left: 0">
              <label style="font-size: 15px" for="sum"><loc:message code="withdrawal.amount"/></label>
            </div>
            <div style="width: auto; " class="col-md-8 input-block-wrapper__input-wrapper">
              <input id="sum"
                     class="form-control input-block-wrapper__input numericInputField"
                     data-currency-name="${currency.name}"
                     data-max-amount="${balance}"
                     data-min-amount
                     data-system-min-sum="${minTransferSum}"
                     data-scale-of-amount="${scaleForCurrency}"
                     data-min-sum-noty-id="#min-sum-notification"
                     data-submit-button-id=".start-transfer"
                     <c:if test="${checkingZeroBalance}">disabled</c:if>
              />
            </div>
            <div class="col-md-6 input-block-wrapper__label-wrapper">
              <div id="min-sum-notification" class="red"><loc:message code="mercnahts.output.minSum"/>
                <strong> ${currency.name} <span id="minSum"><%--<fmt:formatNumber value="${minTransferSum}"
                                                                  pattern="###,##0.00######"/>--%></span>
                </strong>
              </div>
            </div>
          </div>
          <b hidden id="buttonMessage"><loc:message code="merchants.transfer"/></b>
          <div id="merchantList">
            <br>
            <c:forEach var="merchantCurrency" items="${merchantCurrencyData}">
              <c:forEach var="merchantImage" items="${merchantCurrency.listMerchantImage}">
                <div style=" width: 100%; height: 98px; border: 1px solid #d5d5d5; padding: 10px; border-radius: 10px">
                  <div style="float: left; height: 20px;  width: 183px; text-align: right; margin-right: 47px">
                    <img class="img-thumbnail" src="${merchantImage.image_path}"
                         style="width: 168px; height: 52px; margin-right: 10px"/>
                    <div style="float: left; height: 20px;  width: 448px; text-align: left; margin-right: 10px; padding-left: 228px">
                        <span><loc:message code="mercnahts.output.minSum"/></span>
                        <span>${minTransferSum.max(merchantCurrency.minSum).stripTrailingZeros().toPlainString()}</span>
                    </div>
                  </div>
                  <button class="start-transfer btn btn-primary btn-lg start-button"
                          type="button"
                          data-currency-id="${currency.getId()}"
                          data-currency-name="${currency.getName()}"
                          data-merchant-id="${merchantCurrency.merchantId}"
                          data-merchant-name="${merchantCurrency.name}"
                          data-merchant-min-sum="${merchantCurrency.minSum}"
                          data-process_type="${merchantCurrency.processType}"
                          data-min-sum="${minTransferSum.max(merchantCurrency.minSum).stripTrailingZeros().toPlainString()}"
                          data-recipient-user-needed="${merchantCurrency.recipientUserIsNeeded}"
                          data-merchant-image-d="${merchantImage.id}"><loc:message code="merchants.withdraw"/>
                  </button>
                </div>
                <br>
              </c:forEach>
            </c:forEach>
          </div>
        </div>
      </div>
    </c:otherwise>
  </c:choose>

  <%@include file="modal/loading_modal.jsp" %>
  <%@include file="modal/pin_modal.jsp" %>
  <%@include file="modal/check_fin_pass_modal.jsp" %>
  <%@include file="modal/dialogTransferCreation_modal.jsp" %>

</div>

