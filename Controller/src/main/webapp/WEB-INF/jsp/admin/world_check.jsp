<%--
  User: Sasha
  Date: 7/19/2018
  Time: 11:13 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title><loc:message code="admin.users"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/2faSettings.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/kyc-admin.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
</head>

<body id="main-admin">

<%@include file='../fragments/header-simple.jsp' %>

<main class="container orders_new admin side_menu">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-sm-9 content">
            <div class="container">
                <div class="row">
                    <div class="col-sm-8 content">
                        <div class="tab-content">
                            <div class="col-sm-9 content">

                                <div class="input-block-wrapper clearfix">
                                    <div class="col-md-6 input-block-wrapper__label-wrapper">
                                        <label class="input-block-wrapper__label">User</label>
                                    </div>
                                    <div class="col-md-6 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                        <a target="_blank" href="/kyc/getKyc?userId=${wc.userId}" class="alert-danger settings-upload-files">
                                            KYC data</a>
                                    </div>
                                </div>

                                <div class="col-sm-6" style="padding: 0;">
                                    <div class="confirm-button-wrapper">
                                        <button class="btn btn-success"onclick="approveWordCheck(${wc.userId})">Approve</button>
                                    </div>
                                    <div class="confirm-button-wrapper">
                                        <button class="btn btn-danger" onclick="rejectWordCheck(${wc.userId})">Reject</button>
                                    </div>
                                </div>
                                <div class="col-sm-6 content">
                                    <span id="wc__status"><h4>${wc.status}</h4></span>
                                    <span id="wc__status__admin">
                                        <c:if test="${not empty wc.admin}">
                                            <h5>by admin:</h5><h4>${wc.admin}</h4>
                                        </c:if>
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <hr>

</main>
<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>


