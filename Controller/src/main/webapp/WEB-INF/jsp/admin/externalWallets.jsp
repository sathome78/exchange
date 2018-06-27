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
    <title><loc:message code="admin.externalWallets.title"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminExternalWalletsDataTable.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-4 admin-container">
            <div class="text-center"><h4><loc:message code="admin.externalWallets.title"/></h4></div>
            <div class="tab-content">
                <div class="col-md-12 text-center">

                    <table id="external-wallets-table">
                        <thead>
                        <tr>
                            <th><loc:message code="admin.currency.id"/></th>
                            <th><loc:message code="admin.externalWallets.name"/></th>
                            <th><loc:message code="admin.rate.to.usd"/></th>
                            <th><loc:message code="admin.externalWallets.mainWalletBalance"/></th>
                            <th><loc:message code="admin.externalWallets.mainWalletBalanceUSD"/></th>
                            <th><loc:message code="admin.externalWallets.reservedWalletBalance"/></th>
                            <th><loc:message code="admin.externalWallets.coldWalletBalance"/></th>
                            <th><loc:message code="admin.externalWallets.totalWalletBalance"/></th>
                            <th><loc:message code="admin.externalWallets.totalWalletBalanceUSD"/></th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
        </div>
</main>
<div id="editBalanceModal" class="modal modal-md fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.externalWallets.modalTitle"/></h4>
            </div>
            <div class="modal-body">
                <form id="edit-external-wallets-form" class="form_full_width form_auto_height">
                    <input type="hidden" name="currencyId">
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.rate.to.usd"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="rateUsdAdditional" class="input-block-wrapper__input" type="number" min="0">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.externalWallets.mainWalletBalance"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="mainWalletBalance" class="input-block-wrapper__input" type="number" min="0">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.externalWallets.reservedWalletBalance"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input name="reservedWalletBalance" class="input-block-wrapper__input" type="number" min="0">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.externalWallets.coldWalletBalance"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper" >
                            <input  name="coldWalletBalance" class="input-block-wrapper__input" type="number" min="0" style="align-content: center">
                        </div>
                    </div>
                    <button id="submitNewBalance" class="blue-box admin-form-submit" type="submit"><loc:message
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
