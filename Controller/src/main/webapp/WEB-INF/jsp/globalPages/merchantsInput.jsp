<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>--%>
<%--<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>--%>
<%--<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>--%>
<%--<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>--%>
<%--<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>--%>

<%--<html>--%>
<%--<head>--%>
    <%--<title>Exrates</title>--%>
    <%--<link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>--%>

    <%--<meta charset="utf-8">--%>
    <%--<meta http-equiv="X-UA-Compatible" content="IE=edge">--%>
    <%--<meta name="viewport" content="width=device-width, initial-scale=1">--%>
    <%--<meta name="interkassa-verification" content="c4deb5425361141d96dd48d235b6fc4a"/>--%>

    <%--&lt;%&ndash;TOOLS ... &ndash;%&gt;--%>
    <%--<%@include file="../tools/google_head.jsp"%>--%>
    <%--&lt;%&ndash;INTERCOM CHAT&ndash;%&gt;--%>
    <%--<%@include file="../tools/intercom.jsp" %>--%>
    <%--&lt;%&ndash; ... TOOLS&ndash;%&gt;--%>
   <%--&lt;%&ndash; <%@include file="../tools/alexa.jsp" %>&ndash;%&gt;--%>

    <%--<link href='https://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>--%>

    <%--<script src="<c:url value='/client/js/jquery_1.11.3.min.js'/>" type="text/javascript"></script>--%>
    <%--<script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>--%>

    <%--<link href="<c:url value='/client/css/font-awesome.min.css'/>" rel="stylesheet">--%>
    <%--<link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">--%>
    <%--<link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">--%>
    <%--<link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">--%>
    <%--&lt;%&ndash;------&ndash;%&gt;--%>
    <%--<script type="text/javascript" src="<c:url value="/client/js/function.js"/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>--%>

    <%--<script type="text/javascript" src="<c:url value='/client/js/lib/numeral/numbro.min.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value="/client/js/inputOutput/valueInputControl.js"/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value="/client/js/inputOutput/refillConfirmationDialog.js"/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value="/client/js/inputOutput/inputOutput.js"/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value="/client/js/inputOutput/refillCreation.js"/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/tmpl.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/jquery.twbsPagination.min.js'/>"></script>--%>

    <%--<script type="text/javascript" src="<c:url value='/client/js/globalPages/news-init.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/news/news.js'/>"></script>--%>
    <%--&lt;%&ndash;&ndash;%&gt;--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/sockjs114.min.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/app.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/globalPages/settings-init.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/siders/leftSider.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/siders/rightSider.js'/>"></script>--%>
    <%--&lt;%&ndash;&ndash;%&gt;--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/dashboard/chat.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/loc-direction.js'/>"></script>--%>
    <%--&lt;%&ndash;------&ndash;%&gt;--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/stomp.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/kinetic.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/jquery.final-countdown.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/alert-init.js'/>"></script>--%>
    <%--<link href="<c:url value='/client/css/timer.css'/>" rel="stylesheet">--%>

<%--</head>--%>
<%--<body>--%>

<%--<%@include file="../fragments/header-simple.jsp" %>--%>

<%--<main class="container">--%>
    <%--<div class="row_big">--%>
        <%--<%@include file="../fragments/left-sider.jsp" %>--%>
        <%--<div class="cols-md-8 background_white">--%>
            <%--<%@include file="../fragments/merchantsInput-center.jsp" %>--%>
        <%--</div>--%>
        <%--<%@include file="../fragments/right-sider.jsp" %>--%>
    <%--</div>--%>
<%--</main>--%>
<%--<%@include file='../fragments/footer.jsp' %>--%>
<%--<span hidden id="errorNoty">${errorNoty}</span>--%>
<%--<span hidden id="successNoty">${successNoty}</span>--%>
<%--<span hidden id="tabIdx">${tabIdx}</span>--%>

<%--</body>--%>
<%--</html>--%>
