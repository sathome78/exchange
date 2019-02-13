<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script src="<c:url value="/client/js/jquery.noty.packaged.min.js"/>"></script>
<link href="https://fonts.googleapis.com/css?family=Montserrat:500,700" rel="stylesheet">

<%@include file="fragments/banner.jsp"%>
<header>
    <c:set var="path" value="${fn:replace(pageContext.request.requestURI, '/WEB-INF/jsp', '')}"/>
    <c:set var="path" value="${fn:replace(path, '.jsp', '')}"/>
    <%--don't show entrance menu item in header for pages that contain it's own capcha because conflict occurs--%>
    <sec:authorize access="isAuthenticated()" var="isAuth"/>
    <c:set var="showEntrance" value="${
                                (path != '/login')
                                && (path != '/register')
                                && (path != '/forgotPassword')
                                && (path != '/login?error')}"/>
    <nav class="navbar">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                        aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/"><img src="/client/img/logo.png" alt="Logo"></a>
            </div>

            <div id="navbar" class="collapse navbar-collapse">
                <ul class="nav navbar-nav">
                    <sec:authorize access="isAuthenticated()">
                        <li>
                            <a href="/" class="navabr__link active"><loc:message code="dashboard.general"/></a>
                        </li>
                        <li><a href=""><strong><sec:authentication
                                property="principal.username"/></strong></a></li>

                    </sec:authorize>
                </ul>
            </div>
            <!--/.nav-collapse -->
        </div>
    </nav>
    <%--csrf--%>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</header>

<!-- Modal SIGN IN -->
<sec:authorize access="!isAuthenticated()">
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="myModalLabel"><loc:message code="dashboard.entrance"/></h4>
                </div>
                <div class="modal-body modal-content__input-block-wrapper">
                    <div class="content modal-content__content-wrapper">
                        <c:url value="/login" var="loginUrl"/>
                        <form action="${loginUrl}" method="post">
                                <%--логин--%>
                            <input type="text" name="username" placeholder=<loc:message code="dashboard.loginText"/>>
                                <%--пароль--%>
                            <input type="password" name="password" placeholder=<loc:message
                                    code="dashboard.passwordText"/>>
                            <br/>
                                <%--CAPCHA--%>
                            <div id="cpch-head-field" class="g-recaptcha"
                                 data-sitekey=${captchaProperties.get("captcha.key")}></div>
                            <p class='cpch-error-message' style="color:red">${cpch}</p>
                                <%--войти--%>
                            <button type="submit" class="button_enter"><loc:message code="dashboard.entrance"/></button>
                                <%--Забыли пароль?--%>
                            <a class="button_forgot" href="/forgotPassword"><loc:message
                                    code="dashboard.forgotPassword"/></a>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</sec:authorize>

<%--capcha--%>
<c:if test="${showEntrance && !isAuth}">
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
</style>