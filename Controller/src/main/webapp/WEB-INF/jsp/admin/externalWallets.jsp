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
    <script type="text/javascript"
            src="<c:url value='/client/js/dataTable/adminExternalWalletsDataTable.js'/>"></script>
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
                            <th><loc:message code="admin.report.currency.certainty"/></th>
                            <th><loc:message code="admin.rate.to.usd"/></th>
                            <th><loc:message code="admin.rate.to.btc"/></th>
                            <th><loc:message code="admin.externalWallets.mainWalletBalance"/></th>
                            <th><loc:message code="admin.externalWallets.allReservedWalletBalances"/></th>
                            <th><loc:message code="admin.externalWallets.totalWalletBalance"/></th>
                            <th><loc:message code="admin.externalWallets.totalWalletBalanceUSD"/></th>
                            <th><loc:message code="admin.externalWallets.totalWalletBalanceBTC"/></th>
                            <th><loc:message code="admin.externalWallets.lastUpdatedDate"/></th>
                        </tr>
                        </thead>
                        <tfoot>
                        <tr>
                            <th><loc:message code="admin.externalWallets.summary"/></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th><span id="summary-in-usd"></span></th>
                            <th><span id="summary-in-btc"></span></th>
                            <th></th>
                        </tr>
                        </tfoot>
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
            <div class="modal-body" style="height: content-box;">
                <label id="currencyIdForPopUp" style="display: none"></label>

                    <div class="input-block-wrapper">
                        <div class="col-md-6">
                            <label><loc:message code="admin.rate.to.usd"/></label>
                        </div>
                        <div class="col-md-6">
                            <label id="usd-rate-label"></label>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-6">
                            <label><loc:message code="admin.rate.to.btc"/></label>
                        </div>
                        <div class="col-md-6">
                            <label id="btc-rate-label"></label>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-6">
                            <label><loc:message code="admin.externalWallets.mainWalletBalance"/></label>
                        </div>
                        <div class="col-md-6">
                            <label id="main-balance-label"></label>
                        </div>
                    </div>
                <div class="input-block-wrapper">
                    <div class="col-md-6">
                        <label><loc:message code="admin.report.currency.certainty"/></label>
                    </div>
                    <div class="col-md-6">
                        <input type="checkbox" id="certainty" name="signOfCertainty"/>
                    </div>
                </div>

                    <div class="input-block-wrapper">
                        <div class="col-md-12">
                            <label id="labelReservedWalletBalance"><loc:message code="admin.externalWallets.reservedWalletsBalances"/></label>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <table id="reservedWallets"
                               class="admin-table table table-hover table-striped"
                               style="width:100%">
                            <thead>
                            <tr>
                                <%--Index--%>
                                <th></th>
                                <%--walletAddress--%>
                                <th></th>
                                <%--balance--%>
                                <th></th>
                                <%--Id--%>
                                <th></th>
                            </tr>
                            </thead>
                        </table>

                    </div>
                    <div class="input-block-wrapper">
                        <input id="addNewReserdedWallet" type="button" value="Add reserved wallet"/>
                    </div>
            </div>
        </div>
    </div>
</div>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
