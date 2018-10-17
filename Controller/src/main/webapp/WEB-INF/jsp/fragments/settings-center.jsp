<div id="settings-page" class="settings">
    <div class="row" style="padding: 10px 0">
        <ul id="user-settings-menu" class="nav nav-pills">
            <li class="active"><a data-toggle="pill" href="#passwords-changing-wrapper"><loc:message code="admin.changePasswordTitle"/></a></li>
            <li><a data-toggle="pill" href="#nickname-changing-wrapper"><loc:message code="admin.changeNicknameTitle"/></a></li>
            <li><a data-toggle="pill" href="#2fa-options-wrapper"><loc:message code="message.2fa.title"/></a></li>
            <li><a data-toggle="pill" href="#files-upload-wrapper"><loc:message code="admin.uploadFiles.title"/></a></li>
            <%--<li><a data-toggle="pill" href="#notification-options-wrapper"><loc:message code="notifications.settings"/></a></li>--%>
            <li><a data-toggle="pill" href="#session-options-wrapper"><loc:message code="session.settings"/></a></li>
            <li><a data-toggle="pill" href="#api-options-wrapper"><loc:message code="api.user.settings"/></a></li>
        </ul>
    </div>

    <div class="tab-content">
        <div id="passwords-changing-wrapper" class="tab-pane fade in active">
            <%@include file="settings-center-password.jsp" %>
        </div>
        <div id="nickname-changing-wrapper" class="tab-pane fade">
            <%@include file="settings-center-nickname.jsp" %>
        </div>
        <div id="2fa-options-wrapper" class="tab-pane fade">
            <%@include file="settings-center-2fa.jsp" %>
        </div>
        <div id="files-upload-wrapper" class="tab-pane fade">
            <%@include file="settings-center-filesupload.jsp" %>
        </div>
        <%--<div id="notification-options-wrapper" class="tab-pane fade">
            <%@include file="settings-center-notifications.jsp" %>
        </div>--%>
        <div id="session-options-wrapper" class="tab-pane fade">
            <%@include file="settings-center-session.jsp" %>
        </div>
        <div id="api-options-wrapper" class="tab-pane fade">
            <%@include file="settings-center-api-tokens.jsp" %>
        </div>
    </div>
</div>

