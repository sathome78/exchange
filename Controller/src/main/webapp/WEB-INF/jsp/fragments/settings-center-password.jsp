<%--
  User: Valk
--%>
<section id="passwords-changing">
    <h4 class="h4_green">
        <loc:message code="admin.changePasswordTitle"/>
    </h4>
    <h4 class="under_h4_margin"></h4>

    <div class="myhistory__button-wrapper">
        <button class="orderForm-toggler myhistory__button blue-box"
                data-tabid="tab__mainpass">
            <loc:message code="admin.changePassword"/>
        </button>
        <button class="orderForm-toggler myhistory__button blue-box"
                data-tabid="tab__finpass">
            <loc:message code="admin.changeFinPassword"/>
        </button>
    </div>
    <div class="container">
        <div class="row">
            <div class="col-sm-8 content">
                <div class="tab-content">
                    <%--change main passwod--%>
                    <div class="tab-pane" id="tab__mainpass">
                        <form:form class="form-horizontal" id="settings-user-form"
                                   action="/settings/changePassword/submit"
                                   method="post" modelAttribute="user">
                            <form:input path="id" type="hidden" class="form-control" id="user-id"/>
                            <form:input path="role" type="hidden" class="form-control" id="user-role"/>
                            <form:input path="userStatus" type="hidden" class="form-control"
                                        id="user-status"/>
                            <form:input path="finpassword" type="hidden" class="form-control"
                                        id="user-finpassword"/>
                            <%----%>
                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label for="user-name" class="input-block-wrapper__label"><loc:message
                                            code="admin.login"/></label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input path="nickname" class="form-control input-block-wrapper__input"
                                                id="user-name"
                                                readonly="true"/>
                                </div>
                            </div>
                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label for="user-email" class="input-block-wrapper__label"><loc:message
                                            code="admin.email"/></label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input path="email" class="form-control input-block-wrapper__input"
                                                id="user-email" readonly="true"/>
                                    <form:errors path="email" class="form-control input-block-wrapper__input"
                                                 style="color:red"/>
                                </div>
                            </div>
                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label for="user-password" path="password"
                                           class="input-block-wrapper__label"><loc:message
                                            code="admin.password"/></label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:password path="password" class="form-control input-block-wrapper__input"
                                                   id="user-password"/>
                                </div>
                                <div class="col-md-12 input-block-wrapper__error-wrapper">
                                    <form:errors path="password" class="input-block-wrapper__input"/>
                                </div>
                            </div>
                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label for="user-confirmpassword" path="confirmpassword"
                                           class="input-block-wrapper__label"><loc:message
                                            code="admin.confirmpassword"/></label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:password path="confirmPassword"
                                                   class="form-control input-block-wrapper__input"
                                                   id="user-confirmpassword"/>
                                </div>
                                <div class="col-md-12 input-block-wrapper__error-wrapper">
                                    <form:errors path="confirmPassword" class="input-block-wrapper__input"/>
                                </div>
                            </div>
                            <%----%>
                            <h5><loc:message code="admin.changePasswordSendEmail"/></h5>
                            <%----%>
                            <div class="confirm-button-wrapper">
                                <button class="confirm-button" type="submit"><loc:message
                                        code="admin.save"/></button>
                            </div>
                        </form:form>
                    </div>
                    <%--change fin passwod--%>
                    <div class="tab-pane" id="tab__finpass">
                        <form:form class="form-horizontal" id="settings-userFin-form"
                                   action="/settings/changeFinPassword/submit"
                                   method="post" modelAttribute="user">
                            <form:input path="id" type="hidden" class="form-control" id="userFin-id"/>
                            <form:input path="role" type="hidden" class="form-control"
                                        id="userFin-role"/>
                            <form:input path="userStatus" type="hidden" class="form-control"
                                        id="userFin-status"/>
                            <%----%>
                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label for="userFin-name"
                                           class="input-block-wrapper__label"><loc:message
                                            code="admin.login"/></label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input path="nickname" class="form-control input-block-wrapper__input"
                                                id="userFin-name"
                                                readonly="true"/>
                                </div>
                            </div>
                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label for="userFin-email" class="input-block-wrapper__label"><loc:message
                                            code="admin.email"/></label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input path="email" class="form-control input-block-wrapper__input"
                                                id="userFin-email" readonly="true"/>
                                    <form:errors path="email" class="input-block-wrapper__input"
                                                 style="color:red"/>
                                </div>
                            </div>
                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label for="userFin-password" path="finpassword"
                                           class="input-block-wrapper__label"><loc:message
                                            code="admin.finPassword"/></label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:password path="finpassword"
                                                   class="form-control input-block-wrapper__input"
                                                   id="userFin-password"/>
                                </div>
                                <div class="col-md-12 input-block-wrapper__error-wrapper">
                                    <form:errors path="finpassword" class="input-block-wrapper__input"/>
                                </div>
                            </div>
                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label for="userFin-confirmpassword" path="confirmpassword"
                                           class="input-block-wrapper__label"><loc:message
                                            code="admin.confirmpassword"/></label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:password path="confirmFinPassword"
                                                   class="form-control input-block-wrapper__input"
                                                   id="userFin-confirmpassword"/>
                                </div>
                                <div class="col-md-12 input-block-wrapper__error-wrapper">
                                    <form:errors path="confirmFinPassword" class="input-block-wrapper__input"/>
                                </div>
                            </div>
                            <%----%>
                            <h5><loc:message code="admin.changePasswordSendEmail"/></h5>
                            <%----%>
                            <div class="confirm-button-wrapper">
                                <button class="confirm-button" type="submit"><loc:message
                                        code="admin.save"/></button>
                            </div>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>

    </div>
</section>
