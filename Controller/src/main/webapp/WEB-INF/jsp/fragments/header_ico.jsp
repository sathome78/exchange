<%@include file="../tools/google_body.jsp"%>
<%@ page import="me.exrates.controller.AdminController"%>
<%@ page import="org.springframework.web.servlet.support.RequestContext" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%--CAPTCHA--%>
<%@ taglib prefix="botDetect" uri="botDetect" %>
<%--CAPTCHA--%>
<script type="text/javascript" src="/client/js/jquery.cookie.js"></script>
<script src="<c:url value="/client/js/jquery.noty.packaged.min.js"/>"></script>
<script src="<c:url value="/client/js/notifications/notifications.js"/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<link href="https://fonts.googleapis.com/css?family=Montserrat:500,700" rel="stylesheet">

<c:set var="path" value="${fn:replace(pageContext.request.requestURI, '/WEB-INF/jsp', '')}"/>
<c:set var="path" value="${fn:replace(path, '.jsp', '')}"/>
<%--don't show entrance menu item in header for pages that contain it's own capcha because conflict occurs--%>
<sec:authorize access="isAuthenticated()" var="isAuth"/>
<c:set var="showEntrance" value="${
                                (path != '/login')
                                && (path != '/register')
                                && (path != '/forgotPassword')
                                && (path != '/login?error')}"/>
<c:set var="showRegistration" value="${(path != '/register')}"/>
<input id="user_auth_status" type="hidden" value="${isAuth}"/>

<%@include file="banner.jsp"%>
<header class="header">
    <div class="container">
        <div class="cols-md-2"><a href="/" class="logo"><img src="/client/img/Logo_blue.png" alt="Exrates Logo"></a>
        </div>
        <div class="cols-md-8">
            <ul class="nav header__nav">
                <li>
                    <a class="nav__link predictions" href="<c:url value='https://predictionlab.exrates.me/'/>" target="_blank">
                        Predictions
                    </a>
                </li>
                <li id="menu-traiding">
                    <a class="nav__link ieo-text" style="color: #d9dbff;" href="<c:url value='#'/>">IEO</a>
                </li>
                <sec:authorize access="isAuthenticated()">
                    <li><a href="/dashboard?startupPage=balance-page" class="nav__link"><loc:message code="usermenu.mywallets"/></a>
                    </li>
                    <li><a href="/dashboard?startupPage=myhistory" class="nav__link"><loc:message code="usermenu.myorders"/></a>
                    </li>
                    <li><a href="/dashboard?startupPage=orders" class="nav__link"><loc:message code="usermenu.orders"/></a></li>
                </sec:authorize>
                <li><a href="<c:url value="https://news.exrates.me" />" target="_blank" class="nav__link">
                    <loc:message code="dashboard.news"/></a>
                </li>

                <sec:authorize access="isAuthenticated()">
                    <li id="adminka-entry">
                        <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                            <a class="nav__link" href="<c:url value='/2a8fy7b07dxe44'/>">
                                <loc:message code="admin.title"/>
                            </a>
                        </sec:authorize>
                        <sec:authorize access="<%=AdminController.traderAuthority%>">
                            <a class="nav__link" href="<c:url value='/2a8fy7b07dxe44/removeOrder'/>">
                                <loc:message code="manageOrder.title"/>
                            </a>
                        </sec:authorize>
                        <sec:authorize access="<%=AdminController.botAuthority%>">
                            <a class="nav__link" href="<c:url value='/2a8fy7b07dxe44/autoTrading'/>">
                                <loc:message code="admin.title"/>
                            </a>
                        </sec:authorize>
                    </li>
                </sec:authorize>

                <sec:authorize access="isAuthenticated()">
                    <li id="hello-my-friend"><a class="nav__link" href="">

                        <strong><sec:authentication property="principal.username"/></strong></a>
                    </li>
                </sec:authorize>

                <ul class="padding0 pull-right">
                    <sec:authorize access="! isAuthenticated()">
                        <li class="pull-left paddingtop10"> <a id="login_link" data-fancybox href="#login" class="focus-white nav__link"><loc:message code="dashboard.loginText"/></a></li>
                        <%--<a id="login_link" data-fancybox href="#login" class="demo-bar-item">login</a>--%>
                    </sec:authorize>
                </ul>
            </ul>
        </div>
        <div class="cols-md-2 right_header_nav">

            <ul class="padding0">
                <sec:authorize access="! isAuthenticated()">
                    <c:if test="${showRegistration}">
                        <li class="pull-left paddingtop10"> <a id="regT" data-fancybox href="#registration" class="focus-white nav__link"><loc:message code="dashboard.signUp"/></a></li>
                    </c:if>
                </sec:authorize>
                <sec:authorize access="isAuthenticated()">
                    <li class="">
                        <form action="/logout" class="dropdown-menu__logout-form" method="post">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button type="submit" class="logout-button">
                                <loc:message code="dashboard.goOut"/>
                            </button>
                        </form>
                    </li>
                </sec:authorize>

                <li role="presentation" class="dropdown paddingtop10 open-language">
                    <%String lang = (new RequestContext(request)).getLocale().getLanguage();%>
                    <c:set var="lang" value="<%=me.exrates.controller.DashboardController.convertLanguageNameToMenuFormat(lang)%>"/>
                    <a id="language" class="dropdown-toggle focus-white nav__link" data-toggle="dropdown" href="#"
                       role="button" aria-haspopup="true" aria-expanded="false">
                        ${fn:toUpperCase(lang)} <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu choose-language">
                        <li><a href="#" class="language">EN</a></li>
                        <li><a href="#" class="language">RU</a></li>
                        <li><a href="#" class="language">CH</a></li>
                        <li><a href="#" class="language">ID</a></li>
                        <li><a href="#" class="language">KO</a></li>
                        <!--
                        <li><a href="#" class="language">AR</a></li>
                        -->
                    </ul>

                </li>
                <sec:authorize access="isAuthenticated()">
                    <li class="settings-menu-item">
                        <a href="<c:url value="/settings"/>">
                            <span class="glyphicon glyphicon-cog nav__link"></span>
                        </a>
                    </li>
                    <%--<li>
                        <%@include file="../fragments/notification-header.jsp" %>
                    </li>--%>
                </sec:authorize>

            </ul>
        </div>
    </div>

    <!-- Fancybox -->
    <input id="login_error" hidden value='${loginErr}'/>
    <div id="login" class="popup">
        <div class="popup__inner">
            <div class="popup__caption">Log in</div>
            <c:if test="${not empty loginErr}">
                <div class='field__error' style="text-align: center">
                        ${loginErr}
                </div>
            </c:if>

            <form id="login_form" action="/login" class="form" method="post">
                <input type="hidden"  class="csrfC" name="_csrf" value="${_csrf.token}"/>
                <div class="field">
                    <div class="field__label">Email</div>
                    <input id="auth_email" class="field__input" type="email" name="username" placeholder="Email" required>
                </div>
                <div class="field">
                    <div class="field__label">Password</div>
                    <div class="field__pwd-show / js-show-pwd"></div>
                    <input id="auth_pass" class="field__input / js-pwd" type="password" name="password" placeholder="Password" required>
                </div>

                <input id="log_geetest_challenge" type="hidden" name="geetest_challenge">
                <input id="log_geetest_validate" type="hidden" name="geetest_validate">
                <input id="log_geetest_seccode" type="hidden" name="geetest_seccode">

                <div class="field field--btn__new">
                    <input id="login_submit" class="btn__new btn__new--form" value="Authorise me" disabled readonly>
                </div>
            </form>

            <div class="popup__bottom-links-row">
                <a id="go_to_register" class="popup__bottom-link">Go to registration form</a>
                <a id="forgot_pwd" class="popup__bottom-link">Forgot password?</a>
                <a id="forgot_pwd_hide" data-fancybox href="#pwd_restore" class="popup__bottom-link" style="display: none">Forgot password?</a>
            </div>
        </div>
    </div>

    <input id="restore_error" hidden value='${recoveryError}'/>
    <div id="pwd_restore" class="popup">
        <div class="popup__inner">
            <div class="popup__caption">Forgot password?</div>
            <c:if test="${not empty recoveryError}">
                <div class='field__error' style="text-align: center">
                        ${recoveryError}
                </div>
            </c:if>

            <form id="pwd_restore_form" class="form" method="post">
                <div class="field">
                    <div class="field__label">Email</div>
                    <c:choose>
                        <c:when test="${not empty recoveryError}">
                            <input id="email_pwd_restore" class="field__input" type="email" name="email" placeholder="Email" value="${userEmail}" required>
                        </c:when>
                        <c:otherwise>
                            <input id="email_pwd_restore" class="field__input" type="email" name="email" placeholder="Email" required>
                        </c:otherwise>
                    </c:choose>
                    <div id="email_pwd_restore_wrong" class='field__error' style="display:none">
                        Wrong email
                    </div>
                    <div id="email_pwd_restore_notExist" class='field__error' style="display:none">
                        Such email not exists
                    </div>
                </div>

                <div class="field field--btn__new">
                    <input id="pwd_restore_submit" class="btn__new btn__new--form" type="submit" value="Reset password" disabled>
                </div>
            </form>

            <div class="popup__bottom-links-row">
                <a id="back_login" class="popup__bottom-link popup__bottom-link--back">Back to log in</a>
            </div>
        </div>
    </div>

    <div id="registration" class="popup">
        <div class="popup__inner">
            <div class="popup__caption">Registration</div>

            <form id="create_me" class="form" method="post">
                <input id="csrfC" type="hidden"  class="csrfC" name="_csrf"/>
                <%--<div class="field">--%>
                <%--<div class="field__label">Nickname</div>--%>
                <%--<input id="nickname" class="field__input" type="text" name="nickname" placeholder="Nickname" required>--%>
                <%--<div id="nickname_exists" class='field__error' style="display:none">--%>
                <%--Nichname exists--%>
                <%--</div>--%>
                <%--<div id="nichname_wrong" class='field__error' style="display:none">--%>
                <%--Wrong nichname--%>
                <%--</div>--%>
                <%--</div>--%>
                <div class="field">
                    <div class="field__label">Email</div>
                    <input id="email" class="field__input" type="email" name="email" placeholder="Email" required>
                    <div id="reg__email_exists" class='field__error' style="display:none">
                        Email exists
                    </div>
                    <div id="reg__email_wrong" class='field__error' style="display:none">
                        Wrong email
                    </div>
                    <div id="reg__email_regex" class='field__error' style="display:none">
                        Email cannot contain special characters except period (.), plus (+), underscore (_) and dash (-)
                    </div>
                    <div id="reg__email_reequired" class='field__error' style="display:none">
                        Email is required
                    </div>
                </div>

                <div class="field field--btn__new">
                    <input id="reg_submit" class="btn__new btn__new--form" type="submit" value="Create an account" disabled>
                </div>

                <div class="popup__bottom">
                    <div class="popup__privacy">
                        <input id="privacy__checked" class="privacy__checkbox" type="checkbox" />
                        I agree to exrates
                        <a href="/termsAndConditions" class="popup__bottom-link" target="_blank">Terms of Use</a>
                    </div>
                    <div class="popup__bottom-row">Already have an account? <a id="go_login" class="popup__bottom-link">Log in</a></div>
                </div>
            </form>
        </div>
    </div>

    <a data-fancybox id="confirm-success" href="#confirm" class="demo-bar-item" style="display: none">Confirm</a>
    <div id="confirm" class="popup">
        <div class="popup__inner">
            <div class="popup__caption">Confirm the email</div>

            <div class="popup__text">
                We've sent the confirmation link to<br>
                <a id="confirm_email" href="" class="popup__text-link"></a>
            </div>
            <div class="popup__text">
                Please check your email and follow instructions.
            </div>

            <div class="popup__hr"></div>

            <div class="popup__bottom">
                <div class="popup__bottom-row">If you haven't received the email, do the following:</div>
                <div class="popup__bottom-row">
                    Check spam or other folders.<br>
                    Set email address whitelist. <a href="" class="popup__bottom-link">How to set?</a><br>
                    Check the mail client works normally.
                </div>
            </div>
        </div>
    </div>

    <%--Geetest--%>
    <a data-fancybox id="geetest_confirm" href="#getest" class="demo-bar-item" style="display: none">Geetest</a>
    <div id="getest" class="popup">
        <div class="popup__inner">
            <div>
                <label>Verification</label>

                <div id="captcha_mssg" class="popup__text" style="display: none">
                    Resolve the captcha please<br>
                </div>

                <div class="popup__hr"></div>

                <div id="captcha1">
                    <div id="wait1" class="popup__text">
                        Loading verification code...<br>
                    </div>
                </div>
            </div>
            <br>
        </div>
    </div>

</header>

<%--capcha--%>
<c:if test="${showEntrance && !isAuth && captchaType==\"RECAPTCHA\"}">
    <script type="text/javascript" src="<c:url value='/client/js/capchahead.js'/>"></script>
    <c:set value="${pageContext.response.locale}" var="locale"></c:set>
    <c:if test="${locale=='cn'}">
        <c:set value="zh-CN" var="locale"></c:set>
    </c:if>
    <script src="https://www.google.com/recaptcha/api.js?onload=onloadCallbackHead&render=explicit&hl=${locale}"
            async defer>
    </script>
</c:if>

<style>
    .nav__link{
        padding: 14px 10px !important;
    }
    .predictions{
        position: relative;
        padding-right: 34px !important;
    }
    .predictions:after{
        position: absolute;
        top: 8px;
        right: 0;
        content:'New';
        display: inline-block;
        background-color: #34b646;
        padding: 0px 8px;
        -webkit-border-radius: 11px;
        -moz-border-radius: 11px;
        border-radius: 11px;
        text-transform: uppercase;
        color:#fff;
        font-size: 8px;
        line-height: 12px;
        font-family: 'Roboto';
    }
    .ieo-text{
        position: relative;
        padding-right: 34px !important;
    }
    .ieo-text:after{
        position: absolute;
        top: 8px;
        right: 0;
        content:'Soon';
        display: inline-block;
        background-color: #34b646;
        padding: 0px 8px;
        -webkit-border-radius: 11px;
        -moz-border-radius: 11px;
        border-radius: 11px;
        text-transform: uppercase;
        color:#fff;
        font-size: 8px;
        line-height: 12px;
        font-family: 'Roboto';
    }
</style>