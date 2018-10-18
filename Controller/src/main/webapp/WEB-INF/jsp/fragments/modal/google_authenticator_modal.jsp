
<div id="google_authenticator_modal" class="modal fade order-info__modal modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body">
                <div id="google2fa_connect_block" style="border: 1px solid rgba(0, 0, 0, 0.29); border-radius: 4px; margin: 0 5px; padding: 10px">
                    <div style="border: 1px solid rgba(0, 0, 0, 0.29); border-radius: 4px; margin: 0 5px; padding: 10px">
                        <h5 class="modal-title"><loc:message code="message.attention"/></h5>
                    </div>
                    <hr>
                    <div class="" style="width: 43%; float:left;">
                        <div id="qr" style="margin-bottom: 24px;"> <img /></div>
                        <div><b><loc:message code="ga.2fa_16"/></b> <span style="color:red; text-transform: uppercase;">${googleAuthenticatorCode}</span></div>
                        <div style="text-decoration: underline; margin-bottom: 24px;"><loc:message code="ga.2fa_save"/></div>
                        <div>
                            <loc:message code="ga.2fa_note"/>
                        </div>

                    </div>

                    <div class="" style="width: 52%; float:right;">
                        <h4 style="margin-top: 0;"><loc:message code="ga.2fa_disable_title"/></h4>
                        <div><loc:message code="ga.2fa_recommend"/></div>
                        <hr>
                            <div style="margin-bottom: 4px; ">
                                <span style="float:left;">Username/Email:</span>
                                <span style="float:right;">${user.getEmail()}</span>
                                <div class="clearfix"></div>
                            </div>
                            <div style="margin-bottom: 12px; ">
                                <span style="float:left;"><loc:message code="message.sms.code"/>:</span>
                                <input id="google2fa_code_input" type="text" style="float:right; border: 1px solid grey;">
                                <div class="clearfix"></div>
                            </div>
                            <div style="margin-bottom: 8px;"><loc:message code="ga.2fa_turnon"/></div>
                            <div style="margin-bottom: 24px; max-width: 230px;">
                                <input id="backed_up_16" type="checkbox" name="" value="" style="vertical-align: middle;margin: 0;">
                                <span style="vertical-align: middle;"><loc:message code="ga.2fa_backed_up"/></span>
                                <div class="clearfix"></div>
                            </div>
                            <button id='google2fa_send_code_button'  class="btn btn-default" style="float:right;" disabled >
                                <loc:message code="ga.2fa_enable_button"/>
                            </button>
                    </div>
                    <div class="clearfix"></div>
            </div>
                <div id="google2fa_disconnect_block" style="border: 1px solid rgba(0, 0, 0, 0.29); border-radius: 4px; margin: 0 5px; padding: 10px">
                    <div style="border: 1px solid rgba(0, 0, 0, 0.29); border-radius: 4px; margin: 0 5px; padding: 10px">
                        <h5 class="modal-title"><loc:message code="message.attention"/></h5>
                    </div>
                    <hr>
                    <div class="" style="width: 43%; float:left;">
                        <div id="disconnect_qr" style="margin-bottom: 24px;"> <img /></div>
                        <div><b><loc:message code="ga.2fa_16"/></b> <span style="color:red; text-transform: uppercase;">${googleAuthenticatorCode}e</span></div>
                        <div style="text-decoration: underline; margin-bottom: 24px;"><loc:message code="ga.2fa_save"/></div>
                        <div>
                            <loc:message code="ga.2fa_note"/>
                        </div>

                    </div>

                    <div class="" style="width: 52%; float:right;">
                        <h4 style="margin-top: 0;"><loc:message code="ga.2fa_enable_title"/></h4>
                        <div><loc:message code="ga.2fa_turnoff"/> "<loc:message code="ga.2fa_disable_button"/>".</div>
                        <hr>
                        <div style="margin-bottom: 4px; ">
                            <span style="float:left;">Username/Email:</span>
                            <span style="float:right;">${user.getEmail()}</span>
                            <div class="clearfix"></div>
                        </div>
                        <div style="margin-bottom: 12px; ">
                            <span style="float:left;"><loc:message code="message.sms.code"/>:</span>
                            <input id="disconnect_google2fa_code_input" type="text" style="float:right; border: 1px solid grey;">
                            <div class="clearfix"></div>
                        </div>
                        <button id='disconnect_google2fa_send_code_button'  class="btn btn-default" style="float:right;" disabled >
                            <loc:message code="ga.2fa_disable_button"/>
                        </button>
                    </div>
                    <div class="clearfix"></div>
                </div>
        </div>
    </div>
</div>
</div>