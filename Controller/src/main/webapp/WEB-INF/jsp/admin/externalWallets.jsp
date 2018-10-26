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
                <form enctype="multipart/form-data" action="" method="post">
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
                        <div class="col-md-12">
                            <label><loc:message code="admin.externalWallets.reservedWalletsBalances"/></label>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div id="reserved-wallets-id">
                            <c:forEach var="rw" items="reservedWallets"
                            <%--<div id="reserve-wallet-1">--%>
                                <%--<div class="col-md-4">--%>
                                    <%--<input name="wallet" type="text" placeholder="Address/Name">--%>
                                <%--</div>--%>
                                <%--<div class="col-md-4">--%>
                                    <%--<input name="balance" type="number" min="0" placeholder="0">--%>
                                <%--</div>--%>
                                <%--<div class="col-md-4">--%>
                                    <%--<input name="remove" type="button"--%>
                                           <%--onclick="javascript:removeElement(this.id); return false;"--%>
                                           <%--value="Remove">--%>
                                <%--</div>--%>
                            <%--</div>--%>
                        </div>
                        <p><input type="button" value="Add File" onclick="addFile();"/></p>
                    </div>
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
