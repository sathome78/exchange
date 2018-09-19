<div id="google_authenticator_modal" class="modal fade order-info__modal modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body">
                <div id="google2fa_connect_block">
                    <div style="border: 1px solid rgba(0, 0, 0, 0.29); border-radius: 4px; margin: 0 5px; padding: 10px">
                        <h5 class="modal-title"><loc:message code="message.attention"/></h5>
                    </div>

                    <br>
                    <div id="qr">
                        <p><loc:message code="message.google2fa.barcode"/>
                            <a href="https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2">Android</a> and
                            <a href="https://itunes.apple.com/us/app/google-authenticator/id388497605">iPhone</a>

                        </p><br/>
                        <p><loc:message code="message.google2fa.code" arguments="${googleAuthenticatorCode}"/>

                        <img class="center"/>

                    </div>

                    <label for="google2fa_code_input"><loc:message code="message.sms.code"/></label>
                    <input id="google2fa_code_input"/>
                    <button id='google2fa_send_code_button'  class="btn btn-default"><loc:message code="orderinfo.ok"/></button>
                    <button class="btn btn-default" type="button" style="float:right" data-dismiss="modal"><loc:message code="admin.cancel"/></button>
                </div>

            </div>
        </div>
    </div>
</div>