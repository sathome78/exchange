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
            <h3>HOW TO SUBMIT A COIN FOR LISTING</h3>
            <hr>
            <div style="margin-bottom: 70px" id="termsContent">
                <div class="col-md-7" style="padding-left: 0">
                    <p style="margin-top: 0; margin-bottom: 0;">Exrates.me team is ready to consider your request to add your coin or token to the listing. To submit a request, please provide the following technical information on your coin (token):</p>
                    <p style="margin-top: 16px; margin-bottom: 0;">1. General description of technology - whether it is blockchain-driven or not, smart contract support etc.</p>
                    <p style="margin-top: 16px; margin-bottom: 0;">2. Full node - where to download, how to install and run on Windows and Linux (Ubuntu), documented configuration options and command line arguments.</p>
                    <p style="margin-top: 16px; margin-bottom: 0;">3. Detailed documentation for node API, primarily following methods: </p>
                    <ul>
                        <li>create new address (account);</li>
                        <li>send transaction;</li>
                        <li>get transaction by ID;</li>
                        <li>find all transactions by parameters - receiver address (account), time period, block etc;</li>
                    </ul>
                    <p style="margin-top: 16px; margin-bottom: 0;">4. Whether there is a system of notifications on new block or incoming transaction (like in Bitcoin Core). If there is - documentation on that system.</p>
                    <p style="margin-top: 16px; margin-bottom: 0;">5. Available production-ready solutions (libraries/SDK) for different programming languages.</p>
                    <p style="margin-top: 16px; margin-bottom: 0;">6. Access to testnet, test online wallets, faucets or other ways to receive test coins.</p>
                    <p style="margin-top: 16px; margin-bottom: 0;">7. Links to GitHub and supporting community - Slack, Reddit, Gitter etc.</p>
                    <p style="margin-top: 16px; margin-bottom: 0;">We will inform you on exact price only after studying all the technical documentation and the approval of the management.</p>
                    <p style="margin-top: 8px; margin-bottom: 0;">If you are interested, please mail to <a href="mailto:listing@exrates.me?Subject=Add%20coin">listing@exrates.me</a> with all required technical data.</p>
                    <p style="margin-top: 8px; margin-bottom: 0;"> <b>The head of the sales team: </b><a href="mailto:listing@exrates.me?Subject=Add%20coin">listing@exrates.me</a> </p>
                    <p style="margin-top: 8px; margin-bottom: 0;"><b>Sales manager: </b><a href="mailto:listmycoin@exrates.me?Subject=Add%20coin">listmycoin@exrates.me</a> </p>
                    <p style="margin-top: 8px; margin-bottom: 0;"><b>Sales manager: </b><a href="mailto:listme@exrates.me?Subject=Add%20coin">listme@exrates.me</a> </p>
                </div>
                <div class="col-md-4 col-md-offset-1">
                    <div class="pipedriveWebForms" data-pd-webforms="https://pipedrivewebforms.com/form/020d70347deb09bd6f285e7bb17c1c523330571" id="ideqeofa" style="width: 100%; height: 100%; overflow: hidden; min-width: 320px; position: relative;">
                        <script src="/client/js/webforms.min.js"></script><iframe src="https://pipedrivewebforms.com/form/020d70347deb09bd6f285e7bb17c1c523330571?embeded=1&amp;uuid=ideqeofa" scrolling="no" name="https://exrates.me/partners-ideqeofa" style="border: none; overflow: hidden; width: 100%; max-width: 768px; min-width: 320px; height: 749px; position: relative;"></iframe>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-8 col-md-offset-2 content sponsors" style="background-color: white; margin-top: 2.5%;">
            <h1 style="text-align: center">Our partners</h1>
            <table class="img-table">
                <tbody>
                <tr>
                    <td colspan="2"><img class="img-thumbnail partners-img" src="/client/img/partners/coingecko.png"></td>
                    <td><img class="img-thumbnail partners-img"></td>
                    <td colspan="2"><img class="img-thumbnail partners-img" src="/client/img/partners/icomarks.png"></td>
                </tr>
                <tr>
                    <td><img class="img-thumbnail partners-img" src="/client/img/partners/icoalert.png"></td>
                    <td><img class="img-thumbnail partners-img"></td>
                    <td><img class="img-thumbnail partners-img" src="/client/img/partners/coinmarketcap.png"></td>
                    <td><img class="img-thumbnail partners-img"></td>
                    <td><img class="img-thumbnail partners-img" src="/client/img/partners/jedis.png"></td>
                </tr>
                <tr>
                    <td colspan="2"><img class="img-thumbnail partners-img" src="/client/img/partners/ICOUnicorn.png"></td>
                    <td><img class="img-thumbnail partners-img"></td>
                    <td colspan="2"><img class="img-thumbnail partners-img" src="/client/img/partners/trueusdcoin.png"></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div style="margin-bottom: 5%"></div>

</main>
<%@include file='../fragments/footer.jsp' %>
</body>
</html>
