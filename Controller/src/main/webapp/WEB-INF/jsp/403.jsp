<%@ page language="java" contentType="text/html;charset=UTF-8"
 pageEncoding="UTF-8"%>
 <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<title><loc:message code="accessDenied.title" /> </title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>
    <meta charset="UTF-8">

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

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

    <%@include file="tools/alexa.jsp" %>
    <%--<%@include file="tools/yandex.jsp" %>--%>
</head>
<body>
<%@include file="fragments/header-simple.jsp" %>
<main class="container">
    <div class="row">
        <div class="col-md-4 col-md-offset-4 content">
            <div class="white-box text-center">
                <div class="access-denied-header">
                    <img id="access-denied-img" src="<c:url value='/client/img/access_denied.png'/>">
                    <span id="access-denied-title"><loc:message code="accessDenied.title" />
                </span>
                </div>
                <p><loc:message code="accessDenied.message" /></p>

                <c:url value="/logout" var="logoutUrl" />
                <form action="${logoutUrl}" method="post">
                    <input type="submit" class="blue-box" style="width: auto" value="<loc:message code="accessDenied.signInDifferentUser" />" />
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                </form>
            </div>


        </div>


    </div>
</main>




<%@include file='fragments/footer.jsp' %>
</body>
</html>