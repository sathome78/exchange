<div id="merchants-output-center">
  <div hidden>
    <div id="enter-other-bank-phrase"><loc:message code="merchants.invoice.otherBank"/></div>
    <div id="bank-not-selected"><loc:message code="merchants.notSelected"/></div>
  </div>
  <h4><loc:message code="merchants.outputTitle"/></h4>
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
                <%--TO DO | START | Delete after fix --%>
                      <c:choose>
                        <c:when test="${currency.name == 'B2X'}">
                          <c:choose>
                            <c:when test="${balance >= 3000}">
                              data-max-amount="3000"
                            </c:when>
                            <c:otherwise>
                              data-max-amount="${balance}"
                            </c:otherwise>
                          </c:choose>
                        </c:when>
                        <c:otherwise>
                          data-max-amount="${balance}"
                        </c:otherwise>
                      </c:choose>
                <%--TO DO | END | Delete after fix --%>
                <%--BEFORE to do| START | Return after fix --%>
                  <%--data-max-amount="${balance}"--%>
                <%--BEFORE to do| END | Return after fix --%>
                     data-currency-name="${currency.name}"
                     data-max-amount="${balance}" <%--для USER_TRANSFER другое значение: ищи #maxForTransfer--%>
                     data-min-amount
                     data-system-min-sum="${minWithdrawSum}"
                     data-scale-of-amount="${scaleForCurrency}"
                     data-min-sum-noty-id="#min-sum-notification"
                     data-submit-button-id=".start-withdraw"
                     <c:if test="${checkingZeroBalance}">disabled</c:if>
              />
            </div>
            <div class="col-md-6 input-block-wrapper__label-wrapper">
              <div id="min-sum-notification" class="red"><loc:message code="mercnahts.output.minSum"/>
                <strong> <span id="minSum"><%--<fmt:formatNumber value="${minWithdrawSum}"
                                                                  pattern="###,##0.00######"/>--%></span>
                                                                  ${currency.name}
                </strong>
              </div>
            </div>
          </div>
          <b hidden id="buttonMessage"><loc:message code="merchants.withdraw"/></b>
          <div id="merchantList">
            <br>
            <c:forEach var="merchantCurrency" items="${merchantCurrencyData}">
              <c:forEach var="merchantImage" items="${merchantCurrency.listMerchantImage}">
                <div style=" width: 100%; height: 98px; border: 1px solid #d5d5d5; padding: 10px; border-radius: 10px">
                  <div style="float: left; height: 20px;  width: 183px; text-align: right; margin-right: 47px">
                    <img class="img-thumbnail" src="${merchantImage.image_path}"
                         style="width: 168px; height: 52px; margin-right: 10px"/>
                    <div style="float: left;height: 20px;width: 468px;text-align: left;margin-right: 10px;padding-left: 228px;">
                      <c:if test="${(merchantCurrency.minSum > 0) && (merchantCurrency.processType != \"INVOICE\")}">
                        <span><loc:message code="mercnahts.output.minSum"/></span>
                        <span >${minWithdrawSum.max(merchantCurrency.minSum).stripTrailingZeros().toPlainString()}</span>
                      </c:if>
                      <br>
                      <span><loc:message code="merchants.commission"/>:</span>
                      <c:choose>
                        <c:when test="${merchantCurrency.comissionDependsOnDestinationTag}">
                          <loc:message code="message.comission.dynamic"/>
                        </c:when>
                        <c:when test="${merchantCurrency.specMerchantComission}">
                          <loc:message code="message.comission.fixed"/>
                        </c:when>
                        <c:otherwise>
                          <span>${merchantCurrency.outputCommission.stripTrailingZeros().toPlainString()}%</span>
                        </c:otherwise>
                      </c:choose>
                    </div>
                  </div>
                  <button class="start-withdraw btn btn-primary btn-lg start-button"
                          type="button"
                          data-currency-id="${currency.getId()}"
                          data-currency-name="${currency.getName()}"
                          data-merchant-id="${merchantCurrency.merchantId}"
                          data-merchant-name="${merchantCurrency.name}"
                          data-merchant-min-sum="${merchantCurrency.minSum}"
                          data-min-sum="${minWithdrawSum.max(merchantCurrency.minSum).stripTrailingZeros().toPlainString()}"
                          data-process_type="${merchantCurrency.processType}"
                          data-spec-merchan-comission="${merchantCurrency.specMerchantComission}"
                          data-comission-depends-on-destination-tag="${merchantCurrency.comissionDependsOnDestinationTag}"
                          data-additional-field-needed="${merchantCurrency.additionalTagForWithdrawAddressIsUsed}"
                          data-additional-field-name="${merchantCurrency.additionalFieldName}"
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

  <%@include file="modal/pin_modal.jsp"%>
  <%@include file="modal/loading_modal.jsp" %>
  <%@include file="modal/check_fin_pass_modal.jsp" %>
  <%@include file="modal/check_wallet_address_modal.jsp" %>
  <%@include file="modal/dialogWithdrawCreation_modal.jsp" %>
  <%@include file="modal/dialogWithdrawDetailedParamsEnter_modal.jsp" %>

</div>

