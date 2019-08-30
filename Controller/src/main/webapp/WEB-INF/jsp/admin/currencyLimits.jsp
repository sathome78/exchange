<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 23.09.2016
  Time: 12:30
  To change this template use File | Settings | File Templates.
--%>
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
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminCurrenciesDataTable.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-6 col-md-offset-2 admin-container">
            <div class="text-center"><h4><loc:message code="admin.currencyLimits.title"/></h4></div>
            <div id="limitsMenu" class="buttons text-center">
                <button class="active adminForm-toggler blue-box">
                    <loc:message code="admin.currencyLimits.menu.currencies"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="admin.currencyLimits.menu.currencyPairs"/>
                </button>
            </div>
            <div class="tab-content">
                <div id="panel1" class="tab-pane active">
                    <div class="col-md-6 col-md-offset-3 text-center">
                        <h5>
                            <loc:message code="admin.currencyLimits.menu.currencies"/>
                        </h5>
                        <div class="col-md-6">
                            <select id="roleName" class="input-block-wrapper__input admin-form-input">
                                <c:forEach items="${roleNames}" var="roleName">
                                    <option value="${roleName}">${roleName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <select id="operationType" class="input-block-wrapper__input admin-form-input">
                                <c:forEach items="${operationTypes}" var="operationType">
                                    <option value="${operationType}">${operationType}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <hr/>

                        <table id="currency-limits-table">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.currencyLimits.currencyId"/></th>
                                <th><loc:message code="admin.currencyLimits.name"/></th>
                                <th><loc:message code="admin.currencyLimits.usdRate"/></th>
                                <th><loc:message code="admin.currencyLimits.minLimit"/></th>
                                <th><loc:message code="admin.currencyLimits.minLimitUsdRate"/></th>
                                <th><loc:message code="admin.currencyLimits.maxLimit"/></th>
                                <th><loc:message code="admin.currencyLimits.maxDailyRequest"/></th>
                                <th><loc:message code="admin.currencyLimits.changeAutomaticallyByUSD"/></th>
                            </tr>
                            </thead>
                        </table>
                    </div>

                </div>

                <div id="panel2" class="tab-pane">
                    <div class="col-md-6 col-md-offset-3 text-center">
                        <h5>
                            <loc:message code="admin.currencyLimits.menu.currencyPairs"/>
                        </h5>
                        <div class="col-md-6">
                            <select id="roleName-pair" class="input-block-wrapper__input admin-form-input">
                                <c:forEach items="${roleNames}" var="roleName">
                                    <option value="${roleName}">${roleName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <select id="orderType" class="input-block-wrapper__input admin-form-input">
                                <c:forEach items="${orderTypes}" var="orderType">
                                    <option value="${orderType}">${orderType}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <hr/>

                        <table id="currency-pair-limits-table">
                            <thead>
                            <tr>
                                <th></th>
                                <th><loc:message code="admin.currencyLimits.name"/></th>
                                <th><loc:message code="admin.currencyLimits.minRate"/></th>
                                <th><loc:message code="admin.currencyLimits.maxRate"/></th>
                                <th><loc:message code="admin.currencyLimits.minAmount"/></th>
                                <th><loc:message code="admin.currencyLimits.maxAmount"/></th>
                                <th><loc:message code="admin.currencyLimits.minTotal"/></th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>


                    </div>
                </div>
            </div>


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
                <form id="edit-currency-limit-form" class="form_full_width form_auto_height">
                    <input type="hidden" name="currencyId">
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="withdrawal.currency"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="currency-name" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.commissions.operationType"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="operationType" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.role"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="roleName" class="input-block-wrapper__input" readonly type="text">
                            <input type='checkbox' id="allRolesEdit" name="allRolesEdit"/>
                            <label class="input-block-wrapper__label"><loc:message code="admin.currencyLimits.allRoles"/></label>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.currencyLimits.minLimit"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="minAmount" name="minAmount" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.currencyLimits.minLimitUSD"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="minAmountUSD" name="minAmountUSD" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.currencyLimits.maxLimit"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="maxAmount" name="maxAmount" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.currencyLimits.usdRate"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="usdRate" name="usdRate" class="input-block-wrapper__input" type="number" readonly>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.currencyLimits.maxDailyRequest"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper" >
                            <input  name="maxDailyRequest" class="input-block-wrapper__input" type="number" style="align-content: center">
                        </div>
                    </div>
                    <button id="submitNewLimit" class="blue-box admin-form-submit" type="submit"><loc:message
                            code="admin.refSubmitEditCommonRoot"/></button>
                </form>
            </div>
        </div>
    </div>
</div>

<div id="editPairLimitModal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.currencyLimits.modalTitle"/></h4>
            </div>
            <div class="modal-body">
                <form id="edit-currency-pair-limit-form" class="form_full_width form_auto_height">
                    <input type="hidden" name="currencyPairId">
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="currency.pair"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="currency-pair-name" class="input-block-wrapper__input admin-form-input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.commissions.operationType"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="orderType" class="input-block-wrapper__input admin-form-input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.role"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="roleName" class="input-block-wrapper__input admin-form-input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.currencyLimits.minRate"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="minRate" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.currencyLimits.maxRate"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="maxRate" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.currencyLimits.minAmount"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="minAmount" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.currencyLimits.maxAmount"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="maxAmount" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.currencyLimits.minTotal"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="minTotal" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                    <button id="submitNewPairLimit" class="blue-box admin-form-submit" type="submit"><loc:message
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
