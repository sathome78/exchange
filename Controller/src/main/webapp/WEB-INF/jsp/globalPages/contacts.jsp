<%--
  Created by IntelliJ IDEA.
  User: ogolv
  Date: 09.08.2016
  Time: 8:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title><loc:message code="dashboard.contactsAndSupport"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>
    <meta charset="UTF-8">

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="interkassa-verification" content="c4deb5425361141d96dd48d235b6fc4a"/>

    <%----------------------------------------%>
    <%@include file="../tools/google_head.jsp"%>
    <%@include file="../tools/alexa.jsp" %>
    <%--<%@include file="../tools/yandex.jsp" %>--%>

    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/loc-direction.js'/>"></script>

    <%--... Alerts --%>
    <script src="https://cdn.jsdelivr.net/sockjs/1/sockjs.min.js"></script>
    <script type="text/javascript" src="<c:url value='/client/js/stomp.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/kinetic.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/jquery.final-countdown.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/alert-init.js'/>"></script>
    <link href="<c:url value='/client/css/timer.css'/>" rel="stylesheet">
    <%@include file="../tools/newCapchaScripts.jsp" %>

</head>
<body>
<%@include file="../fragments/header-simple.jsp" %>
<main class="container">
    <div class="row">
        <div class="col-md-8 col-md-offset-2 content legal_content">
            <h3><loc:message code="contacts.title"/> </h3>
            <hr style="margin-bottom: 16px; margin-top: 16px;">
            <div>
                <div class="col-md-7" style="padding-left: 0;">
                    <p style="margin-top: 0;"> <loc:message code="contacts.intro"/>
                        <br>
                        <br>
                        <loc:message code="contacts.findUs"/>
                    </p>
                    <div>
                        <h5 style="margin-top: 32px; margin-bottom: 16px;"><loc:message code="country.china"/></h5>
                        <p style="margin-bottom: 0; margin-top: 0;">
                            <b><loc:message code="contacts.officeLocation.title"/></b>
                        </p>
                        <p style="margin-top: 0; margin-bottom:0; text-align: left;"><loc:message code="contacts.officeLocation.china"/></p>
                        <p style="margin-top: 8px; margin-bottom: 0;">
                            <b><loc:message code="admin.phone"/>:</b> +8615313750545
                        </p>
                        <p style="margin-top: 8px; margin-bottom: 8px;">
                            <b><loc:message code="contacts.partners.title"/> </b>
                        </p>
                        <a style="margin-top: 0; margin-bottom: 8px; display: inline-block;" href="http://www.linkedin.com/in/fangjinzhu4284/"><loc:message code="contacts.partners"/></a>
                        <br>
                        <a href="mailto:fang.chinalisting@exrates.me">fang.chinalisting@exrates.me</a>
                    </div>
                    <div>
                        <h5 style="margin-top: 32px;margin-bottom: 16px;"><loc:message code="country.switzerland"/></h5>
                        <p style="margin-bottom: 0; margin-top: 0;">
                            <b><loc:message code="contacts.headquarters.title"/></b>
                        </p>
                        <p style="margin-top: 0; margin-bottom:0; text-align: left;"><loc:message code="contacts.officeLocation.switzerland"/></p>
                        <p style="margin-top: 8px; margin-bottom: 0;">
                            <b><loc:message code="admin.phone"/>:</b> +41788832270
                        </p>
                        <p style="margin-top: 8px; margin-bottom: 8px;">
                            <b><loc:message code="contacts.chairman.title"/> </b>
                        </p>
                        <a style="margin-top: 0; margin-bottom: 8px; display: inline-block;" href="https://www.linkedin.com/in/nina-bar-b87945170/"><loc:message code="contacts.support.listing.manager"/> </a>
                        <br>
                        <a href="mailto:merkuri@exrates.me">merkuri@exrates.me</a>
                    </div>
                    <div>
                        <h5 style="margin-top: 32px; margin-bottom: 16px;"><loc:message code="contacts.support.title"/></h5>
                        <p style="margin-top: 8px; margin-bottom: 0;">
                            <a style="margin-top: 0; margin-bottom: 8px; display: inline-block;" href="https://www.linkedin.com/in/vladislav-postoupalski-41b015126/"><loc:message code="contacts.chairman"/> </a>
                            <b><loc:message code="admin.phone"/>:</b> Nina Bar
                        </p>
                        <a href="mailto:merkuri@exrates.me">listing@exrates.me</a>
                    </div>
                </div>
                <div class="col-md-4 col-md-offset-1">
                    <div class="pipedriveWebForms" data-pd-webforms="https://pipedrivewebforms.com/form/020d70347deb09bd6f285e7bb17c1c523330571">
                        <script data-cfasync="false" src="https://exrates.me/cdn-cgi/scripts/5c5dd728/cloudflare-static/email-decode.min.js"></script>
                        <script src="https://webforms.pipedriveassets.com/webforms.min.js"></script>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div style="margin-bottom: 5%"></div>


</main>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
<%@include file='../fragments/footer.jsp' %>
</body>
</html>
