<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
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
    <title><loc:message code="register.title"/></title>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <%----------------------------------------%>
    <%@include file="tools/google_head.jsp"%>
    <%@include file="tools/alexa.jsp" %>
    <%--<%@include file="tools/yandex.jsp" %>--%>

    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value="/client/js/function.js"/>"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
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

</head>


<body>

<%@include file="fragments/header.jsp" %>

<main class="container">
    <div class="row">
        <div class="col-sm-4 login__container">
            <hr>
            <h4 class=""><loc:message code="register.title"/></h4>
            <hr>

            <form:form method="post" action="create" modelAttribute="user" >
                <%--nickname--%>
                <%--<div class="input-block-wrapper clearfix">--%>
                    <%--<loc:message code="register.nickname" var="login"/>--%>
                    <%--<div class="col-md-11 input-block-wrapper__input-wrapper">--%>
                        <%--<form:input id="login" path="nickname"--%>
                                                <%--placeholder="${login}"--%>
                                                <%--class="form-control input-block-wrapper__input"/>--%>
                    <%--</div>--%>
                    <%--<div class="col-md-11 input-block-wrapper__error-wrapper">--%>
                        <%--<form:errors path="nickname" class="input-block-wrapper__input"/>--%>
                    <%--</div>--%>
                <%--</div>--%>
                <%--email--%>
                <div class="input-block-wrapper clearfix">
                    <loc:message code="register.email" var="email"/>
                    <div class="col-md-11 input-block-wrapper__input-wrapper">
                        <form:input id="email" path="email"
                                                placeholder="${email}" type="email"
                                                class="form-control input-block-wrapper__input"/>
                    </div>
                    <div class="col-md-11 input-block-wrapper__error-wrapper">
                        <form:errors path="email" class="input-block-wrapper__input"/>
                    </div>
                    <div class="col-md-11">
                        <loc:message code="register.loginLabel"/>
                    </div>
                </div>
                <%--Password--%>
                <div class="input-block-wrapper clearfix">
                    <loc:message code="register.password" var="password"/>
                    <div class="col-md-11 input-block-wrapper__input-wrapper">
                        <form:input id="pass" path="password"
                                                type="password"
                                                placeholder="${password}"
                                                class="form-control input-block-wrapper__input"/>
                    </div>
                    <div class="col-md-11 input-block-wrapper__error-wrapper">
                        <form:errors path="password" class="input-block-wrapper__input"/>
                    </div>
                </div>
                <%--Re password--%>
                <div class="input-block-wrapper clearfix">
                    <loc:message code="register.repeatpassword" var="repassword"/>
                    <div class="col-md-11 input-block-wrapper__input-wrapper">
                        <form:input id="repass" path="confirmPassword"
                                                type="password"
                                                placeholder="${repassword}"
                                                class="form-control input-block-wrapper__input"/>
                    </div>
                    <span class="repass green"><i class="glyphicon glyphicon-ok"></i></span>
                    <span class="repass-error red"><i class="glyphicon glyphicon-remove"></i></span>
                    <div class="col-md-11 input-block-wrapper__error-wrapper">
                        <form:errors path="confirmPassword" class="input-block-wrapper__input"/>
                    </div>
                </div>
                <%--refferal--%>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-11 input-block-wrapper__label-wrapper">
                        <loc:message code="register.sponsor"/>
                    </div>

                    <div class="col-md-11 input-block-wrapper__input-wrapper">
                        <form:input id="parentEmail" path="parentEmail"
                                                readonly="true"
                                                class="form-control input-block-wrapper__input"/>
                    </div>
                </div>
                <c:if test="${captchaType==\"RECAPTCHA\"}">
                    <%--CAPTCHA GOOGLE--%>
                    <div class="col-md-11 login__captcha-wrapper">
                        <div id="cpch-field" class="login__captcha--recaptcha g-recaptcha"
                             data-sitekey=${captchaProperties.get("captcha.key")}></div>
                            <%--<p class='cpch-error-message' style="color:red">${cpch}</p>--%>
                        <br/>
                    </div>
                    <div class="col-md-11 input-block-wrapper__error-wrapper">
                        <p class='cpch-error-message' style="color:red">${cpch}</p>
                    </div>
                </c:if>
                <c:if test="${captchaType==\"BOTDETECT\"}">
                    <%--CAPTCHA BotDetect--%>
                    <div id="cpch-field" class="col-md-11 login__captcha--botdetect passed">
                        <botDetect:captcha id="registerFormRegCaptcha" userInputID="captchaCode"/>
                        <input name="captchaCode" type="text" id="captchaCode"/>
                        <input type="hidden" name="captchaId" value="registerFormRegCaptcha"/>
                    </div>
                    <div class="col-md-11 input-block-wrapper__error-wrapper">
                        <p class='cpch-error-message' style="color:red">${cpch}</p>
                    </div>
                </c:if>
                <input type="hidden" name="captchaType" value="${captchaType}"/>
                <%----%>
                <div class="col-md-11 login__button-wrapper">
                    <button id="register_button" class="login__button" type="submit"><loc:message
                            code="register.submit"/></button>
                </div>
            </form:form>
        </div>
    </div>
</main>
<%@include file='fragments/footer-fixed.jsp' %>
</body>
</html>

