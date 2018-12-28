<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>Exrates</title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="interkassa-verification" content="c4deb5425361141d96dd48d235b6fc4a"/>

    <%----------%>
    <%--TOOLS ... --%>
    <%@include file="../tools/google_head.jsp"%>
    <%--ZOPIM CHAT--%>
    <%--<%@include file="../tools/zopim.jsp" %>--%>
    <%-- ... TOOLS--%>
   <%-- <%@include file="../tools/alexa.jsp" %>--%>
    <%--<%@include file="../tools/yandex.jsp" %>--%>

    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/jquery.dataTables.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/dataTables.bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/font-awesome.min.css'/>" rel="stylesheet">

<%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/tmpl.js'/>"></script>
    <%----%>
    <script type="text/javascript" src="<c:url value='/client/js/lib/numeral/numbro.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/sockjs114.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/app.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/globalPages/settings-init.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/siders/leftSider.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/siders/rightSider.js'/>"></script>
    <%----%>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dashboard/chat.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/loc-direction.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notifications/notification-settings.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/client/js/stomp.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/kinetic.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/jquery.final-countdown.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/alert-init.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/lib/jquery-datatables/jquery.dataTables.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/settings/api-settings.js'/>"></script>
    <link href="<c:url value='/client/css/timer.css'/>" rel="stylesheet">
    <%@include file="../tools/newCapchaScripts.jsp" %>
    <script type="text/javascript">
        $(function () {
            $('#return-btn').click(function () {
                window.location.href = '/settings';
            })
        })

    </script>

</head>
<body>

<%@include file="../fragments/header-simple.jsp" %>
<%@include file="../tools/google_body.jsp"%>

<main class="container">
    <div class="row_big">
        <%@include file="../fragments/left-sider.jsp" %>
        <div class="cols-md-8 background_white">
            <div class="row">
                <h4 class="h4_green">
                    <loc:message code="api.user.settings"/>
                </h4>
                <h4 class="under_h4_margin"></h4>
                <div class="container">
                    <div class="row text-center">
                        <p class="red" style="font-size: 1.5rem"><loc:message code="api.user.settings.privateKey.warning"/></p>
                        <div class="col-md-8 col-md-offset-2 well">
                            <form class="form-horizontal">
                                <div class="form-group">
                                    <label style="font-size: 1.5rem" class="control-label col-sm-2" for="publicKey"><loc:message code="api.user.settings.publicKey"/></label>
                                    <div class="col-sm-10">
                                        <input type="text" class="form-control" id="publicKey" value='<c:out value="${publicKey}"/>' readonly>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label style="font-size: 1.5rem" class="control-label col-sm-2" for="privateKey"><loc:message code="api.user.settings.privateKey"/></label>
                                    <div class="col-sm-10">
                                        <input type="text" class="form-control" id="privateKey" value='<c:out value="${privateKey}"/>' readonly>
                                    </div>
                                </div>
                            </form>

                        </div>
                    </div>
                </div>
                <button class="blue-box" id="return-btn"><loc:message code="api.user.settings.return"/></button>

            </div>
        </div>
        <%@include file="../fragments/right-sider.jsp" %>
    </div>
</main>
<%@include file='../fragments/footer.jsp' %>

<c:if test="${message!=null}">
    <label class="alert-danger"><loc:message code="${message}"/></label>
</c:if>

</body>
</html>
