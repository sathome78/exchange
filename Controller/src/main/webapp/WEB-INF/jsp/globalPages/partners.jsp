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
    <title><loc:message code="dashboard.terms"/></title>
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

    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/loc-direction.js'/>"></script>
    <%@include file="../tools/alexa.jsp" %>
    <%@include file="../tools/ga.jsp" %>
    <%--<%@include file="../tools/yandex.jsp" %>--%>
</head>
<body>
<%@include file="../fragments/header-simple.jsp" %>
<main class="container">
    <div class="row">
        <div class="col-md-8 col-md-offset-2 content legal_content">
            <c:set var="termsHeading">
                <loc:message code="partners.title"/>
            </c:set>
            <c:set var="supportLink">
                <a href="<c:url value="/contacts"/>"><loc:message code="dashboard.contactsAndSupport"/></a>
            </c:set>
            <h3>${fn:toUpperCase(termsHeading)}</h3>
            <hr/>
            <div style="margin-bottom: 30px" id="termsContent">
                <loc:message code="partners.content" arguments="${supportLink}"/>
            </div>

        </div>

    </div>
</main>
<%@include file='../fragments/footer.jsp' %>
</body>
</html>
