<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script src="<c:url value="/client/js/jquery.noty.packaged.min.js"/>"></script>
<link href="https://fonts.googleapis.com/css?family=Montserrat:500,700" rel="stylesheet">
<a href="https://t.me/exrates_official" target="_blank" class="banner-wrap" id="banner">
    <span class="banner">
        <span class="banner__logo">
        <div class="logo-text">Exrates lab</div>
    </span>
    <span class="banner__text">Get the pump-and-dump monthly prediction of BTC rate</span>
    <span class="banner__link">Join</span>
    <span class="banner__nommo">
        <img src="/client/img/nommo.png">
    </span>
    <button class="banner__btn"onclick="document.getElementById('banner').style.display='none';return false;">Close
        <span>
            <svg xmlns="http://www.w3.org/2000/svg" width="12px" height="13px">
                <path fill-rule="evenodd" fill="rgb(255, 255, 255)"
                      d="M12.010,10.740 L10.243,12.509 L6.000,8.263 L1.757,12.509 L-0.010,10.740 L4.232,6.495 L-0.010,2.249 L1.757,0.480 L6.000,4.726 L10.243,0.480 L12.010,2.249 L7.768,6.495 L12.010,10.740 Z"/>
            </svg>
        </span>
    </button>
    </span>
</a>
<header>
    <c:set var="path" value="${fn:replace(pageContext.request.requestURI, '/WEB-INF/jsp', '')}"/>
    <c:set var="path" value="${fn:replace(path, '.jsp', '')}"/>
    <%@include file="../jsp/tools/google_head.jsp"%>
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
    .banner-wrap{
        text-decoration: none;
    }
    .banner {
        display: flex;
        align-items: center;
        height: 32px;
        min-width: 1220px;
        position: relative;
        background-image: url(/client/img/bg.png);
        background-size: 100% 100%;
        background-repeat: no-repeat;
        background-position: top center;
        font-family: 'Montserrat';
    }

    .banner__logo {
        display: flex;
        align-items: center;
        margin-left: 30px;

    }

    .banner__logo>.logo {
        width: 26px;
        height: 26px;
    }

    .banner__logo>.logo-text {
        font-size: 14px;
        font-weight: bold;
        margin-left: 8px;
        text-transform: uppercase;
        color: #fff;
    }

    .banner__text {
        position: relative;
        z-index: 2;
        font-weight: bold;
        font-size: 10px;
        color: #fff;
        text-transform: uppercase;
        margin: 0 35px;
    }

    .banner__link {
        position: relative;
        z-index: 2;
        display: inline-block;
        background-color: #fff;
        padding: 6px 12px;
        -webkit-border-radius: 12px;
        -moz-border-radius: 12px;
        border-radius: 12px;
        color: #105dfb;
        text-transform: uppercase;
        text-decoration: none;
        font-weight: bold;
        font-size: 10px;
    }

    .banner__nommo {
        margin-left: 40px;
        height: 100%;
    }

    .banner__btn {
        margin-left: auto;
        margin-right: 30px;
        color: #fff;
        font-weight: 500;
        font-size: 10px;
        font-family: 'Montserrat';
        background-color: transparent;
        border: none;
    }

    .banner__btn:hover {
        cursor: pointer;
    }

    .banner__btn span {
        display: inline-block;
        width: 13px;
        height: 13px;
        margin-left: 10px;
        vertical-align: middle;
    }
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