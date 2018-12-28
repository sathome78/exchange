<div id="merchants-input-center">
  <div hidden>
    <div id="enter-other-bank-phrase"><loc:message code="merchants.invoice.otherBank"/></div>
    <div id="bank-not-selected"><loc:message code="merchants.notSelected"/></div>
  </div>
  <h4><loc:message code="merchants.inputTitle"/></h4>
  <label class="alert-danger has-error">
    <c:if test="${not empty error}">
      <loc:message code="${error}"/>
    </c:if>
  </label>
  <c:choose>
    <c:when test="${empty merchantCurrencyData || accessToOperationForUser eq false}">
      <p class="red noMerchants"><loc:message code="merchant.operationNotAvailable"/></p>
    </c:when>
    <c:otherwise>
      <div class="row">
        <div hidden id="operationType">${payment.operationType}</div>
        <div class="form-horizontal refill__money">
          <div class="input-block-wrapper clearfix">
            <div class="col-md-4 input-block-wrapper__label-wrapper" style="width:225px; padding-left: 0">
              <label style="font-size: 15px" for="currencyName" class="input-block-wrapper__label"><loc:message
                      code="merchants.inputCurrency"/></label>
            </div>
            <div class="col-md-8 input-block-wrapper__input-wrapper">
              <input id="currencyName"
                     style="float: left; width: auto"
                     class="form-control input-block-wrapper__input"
                     readonly
                     value="${currency.name}"/>
            </div>

          </div>
          <c:if test="${isAmountInputNeeded}">
            <div class="input-block-wrapper clearfix">
              <div class="col-md-4 input-block-wrapper__label-wrapper" style="width:225px; padding-left: 0">
                <label style="font-size: 15px" for="sum"><loc:message code="withdrawal.amount"/></label>
              </div>
              <div style="width: auto; " class="col-md-8 input-block-wrapper__input-wrapper">
                <input id="sum"
                       class="form-control input-block-wrapper__input numericInputField"
                       data-currency-name="${currency.name}"
                       data-max-amount="${balance}" <%--для USER_TRANSFER другое значение: ищи #maxForTransfer--%>
                       data-min-amount
                       data-system-min-sum="${minRefillSum}"
                       data-scale-of-amount="${scaleForCurrency}"
                       data-min-sum-noty-id="#min-sum-notification"
                       data-submit-button-id=".start-refill"/>
              </div>
              <div class="col-md-6 input-block-wrapper__label-wrapper">
                <div id="min-sum-notification" class="red"><loc:message code="merchants.input.minSum"/>
                  <strong> <span id="minSum"> <%--<fmt:formatNumber value="${minRefillSum}"
                                                                    pattern="###,##0.00######"/>--%></span>
                      ${currency.name}
                  </strong></div>
              </div>
            </div>
          </c:if>
          <b hidden id="buttonMessage"><loc:message code="merchants.deposit"/></b>
          <div id="merchantList">
            <br>
            <c:forEach var="merchantCurrency" items="${merchantCurrencyData}">
              <c:forEach var="merchantImage" items="${merchantCurrency.listMerchantImage}">
                <div style=" width: 100%; min-height: 112px; border: 1px solid #d5d5d5; padding: 10px; border-radius: 10px">
                  <div style="float: left; height: 20px;  width: 208px; text-align: right; margin-right: 19px">
                    <img class="img-thumbnail" src="${merchantImage.image_path}"
                         style="width: 168px; height: 52px; margin-right: 35px"/>
                    <div style="float: left; height: 20px;  width: 208px; text-align: left; margin-right: 10px; padding-left: 10px">
                      <c:if test="${(merchantCurrency.minSum > 0) && (merchantCurrency.processType != \"INVOICE\")}">
                        <c:choose>
                          <c:when test="${merchantCurrency.processType == \"CRYPTO\"}">
                            <span><loc:message code="merchants.input.recommendedMinSum"/></span>
                          </c:when>
                          <c:otherwise>
                            <span><loc:message code="merchants.input.minSum"/></span>
                          </c:otherwise>
                        </c:choose>
                        <span>${minRefillSum.max(merchantCurrency.minSum).stripTrailingZeros().toPlainString()}</span>
                      </c:if>
                      <br>
                      <span><loc:message code="merchants.commission"/>:</span>
                      <span>${merchantCurrency.inputCommission.stripTrailingZeros().toPlainString()}%</span>
                    </div>
                  </div>
                  <c:choose>
                    <c:when test="${empty merchantCurrency.address}">
                      <button class="start-refill btn btn-primary btn-lg start-button"
                              type="button"
                              data-currency-id="${currency.getId()}"
                              data-currency-name="${currency.getName()}"
                              data-merchant-id="${merchantCurrency.merchantId}"
                              data-merchant-name="${merchantCurrency.name}"
                              data-merchant-min-sum="${merchantCurrency.minSum}"
                              data-min-sum="${minRefillSum.max(merchantCurrency.minSum).stripTrailingZeros().toPlainString()}"
                              data-process_type="${merchantCurrency.processType}"
                              data-is-amount-input-needed="${isAmountInputNeeded}"
                              data-merchant-image-d="${merchantImage.id}"
                              data-merchant-child-merchant="${merchantImage.child_merchant}">
                        <loc:message code="merchants.deposit"/>
                      </button>
                    </c:when>
                    <c:otherwise>
                      <div style="overflow:auto; ">
                        <div>
                          <div class="alert alert-warning"><loc:message code="message.additional.deposit.warning" arguments="${currency.name}"/></div>
                          <c:choose>

                            <c:when test="${merchantCurrency.additionalTagForRefillIsUsed}">
                              <div class="alert alert-success">
                                <loc:message code="refill.messageAboutCurrentAddressSimple"/>
                                <div id="address-to-pay" <%--style="font-size:16px"--%>>
                                  <p class="pay_addr">${merchantCurrency.mainAddress}</p>
                                </div>
                                <button id="address-copy" class="btn btn-danger" style="padding: 0 20px"><loc:message
                                        code="refill.copy"/></button>
                              </div>
                              <%--<loc:message code="merchants.modalOutputAddressTag"/>--%>
                              <div class="alert alert-success">
                                <div>${merchantCurrency.additionalFieldName}:</div>
                                <div id="add-address-to-pay" style="font-size:14px; overflow:auto">
                                    ${merchantCurrency.address}
                                </div>

                                <button id="add-address-copy" class="btn btn-danger" style="padding: 0 20px"><loc:message
                                        code="refill.copyAdd" arguments="${merchantCurrency.additionalFieldName}"/></button>
                              </div>
                              <div class="alert alert-warning"><loc:message code="message.additional.address.warning.${currency.getName()}"/></div>
                            </c:when>
                            <c:otherwise>
                              <loc:message code="refill.messageAboutCurrentAddressSimple"/>
                              <div id="address-to-pay" style="font-size:14px; overflow:auto">
                                  ${merchantCurrency.address}
                              </div>
                              <button id=address-copy class="btn btn-danger" style="padding: 0 20px"><loc:message
                                      code="refill.copy"/></button>
                            </c:otherwise>
                          </c:choose>
                          <div class="timeoutWarning">
                            <c:forEach var="warningCode" items="${warningCodeList}">
                              <div><strong><loc:message code="${warningCode}"/></strong></div>
                            </c:forEach>
                          </div>
                          <div>
                            <img src='https://chart.googleapis.com/chart?chs=100x100&chld=L|2&cht=qr&chl=<c:out value="${merchantCurrency.address}"/>'/>
                          </div>
                          <c:if test="${merchantCurrency.generateAdditionalRefillAddressAvailable}">
                            <button id=address-generate class="btn start-refill" style="padding: 0 20px"
                                    data-currency-id="${currency.getId()}"
                                    data-currency-name="${currency.getName()}"
                                    data-merchant-id="${merchantCurrency.merchantId}"
                                    data-merchant-name="${merchantCurrency.name}"
                                    data-merchant-min-sum="${merchantCurrency.minSum}"
                                    data-process_type="${merchantCurrency.processType}"
                                    data-merchant-image-d="${merchantImage.id}"
                                    data-merchant-child-merchant="${merchantImage.child_merchant}">
                              <loc:message code="refill.generate"/></button>
                          </c:if>
                        </div>
                      </div>
                    </c:otherwise>
                  </c:choose>
                </div>
                <br>
              </c:forEach>
            </c:forEach>
          </div>
        </div>
        <div id="unconfirmed-refills-container" style="display: none">
          <div>
            <h4><loc:message code="refill.unconfirmed.invoices"/></h4>
          </div>
          <div class="col-md-8 col-md-offset-2">
            <table id="unconfirmed-refills-table" class="table" style="border: none">
              <thead>
              <tr>
                <th class="col-2 center"><loc:message code="inputoutput.datetime"/></th>
                <th class="col-1 center"><loc:message code="orderinfo.id"/></th>
                <th class="col-1 center"><loc:message code="inputoutput.currency"/></th>
                <th class="col-08 right"><loc:message code="inputoutput.amount"/></th>
                <th class="col-1 center"><loc:message code="inputoutput.merchant"/></th>
                <th class="col-3 center"></th>
              </tr>
              </thead>
              <tbody>
              <script type="text/template" id="unconfirmed-refills-table-row">
                <tr>
                  <td class="center"><@=datetime@></td>
                  <td class="center"><@=id@></td>
                  <td class="center"><@=currencyName@></td>
                  <td class="right"><@=amount@></td>
                  <td class="center"><@=merchantName@></td>
                  <td class="center table-button-block" style="text-align: right;">
                    <form id="inputoutput-center-tableBody__form" class="table-button-block__form">
                      <input type="text" hidden value="" name="transactionId" >
                      <input type="text" hidden value="" name="action">
                      <input type="text" hidden value="" name="sourceType">
                      <@=getButtonsSet(id, sourceType, merchantName, buttons, "unconfirmed-refills-table")@>
                    </form>
                </tr>
              </script>
              </tbody>
            </table>

            <ul id="unconfirmed-pagination" class="pagination-sm"></ul>
          </div>


        </div>
      </div>
    </c:otherwise>
  </c:choose>
  <%@include file="modal/loading_modal.jsp" %>
  <%@include file="modal/dialogRefillCreation_modal.jsp" %>
  <%@include file="modal/dialogRefillDetailedParamsEnter_modal.jsp" %>
  <%@include file="modal/dialogRefillConfirmationParamsEnter_modal.jsp" %>
  <%@include file="modal/confirm_with_info_modal.jsp" %>
  <%@include file="modal/warning_temporary_validity_refill_request_merchant.jsp"%>
</div>
<c:if test="${not empty warningSingleAddress}">
  <div class="row inout-warning">
    <strong><loc:message code="${warningSingleAddress}"/></strong>
  </div>
</c:if>