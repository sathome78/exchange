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
            <h3>Contacts & Representatives </h3>
            <hr>
            <div>
                <div class="col-md-7" style="padding-left: 0;">
                    <p style="margin-top: 0;">Dear community please visit our representative offices and get detailed information about the listing and partnership with Exrates.
                        <br>
                        You can find us: </p>
                    <div>
                        <p>
                        <h5 style="margin-top: 30px;">China</h5>
                        <p>
                            <b>Representative office location:</b>
                        </p>
                        <p style="margin-top: 0;" text-align: left;>北京市朝阳区朝阳路都会国际 China, Beijing, Chaoyang dstr, Chaoyang rd, Duhui international<p>
                        <p>
                            <b>Phone:</b> +8615313750545
                        </p>
                        <p>
                            <b>Managing Partner </b>
                        </p>
                        <a style="margin-top: 0; margin-bottom: 10px; display: inline-block;" href="http://www.linkedin.com/in/fangjinzhu4284/"> 房金柱 mr.Fang</a>
                        <br>
                        <a href="email:fang.chinalisting@exrates.me">fang.chinalisting@exrates.me</a>
                        </p>
                    </div>
                    <div>
                        <h5 style="margin-top: 30px;">Switzerland </h5>
                        <p>
                            <b>Representative office location:</b>
                        </p>
                        <p style="margin-top: 0; text-align: left;">Gotthardstrasse 191, Goeschenen, CH-6487, Switzerland (MERKURI HOLDING AG)</p>
                        <p>
                            <b>Phone:</b> +41788832270
                        </p>
                        <p>
                            <b>Chairman of the Board  </b>
                        </p>
                        <a style="margin-top: 0; margin-bottom: 10px; display: inline-block;" href="https://www.linkedin.com/in/vladislav-postoupalski-41b015126/">Vladislav Postoupalski</a>
                        <br>
                        <a href="email:merkuri@exrates.me">merkuri@exrates.me</a>
                    </div>
                    <div>
                        <h5 style="margin-top: 30px;">Listing Support Line </h5>
                        <p>
                            <b>Phone:</b> +6281547581914
                        </p>
                        <a href="email:merkuri@exrates.me">listing@exrates.me</a>
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
