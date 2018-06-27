<%--
  Created by IntelliJ IDEA.
  User: ogolv
  Date: 09.08.2016
  Time: 8:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title><loc:message code="dashboard.contactsAndSupport"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>
    <meta charset="UTF-8">

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="interkassa-verification" content="c4deb5425361141d96dd48d235b6fc4a"/>

    <%----------------------------------------%>
    <%@include file="../tools/google_head.jsp"%>
    <%@include file="../tools/alexa.jsp" %>
    <%--<%@include file="../tools/yandex.jsp" %>--%>

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

    <%--... Alerts --%>
    <script src="https://cdn.jsdelivr.net/sockjs/1/sockjs.min.js"></script>
    <script type="text/javascript" src="<c:url value='/client/js/stomp.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/kinetic.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/jquery.final-countdown.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/alert-init.js'/>"></script>
    <link href="<c:url value='/client/css/timer.css'/>" rel="stylesheet">

</head>
<body>
<%@include file="../fragments/header-simple.jsp" %>
<main class="container">
    <div class="row">
        <h3><loc:message code="dashboard.contactsAndSupport"/></h3>
        <hr/>

        <div class="col-md-6 col-md-offset-3 content">
           <div class="text-center">
               <h4><loc:message code="contacts.feedback"/></h4>
           </div>
            <div class="panel-body">
                <form:form action="/sendFeedback" id="feedback-form" cssClass="form_auto_height form_full_width"
                           method="post" accept-charset="UTF-8" modelAttribute="messageForm">
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label for="sender-name" class="input-block-wrapper__label"><loc:message
                                    code="contacts.name"/></label>
                        </div>

                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <form:input path="senderName" class="input-block-wrapper__input full-width"
                                   id="sender-name"/>
                            <form:errors path="senderName" class="input-block-wrapper__input red"/>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label for="sender-email" class="input-block-wrapper__label"><loc:message
                                    code="login.email"/></label>
                        </div>
                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <form:input path="senderEmail" class="input-block-wrapper__input full-width"
                                   id="sender-email"/>
                            <form:errors path="senderEmail" class="input-block-wrapper__input red"/>
                        </div>
                    </div>

                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label for="message-text" class="input-block-wrapper__label"><loc:message
                                    code="contacts.messageText"/></label>
                        </div>
                        <div class="col-md-9">
                            <form:textarea path="messageText" class="textarea non-resize"
                                           id="message-text"></form:textarea>
                            <form:errors path="messageText" class="input-block-wrapper__input red"/>

                        </div>
                    </div>
                    <br/>
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">

                        </div>
                        <div class="col-md-9">
                            <c:if test="${captchaType==\"RECAPTCHA\"}">
                                <%--CAPTCHA GOOGLE--%>
                                <div id="cpch-head-field" class="g-recaptcha"
                                     data-sitekey=${captchaProperties.get("captcha.key")}></div>
                                <p class='cpch-error-message' style="color:red">${cpch}</p>
                            </c:if>
                            <c:if test="${captchaType==\"BOTDETECT\"}">
                                <%--CAPTCHA BotDetect--%>
                                <div class="validationDiv">
                                    <botDetect:captcha id="feedbackMessageCaptcha" userInputID="captchaCode"/>
                                    <input name="captchaCode" type="text" id="captchaCode"/>
                                    <input type="hidden" name="captchaId" value="feedbackMessageCaptcha"/>
                                </div>
                            </c:if>
                            <input type="hidden" name="captchaType" value="${captchaType}"/>


                        </div>
                    </div>
                    <div class="input-block-wrapper paddingtop10">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                        </div>
                        <div class="col-md-9">
                            <button id="feedbackSubmit" type="submit" class="blue-box"><loc:message
                                    code="dashboard.onlinechatsend"/></button>
                        </div>
                    </div>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>


                    <%----%>



                </form:form>

        </div>
    </div>

    </div>
    <div class="row">
        <loc:message code="contacts.address"/>
        <p><loc:message code="contacts.contactPhone"/> </p>
        <p><loc:message code="contacts.fax"/> </p>
    </div>
    <div style="margin-bottom: 15%"></div>


</main>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
<%@include file='../fragments/footer.jsp' %>
</body>
</html>
