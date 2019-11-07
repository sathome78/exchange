<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="login.title"/></title>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <%----------------------------------------%>
    <%@include file="tools/google_head.jsp"%>

    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value="/client/js/function.js"/>"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/loc-direction.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/login.js'/>"></script>
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
<%@include file="../jsp/tools/google_body.jsp"%>

<main class="container">
    <div class="row">
        <div class="col-sm-4 login__container">
            <hr>

                <c:if test="${pinNeed == null}">
                    <div id="login_block">
                    <h4 class=""><loc:message code="login.title"/></h4>
                    <hr>
                    <c:url value="/login" var="loginUrl"/>
                    <div class="clearfix">
                        <p class="login__error">${error}
                            <br/>
                            <c:if test="${not empty contactsUrl}">
                                <a href="<c:url value='/contacts'/>"><loc:message code="dashboard.contactsAndSupport" /> </a>
                            </c:if>
                        </p>


                        <form action="${loginUrl}" method="post" id="login_form" class="clearfix">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <div class="input-block-wrapper clearfix">
                                <loc:message code="login.email" var="login"/>
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        ${login}
                                    </label>
                                </div>
                                <div class="col-md-7 input-block-wrapper__input-wrapper">
                                    <input id="login__name" name="username" type="email"
                                           autofocus
                                           placeholder="${login}"
                                           class="form-control input-block-wrapper__input"/>
                                </div>
                            </div>
                            <%--Пароль--%>
                            <div class="input-block-wrapper clearfix">
                                <loc:message code="login.password" var="password"/>
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        ${password}
                                    </label>
                                </div>
                                <div class="col-md-7 input-block-wrapper__input-wrapper">
                                    <input id="login__password" name="password"
                                           type="password"
                                           placeholder="${password}"
                                           class="form-control input-block-wrapper__input"/>
                                </div>
                            </div>
                            <div>
                                <a href="/forgotPassword" class="darkblue forgot-password forgot-password--largeform"><loc:message
                                        code="dashboard.forgotPassword"/></a>
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
                                    <botDetect:captcha id="loginFormRegCaptcha" userInputID="captchaCode"/>
                                    <input name="captchaCode" type="text" id="captchaCode"/>
                                    <input type="hidden" name="captchaId" value="loginFormRegCaptcha"/>
                                </div>
                                <div class="col-md-10 input-block-wrapper__error-wrapper">
                                    <p class='cpch-error-message' style="color:red">${cpch}</p>
                                </div>
                            </c:if>
                            <input type="hidden" name="captchaType" value="${captchaType}"/>
                            <%----%>
                            <div class="col-md-10 login__button-wrapper">
                                <button id="login_button" class="login__button" type="submit"><loc:message
                                        code="login.submit"/></button>
                            </div>
                        </form>
                    </div>
                    </div>
                </c:if>

                <c:if test="${pinNeed != null}">
                <%--PIN--%>
                    <div id="pin_block">
                        <h4 class=""><loc:message
                                code="message.pin_code"/></h4>
                        <h5 id="res">${pinNeed}</h5>
                        <hr>
                        <c:url value="/login" var="loginUrl"/>
                        <div class="clearfix">
                            <p class="login__error">${error}
                                <br/>
                                <c:if test="${not empty contactsUrl}">
                                    <a href="<c:url value='/contacts'/>"><loc:message code="dashboard.contactsAndSupport" /> </a>
                                </c:if>
                            </p>
                            <form action="${loginUrl}" method="post" id="pin_code" >
                                <div class="input-block-wrapper clearfix">

                                    <div class="input-block-wrapper clearfix" >
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <loc:message code="message.pin_code" var="pin"/>
                                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">
                                                ${pin}
                                            </label>
                                        </div>
                                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                                            <input id="pin" name="l_pin"
                                                   type="text"
                                                   placeholder="${pin}"
                                                   class="form-control input-block-wrapper__input"/>
                                        </div>
                                    </div>
                                </div>
                                <a id="send_pin_again" style="cursor: pointer; margin-left: 14px;"><loc:message
                                        code="login.pin.sendagain"/></a>
                                <div id="send_pin_res" style="margin-left: 14px;"></div>
                                <br>
                                <div class="col-md-10 login__button-wrapper">
                                    <button class="login__button" id="send_pin" disabled><loc:message
                                            code="login.submit"/></button>
                                </div>
                            </form>
                        </div>
                    </div>
                </c:if>

        </div>
    </div>
</main>
<%@include file='fragments/footer-fixed.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>

</body>
</html>

