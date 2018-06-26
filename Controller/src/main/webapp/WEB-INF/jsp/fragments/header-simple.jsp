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
<script type="text/javascript" src="<c:url value='/client/js/app.js'/>"></script>
<script src="<c:url value="/client/js/notifications/notifications.js"/>"></script>

<link href="<c:url value='/client/css/action-buttons.css'/>" rel="stylesheet">

<c:set var="path" value="${fn:replace(pageContext.request.requestURI, '/WEB-INF/jsp', '')}"/>
<c:set var="path" value="${fn:replace(path, '.jsp', '')}"/>
<%--don't show entrance menu item in header for pages that contain it's own capcha because conflict occurs--%>
<sec:authorize access="isAuthenticated()" var="isAuth"/>
<c:set var="showEntrance" value="${
                                (path != '/login')
                                && (path != '/register')
                                && (path != '/forgotPassword')
                                && (path != '/login?error')}"/>
<header class="header">
    <div class="container">
        <div class="cols-md-2"><a href="/" class="logo"><img src="/client/img/Logo_blue.png" alt="Exrates Logo"></a>
        </div>

        <div class="cols-md-8" style="overflow-y: hidden;">
            <ul class="nav header__nav">
                <li><a href="/" class="nav__link">
                    <loc:message code="dashboard.trading"/></a>
                </li>
                <li><a href="<c:url value="http://support.exrates.me/"/>" target="_blank" class="nav__link">
                    <loc:message code="dashboard.support"/></a>
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

                    <li id="hello-my-friend"><a class="nav__link" href="">
                        <strong><sec:authentication property="principal.username"/></strong></a>
                    </li>
                </sec:authorize>
            </ul>
        </div>
        <div class="cols-md-2 right_header_nav">
            <ul class="padding0">
                <sec:authorize access="! isAuthenticated()">
                    <c:if test="${showEntrance}">
                        <li role="presentation" class="dropdown paddingtop10 open-li">
                            <a class="dropdown-toggle nav__link focus-white" data-toggle="dropdown" href="#"
                               role="button"
                               aria-haspopup="true" aria-expanded="false">
                                <loc:message code="dashboard.entrance"/> <span class="caret"></span>
                            </a>

                            <div class="dropdown-menu">
                                <form action="/login" class="dropdown-menu__form" method="post">
                                    <input name="username" type="email" placeholder=
                                        <loc:message code="dashboard.loginText"/>
                                            class="form_input">
                                    <input name="password" type="password" placeholder=
                                        <loc:message
                                                code="dashboard.passwordText"/> class="form_input">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                    <br/>
                                    <c:if test="${captchaType==\"RECAPTCHA\"}">
                                        <%--CAPTCHA GOOGLE--%>
                                        <div id="cpch-head-field" class="g-recaptcha"
                                             data-sitekey=${captchaProperties.get("captcha.key")}></div>
                                        <p class='cpch-error-message' style="color:red">${cpch}</p>
                                    </c:if>
                                    <c:if test="${captchaType==\"BOTDETECT\"}">
                                        <%--CAPTCHA BotDetect--%>
                                        <div class="validationDiv">
                                            <botDetect:captcha id="headerRegCaptcha" userInputID="captchaCode"/>
                                            <input name="captchaCode" type="text" id="captchaCode"/>
                                            <input type="hidden" name="captchaId" value="headerRegCaptcha"/>
                                        </div>
                                    </c:if>
                                    <input type="hidden" name="captchaType" value="${captchaType}"/>
                                        <%----%>
                                    <button type="submit" class="login_button"><loc:message
                                            code="dashboard.entrance"/></button>
                                    <a href="/forgotPassword" class="white forgot-password"><loc:message
                                            code="dashboard.forgotPassword"/></a>

                                    <div></div>
                                        <%--QR--%>
                                        <%--<div class="inline-block"><img src="/client/img/qr.jpg"></div>
                                        <span class="white margin-left20">To enter via QR code</span>--%>
                                    <sec:authorize access="! isAuthenticated()">
                                        <a href="/register" class="register"><loc:message code="dashboard.signUp"/></a>
                                    </sec:authorize>
                                </form>
                                <sec:authorize access="isAuthenticated()">
                                    <form action="/logout" class="dropdown-menu__logout-form" method="post">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <button type="submit" class="register">
                                            <strong><loc:message code="dashboard.goOut"/></strong>
                                        </button>
                                    </form>
                                </sec:authorize>
                            </div>
                        </li>
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
                <li class="home-menu-item">
                    <a href="/">
                        <span class="glyphicon glyphicon-home nav__link"></span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</header>

<%@include file="../fragments/alerts.jsp" %>
<input type="hidden" class="s_csrf" name="${_csrf.parameterName}" value="${_csrf.token}"/>
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
