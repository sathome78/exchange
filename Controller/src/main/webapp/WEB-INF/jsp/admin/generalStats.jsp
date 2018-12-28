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
<!DOCTYPE html>
<html>
<head>
    <title><loc:message code="admin.generalStats.title"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/reportAdmin.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/generalStats.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>

        <div class="col-md-7 content admin-container">
            <div class="text-center"><h4><loc:message code="admin.generalStats.title"/></h4></div>

            <div id="limitsMenu" class="buttons text-center">
                <button class="active adminForm-toggler blue-box">
                    <loc:message code="admin.generalStats.menu.stats"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="reports.balances.buttonTitle"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="reports.sliceBalances"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="reports.archive.sliceBalances"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="reports.archive.sliceInOut"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="admin.generalStats.menu.mailing"/>
                </button>
            </div>
            <div class="tab-content">
                <div id="panel1" class="tab-pane active">
                    <div class="row text-center" style="margin: 20px">
                        <div class="form_full_height_width">
                            <div class="input-block-wrapper">
                                <div class="col-md-2 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label"><loc:message
                                            code="userwallets.startDate"/></label>
                                </div>
                                <div class="col-md-4 input-block-wrapper__input-wrapper">
                                    <input id="datetimepicker_start" type="text"
                                           class="input-block-wrapper__input admin-form-input" name="startTime">
                                </div>
                                <div class="col-md-2 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label"><loc:message
                                            code="userwallets.endDate"/></label>
                                </div>
                                <div class="col-md-4 input-block-wrapper__input-wrapper">
                                    <input id="datetimepicker_end" type="text"
                                           class="input-block-wrapper__input admin-form-input" name="startTime">
                                </div>
                            </div>

                            <div class="input-block-wrapper" <%--style="margin-top: 50px"--%>>
                                <c:forEach items="${defaultRoleFilter}" var="role">
                                    <div class="col-md-2 input-block-wrapper__label-wrapper">
                                        <label class="input-block-wrapper__label">${role.key}</label>
                                    </div>
                                    <div class="col-md-1 input-block-wrapper__input-wrapper">
                                        <input class="roleFilter" type="checkbox"
                                            <c:out value="${role.value ? 'checked' : ''}"/>
                                               name="<c:out value="${role.key}" />">
                                    </div>
                                </c:forEach>
                            </div>
                        </div>


                    </div>
                    <%--Статистика по пользователям--%>
                    <div class="form_full_height_width col-md-8 col-md-offset-2">
                        <div class="input-block-wrapper">
                            <div class="col-md-8 input-block-wrapper__label-wrapper">
                                <table id="user-info-table-header">
                                    <tr>
                                        <th><label class="input-block-wrapper__label"><loc:message code="admin.users.new"/></label></th>
                                    </tr>
                                    <tr>
                                        <th><label class="input-block-wrapper__label"><loc:message code="admin.users.all"/></label></th>
                                    </tr>
                                    <tr>
                                        <th><label class="input-block-wrapper__label"><loc:message code="admin.users.notZeroBalances"/></label></th>
                                    </tr>
                                    <tr>
                                        <th><label class="input-block-wrapper__label"><loc:message code="admin.users.active"/></label></th>
                                    </tr>
                                    <tr>
                                        <th><label class="input-block-wrapper__label"><loc:message code="admin.users.successInput"/></label></th>
                                    </tr>
                                    <tr>
                                        <th><label class="input-block-wrapper__label"><loc:message code="admin.users.successOutput"/></label></th>
                                    </tr>
                                </table>
                            </div>
                            <div class="col-md-2 input-block-wrapper__label-wrapper">
                                <table id="user-info-table-body">
                                    <tr>
                                        <th><span id="new-users-quantity"></span></th>
                                    </tr>
                                    <tr>
                                        <th><span id="all-users-quantity"></span></th>
                                    </tr>
                                    <tr>
                                        <th><span id="not-zero-balances-users-quantity"></span></th>
                                    </tr>
                                    <tr>
                                        <th><span id="active-users-quantity"></span></th>
                                    </tr>
                                    <tr>
                                        <th><span id="success-input-users-quantity"></span></th>
                                    </tr>
                                    <tr>
                                        <th><span id="success-output-users-quantity"></span></th>
                                    </tr>
                                </table>
                            </div>
                            <div class="col-md-2 input-block-wrapper__input-wrapper">
                                <button id="refresh-users" class="btn btn-sm btn-default pull-right" style="margin-bottom: 10px" onclick="refreshUsersInfo()">
                                    <span class="glyphicon glyphicon-refresh"></span>
                                </button>
                            </div>
                        </div>
                        <p></p>

                        <%--Статистика оборота по валютным парам--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label"><loc:message
                                        code="admin.generalStats.button.currencyPairs"/></label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <button id="download-currency-pairs-report" class="blue-box" onclick="getTurnoverStatisticByPairsToDownload()">
                                    <loc:message code="admin.stats.download"/></button>
                            </div>
                        </div>
                        <%--Выгрузить статистику по вводу-выводу--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label"><loc:message
                                        code="admin.generalStats.button.currencyPairsInOutComis"/></label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <button id="download-input-output-summary-with-commissions" class="blue-box" onclick="getInOutStatisticByPairsToDownload()">
                                    <loc:message code="admin.stats.download"/></button>
                            </div>
                        </div>
                        <%--Выгрузить срез по кошелькам--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label"><loc:message
                                        code="reports.totalBalances"/></label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <button id="download-wallet-balances-for-period" class="blue-box" onclick="getWalletBalancesForPeriodToDownload()">
                                    <loc:message code="admin.stats.download"/></button>
                            </div>
                        </div>
                        <%--Выгрузить разбаланс--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label"><loc:message
                                        code="reports.totalBalancesWithInOut"/></label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <button id="download-wallet-balances-for-period-with-in-out" class="blue-box" onclick="getWalletBalancesForPeriodWithInOutToDownload()">
                                    <loc:message code="admin.stats.download"/></button>
                            </div>
                        </div>
                        <%--Выгрузить ордера--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label"><loc:message
                                        code="wallets.downloadOrders"/></label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <button id="download-orders" class="blue-box" onclick="uploadUserWalletsOrders()">
                                    <loc:message code="admin.stats.download"/></button>
                            </div>
                        </div>
                        <%--Выгрузить свод ввода-вывода--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label"><loc:message
                                        code="wallets.downloadInputOutputReport"/></label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <button id="download-input-output" class="blue-box" onclick="uploadInputOutputSummaryReport()">
                                    <loc:message code="admin.stats.download"/></button>
                            </div>
                        </div>
                        <%--Выгрузить данные--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label"><loc:message
                                        code="wallets.download"/></label>
                            </div>
                            <div class="col-md-4 input-block-wrapper__input-wrapper">
                                <button id="download-wallets-data" class="blue-box" onclick="uploadUserWallets()">
                                    <loc:message code="admin.stats.download"/></button>
                            </div>
                            <div class="col-md-1 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label"><loc:message
                                        code="admin.email"/></label>
                            </div>
                            <div class="col-md-4 input-block-wrapper__input-wrapper">
                                <input id="user-email" type="text"
                                       class="input-block-wrapper__input admin-form-input" name="userEmail"/>
                            </div>
                        </div>
                    </div>

                </div>

                <div id="panel2" class="tab-pane">
                    <div class="row text-center" style="margin: 20px">
                        <h4><loc:message code="reports.totalBalances"/></h4>
                    </div>


                    <div class="col-md-8">
                        <table id="total-balances-table">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.currency.id"/></th>
                                <th><loc:message code="transaction.currency"/></th>
                                <th><loc:message code="admin.rate.to.usd"/></th>
                                <th><loc:message code="admin.stats.allRealUsers"/></th>
                                <c:forEach items="${roleGroups}" var="roleGroup">
                                    <th>${roleGroup.name()}</th>
                                </c:forEach>
                            </tr>
                            </thead>
                        </table>

                    </div>

                </div>

                <div id="panel3" class="tab-pane">
                    <div class="row text-center" style="margin: 20px">
                        <h4><loc:message code="reports.sliceBalances"/></h4>
                    </div>


                    <div class="col-md-8">
                        <table id="balances-slice-statistic-table">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.currency.id"/></th>
                                <th><loc:message code="admin.externalWallets.name"/></th>
                                <th><loc:message code="admin.report.currency.certainty"/></th>
                                <th><loc:message code="admin.rate.to.usd"/></th>
                                <th><loc:message code="admin.rate.to.btc"/></th>
                                <th><loc:message code="admin.externalWallets.totalWalletBalance"/></th>
                                <th><loc:message code="admin.externalWallets.totalWalletBalanceUSD"/></th>
                                <th><loc:message code="admin.externalWallets.totalWalletBalanceBTC"/></th>
                                <th><loc:message code="admin.externalWallets.totalExratesBalance"/></th>
                                <th><loc:message code="admin.externalWallets.totalExratesBalanceUSD"/></th>
                                <th><loc:message code="admin.externalWallets.totalExratesBalanceBTC"/></th>
                                <th><loc:message code="admin.externalWallets.deviation"/></th>
                                <th><loc:message code="admin.externalWallets.deviationUSD"/></th>
                                <th><loc:message code="admin.externalWallets.deviationBTC"/></th>
                                <th><loc:message code="admin.externalWallets.lastUpdatedDate"/></th>
                            </tr>
                            </thead>
                        </table>

                    </div>

                </div>

                <div id="panel4" class="tab-pane">
                    <div class="row text-center" style="margin: 20px">
                        <h4><loc:message code="reports.archive.sliceBalances"/></h4>
                    </div>

                    <div class="col-md-6">
                        <table id="archive-balances-table"></table>
                    </div>
                    <div class="col-md-6">
                        <input id="datepicker-balances" type="text" class="input-block-wrapper__input admin-form-input">
                        <button id="download-balances-button" onclick="javascript:getArchiveBalances();return false;"><loc:message code="admin.generalStats.downloadList"/></button>
                    </div>
                </div>

                <div id="panel5" class="tab-pane">
                    <div class="row text-center" style="margin: 20px">
                        <h4><loc:message code="reports.archive.sliceInOut"/></h4>
                    </div>

                    <div class="col-md-6">
                        <table id="archive-inout-table"></table>
                    </div>
                    <div class="col-md-6">
                        <input id="datepicker-inout" type="text" class="input-block-wrapper__input admin-form-input">
                        <button id="download-inout-button" onclick="javascript:getArchiveInputOutput();return false;"><loc:message code="admin.generalStats.downloadList"/></button>
                    </div>
                </div>

                <div id="panel6" class="tab-pane">
                    <div class="col-md-6 col-md-offset-3">
                        <div class="form_full_height_width " style="margin: 50px 0">

                            <div class="input-block-wrapper">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label"><loc:message
                                            code="admin.generalStats.mailing.status"/> </label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <span id="mailing-status-indicator" style="font-size: 1.5rem"><i
                                            class="fa fa-close red"></i></span>
                                </div>
                            </div>

                            <div class="input-block-wrapper">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label"><loc:message
                                            code="admin.generalStats.mailing.time"/></label>
                                </div>
                                <div class="col-md-5 input-block-wrapper__input-wrapper">
                                    <input id="timepicker_mailtime" type="text"
                                           class="input-block-wrapper__input admin-form-input">
                                </div>
                                <div class="col-md-3 input-block-wrapper__input-wrapper">
                                    <button id="mail-time-submit" class="btn btn-primary btn-sm"><loc:message
                                            code="admin.submit"/></button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-4 col-md-offset-4">
                        <table id="report-emails-table">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.generalStats.mailing.email"/></th>
                                <th></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>

        </div>
    </div>

</main>

<div id="add-email-modal" class="modal fade modal-form-dialog">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
            </div>
            <div class="modal-body">
                <form id="add-email-form" class="form_full_height_width">
                    <div class="input-block-wrapper">
                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="admin.generalStats.mailing.email"/></label>
                        </div>
                        <div class="col-md-8 input-block-wrapper__input-wrapper">
                            <input name="email" class="input-block-wrapper__input admin-form-input">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <div class="delete-order-info__button-wrapper">
                    <button id="submit-email" class="delete-order-info__button">
                        <loc:message code="admin.submit"/></button>
                    <button class="delete-order-info__button" data-dismiss="modal">
                        <loc:message code="admin.cancel"/></button>
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
