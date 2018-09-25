<%--
  Created by IntelliJ IDEA.
  User: ogolv
  Date: 01.08.2016
  Time: 14:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <title><loc:message code="partners.title"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="interkassa-verification" content="c4deb5425361141d96dd48d235b6fc4a"/>

    <%----------------------------------------%>
    <%@include file="../tools/google_head.jsp"%>
  <%--  <%@include file="../tools/alexa.jsp" %>--%>
    <%--<%@include file="../tools/yandex.jsp" %>--%>

    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">
    <%--... Alerts --%>
    <script type="text/javascript" src="<c:url value='/client/js/sockjs114.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/stomp.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/kinetic.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/jquery.final-countdown.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/alert-init.js'/>"></script>
    <link href="<c:url value='/client/css/timer.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/loc-direction.js'/>"></script>
    <%@include file="../tools/newCapchaScripts.jsp" %>
</head>
<body>
<%@include file="../fragments/header-simple.jsp" %>
<main class="container">
    <div class="row">
        <div class="col-md-8 col-md-offset-2 content legal_content">
            <c:set var="termsHeading">
                <loc:message code="partners.title"/>
            </c:set>
            <c:set var="salesLink1">
                <a href="mailto:listing@exrates.me?Subject=Add%20coin">listing@exrates.me</a>
            </c:set>
            <c:set var="salesLink2">
                <a href="mailto:listmycoin@exrates.me?Subject=Add%20coin">listmycoin@exrates.me</a>
            </c:set>
            <c:set var="salesLink3">
                <a href="mailto:listme@exrates.me?Subject=Add%20coin">listme@exrates.me</a>
            </c:set>
            <c:set var="salesLink4">
                <a href="mailto:listing@exrates.top?Subject=Add%20coin">listing@exrates.top</a>
            </c:set>
            <h3>${fn:toUpperCase(termsHeading)}</h3>
            <hr/>
            <div style="margin-bottom: 70px" id="termsContent">
                <div class="col-md-7" style="padding-left: 0">
                    <loc:message code="partners.content" arguments="${salesLink1}, ${salesLink2}, ${salesLink3}, ${salesLink4}"/>
                </div>
                <div class="col-md-4 col-md-offset-1">
                    <div class="pipedriveWebForms" data-pd-webforms="https://pipedrivewebforms.com/form/020d70347deb09bd6f285e7bb17c1c523330571">
                        <script src="/client/js/webforms.min.js"></script></div>
                </div>
            </div>
        </div>

        <div class="col-md-8 col-md-offset-2 content sponsors" style="background-color: white">
            <h1 style="text-align: center"><loc:message code="our.partners"/></h1>
            <table class="img-table">
                <tr><td><img class="img-thumbnail partners-img" src="/client/img/partners/coingecko.png"/></td>
                    <td><img class="img-thumbnail partners-img" src="/client/img/partners/coinmarketcap.png"/></td>
                    <td><img class="img-thumbnail partners-img" src="/client/img/partners/icomarks.png"/></td>
                </tr>
                <tr>
                    <td><img class="img-thumbnail partners-img" src="/client/img/partners/ICOUnicorn.png"/></td>
                    <td><img class="img-thumbnail partners-img" src="/client/img/partners/jedis.png"/></td>
                </tr>
            </table>
        </div>
        <div class="col-md-8 col-md-offset-2 content sponsors">

        </div>
    </div>
</main>
<%@include file='../fragments/footer.jsp' %>
</body>
</html>
