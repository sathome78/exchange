<%@ page import="me.exrates.model.enums.invoice.InvoiceOperationPermission" %>
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
    <title><loc:message code="admin.title"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <%@include file='links_scripts.jsp' %>

    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value="/client/css/ekko-lightbox.min.css"/>" rel="stylesheet">
    <script type="text/javascript" src="<c:url value='/client/js/app.js'/>"></script>

    <%----------%>
    <script type="text/javascript" src="<c:url value="/client/js/ekko-lightbox.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminWalletsDataTable.js'/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery-ui.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery-ui.js'/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminTransactionsDataTable.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminOrdersDataTable.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminCommentsDataTable.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/order/adminDeleteOrder.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/order/adminDeleteStopOrder.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/userCurrencyOperationPermissions.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/downloadTransactions.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/referrals.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/jquery.tmpl.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/2faSettings.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/jquery.twbsPagination.min.js'/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/refTable.css"/>">
    <%--delete?--%>
    <script type="text/javascript" src="<c:url value='/client/js/reportAdmin.js'/>"></script>

    <c:set var="admin_manualBalanceChange" value="<%=AdminAuthority.MANUAL_BALANCE_CHANGE%>"/>

    <sec:authorize access="hasAuthority('${admin_manualBalanceChange}')">
        <script type="text/javascript"
                src="<c:url value='/client/js/admin-balance-change/adminBalanceChange.js'/>"></script>
    </sec:authorize>

</head>

<body>
<%@include file='../fragments/header-simple.jsp' %>

<main class="container orders_new admin side_menu">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-md-offset-1 content admin-container">
            <div class="buttons">
                <%--Редактирование пользователя--%>
                <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                    <button class="active adminForm-toggler blue-box">
                        <loc:message code="admin.user"/>
                    </button>
                </sec:authorize>
                <%--Список транзакций--%>
                <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                    <button class="adminForm-toggler blue-box">
                        <loc:message code="admin.transactions"/>
                    </button>
                </sec:authorize>
                <%--Список кошельков--%>
                <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                    <button class="adminForm-toggler blue-box">
                        <loc:message code="admin.wallets"/>
                    </button>
                </sec:authorize>
                <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                    <%--Orders list--%>
                    <button class="adminForm-toggler blue-box">
                        <loc:message code="orders.title"/>
                    </button>
                    <%--Comments--%>
                    <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                        <button class="adminForm-toggler blue-box">
                            <loc:message code="admin.comments"/>
                        </button>
                    </sec:authorize>
                    <%--Comments--%>
                    <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                        <button class="adminForm-toggler blue-box">
                            <loc:message code="admin.referral"/>
                        </button>
                    </sec:authorize>

                    <%--Access for operation | START--%>
                    <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                        <button class="adminForm-toggler yellow-box">
                            Access
                        </button>
                    </sec:authorize>
                    <%--Access for operation | END--%>

                    <sec:authorize access="hasAuthority('${admin_manageAccess}')">
                        <c:if test="${user.role == adminEnum || user.role == accountantEnum || user.role == admin_userEnum || user.role == admin_finOperatorEnum}">
                            <button class="adminForm-toggler red-box">
                                <loc:message code="admin.accessRights"/>
                            </button>
                        </c:if>
                    </sec:authorize>

                    <sec:authorize access="hasAuthority('${admin_manageAccess}')">
                        <sec:authorize access="hasAnyAuthority('${admin_processInvoice}','${admin_processWithdraw}')">
                            <c:if test="${user.role == adminEnum || user.role == accountantEnum || user.role == admin_userEnum || user.role == admin_finOperatorEnum}">
                                <button class="adminForm-toggler red-box">
                                    <loc:message code="admin.userCurrencyPermissions"/>
                                </button>
                            </c:if>
                        </sec:authorize>
                    </sec:authorize>
                </sec:authorize>
            </div>
            <%--Current user and email--%>
            <div>
                <h5><b>
                    <c:choose>
                    <c:when test="${empty user.nickname}">
                        ${user.email}
                    </c:when>
                    <c:otherwise>
                        ${user.nickname}, ${user.email}
                    </c:otherwise>
                    </c:choose>
            </div>
            <div id="u_email" hidden>${user.email}</div>
            <%--контейнер для данных пользователей--%>
            <div class="tab-content">
                <%--форма редактирование пользователя--%>
                <div id="panel1" class="tab-pane active">
                    <div class="col-md-8 content">
                        <div class="text-center"><h4>
                            <b><loc:message code="admin.editUser"/></b>
                        </h4></div>

                        <div class="panel-body">

              <form:form class="form-horizontal form_full_height_width" id="user-edit-form"
                         action="/2a8fy7b07dxe44/edituser/submit"
                         method="post" modelAttribute="user">
                <div>
                  <fieldset class="field-user">
                    <div class="input-block-wrapper">
                      <div class="col-md-8 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label"><loc:message code="message.2fa.title"/></label>
                      </div>

                      <div class="col-md-4 input-block-wrapper__input-wrapper">
                        <input type="checkbox" name="user2fa" class="input-block-wrapper__input admin-form-input"
                               readonly disabled
                               <c:if test="${user2fa}">checked</c:if>
                        />
                      </div>
                    </div>

                    <div class="input-block-wrapper">
                      <div class="col-md-3 input-block-wrapper__label-wrapper">
                        <label for="user-name"
                               class="input-block-wrapper__label"><loc:message
                                code="admin.login"/></label>
                      </div>

                                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                <form:input path="id" type="hidden"
                                                            class="input-block-wrapper__input"
                                                            id="user-id"/>
                                                <form:input path="nickname"
                                                            class="input-block-wrapper__input admin-form-input"
                                                            id="user-name"
                                                            readonly="true"/>
                                            </div>
                                        </div>
                                        <div class="input-block-wrapper">
                                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                <label for="user-email"
                                                       class="input-block-wrapper__label"><loc:message
                                                        code="admin.email"/></label>
                                            </div>
                                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                <form:input path="email"
                                                            class="input-block-wrapper__input admin-form-input"
                                                            id="user-email"
                                                            readonly="true"/>
                                                <form:errors path="email" class="input-block-wrapper__input"
                                                             style="color:red"/>
                                            </div>
                                        </div>

                                        <div class="input-block-wrapper">
                                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                <label for="user-phone"
                                                       class="input-block-wrapper__label"><loc:message
                                                        code="admin.phone"/></label>
                                            </div>
                                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                <form:input path="phone"
                                                            class="input-block-wrapper__input admin-form-input"
                                                            id="user-phone"/>
                                                <form:errors path="phone" class="input-block-wrapper__input"
                                                             style="color:red"/>
                                            </div>
                                        </div>
                                        <sec:authorize access="hasAuthority('${adminEnum}')">
                                            <c:if test="${roleSettings.manualChangeAllowed}">
                                                <div class="input-block-wrapper">
                                                    <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                        <label for="user-role"
                                                               class="input-block-wrapper__label"><loc:message
                                                                code="admin.role"/></label>
                                                    </div>
                                                    <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                            <%--<form:input path="role" id="user-role"
                                                                        class="input-block-wrapper__input admin-form-input"
                                                                        name="user-role" />--%>
                                                        <form:select path="role" id="user-role"
                                                                     class="input-block-wrapper__input admin-form-input"
                                                                     name="user-role">
                                                            <c:forEach items="${roleList}" var="role">
                                                                <option value="${role}"
                                                                        <c:if test="${role eq user.role}">SELECTED</c:if>>${role}</option>
                                                            </c:forEach>
                                                        </form:select>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </sec:authorize>
                                        <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                                            <form:hidden path="role" name="user-role"/>
                                        </sec:authorize>
                                        <div class="input-block-wrapper">
                                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                <label for="user-status" class="input-block-wrapper__label">
                                                    <loc:message code="admin.status"/>
                                                </label>
                                            </div>
                                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                <form:select path="userStatus" id="user-status"
                                                             class="input-block-wrapper__input admin-form-input"
                                                             name="user-status">
                                                    <c:forEach items="${statusList}" var="status">
                                                        <option value="${status}"
                                                                <c:if test="${status eq user.userStatus}">SELECTED</c:if>>${status}</option>
                                                    </c:forEach>
                                                </form:select>
                                            </div>
                                        </div>

                                        <div class="input-block-wrapper">
                                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                <label for="user-name"
                                                       class="input-block-wrapper__label"><loc:message
                                                        code="register.sponsor"/></label>
                                            </div>

                                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                <form:input path="parentEmail" readonly="true"
                                                            class="input-block-wrapper__input admin-form-input"
                                                            id="parentEmail"/>
                                            </div>
                                        </div>

                                        <sec:authorize access="hasAuthority('${admin_editUser}')">
                                            <div class="admin-submit-group">
                                                <div>
                                                    <loc:message code="admin.save" var="saveSubmit"></loc:message>
                                                    <button class="blue-box" type="submit">${saveSubmit}</button>

                                                    <loc:message code="admin.cancel" var="cancelSubmit"></loc:message>
                                                    <button class="blue-box" type="reset"
                                                            onclick="javascript:window.location='/2a8fy7b07dxe44';">${cancelSubmit}</button>
                                                </div>
                                            </div>
                                        </sec:authorize>
                                    </fieldset>
                                </div>
                            </form:form>
                            <c:choose>
                                <c:when test="${userFiles.size() != 0}">
                                    <h4><loc:message code="admin.yourFiles"/></h4>
                                    <div class="row usr_doc_row">
                                        <div class="col-md-offset-0 col-md-10">
                                            <c:forEach var="image" items="${userFiles}">
                                                <div id="_${image.id}">
                                                    <a href="${image.path}" class="col-sm-4" data-title="<form class='delete_img'>
                                                        <input type='hidden' name='id' value='${image.id}'/>
                                                        <input type='hidden' name='path' value='${image.path}'/>
                                                        <input type='hidden' name='userId' value='${image.userId}'/>
                                                        <button id='apr_delete' type='submit' class='btn-md btn-danger'><loc:message code='admin.modal.delete'/></button>
                                                        </form>" data-toggle="lightbox">
                                                        <img src="${image.path}" class="img-responsive">
                                                    </a>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </c:when>
                            </c:choose>
                            <%-- <%@include file="../fragments/admin-settings-user-2fa.jsp" %>--%>
                        </div>
                    </div>
                </div>

                <%--форма список транзакций--%>
                <div id="panel2" class="tab-pane">
                    <div class="col-md-12 content">
                        <%--ИСТОРИЯ ОПЕРАЦИЙ--%>
                        <div class="text-center"><h4><loc:message code="transactions.title"/></h4></div>
                        <button class="blue-box" id="transactions-table-init">
                            <loc:message code="admin.datatable.showData"/></button>
                        <button data-toggle="collapse" class="blue-box" data-target="#transaction-filter">
                            <loc:message code="admin.user.transactions.extendedFilter"/></button>

                        <button class="blue-box" id="download_trans_history" style="margin: 10px 0;">
                            <loc:message code="admin.user.transactions.downloadHistory"/></button>
                        <div id="transaction-filter" class="collapse">
                            <form id="transaction-search-form" class="form_auto_height" method="get">
                                <%--STATUS--%>
                                <div class="input-block-wrapper">
                                    <div class="col-md-3 input-block-wrapper__label-wrapper">
                                        <label class="input-block-wrapper__label">
                                            <loc:message code="admin.status"/>
                                        </label>
                                    </div>
                                    <div class="col-md-9 input-block-wrapper__input-wrapper">
                                        <ul class="checkbox-grid">
                                            <li><input type="radio" name="status" value="-1"><span>ALL</span></li>
                                            <li><input type="radio" name="status" value="1"><span><loc:message
                                                    code="transaction.provided"/></span></li>
                                            <li><input type="radio" name="status" value="0"><span><loc:message
                                                    code="transaction.notProvided"/></span></li>
                                        </ul>
                                    </div>
                                </div>
                                <%--TYPE--%>
                                <div class="input-block-wrapper">
                                    <div class="col-md-3 input-block-wrapper__label-wrapper">
                                        <label class="input-block-wrapper__label">
                                            <loc:message code="transaction.operationType"/>
                                        </label>
                                    </div>
                                    <div class="col-md-9 input-block-wrapper__input-wrapper">
                                        <ul class="checkbox-grid">
                                            <c:forEach items="${transactionTypes}" var="type">
                                                <li><input type="checkbox" name="types"
                                                           value="${type}"><span>${type}</span></li>
                                            </c:forEach>
                                        </ul>
                                    </div>

                                </div>
                                <%--MERCHANT--%>
                                <div class="input-block-wrapper">
                                    <div class="col-md-3 input-block-wrapper__label-wrapper">
                                        <label class="input-block-wrapper__label">
                                            <loc:message code="withdrawal.merchant"/>
                                        </label>
                                    </div>
                                    <div class="col-md-9 input-block-wrapper__input-wrapper">
                                        <ul class="checkbox-grid">
                                            <c:forEach items="${merchants}" var="merchant">
                                                <li><input type="checkbox" name="merchants"
                                                           value="${merchant.id}"><span>${merchant.name}</span>
                                                </li>
                                            </c:forEach>
                                        </ul>

                                    </div>

                                </div>

                                <%--AMOUNT--%>
                                <div class="input-block-wrapper">
                                    <div class="col-md-3 input-block-wrapper__label-wrapper">
                                        <label class="input-block-wrapper__label">
                                            <loc:message code="orders.amount"/>
                                        </label>
                                    </div>
                                    <div class="col-md-9 input-block-wrapper__input-wrapper">
                                        <input type="number" id="amountFrom" name="amountFrom">
                                        <input type="number" id="amountTo" name="amountTo">
                                    </div>
                                </div>
                                <%--COMMISSION_AMOUNT--%>
                                <div class="input-block-wrapper">
                                    <div class="col-md-3 input-block-wrapper__label-wrapper">
                                        <label class="input-block-wrapper__label">
                                            <loc:message code="inputoutput.commissionAmount"/>
                                        </label>
                                    </div>
                                    <div class="col-md-9 input-block-wrapper__input-wrapper">
                                        <input type="number" id="commission-amount-from" name="commissionAmountFrom">
                                        <input type="number" id="commission-amount-to" name="commissionAmountTo">
                                    </div>
                                </div>
                                <button id="filter-apply" class="blue-box"><loc:message
                                        code="admin.user.transactions.applyFilter"/></button>
                                <button id="filter-reset" class="blue-box"><loc:message
                                        code="admin.user.transactions.resetFilter"/></button>

                            </form>

                        </div>

                        <%--TIME--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ordersearch.date"/>
                                </label>
                            </div>

                            <form id="transaction-search-datetime-form">
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input id="datetimepicker_start" type="text" name="startDate">
                                    <input id="datetimepicker_end" type="text" name="endDate">
                                    <button id="transactions_change_date" class="blue-box"><loc:message
                                            code="admin.user.transactions.aplly_dates"/></button>
                                </div>

                            </form>
                        </div>


                        <table id="transactionsTable"
                               class="admin-table table table-hover table-bordered table-striped"
                               style="width:100%">
                            <thead>
                            <tr>
                                <%--Дата--%>
                                <th><loc:message code="transaction.datetime"/></th>
                                <th></th>
                                <%--Тип--%>
                                <th><loc:message code="transaction.operationType"/></th>
                                <%--Статус--%>
                                <th><loc:message code="orderstatus.name"/></th>
                                <%--Валюта--%>
                                <th><loc:message code="transaction.currency"/></th>
                                <%--Сумма--%>
                                <th><loc:message code="transaction.amount"/></th>
                                <%--Сумма <br> комиссии--%>
                                <th><loc:message code="transaction.commissionAmount"/></th>
                                <%--Платежная <br> система--%>
                                <th><loc:message code="transaction.merchant"/></th>
                                <th>Source ID</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>

                <%--форма список кошельков--%>
                <div id="panel3" class="tab-pane">
                    <%--<div class="container orders_new transaction my_orders orders .container_footer_bottom my_wallets">--%>
                    <%--<div class="row">--%>
                    <div class="col-md-10 content">
                        <%--СПИСОК СЧЕТОВ--%>
                        <div class="text-center"><h4><loc:message code="admin.wallets"/></h4></div>
                        <div class='col-md-12' id="exclude-zero-balances-container">
                            <div class="col-md-1">
                                <input type='checkbox' id='exclude-zero-balances'>
                            </div>
                            <div class="col-md-11">
                                <label for="exclude-zero-balances"><loc:message code="userWallets.excludeZero"/></label>
                            </div>
                        </div>
                        <div class="col-md-12">
                            <button class="blue-box" id="wallets-table-init">
                                <loc:message code="admin.datatable.showData"/></button>
                            <button class="blue-box" id="wallets-table-balances-only">
                                <loc:message code="admin.datatable.showBalances"/></button>
                        </div>
                        <span hidden id="walletsExtendedInfoRequired">${walletsExtendedInfoRequired}</span>
                        <table id="walletsTable"
                               class="admin-table table table-hover table-bordered table-striped"
                               style="width:100%">
                            <c:choose>
                                <c:when test="${walletsExtendedInfoRequired}">
                                    <thead>
                                    <tr>
                                        <th></th>
                                        <th><loc:message code="mywallets.totalBalance"/></th>
                                        <th><loc:message code="mywallets.abalance"/></th>
                                        <th><loc:message code="mywallets.rbalance"/></th>
                                        <th><loc:message code="mywallets.reservedonorders"/></th>
                                        <th><loc:message code="mywallets.reservedonwithdraw"/></th>
                                        <th><loc:message code="userWallet.input"/></th>
                                        <th><loc:message code="userWallet.sell"/></th>
                                        <th><loc:message code="userWallet.buy"/></th>
                                        <th><loc:message code="userWallet.output"/></th>
                                    </tr>
                                    </thead>
                                </c:when>
                                <c:otherwise>
                                    <thead>
                                    <tr>
                                        <th></th>
                                        <th><loc:message code="mywallets.totalBalance"/></th>
                                        <th><loc:message code="mywallets.abalance"/></th>
                                        <th><loc:message code="mywallets.rbalance"/></th>
                                    </tr>
                                    </thead>
                                </c:otherwise>
                            </c:choose>

                        </table>

                        <sec:authorize
                                access="(hasAuthority('${admin_manualBalanceChange}') && ${manualChangeAllowed})">
                            <hr/>
                            <div class="text-center"><h4><loc:message code="admin.manualBalanceChange.title"/></h4>
                            </div>
                            <div class="col-md-12">
                                <form id="manualBalanceChangeForm" action="/2a8fy7b07dxe44/changeActiveBalance/submit"
                                      method="post">
                                    <div class="form-item form-group">
                                        <label for="currency"><loc:message code="mywallets.currency"/> </label>
                                        <select id="currency" name="currency" class="form-control">
                                            <c:forEach items="${currencies}" var="currency">
                                                <option value="${currency.id}">${currency.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-item form-group">
                                        <label for="amount"><loc:message code="mywallets.amount"/> </label>
                                        <input id="amount" name="amount" type="number" step="any" class="form-control"/>
                                    </div>
                                    <input type="hidden" name="userId" value="${user.id}">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                    <button id="manualBalanceSubmit" type="button" class="blue-box"><loc:message
                                            code="admin.submit"/></button>
                                    <div class="form-item form-group">
                                        <label for="comment"><loc:message code="mywallets.comment"/> </label>
                                        <textarea id="comment" name="comment"
                                                  class="form-control"
                                                  placeholder="Enter your comment..." required="required"></textarea>
                                    </div>
                                </form>
                            </div>

                        </sec:authorize>

                    </div>

                </div>
                <%--Orders list form--%>
                <div id="panel4" class="tab-pane">
                    <div style="width: 98%">
                        <div class="col-md-12" style="margin-top: 20px">
                            <button class="blue-box" id="orders-tables-init">
                                <loc:message code="admin.datatable.showData"/></button>
                        </div>
                        <div class="col-md-12" style="float: left; display: inline-block">
                            <button id="myorders-button-deal" class="myorders__button green-box margin-box">
                                <loc:message
                                        code="myorders.deal"/></button>
                            <button id="myorders-button-opened" class="myorders__button blue-box margin-box">
                                <loc:message
                                        code="myorders.opened"/></button>
                            <button id="myorders-button-cancelled" class="myorders__button red-box margin-box">
                                <loc:message
                                        code="myorders.cancelled"/></button>
                        </div>
                        <div style="float: right; display: inline-block">
                            <select style="width: auto" id="currency-pair-selector"
                                    class="form-control"
                                    name="currency-pair-selector">
                                <option value="0"><loc:message code="currency.allpairs"/></option>
                                <c:forEach items="${currencyPairs}" var="currencyPair">
                                    <option value="${currencyPair.id}">${currencyPair.name}</option>
                                </c:forEach>
                            </select>
                        </div>

                    </div>
                    <div class="col-md-12 content">
                        <%--Orders --%>
                        <div class="text-center"><h4><loc:message code="myorders.sellorders"/></h4></div>

                        <table id="ordersSellTable"
                               class="admin-table table table-hover table-bordered table-striped"
                               style="width:100%">
                            <thead>
                            <tr>
                                <th class="col-2 myo_orid center blue-white"><loc:message code="myorders.id"/></th>
                                <th class="col-3 myo_dcrt center blue-white"><loc:message
                                        code="myorders.datecreation"/></th>
                                <th class="col-2 myo_crpr center blue-white"><loc:message
                                        code="myorders.currencypair"/></th>
                                <th class="col-2 myo_amnt right blue-white"><loc:message
                                        code="myorders.amount"/></th>
                                <th class="col-2 myo_rate right blue-white"><loc:message code="myorders.rate"/></th>
                                <th class="col-2 myo_totl right blue-white"><loc:message
                                        code="myorders.total"/></th>
                                <th class="col-2 myo_comm right blue-white"><loc:message
                                        code="myorders.commission"/></th>
                                <th class="col-2 myo_amcm right blue-white"><loc:message
                                        code="myorders.amountwithcommission"/></th>
                                <%--<th class="col-2 myo_delt center blue-white"></th>--%>
                                <%--<th class="col-4 myo_cnsl right blue-white"><loc:message code="myorders.canceldate"/></th>--%>
                                <th id="dateModificated" class="col-4 myo_deal right blue-white"><loc:message
                                        code="myorders.dealdate"/></th>
                            </tr>
                            </thead>
                        </table>

                        <div class="text-center"><h4><loc:message code="myorders.buyorders"/></h4></div>

                        <table id="ordersBuyTable"
                               class="admin-table table table-hover table-bordered table-striped"
                               style="width:100%">
                            <thead>
                            <tr>
                                <th class="col-2 myo_orid center blue-white"><loc:message code="myorders.id"/></th>
                                <th class="col-3 myo_dcrt center blue-white"><loc:message
                                        code="myorders.datecreation"/></th>
                                <th class="col-2 myo_crpr center blue-white"><loc:message
                                        code="myorders.currencypair"/></th>
                                <th class="col-2 myo_amnt right blue-white"><loc:message
                                        code="myorders.amount"/></th>
                                <th class="col-2 myo_rate right blue-white"><loc:message code="myorders.rate"/></th>
                                <th class="col-2 myo_totl right blue-white"><loc:message
                                        code="myorders.total"/></th>
                                <th class="col-2 myo_comm right blue-white"><loc:message
                                        code="myorders.commission"/></th>
                                <th class="col-2 myo_amcm right blue-white"><loc:message
                                        code="myorders.amountwithcommission"/></th>
                                <%--<th class="col-2 myo_delt center blue-white"></th>--%>
                                <%--<th class="col-4 myo_cnsl right blue-white"><loc:message code="myorders.canceldate"/></th>--%>
                                <th class="col-4 myo_deal right blue-white"><loc:message
                                        code="myorders.dealdate"/></th>
                            </tr>
                            </thead>
                        </table>

                        <div class="text-center"><h4><loc:message code="myorders.stoporders"/></h4></div>

                        <table id="stopOrdersTable"
                               class="admin-table table table-hover table-bordered table-striped"
                               style="width:100%">
                            <thead>
                            <tr>
                                <th class="col-2 myo_orid center blue-white"><loc:message code="myorders.id"/></th>
                                <th class="col-3 myo_dcrt center blue-white"><loc:message
                                        code="myorders.datecreation"/></th>
                                <th class="col-2 myo_crpr center blue-white"><loc:message
                                        code="myorders.currencypair"/></th>
                                <th class="col-2 myo_crpr center blue-white"><loc:message
                                        code="admin.commissions.operationType"/></th>
                                <th class="col-2 myo_amnt right blue-white"><loc:message
                                        code="myorders.amount"/></th>
                                <th class="col-2 myo_rate right blue-white"><loc:message code="myorders.rate"/></th>
                                <th class="col-2 myo_rate right blue-white"><loc:message code="myorders.stopRate"/></th>
                                <th class="col-2 myo_totl right blue-white"><loc:message
                                        code="myorders.total"/></th>
                                <th class="col-2 myo_comm right blue-white"><loc:message
                                        code="myorders.commission"/></th>
                                <th class="col-2 myo_amcm right blue-white"><loc:message
                                        code="myorders.amountwithcommission"/></th>
                                <%--<th class="col-2 myo_delt center blue-white"></th>--%>
                                <%--<th class="col-4 myo_cnsl right blue-white"><loc:message code="myorders.canceldate"/></th>--%>
                                <th class="col-4 myo_deal right blue-white"><loc:message
                                        code="myorders.dealdate"/></th>
                            </tr>
                            </thead>
                        </table>


                    </div>
                </div>

                <%--Users comments--%>
                <div id="panel5" class="tab-pane">
                    <div class="col-md-12 content">
                        <%--Comments --%>
                        <div class="text-center"><h4><loc:message code="admin.comments"/></h4></div>
                        <div style="width: 98%">
                            <div style="float: left; display: inline-block">
                                <button class="blue-box" id="comments-table-init">
                                    <loc:message code="admin.datatable.showData"/></button>
                                <button id="comments-button" class="comments__button green-box margin-box">
                                    <loc:message
                                            code="admin.createComment"/></button>
                            </div>
                        </div>

                        <%--MODAL ... --%>
                        <div class="modal fade comment" id="myModal">
                            <div class="modal-dialog modal-sm">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h4 class="modal-title">${user.nickname}, ${user.email}, ${userLang} </h4>
                                    </div>
                                    <div class="modal-body">
                                        <input hidden id="commentId">
                                        <p><loc:message code="admin.comment"/>:<Br>
                                            <textarea class="form-control" cols="40" rows="3" id="commentText"
                                                      autofocus></textarea>
                                        <p><span class="checkLengthComment"><loc:message
                                                code="admin.checkLengthComment"/>
                      <span id="checkLengthComment"></span>/<span id="checkMaxLengthComment"></span>
                    </span>
                                        <p><input style="vertical-align: bottom" id="sendMessageCheckbox"
                                                  type="checkbox">
                                            <loc:message code="admin.sendMessage"/>
                                        <p><span id="checkMessage" style="color: #FF0000; " hidden><loc:message
                                                code="admin.checkLanguage"/></span>

                                    </div>
                                    <div class="modal-footer">
                                        <div>
                                            <button class="modal-button" type="button" id="createCommentConfirm">
                                                <loc:message code="merchants.continue"/>
                                            </button>

                                            <button class="modal-button" type="button" id="createCommentCancel"
                                                    data-dismiss="modal">
                                                <loc:message code="merchants.close"/>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                                <!-- /.modal-content -->
                            </div>
                            <!-- /.modal-dialog -->
                        </div>
                        <%--... MODAL--%>

                        <table id="commentsTable"
                               class="admin-table table table-hover table-striped"
                               style="width:100%">
                            <thead>
                            <tr>
                                <%--Date time--%>
                                <th><loc:message code="admin.dateTime"/></th>
                                <%--Creator>--%>
                                <th><loc:message code="admin.creator"/></th>
                                <%--Comment--%>
                                <th><loc:message code="admin.comment"/></th>
                                <%--MessageSent--%>
                                <th></th>
                                <%--Comment id--%>
                                <th>123</th>
                            </tr>
                            </thead>
                        </table>


                    </div>
                </div>
                <%--User referrals--%>
                <div id="panel7" class="tab-pane">
                    <%@include file='../fragments/referralStructure.jsp' %>
                </div>
                <%--Access management--%>

                <%--Access for operation | START--%>
                <%@include file='../fragments/admin/editUser/accessToOperationsForTheUser.jsp' %>
                <%--Access for operation | END--%>

                <sec:authorize access="hasAuthority('${admin_manageAccess}')">
                    <c:if test="${user.role == adminEnum || user.role == accountantEnum || user.role == admin_userEnum || user.role == admin_finOperatorEnum}">
                        <div id="panel6" class="tab-pane">
                            <div class="col-md-6 content">
                                <div class="text-center"><h4><loc:message code="admin.accessRights"/></h4></div>
                                <hr/>
                                <div>
                                    <form:form method="post" action="/2a8fy7b07dxe44/editAuthorities/submit"
                                               modelAttribute="authorityOptionsForm">
                                        <table id="authoritiesTable" class="table table-striped table-bordered">
                                            <tbody>
                                            <c:forEach items="${authorityOptionsForm.options}" var="authority"
                                                       varStatus="authStatus">
                                                <tr>
                                                    <td>${authority.adminAuthorityLocalized}</td>
                                                    <td><form:checkbox
                                                            path="options[${authStatus.index}].enabled"
                                                            value="${authority.enabled}"/></td>
                                                    <form:hidden
                                                            path="options[${authStatus.index}].adminAuthority"/>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                        <form:hidden path="userId"/>
                                        <button type="submit" class="blue-box"><loc:message
                                                code="admin.submit"/></button>
                                    </form:form>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </sec:authorize>

                <sec:authorize access="hasAuthority('${admin_manageAccess}')">
                <c:if test="${user.role == adminEnum || user.role == accountantEnum || user.role == admin_userEnum || user.role == admin_finOperatorEnum}">
                <div id="panel6" class="tab-pane">
                    <div id="currency_permissions">
                        <c:set value="<%=InvoiceOperationPermission.NONE%>" var="none"/>
                        <c:set value="<%=InvoiceOperationPermission.VIEW_ONLY%>" var="view"/>
                        <c:set value="<%=InvoiceOperationPermission.ACCEPT_DECLINE%>" var="write"/>
                        <sec:authorize access="hasAuthority('${admin_processInvoice}')">
                            <div class="col-md-6 content">
                                <c:if test="${!fn:contains(userActiveAuthorityOptions, admin_processInvoice)}">
                                    <c:set value="disabled" var="refillDisabledAttrib"/>
                                </c:if>
                                <div class="text-center"><h4><loc:message
                                        code="admin.userCurrencyPermissionsRefill"/></h4></div>
                                <hr/>
                                <div id="currency_permissions-refill" class="currency_permissions">
                                    <table class="table table-striped currency_permissions__table">
                                        <thead>
                                        <tr>
                                            <th class="left"><loc:message code="inputoutput.currency"/></th>
                                            <th class="center"><a class="sel_col" data-col="1"
                                                                  style="cursor: pointer">${none}</a></th>
                                            <th class="center"><a class="sel_col" data-col="2"
                                                                  style="cursor: pointer">${view}</a></th>
                                            <th class="center"><a class="sel_col" data-col="3"
                                                                  style="cursor: pointer">${write}</a></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${usersInvoiceRefillCurrencyPermissions}"
                                                   var="refillPermission">
                                            <tr class="currency_permissions__item">
                                                <td class="left">
                                                        ${refillPermission.currencyName}
                                                </td>
                                                <td class="center col1">
                                                    <input type="radio" name=refill"${refillPermission.currencyId}"
                                                           value="${none}"
                                                           data-userId="${refillPermission.userId}"
                                                           data-id="${refillPermission.currencyId}"
                                                           data-direction="REFILL"
                                                           data-checked="${refillPermission.invoiceOperationPermission==none}"
                                                        <c:out value="${refillDisabledAttrib}"/>>
                                                </td>
                                                <td class="center col2">
                                                    <input type="radio" name=refill"${refillPermission.currencyId}"
                                                           value="${view}"
                                                           data-userId="${refillPermission.userId}"
                                                           data-id="${refillPermission.currencyId}"
                                                           data-direction="REFILL"
                                                           data-checked="${refillPermission.invoiceOperationPermission==view}"
                                                        <c:out value="${refillDisabledAttrib}"/>>
                                                </td>
                                                <td class="center col3">
                                                    <input type="radio" name=refill"${refillPermission.currencyId}"
                                                           value="${write}"
                                                           data-userId="${refillPermission.userId}"
                                                           data-id="${refillPermission.currencyId}"
                                                           data-direction="REFILL"
                                                           data-checked="${refillPermission.invoiceOperationPermission==write}"
                                                        <c:out value="${refillDisabledAttrib}"/>>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('${admin_processWithdraw}')">
                            <div class="col-md-6 content">
                                <c:if test="${!fn:contains(userActiveAuthorityOptions, admin_processWithdraw)}">
                                    <c:set value="disabled" var="withdrawDisabledAttrib"/>
                                </c:if>
                                <div class="text-center"><h4><loc:message
                                        code="admin.userCurrencyPermissionsWithdraw"/></h4></div>
                                <hr/>
                                <div id="currency_permissions-withdraw" class="currency_permissions">
                                    <table class="table table-striped currency_permissions__table">
                                        <thead>
                                        <tr>
                                            <th class="left"><loc:message code="inputoutput.currency"/></th>
                                            <th class="center"><a class="sel_col" data-col="1"
                                                                  style="cursor: pointer">${none}</a></th>
                                            <th class="center"><a class="sel_col" data-col="2"
                                                                  style="cursor: pointer">${view}</a></th>
                                            <th class="center"><a class="sel_col" data-col="3"
                                                                  style="cursor: pointer">${write}</a></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${usersInvoiceWithdrawCurrencyPermissions}"
                                                   var="withdrawPermission">
                                            <tr class="currency_permissions__item">
                                                <td class="left">
                                                        ${withdrawPermission.currencyName}
                                                </td>
                                                <td class="center col1">
                                                    <input type="radio" name=withdraw"${withdrawPermission.currencyId}"
                                                           value="${none}"
                                                           data-userId="${withdrawPermission.userId}"
                                                           data-id="${withdrawPermission.currencyId}"
                                                           data-direction="WITHDRAW"
                                                           data-checked="${withdrawPermission.invoiceOperationPermission==none}"
                                                        <c:out value="${withdrawDisabledAttrib}"/>>
                                                </td>
                                                <td class="center col2">
                                                    <input type="radio" name=withdraw"${withdrawPermission.currencyId}"
                                                           value="${view}"
                                                           data-userId="${withdrawPermission.userId}"
                                                           data-id="${withdrawPermission.currencyId}"
                                                           data-direction="WITHDRAW"
                                                           data-checked="${withdrawPermission.invoiceOperationPermission==view}"
                                                        <c:out value="${withdrawDisabledAttrib}"/>>
                                                </td>
                                                <td class="center col3">
                                                    <input type="radio" name=withdraw"${withdrawPermission.currencyId}"
                                                           value="${write}"
                                                           data-userId="${withdrawPermission.userId}"
                                                           data-id="${withdrawPermission.currencyId}"
                                                           data-direction="WITHDRAW"
                                                           data-checked="${withdrawPermission.invoiceOperationPermission==write}"
                                                        <c:out value="${withdrawDisabledAttrib}"/>>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('${admin_processWithdraw}')">
                            <div class="col-md-6 content">
                                <c:if test="${!fn:contains(userActiveAuthorityOptions, admin_processWithdraw)}">
                                    <c:set value="disabled" var="withdrawDisabledAttrib"/>
                                </c:if>
                                <div class="text-center"><h4><loc:message
                                        code="admin.userCurrencyPermissionsTransfer"/></h4></div>
                                <hr/>
                                <div id="currency_permissions-withdraw" class="currency_permissions">
                                    <table class="table table-striped currency_permissions__table">
                                        <thead>
                                        <tr>
                                            <th class="left"><loc:message code="inputoutput.currency"/></th>
                                            <th class="center"><a class="sel_col" data-col="1"
                                                                  style="cursor: pointer">${none}</a></th>
                                            <th class="center"><a class="sel_col" data-col="2"
                                                                  style="cursor: pointer">${view}</a></th>
                                            <th class="center"><a class="sel_col" data-col="3"
                                                                  style="cursor: pointer">${write}</a></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${usersInvoiceTransferCurrencyPermissions}"
                                                   var="transferPermission">
                                            <tr class="currency_permissions__item">
                                                <td class="left">
                                                        ${transferPermission.currencyName}
                                                </td>
                                                <td class="center col1">
                                                    <input type="radio" name=transfer"${transferPermission.currencyId}"
                                                           value="${none}"
                                                           data-userId="${transferPermission.userId}"
                                                           data-id="${transferPermission.currencyId}"
                                                           data-direction="TRANSFER_VOUCHER"
                                                           data-checked="${transferPermission.invoiceOperationPermission==none}"
                                                        <c:out value="${withdrawDisabledAttrib}"/>>
                                                </td>
                                                <td class="center col2">
                                                    <input type="radio" name=transfer"${transferPermission.currencyId}"
                                                           value="${view}"
                                                           data-userId="${transferPermission.userId}"
                                                           data-id="${transferPermission.currencyId}"
                                                           data-direction="TRANSFER_VOUCHER"
                                                           data-checked="${transferPermission.invoiceOperationPermission==view}"
                                                        <c:out value="${withdrawDisabledAttrib}"/>>
                                                </td>
                                                <td class="center col3">
                                                    <input type="radio" name=transfer"${transferPermission.currencyId}"
                                                           value="${write}"
                                                           data-userId="${transferPermission.userId}"
                                                           data-id="${transferPermission.currencyId}"
                                                           data-direction="TRANSFER_VOUCHER"
                                                           data-checked="${transferPermission.invoiceOperationPermission==write}"
                                                        <c:out value="${withdrawDisabledAttrib}"/>>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </sec:authorize>
                        <div class="raw">
                            <div class="col-md-12 content">
                                <div class="currency_permissions__buttons">
                                    <button class="currency_permissions_btnOk blue-box"><loc:message
                                            code="admin.submit"/></button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            </c:if>

            </sec:authorize>

        </div>

    </div>
    <hr>
    <div hidden id="user-id">${user.id}</div>
</main>
<div hidden id="prompt_delete_rqst">
    <loc:message code="admin.promptDeleteUserFiles"/>
</div>
<div id="prompt_send_message_rqst" style="display: none">
    <loc:message code="admin.promptSendMessageRequestAccept"/>
</div>
<div id="prompt_delete_user_comment_rqst" style="display: none">
    <loc:message code="admin.promptDeleteUserComment"/>
</div>
<%@include file='order-modals.jsp' %>
<%@include file='../fragments/modal/withdraw_info_modal.jsp' %>
<%@include file='../fragments/modal/dialogRefill_info_modal.jsp' %>
<%@include file='stop-order-modals.jsp' %>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>


</body>
</html>

