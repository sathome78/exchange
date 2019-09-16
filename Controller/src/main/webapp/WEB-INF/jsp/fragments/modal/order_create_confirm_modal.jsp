<%--
  User: Valk
--%>
<div id="order-create-confirm__modal" class="modal fade modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="submitorder.text"/></h4>
            </div>
            <div class="modal-body order-create-confirm">
                <div class="clearfix">
                    <form id="order-create-confirm__form" action="" method="post">
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="orders.type"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="operationTypeName" name="operationTypeName"
                                       readonly="true"
                                       autocomplete="off"
                                       class="form-control input-block-wrapper__input"/>
                            </div>
                        </div>
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="orders.currencypair"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="currencyPairName" name="currencyPairName"
                                       readonly="true"
                                       autocomplete="off"
                                       class="form-control input-block-wrapper__input"/>
                            </div>
                        </div>
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="mywallets.abalance"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="balance" name="balance"
                                       readonly="true"
                                       class="form-control input-block-wrapper__input numericInputField"/>
                            </div>
                            <div for="balance" class="col-md-12 input-block-wrapper__error-wrapper">
                                <div class="input-block-wrapper__error"></div>
                            </div>
                        </div>
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label id="amountSellLabel" class="input-block-wrapper__label">
                                    <loc:message code="orders.amount"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="amount" name="amount"
                                       readonly="true"
                                       class="form-control input-block-wrapper__input numericInputField"/>
                            </div>
                            <div for="amount" class="col-md-12 input-block-wrapper__error-wrapper">
                                <div class="input-block-wrapper__error"></div>
                            </div>
                        </div>
                        <div class="input-block-wrapper clearfix stop-rate">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="order.stop"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="stop" name="exrate"
                                       readonly="true"
                                       class="form-control input-block-wrapper__input numericInputField"/>
                            </div>
                            <div for="stop" class="col-md-12 input-block-wrapper__error-wrapper">
                                <div class="input-block-wrapper__error"></div>
                            </div>
                        </div>
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="order.rate"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="exrate" name="exrate"
                                       readonly="true"
                                       class="form-control input-block-wrapper__input numericInputField"/>
                            </div>
                            <div for="exrate" class="col-md-12 input-block-wrapper__error-wrapper">
                                <div class="input-block-wrapper__error"></div>
                            </div>
                        </div>
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="order.total"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="total" name="total"
                                       readonly="true"
                                       class="form-control input-block-wrapper__input numericInputField"/>
                            </div>
                            <div for="total" class="col-md-12 input-block-wrapper__error-wrapper">
                                <div class="input-block-wrapper__error"></div>
                            </div>
                        </div>
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="order.commission"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="commission" name="commission"
                                       readonly="true"
                                       class="form-control input-block-wrapper__input numericInputField"/>
                            </div>
                        </div>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="order.amountwithcommission"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="totalWithComission" name="totalWithComission"
                                       readonly="true"
                                       class="form-control input-block-wrapper__input numericInputField"/>
                            </div>
                            <div for="totalwc" class="col-md-12 input-block-wrapper__error-wrapper">
                                <div class="input-block-wrapper__error"></div>
                            </div>
                            <div for="permission" class="col-md-12 input-block-wrapper__error-wrapper">
                                <div class="input-block-wrapper__error"></div>
                            </div>
                        </div>
                    </form>

                </div>
            </div>
            <div class="modal-footer">
                <div class="order-create-confirm__button-wrapper">
                    <button id="order-create-confirm__submit" class="order-create-confirm__button">
                        <loc:message code="orders.submit"/>
                    </button>
                    <button class="order-create-confirm__button" data-dismiss="modal">
                        <loc:message code="submitorder.cancell"/>
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
