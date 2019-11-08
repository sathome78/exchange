<%--
  Created by IntelliJ IDEA.
  User: ogolv
  Date: 01.08.2016
  Time: 14:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html ng-app='app'
      ng-controller="rootCtrl as rootCtrl">
<head>
  <title><loc:message code="dashboard.privacy"/></title>
  <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="interkassa-verification" content="c4deb5425361141d96dd48d235b6fc4a"/>

  <%----------------------------------------%>
  <%@include file="../tools/google_head.jsp"%>

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

  <%--Angular and binded ... --%>
  <%--base--%>
  <script type="application/javascript" src="/client/js/angular/1.5.8/angular.1.5.8.min.js"></script>
  <script type="application/javascript" src="/client/js/angular/1.5.8/angular-cookies.js"></script>
  <script type="application/javascript" src="client/js/angular/1.5.8/angular-route.js"></script>
  <%--modules--%>
  <script type="application/javascript" src="/client/js/angular/module/aboutUs/aboutUs.js"></script>
  <%--general directives and services--%>
  <script type="application/javascript" src="/client/js/angular/general/directive/bindCompiledHtml.js"></script>
  <script type="application/javascript" src="/client/js/angular/general/service/languageFactory.js"></script>
  <script type="application/javascript" src="/client/js/angular/general/controller/rootCtrl.js"></script>
  <script type="application/javascript" src="/client/js/angular/general/service/rootFactory.js"></script>
  <script type="application/javascript" src="/client/js/angular/general/controller/newsUploadCtrl.js"></script>
  <script type="application/javascript" src="/client/js/angular/general/service/newsUploadFactory.js"></script>
  <script type="application/javascript" src="/client/js/angular/general/service/topicFactory.js"></script>
  <script type="application/javascript" src="/client/js/angular/general/controller/topicCtrl.js"></script>
  <script type="application/javascript" src="/client/js/angular/general/service/newsManipulatorFactory.js"></script>
  <%--controllers and services--%>
  <script type="application/javascript" src="/client/js/angular/module/aboutUs/controller/aboutUsCtrl.js"></script>
  <script type="application/javascript" src="/client/js/angular/module/aboutUs/service/aboutUsFactory.js"></script>
  <%--... Alerts --%>
  <script type="text/javascript" src="<c:url value='/client/js/sockjs114.min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/client/js/stomp.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/client/js/kinetic.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/client/js/jquery.final-countdown.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/client/js/alert-init.js'/>"></script>
  <link href="<c:url value='/client/css/timer.css'/>" rel="stylesheet">
  <%--... Angular and binded--%>
  <%@include file="../tools/newCapchaScripts.jsp" %>
</head>

<style>
  .materials-page-content {
    background: transparent;
  }
</style>
<body>
<%@include file="../fragments/header-simple.jsp" %>
<%@include file="../tools/google_body.jsp"%>
<main class="primary container"
      ng-controller="aboutUsCtrl as aboutUsCtrl">
  <div class="row">
    <div class="col-md-8 col-md-offset-2 content legal_content">
      <h3><loc:message code="dashboard.aboutUs"/></h3>
      <hr/>
      <div class="materials-page-content"
           bind-compiled-html="aboutUsCtrl.topic.content">
      </div>
      <div>
        <jsp:include page="../fragments/news-editAndDelete.jsp">
          <jsp:param name="ctrl" value="aboutUsCtrl"/>
        </jsp:include>
      </div>
    </div>
  </div>
</main>

<%@include file='../fragments/footer.jsp' %>

<section>
  <jsp:include page="../fragments/modal/news_pageMaterials_add_modal.jsp"/>
</section>

</body>
</html>
