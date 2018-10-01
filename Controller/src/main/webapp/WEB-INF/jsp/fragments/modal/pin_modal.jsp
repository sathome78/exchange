
<div class="modal fade" id="pin_modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel"><loc:message code="message.pin_code"/></h4>
            </div>
            <div class="modal-body modal-content__input-block-wrapper">
                <div class="content modal-content__content-wrapper">
                    <p id='pin_text'></p>
                    <loc:message code="message.pin_code" var="pinPlaceholder"/>
                    <input id="pin_code" type="password" name="pin" autocomplete="off"  autofocus placeholder="${pinPlaceholder}"/>
                    <%--csrf--%>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <br/>
                    <button disabled id="check-pin-button" type="button" class="button_enter">
                        <loc:message code="admin.submit"/></button>
                </div>
                <p id="pin_wrong" hidden style="color: red"><loc:message code="message.pin_code.incorrect"/></p>
            </div>
        </div>
    </div>
</div>

