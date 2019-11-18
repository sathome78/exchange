
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
    <title><loc:message code="admin.alertsSettings"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/adminAlertMessages.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-6 col-md-offset-2 admin-container">
            <div class="text-center"><h4><loc:message code="admin.alertsSettings"/></h4></div>
            <div class="tab-content">
                <div id="panel1" class="tab-pane active">
                    <div class="col-sm-12">
                        <div class="text-center"><h5><loc:message code="admin.alert.aboutUpdate"/></h5></div>
                        <form id="updatesForm" action="/2a8fy7b07dxe44/alerts/update"
                              class="form_full_width form_auto_height" method = "POST">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <c:if test="${not update.enabled}">
                                <div class="col-md-6 input-block-wrapper__label-wrapper">
                                    <label for="minutes" class="input-block-wrapper__label"><loc:message code="admin.alarm.timeToStart"/></label>
                                </div>
                                <div class="col-md-5 input-block-wrapper__input-wrapper">
                                    <input id="minutes" class="ch_field input-block-wrapper__input"
                                           name="minutes"/>
                                </div>
                                <div class="col-md-6 input-block-wrapper__label-wrapper">
                                    <label for="length" class="input-block-wrapper__label"><loc:message code="admin.alarm.lengthMinutes"/></label>
                                </div>
                                <div class="col-md-5 input-block-wrapper__input-wrapper">
                                    <input id="length" class="ch_field input-block-wrapper__input"
                                           name="lenghtOfWorks"/>
                                </div>
                            </c:if>
                            <c:if test="${update.enabled}">
                                <div class="col-md-6 input-block-wrapper__label-wrapper">
                                    <label for="eventStart" class="input-block-wrapper__label"><loc:message code="admin.alarm.timeOfStart"/></label>
                                </div>
                                <div class="col-md-5 input-block-wrapper__input-wrapper">
                                    <input disabled id="eventStart" class="input-block-wrapper__input" value="${update.eventStart}"/>
                                </div>
                                <div class="col-md-6 input-block-wrapper__label-wrapper">
                                    <label for="lengthStarted" class="input-block-wrapper__label"><loc:message code="admin.alarm.lengthMinutes"/></label>
                                </div>
                                <div class="col-md-5 input-block-wrapper__input-wrapper">
                                    <input disabled id="lengthStarted" class="input-block-wrapper__input"
                                           name="lenghtOfWorks" value="${update.lenghtOfWorks}"/>
                                </div>
                            </c:if>
                            <div class="col-md-6 input-block-wrapper__label-wrapper">
                                <label for="enableUpdate" class="input-block-wrapper__label"><loc:message code="admin.alarm.active"/></label>
                            </div>
                            <div class="col-md-5 input-block-wrapper__input-wrapper">
                                <input class="input-block-wrapper__input" id="enableUpdate"
                                       <c:if test="${update.enabled}">checked="checked"</c:if> name="enabled" type="checkbox"/>
                            </div>
                            <input name="alertType" hidden value="${update.alertType}"/>
                            <div hidden id="enabled">${update.enabled}</div>
                            <button type="submit" id="update_updates" class="blue-box admin-form-submit">
                                <loc:message code="button.update"/></button>
                        </form>
                        <div class="text-center"><h5><loc:message code="admin.alert.aboutTechWorks"/></h5></div>
                        <form id="technicalProblems" method="post" action="/2a8fy7b07dxe44/alerts/update"
                              class="form_full_width form_auto_height" >
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <div class="col-md-6 input-block-wrapper__label-wrapper">
                                <label for="enableTech" class="input-block-wrapper__label"><loc:message code="admin.alarm.active"/></label>
                            </div>
                            <div class="col-md-5 input-block-wrapper__input-wrapper">
                                <input class="input-block-wrapper__input" name="enabled" type="checkbox"
                                       <c:if test="${tech.enabled}">checked="checked"</c:if> id="enableTech"/>
                            </div>
                            <input name="alertType" hidden value="${tech.alertType}"/>
                            <button type="submit" id="update_tech" class="blue-box admin-form-submit">
                                <loc:message code="button.update"/></button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
</main>


<div id="editSettings" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.editPrice"/></h4>
            </div>
            <div class="modal-body">
                <form id="edit-commission-form" class="form_full_width form_auto_height">
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="userRole" class="input-block-wrapper__label"><loc:message code="admin.role"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="userRole" name="userRole" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="notificatorName" class="input-block-wrapper__label"><loc:message code="admin.notificatorName"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="notificatorName" name="operationType" class="input-block-wrapper__input" readonly type="text">
                        </div>
                        <div id="notificator_id" hidden></div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="commissionValue" class="input-block-wrapper__label"><loc:message code="notificator.price.value"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="commissionValue" name="commissionValue" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <button id="submitCommission" class="blue-box admin-form-submit" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                </form>
            </div>
        </div>
    </div>
</div>

<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
