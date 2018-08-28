<%--
  User: Sasha
  Date: 7/16/2018
  Time: 2:10 PM
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
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">

    <script type="text/javascript" src="<c:url value='/client/js/2faSettings.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/kyc-admin.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
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
                                <c:choose>
                                    <c:when test="${kyc.kycType == \"INDIVIDUAL\"}">
                                        <form:form class="form-horizontal" id="kyc_individual_form"
                                                   action="/2a8fy7b07dxe44/kyc/saveByAdmin"
                                                   method="post"
                                                   enctype="multipart/form-data"
                                                   modelAttribute="kyc">
                                            <input type="hidden"  class="csrfC" name="_csrf" value="${_csrf.token}"/>
                                            <form:input type="hidden" value="${kyc.userId}" path="userId"/>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">KYC Type</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" id="name" path="kycType" readonly="true"/>
                                                </div>
                                            </div>

                                            <h4>Personal info</h4>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Name</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" id="name" path="person.name"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Surename</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" id="name" path="person.surname"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Middle name</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" id="name" path="person.middleName"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Sex</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                                    <form:select class="form-control" path="person.gender">
                                                        <form:options itemLabel="val" />
                                                    </form:select>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Date of birth</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input id="date_of_birth" type="text" class="form-control input-block-wrapper__input" path="person.birthDate"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Phone</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" id="name" path="person.phone"/>
                                                </div>
                                            </div>

                                            <h4>Address</h4>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Country</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" id="name" path="address.country"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">City</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" id="name" path="address.city"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Street</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" id="name" path="address.street"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Zip code</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" id="name" path="address.zipCode"/>
                                                </div>
                                            </div>

                                            <h4>Nationality</h4>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Nationality</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" id="name" path="person.nationality"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">ID card</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" id="name" path="person.idNumber"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Confirmation document</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                                    <a target="_blank" href="/kyc/docs/${kyc.person.confirmDocumentPath}" class="alert-danger settings-upload-files">
                                                            ${kyc.person.confirmDocumentPath}</a>
                                                </div>
                                            </div>

                                            <div class="confirm-button-wrapper">
                                                <button class="btn btn-info" type="submit">Save</button>
                                            </div>
                                        </form:form>
                                    </c:when>
                                    <c:otherwise>
                                        <form:form class="form-horizontal" id="kyc_entity_form"
                                                   action="/kyc/saveLegalEntity"
                                                   method="post"
                                                   enctype="multipart/form-data"
                                                   modelAttribute="kyc">

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">KYC Type</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" id="name" path="kycType" readonly="true"/>
                                                </div>
                                            </div>

                                            <h4>Company info</h4>

                                            <form:input id="kyc_type" class="kyc__type" type="hidden" path="kycType"/>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Company name</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" path="companyName" required="true"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Registration country</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" path="regCountry" required="true"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Registration number</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" path="regNumber" required="true"/>
                                                </div>
                                            </div>

                                            <h4>Registration address</h4>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Country</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" path="address.country" required="true"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">City</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" path="address.city" required="true"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Street</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" path="address.street" required="true"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Zip code</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" path="address.zipCode" required="true"/>
                                                </div>
                                            </div>

                                            <h4>Authority person info</h4>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Position</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.position" required="true"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Name</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.name" required="true"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Surname</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.surname" required="true"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Middle name</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.middleName"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Phone</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.phone" required="true"/>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="input-block-wrapper clearfix">
                                                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                        <label class="input-block-wrapper__label">Confirmation document</label>
                                                    </div>
                                                    <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                                        <a target="_blank" href="/kyc/docs/${kyc.person.confirmDocumentPath}" class="alert-danger settings-upload-files">
                                                                ${kyc.person.confirmDocumentPath}</a>
                                                    </div>
                                                </div>
                                            </div>

                                            <h4>Company documents</h4>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Commercial registry</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                                    <a target="_blank" href="/kyc/docs/${kyc.commercialRegistryPath}" class="alert-danger settings-upload-files">
                                                            ${kyc.commercialRegistryPath}</a>
                                                </div>
                                            </div>

                                            <div class="input-block-wrapper clearfix">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label">Company charter</label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                                    <a target="_blank" href="/kyc/docs/${kyc.companyCharterPath}" class="alert-danger settings-upload-files">
                                                            ${kyc.companyCharterPath}</a>
                                                </div>
                                            </div>
                                            <div class="confirm-button-wrapper">
                                                <button class="btn btn-info" type="submit">Save</button>
                                            </div>
                                        </form:form>
                                    </c:otherwise>
                                </c:choose>
                                <div class="col-sm-6" style="padding: 0;">
                                    <div class="confirm-button-wrapper">
                                        <button class="btn btn-success" onclick="approveKyc(${kyc.userId})">Approve</button>
                                    </div>
                                    <div class="confirm-button-wrapper">
                                        <button class="btn btn-danger" onclick="rejectKyc(${kyc.userId})">Reject</button>
                                    </div>
                                </div>
                                <div class="col-sm-6 content">
                                    <span id="kyc__status"><h4>${kyc.kycStatus}</h4></span>
                                    <span id="status__admin">
                                        <c:if test="${not empty kyc.admin}">
                                            <h5>by admin:</h5><h4>${kyc.admin}</h4>
                                        </c:if>
                                    </span>
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


