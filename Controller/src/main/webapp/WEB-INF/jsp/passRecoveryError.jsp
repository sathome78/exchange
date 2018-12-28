<%--
  Created by IntelliJ IDEA.
  User: Sasha
  Date: 6/5/2018
  Time: 4:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="registrationform" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="dashboard.resetPasswordTitle"></loc:message></title>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">

    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/loc-direction.js'/>"></script>
    <%----------%>
    <%--capcha--%>
    <c:if test="${captchaType==\"RECAPTCHA\"}">
        <script type="text/javascript" src="<c:url value='/client/js/capcha.js'/>"></script>
        <c:set value="${pageContext.response.locale}" var="locale"></c:set>
        <c:if test="${locale=='cn'}">
            <c:set value="zh-CN" var="locale"></c:set>
        </c:if>
        <script src="https://www.google.com/recaptcha/api.js?onload=onloadCallback&render=explicit&hl=${locale}"
                async defer>
        </script>
    </c:if>
    <%@include file="tools/google_head.jsp"%>
    <%@include file="tools/alexa.jsp" %>
    <%-- <%@include file="tools/yandex.jsp" %>--%>
</head>
<body>
<%@include file="fragments/header.jsp" %>
<%@include file="../jsp/tools/google_body.jsp"%>
<main class="container">
    <div class="row">
        <div class="col-sm-4">
            <hr>
            <h4 class="">
                <loc:message code="dashboard.resetPasswordDoubleClick"/>
            </h4>
            <div class="clearfix">
                <form:form id="settings-user-form"
                           action="/forgotPassword/submit" method="post" modelAttribute="user">
                    <div class="input-block-wrapper clearfix">
                        <loc:message code="login.email" var="adminEmail"/>
                        <div class="col-md-10 input-block-wrapper__input-wrapper">
                            <c:choose>
                                <c:when test="${email == \"anonymousUser\"}">
                                    <form:input id="user-email" path="email" type="email"
                                                placeholder="${adminEmail}"
                                                class="form-control input-block-wrapper__input"/>
                                </c:when>
                                <c:otherwise>
                                    <form:input id="user-email" path="email" type="email"
                                                placeholder="${adminEmail}"
                                                value="${email}"
                                                class="form-control input-block-wrapper__input"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="col-md-10 input-block-wrapper__error-wrapper">
                            <form:errors path="email" class="input-block-wrapper__input"/>
                        </div>
                    </div>
                    <c:if test="${captchaType==\"RECAPTCHA\"}">
                        <%--CAPTCHA GOOGLE--%>
                        <div class="col-md-10 login__captcha-wrapper">
                            <div id="cpch-field" class="login__captcha--recaptcha g-recaptcha"
                                 data-sitekey=${captchaProperties.get("captcha.key")}></div>
                                <%--<p class='cpch-error-message' style="color:red">${cpch}</p>--%>
                            <br/>
                        </div>
                        <div class="col-md-10 input-block-wrapper__error-wrapper">
                            <p class='cpch-error-message' style="color:red">${cpch}</p>
                        </div>
                    </c:if>
                    <c:if test="${captchaType==\"BOTDETECT\"}">
                        <%--CAPTCHA BotDetect--%>
                        <div id="cpch-field" class="col-md-10 login__captcha--botdetect passed">
                            <botDetect:captcha id="forgotFormRegCaptcha" userInputID="captchaCode"/>
                            <input name="captchaCode" type="text" id="captchaCode"/>
                            <input type="hidden" name="captchaId" value="forgotFormRegCaptcha"/>
                        </div>
                        <div class="col-md-10 input-block-wrapper__error-wrapper">
                            <p class='cpch-error-message' style="color:red">${cpch}</p>
                        </div>
                    </c:if>
                    <input type="hidden" name="captchaType" value="${captchaType}"/>
                    <%----%>
                    <div class="col-md-10 login__button-wrapper">
                        <button class="login__button" type="submit">
                            <loc:message code="dashboard.resetPasswordButton"/>
                        </button>
                    </div>
                </form:form>
            </div>
            <br/>
            <br/>
            <br/>

            <div>
                <h5><loc:message
                        code="admin.changePasswordSendEmail"/></h5>
            </div>
        </div>
    </div>
</main>
<%@include file='fragments/footer.jsp' %>
</body>
</html>