<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title><loc:message code="admin.emailSettings"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <%@include file='links_scripts.jsp' %>

    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value="/client/css/ekko-lightbox.min.css"/>" rel="stylesheet">
    <script type="text/javascript" src="<c:url value='/client/js/app.js'/>"></script>

    <%----------%>
    <script type="text/javascript" src="<c:url value="/client/js/ekko-lightbox.min.js"/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery-ui.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery-ui.js'/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/email_settings_page.js'/>"></script>

<body>
<%@include file='../fragments/header-simple.jsp' %>

<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 content admin-container">

            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

            <div class="text-center">
                <h4 class="modal-title">Email Settings</h4>
            </div>
            <br>
            <div class="col-md-12 content">
                <button id="email_create" class="btn btn-default" style="cursor: pointer"><loc:message
                        code="email.add_new"/></button>
                <br>
                <div id="create_email" class="collapse" hidden>
                    <form id="create_email_rule_form" class="form_full_height_width">

                        <%--Email Host--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="email.host"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper" style="height: auto;">
                                <textarea name="email_host"></textarea>
                            </div>
                        </div>
                        <%--Email Sender--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="email.sender"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper" style="height: auto;">
                                <textarea name="email_sender"></textarea>
                            </div>
                        </div>

                    </form>
                    <button id="email_create_send" class="blue-box" style="cursor: pointer"><loc:message
                            code="email.create_new_rule"/></button>
                    <button id="email_create_close" class="blue-box" style="cursor: pointer"><loc:message
                            code="ieo.close"/></button>

                </div>
                <br>
                <table id="emailTable" style="width:100%; cursor: pointer">
                    <thead>
                    <tr>
                        <th class="col-2 center blue-white"><loc:message code="email.host"/></th>
                        <th class="col-2 center blue-white"><loc:message code="email.sender"/></th>
                    </tr>
                    </thead>
                </table>
                <br>

                <div id="update_email" class="collapse" hidden>

                    <form id="update_email-form" class="form_full_height_width">

                        <%--Email Host--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="email.host"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper" style="height: auto;">
                                <input type="text" name="email_host" id="email_host" disabled>
                            </div>
                        </div>

                        <%--Email sender--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="email.sender"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="text" id="email_sender" name="email_sender">
                            </div>
                        </div>

                        <br>

                        <a id="email_update_send" class="blue-box" style="cursor: pointer"><loc:message
                                code="button.update"/></a>
                        <a id="email_update_close" class="blue-box" style="cursor: pointer"><loc:message
                                code="ieo.close"/></a>
                         <a id="email_delete_send" class="red-box" style="cursor: pointer"><loc:message
                                    code="button.delete"/></a>
                    </form>
                </div>
            </div>

        </div>
</main>

</body>
</html>
