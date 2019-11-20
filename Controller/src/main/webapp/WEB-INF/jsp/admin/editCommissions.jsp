<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 28.11.2016
  Time: 10:09
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
    <title><loc:message code="admin.commissions"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminCommissionsDataTable.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-6 col-md-offset-0 admin-container">
            <div class="text-center"><h4><loc:message code="admin.commissions"/></h4></div>

            <div id="commissionsMenu" class="buttons">
                <button class="active adminForm-toggler blue-box">
                    <loc:message code="admin.stockExchangeCommissions"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="admin.merchantsCommissions"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="admin.editTransferCommission"/>
                </button>
            </div>
            <div class="tab-content">
                <div id="panel1" class="tab-pane active">
                    <div class="col-sm-6">
                        <div class="text-center"><h4><loc:message code="admin.stockExchangeCommissions"/></h4></div>

                        <select id="roleName" class="input-block-wrapper__input admin-form-input">
                            <c:forEach items="${roleNames}" var="roleName">
                                <option value="${roleName}">${roleName}</option>
                            </c:forEach>
                        </select>
                        <hr/>

                        <table id="commissions-table">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.commissions.operationType"/> </th>
                                <th><loc:message code="admin.commissions.value"/></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div id="panel2" class="tab-pane">
                    <div class="col-sm-8">
                        <div class="text-center"><h4><loc:message code="admin.merchantsCommissions"/></h4></div>

                        <table id="merchant-commissions-table">
                            <thead>
                            <tr>
                                <th><loc:message code="withdrawal.merchant"/> </th>
                                <th><loc:message code="withdrawal.currency"/> </th>
                                <th><loc:message code="admin.merchantsCommissions.input"/></th>
                                <th><loc:message code="admin.merchantsCommissions.output"/></th>
                                <th><loc:message code="admin.merchantsCommissions.commissionType"/></th>
                                <th><loc:message code="admin.merchantsCommissions.secondaryCurrency"/></th>
                                <th><loc:message code="admin.merchantsCommissions.secondaryCurrencyAmount"/></th>
                                <th><loc:message code="admin.merchantsCommissions.usdRate"/></th>
                                <th><loc:message code="admin.merchantsCommissions.minFixed"/></th>
                                <th><loc:message code="admin.merchantsCommissions.minFixedUSD"/></th>
                                <th><loc:message code="admin.merchantsCommissions.subtractForWithdraw"/></th>
                                <th><loc:message code="admin.merchantsCommissions.changeAutomaticallyByUSD"/></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div id="panel3" class="tab-pane">
                    <div class="col-sm-8">
                        <div class="text-center"><h4><loc:message code="admin.merchantsCommissions"/></h4></div>
                        <table id="transfer-commissions-table" style="cursor: pointer;">
                            <thead>
                            <tr>
                                <th><loc:message code="withdrawal.merchant"/> </th>
                                <th><loc:message code="withdrawal.currency"/> </th>
                                <th><loc:message code="admin.merchantsCommissions.transfer"/></th>
                                <th><loc:message code="admin.merchantsCommissions.minFixedForTransfer"/></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>

            </div>



        </div>
            <div hidden id="com_types">${merchant_commission_type}</div>
            <div hidden id="com_cur">${currencies_for_commission}</div>
</main>
<div id="editCommissionModal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.editCommission"/></h4>
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
                            <label for="operationType" class="input-block-wrapper__label"><loc:message code="admin.commissions.operationType"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="operationType" name="operationType" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="commissionValue" class="input-block-wrapper__label"><loc:message code="admin.commissions.value"/></label>
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

<div id="editMerchantCommissionModal" class="modal modal-md fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.editCommission"/></h4>
            </div>
            <div class="modal-body">
                <form id="edit-merchantCommission-form" class="form_full_width form_auto_height">
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="merchantName" class="input-block-wrapper__label"><loc:message code="withdrawal.merchant"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="merchantName" name="merchantName" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="currencyName" class="input-block-wrapper__label"><loc:message code="withdrawal.currency"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="currencyName" name="currencyName" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.merchantsCommissions.input"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="inputValue" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.merchantsCommissions.output"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="outputValue" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.merchantsCommissions.secondaryCurrencyAmount"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="secondaryOutputCommission" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.merchantsCommissions.minFixed"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="minFixedAmount"  name="minFixedAmount" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.merchantsCommissions.minFixedUSD"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="minFixedAmountUSD" name="minFixedAmountUSD" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.merchantsCommissions.usdRate"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="usdRate" name="usdRate" class="input-block-wrapper__input" type="number" readonly>
                        </div>
                    </div>
                    <button id="submitMerchantCommission" class="blue-box admin-form-submit" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                </form>
            </div>
        </div>
    </div>
</div>

<div id="editTransferCommissionModal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.editTransferCommission"/></h4>
            </div>
            <div class="modal-body">
                <form id="edit-transferCommission-form" class="form_full_width form_auto_height">
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="merchantName" class="input-block-wrapper__label"><loc:message code="withdrawal.merchant"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="merchantName" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="currencyName" class="input-block-wrapper__label"><loc:message code="withdrawal.currency"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="currencyName" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.merchantsCommissions.transfer"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="transferValue" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.merchantsCommissions.minFixedForTransfer"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="minFixedAmount" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <button id="submitTransferCommission" class="blue-box admin-form-submit" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                </form>
            </div>
        </div>
    </div>
</div>

<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
