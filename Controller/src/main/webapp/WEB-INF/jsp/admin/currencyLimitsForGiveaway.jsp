<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
    <title><loc:message code="admin.currencyLimits.title"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/freecoinsCurrencyLimits.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-6 col-md-offset-2 admin-container">
            <div class="text-center">
                <h4><loc:message code="admin.currencyLimits.title"/></h4>
            </div>
            <hr/>

            <table id="currencyLimitsTable" style="width:100%; cursor: pointer">
                <thead>
                <tr>
                    <th><loc:message code="admin.currencyLimits.id"/></th>
                    <th><loc:message code="admin.currencyLimits.name"/></th>
                    <th><loc:message code="admin.currencyLimits.minAmount"/></th>
                    <th><loc:message code="admin.currencyLimits.minPartialAmount"/></th>
                </tr>
                </thead>
            </table>
        </div>
</main>

<div id="editLimitModal" class="modal modal-md fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.currencyLimits.modalTitle"/></h4>
            </div>
            <div>
                <form id="editCurrencyLimitForm" class="form_full_width form_auto_height">
                    <input type="hidden" id="currency_id" name="currency_id">
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.currencyLimits.name"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="currency_name" name="currency_name" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.currencyLimits.minAmount"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="min_amount" name="min_amount" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.currencyLimits.minPartialAmount"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="min_partial_amount" name="min_partial_amount" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <button id="submitNewLimit" class="blue-box admin-form-submit" type="submit"><loc:message
                            code="admin.refSubmitEditCommonRoot"/></button>
                </form>
            </div>
        </div>
    </div>
</div>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>