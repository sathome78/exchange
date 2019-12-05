
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
    <title><loc:message code="admin.commissions"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminNotificatiorsDataTable.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-6 col-md-offset-2 admin-container">
            <div class="text-center"><h4><loc:message code="admin.notificatorsMessagesSettings"/></h4></div>
            <div class="tab-content">
                <div id="panel1" class="tab-pane active">
                    <div class="col-sm-6">
                        <select id="roleName" class="input-block-wrapper__input admin-form-input">
                            <c:forEach items="${roles}" var="role">
                                <option value="${role.role}">${role}</option>
                            </c:forEach>
                        </select>
                        <p><loc:message code="admin.smsAbout"/></p>
                        <p><loc:message code="admin.telegramAbout"/></p>
                        <hr/>
                        <table id="notificators-table">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.notificatorName"/></th>
                                <th><loc:message code="admin.messageFeePercents"/></th>
                                <th><loc:message code="admin.subscribePrice"/></th>
                                <th><loc:message code="admin.enabled"/></th>
                            </tr>
                            </thead>
                        </table>
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
