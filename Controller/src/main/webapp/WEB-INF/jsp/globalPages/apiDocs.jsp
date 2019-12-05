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
<%@ taglib tagdir="/WEB-INF/tags" prefix="ex" %>
<!DOCTYPE html>
<html>
<head>
    <title><loc:message code="apiDoc.title"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="interkassa-verification" content="c4deb5425361141d96dd48d235b6fc4a"/>
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
    <%@include file="../tools/google_head.jsp"%>
    <%--alexa закоментировано т.к. не используется в данный момент--%>
<%--    <%@include file="../tools/alexa.jsp" %>--%>
    <%@include file="../tools/newCapchaScripts.jsp" %>
    <%--<%@include file="../tools/yandex.jsp" %>--%>
</head>
<body>
<%@include file="../fragments/header-simple.jsp" %>
<%@include file="../tools/google_body.jsp"%>
<main class="container">
    <div class="row">
        <div id="api-method-doc" class="col-md-8 col-md-offset-2 well">
            <div class="col-md-10 col-md-offset-1 content legal_content" >
                <h3><loc:message code="apiDoc.title"/></h3>
                <hr/>
                <div class="col-md-10 col-md-offset-1">
                    <h4><loc:message code="apiDoc.overview.title"/></h4>
                    <p><loc:message code="apiDoc.overview.content" arguments="${baseUrl}"/></p>
                    <hr/>

                    <h4 class="method-group-title"><strong><loc:message code="apiDoc.public.title"/>:</strong></h4>
                    <p><loc:message code="apiDoc.public.overview"/> </p>
                    <ex:api methodsInfo="${publicMethodsInfo}" baseUrl="${baseUrl}" />

                    <hr/>
                    <h4 class="method-group-title"><strong><loc:message code="apiDoc.auth.title"/>:</strong></h4>
                    <p><loc:message code="apiDoc.auth.description"/></p>
                    <div class="apidoc_methods_container">
                        <p><loc:message code="apiDoc.auth.headers.desc"/></p>
                        <div class="well">
                            <table class="apidoc_table">
                                <tbody>
                                <tr>
                                    <td><i>API-KEY</i></td><td><loc:message code="apiDoc.auth.headers.key"/></td>
                                </tr>
                                <tr>
                                    <td><i>API-TIME</i></td><td><loc:message code="apiDoc.auth.headers.time"/></td>
                                </tr>
                                <tr>
                                    <td><i>API-SIGN</i></td><td><loc:message code="apiDoc.auth.headers.sign"/></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        <p><loc:message code="apiDoc.auth.algo.desc"/></p>
                        <div class="well">
                            <p><loc:message code="apiDoc.auth.algo"/></p>
                        </div>
                        <div class="well">
                            <table class="apidoc_table">
                                <tbody>
                                <tr>
                                    <td><i>http_method</i></td><td><loc:message code="apiDoc.auth.algo.method"/></td>
                                </tr>
                                <tr>
                                    <td><i>endpoint</i></td><td><loc:message code="apiDoc.auth.algo.endpoint"/></td>
                                </tr>
                                <tr>
                                    <td><i>timestamp</i></td><td><loc:message code="apiDoc.auth.algo.timestamp"/></td>
                                </tr>
                                <tr>
                                    <td><i>public_key, private_key</i></td><td><loc:message code="apiDoc.auth.algo.keys"/></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>






                    <hr/>
                    <h4 class="method-group-title"><strong><loc:message code="apiDoc.userInfo.title"/>:</strong></h4>
                    <ex:api methodsInfo="${userMethodsInfo}" baseUrl="${baseUrl}" />
                    <hr/>
                    <h4 class="method-group-title"><strong><loc:message code="apiDoc.orders.title"/>:</strong></h4>
                    <ex:api methodsInfo="${orderMethodsInfo}" baseUrl="${baseUrl}" />






                </div>

            </div>
        </div>


    </div>
</main>
<%@include file='../fragments/footer.jsp' %>
</body>
</html>
