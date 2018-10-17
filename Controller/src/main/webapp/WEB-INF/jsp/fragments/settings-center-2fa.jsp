<%@ taglib prefix="loc" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<section id="2fa-options">
    <h4 class="h4_green">
        <loc:message code="message.2fa.title.big"/>
    </h4>
    <h4 class="under_h4_margin"></h4>
    <div class="col-sm-12" style="width: 65%; margin-bottom: 20px;">
        <!-- disable -->
        <div class="g2fa_connect" hidden>
            <h2 style="font-size: 32px;"><loc:message code="message.g2fa.is"/><span style="color:red"> <loc:message code="news.status.disabled"/></span> </h2>
            <div style="width: 45%; float:left;">
                <h4 style="margin-top: 0;"><loc:message code="ga.2fa_disable_title"/></h4>
                <div><loc:message code="ga.2fa_recommend"/></div>
                <hr>
                <form id='connect_g2fa' action="#">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div style="margin-bottom: 4px; ">
                        <span style="float:left;">Username/Email:</span>
                        <span style="float:right;">${user.getEmail()}</span>
                        <div class="clearfix"></div>
                    </div>
                    <div style="margin-bottom: 12px; ">
                        <span style="float:left;"><loc:message code="login.password"/></span>
                        <input name="password" id="2fa_user_pass" type="password" style="float:right; border: 1px solid grey;" autocomplete="off">
                        <div class="clearfix"></div>
                    </div>
                    <div style="margin-bottom: 12px; ">
                        <span style="float:left;"><loc:message code="message.sms.code"/>:</span>
                        <input name="code" id="2fa_user_code" type="text" style="float:right; border: 1px solid grey;" autocomplete="off">
                        <div class="clearfix"></div>
                    </div>
                    <div style="margin-bottom: 8px;"><loc:message code="ga.2fa_turnon"/></div>
                    <div style="margin-bottom: 24px; max-width: 230px;">
                        <input id="backed_up_16" type="checkbox" name="" value="" style="vertical-align: middle;margin: 0;" autocomplete="off">
                        <span style="vertical-align: middle;"><loc:message code="ga.2fa_backed_up"/></span>
                        <div class="clearfix"></div>
                    </div>
                </form>
                <button id="g2fa_connect_button" class="btn btn-default" style="float:right;" disabled><loc:message code="ga.2fa_enable_button"/></button>
            </div>
        </div>
        <!-- enable -->
        <div class="g2fa_connected" hidden>
            <h1 style="font-size: 32px;"><loc:message code="message.g2fa.is"/><span style="color:green"> <loc:message code="admin.enabled"/></span> </h1>
            <div style="width: 45%; float:left;">
                <h4 style="margin-top: 0;"><loc:message code="ga.2fa_enable_title"/></h4>
                <div><loc:message code="ga.2fa_turnoff"/></div>
                <hr>
                <form id='disconnect_g2fa' action="#">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div style="margin-bottom: 4px; ">
                        <span style="float:left;">Username/Email:</span>
                        <span style="float:right;">${user.getEmail()}</span>
                        <div class="clearfix"></div>
                    </div>
                    <div style="margin-bottom: 12px; ">
                        <span style="float:left;"><loc:message code="login.password"/>:</span>
                        <input id="disconnect_pass" name="password" type="password" style="float:right; border: 1px solid grey;" autocomplete="off">
                        <div class="clearfix"></div>
                    </div>
                    <div style="margin-bottom: 24px;">
                        <span style="float:left;"><loc:message code="message.sms.code"/>:</span>
                        <input id="disconnect_code" name="code" type="text" style="float:right; border: 1px solid grey;" autocomplete="off">
                        <div class="clearfix"></div>
                    </div>
                </form>
                <button id="disconnect_google2fa" class="btn btn-default" style="float:right;" disabled><loc:message code="ga.2fa_disable_button"/></button>
            </div>
        </div>
        <!--  qr code block -->
        <div class="g2fa_connect" style="width: 43%; float:right;" hidden>
            <div style="margin-bottom: 10px; height: 220px; margin-top: 15px;">
                <img id="g2fa_qr_code" alt="">
            </div>
            <div><b><loc:message code="ga.2fa_16"/></b> <span id="g2fa_code" style="color:red; text-transform: uppercase;"></span></div>
            <div style="margin-bottom: 24px;"><loc:message code="ga.2fa_save"/></div>
            <div>
                <loc:message code="ga.2fa_note"/>
            </div>
        </div>
        <div class="clearfix"></div>
    </div>



</section>