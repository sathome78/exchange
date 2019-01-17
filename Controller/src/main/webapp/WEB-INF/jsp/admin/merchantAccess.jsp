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
    <title><loc:message code="admin.merchantAccess"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminMerchantAccessDataTable.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-6 col-md-offset-2 admin-container">
            <div id="commissionsMenu" class="buttons" align="center">
                <button class="active adminForm-toggler blue-box">
                    <loc:message code="refill.merchant"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="merchants.transferTitle"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="title.hide.currency.pair"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="title.hide.currency.visibility"/>
                </button>
            </div>

            <div class="tab-content">

                <div id="panel1" class="tab-pane active">
                            <div class="col-sm-4">

                                <table id="merchant-options-table">
                                    <thead>
                                    <tr>
                                        <th><loc:message code="withdrawal.merchant"/> </th>
                                        <th><loc:message code="withdrawal.currency"/> </th>
                                        <th data-operationtype="INPUT"><loc:message code="transaction.operationTypeINPUT"/></th>
                                        <th><loc:message code="transaction.operationTypeOUTPUT"/></th>
                                        <th><loc:message code="merchant.withdrawAuto"/></th>
                                        <th><loc:message code="merchant.withdrawAutoDelay"/></th>
                                        <th><loc:message code="merchant.withdrawAutoThreshold"/></th>
                                    </tr>
                                    </thead>
                                    <tbody>

                                    </tbody>

                                </table>

                                <sec:authorize access="hasAuthority('${adminEnum}')">
                                    <hr/>
                                    <div class="row text-center">
                                        <button id="block-all-input" class="red-box"><loc:message code="admin.blockAllInput" /></button>
                                        <button id="block-all-output" class="red-box"><loc:message code="admin.blockAllOutput" /></button>
                                    </div>
                                    <div class="row text-center">
                                        <button id="unblock-all-input" class="green-box"><loc:message code="admin.unblockAllInput" /></button>
                                        <button id="unblock-all-output" class="green-box"><loc:message code="admin.unblockAllOutput" /></button>
                                    </div>
                                </sec:authorize>
                            </div>
               </div>

                <div id="panel2" class="tab-pane">
                    <div class="col-sm-8 col-sm-offset-2">

                        <table id="transfer-options-table">
                            <thead>
                            <tr>
                                <th><loc:message code="withdrawal.merchant"/> </th>
                                <th><loc:message code="withdrawal.currency"/> </th>
                                <th data-operationtype="USER_TRANSFER"><loc:message code="merchants.transfer"/></th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div id="panel3" class="tab-pane">
                    <div class="col-sm-8 col-sm-offset-2">

                        <table id="currency-pairs-visibility-options-table">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.currency.pair.id"/> </th>
                                <th><loc:message code="title.hide.currency.pair"/> </th>
                                <th><loc:message code="title.visibility"/></th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div id="panel4" class="tab-pane">
                    <div class="col-sm-8 col-sm-offset-2">

                        <table id="currency-visibility-options-table">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.currency.id"/> </th>
                                <th><loc:message code="admin.currencyLimits.description"/> </th>
                                <th><loc:message code="withdrawal.currency"/> </th>
                                <th><loc:message code="title.visibility"/></th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>

            </div>

            </div>
        </div>
</main>
<div hidden id="prompt-toggle-block">
    <loc:message code="admin.toggleBlockPrompt" />
</div>
<div hidden id="prompt-toggle-block-all">
    <loc:message code="admin.blockAllPrompt" />
</div>
<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>

<%@include file='../fragments/modal/merchant_auto_withdraw_params.jsp'%>

</html>
