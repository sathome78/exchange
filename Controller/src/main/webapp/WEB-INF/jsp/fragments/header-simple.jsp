<%@include file="../tools/google_body.jsp"%>
<%@ page import="me.exrates.controller.AdminController"%>
<%@ page import="org.springframework.web.servlet.support.RequestContext" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script type="text/javascript" src="/client/js/jquery.cookie.js"></script>
<script src="<c:url value="/client/js/jquery.noty.packaged.min.js"/>"></script>
<script src="<c:url value="/client/js/notifications/notifications.js"/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/app.js'/>"></script>
<link href="https://fonts.googleapis.com/css?family=Montserrat:500,700" rel="stylesheet">

<link href="<c:url value='/client/css/action-buttons.css'/>" rel="stylesheet">
<script>
    function close() {
        var banner = document.getElementById("banner");
        console.log(banner);
        banner.style.display = "none"
    }
</script>

<c:set var="path" value="${fn:replace(pageContext.request.requestURI, '/WEB-INF/jsp', '')}"/>
<c:set var="path" value="${fn:replace(path, '.jsp', '')}"/>
<%--don't show entrance menu item in header for pages that contain it's own capcha because conflict occurs--%>
<sec:authorize access="isAuthenticated()" var="isAuth"/>

<div class="banner" id="banner">
    <div class="banner__logo">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 39.88 38" width="30" height="26">
            <defs>
                <style>.cls-1 {
                    fill: #fff;
                }</style>
            </defs>
            <title>Ресурс 1logo-exrates</title>
            <g id="Слой_2" data-name="Слой 2">
                <g id="Слой_1-2" data-name="Слой 1">
                    <path class="cls-1"
                          d="M20.76,38a19.24,19.24,0,0,1-5.51-.8,1,1,0,0,1,.6-2A17.08,17.08,0,0,0,37.81,19,17.08,17.08,0,0,0,15.69,2.82a1,1,0,0,1-.76-.1A1,1,0,0,1,15.07.85,19.15,19.15,0,0,1,39.88,19,19.08,19.08,0,0,1,20.76,38ZM.41,14.89a20.83,20.83,0,0,0,0,8.22H22.46l4-4V18.9l-4-4ZM11.68,4.18l8.27,8.22H1.08A20.78,20.78,0,0,1,6.26,4.18ZM6.26,33.83h5.42l8.27-8.22H1.08A20.78,20.78,0,0,0,6.26,33.83Z"/>
                </g>
            </g>
        </svg>
        <div class="logo-text">Exrates lab</div>
    </div>
    <div class="banner__text" >Get the pump-and-dump monthly prediction of BTC rate</div>
    <a href="https://t.me/exrates_official" target="_blank" class="banner__link">Join</a>
    <div class="banner__nommo">
        <img src="/client/img/nommo.png">
    </div>
    <button class="banner__btn" onclick="document.getElementById('banner').style.display='none';return false;">Close
        <span>
            <svg xmlns="http://www.w3.org/2000/svg" width="12px" height="13px">
                <path fill-rule="evenodd" fill="rgb(255, 255, 255)"
                      d="M12.010,10.740 L10.243,12.509 L6.000,8.263 L1.757,12.509 L-0.010,10.740 L4.232,6.495 L-0.010,2.249 L1.757,0.480 L6.000,4.726 L10.243,0.480 L12.010,2.249 L7.768,6.495 L12.010,10.740 Z"/>
            </svg>
        </span>
    </button>

</div>
<header class="header">
    <div class="container">
        <div class="cols-md-2"><a href="/" class="logo"><img src="/client/img/Logo_blue.png" alt="Exrates Logo"></a>
        </div>
        <div class="cols-md-8" style="overflow-y: hidden;">
            <ul class="nav header__nav">
                <li><a href="/" class="nav__link">
                    <loc:message code="dashboard.trading"/></a>
                </li>
                <li><a href="<c:url value="https://help.exrates.me/"/>" target="_blank" class="nav__link">
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

<style>
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
    /*.predictions{*/
    /*position: relative;*/
    /*}*/
    /*.predictions:after{*/
    /*position: absolute;*/
    /*top: 0;*/
    /*right: 0;*/
    /*content:'New';*/
    /*display: inline-block;*/
    /*background-color: #34b646;*/
    /*padding: 4px 8px;*/
    /*-webkit-border-radius: 11px;*/
    /*-moz-border-radius: 11px;*/
    /*border-radius: 11px;*/
    /*text-transform: uppercase;*/
    /*color:#fff;*/
    /*font-size: 14px;*/
    /*}*/
</style>
