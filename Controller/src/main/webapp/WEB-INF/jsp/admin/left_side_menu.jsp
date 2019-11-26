<%@ page import="me.exrates.model.enums.AdminAuthority" %><%--
  Created by IntelliJ IDEA.
  User: ogolv
  Date: 27.07.2016
  Time: 11:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<script type="text/javascript">
    $(function () {
        $('#cryptoWalletsMenu').find('ul').hide();
        emphasizeTitles();
        retrieveCoreWallets();
    });

    function emphasizeTitles() {
        var title = $('title').text().trim();
        var $menuItem = $('.sidebar > ul li').filter(function (index) {

            return ($(this).text().trim() === title) ||
                ($(this).find('ul li').filter(function () {
                    return ($(this).text().trim() === title);
                }).length > 0);

        });
        $menuItem.children('a').wrapInner('<strong></strong>');
    }

    function retrieveCoreWallets() {
        $.get('/2a8fy7b07dxe44/bitcoinWallet/listWallets', function (data) {
            data.forEach(function (item) {
                if (item['localizedTitle']) {
                    var link = '<li><a href="/2a8fy7b07dxe44/bitcoinWallet/' + item['merchantName'] + '">' + item['currencyName'] + ' - ' +
                        item['localizedTitle'] +'</a></li>';
                    $('#cryptoWalletsMenu').find('ul').append(link)
                }
            });
            var adkLink = '<li><a href="/2a8fy7b07dxe44/adkWallet"><loc:message code="admin.adk.wallet"/></a></li>';
            var usdtLink = '<li><a href="/2a8fy7b07dxe44/usdtWallet"><loc:message code="admin.usdt.wallet"/></a></li>';
            var usdxLink = '<li><a href="/2a8fy7b07dxe44/usdxWallet"><loc:message code="admin.usdx.wallet"/></a></li>';

            $('#cryptoWalletsMenu').find('ul').append(adkLink);
            $('#cryptoWalletsMenu').find('ul').append(usdtLink);
            $('#cryptoWalletsMenu').find('ul').append(usdxLink);
            $('#cryptoWalletsMenu').find('ul').show();

        })
    }



</script>

<div id="admin_side_menu" class="col-md-2">
    <c:set var="adminEnum" value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
    <c:set var="accountantEnum" value="<%=me.exrates.model.enums.UserRole.ACCOUNTANT%>"/>
    <c:set var="admin_userEnum" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>
    <c:set var="bot_traderEnum" value="<%=me.exrates.model.enums.UserRole.BOT_TRADER%>"/>
    <c:set var="admin_finOperatorEnum" value="<%=me.exrates.model.enums.UserRole.FIN_OPERATOR%>"/>
    <c:set var="admin_processWithdraw" value="<%=AdminAuthority.PROCESS_WITHDRAW%>"/>
    <c:set var="admin_processInvoice" value="<%=AdminAuthority.PROCESS_INVOICE%>"/>
    <c:set var="admin_deleteOrder" value="<%=AdminAuthority.DELETE_ORDER%>"/>
    <c:set var="admin_commentUser" value="<%=AdminAuthority.COMMENT_USER%>"/>
    <c:set var="admin_manageSessions" value="<%=AdminAuthority.MANAGE_SESSIONS%>"/>
    <c:set var="admin_currencyLimits" value="<%=AdminAuthority.SET_CURRENCY_LIMIT%>"/>
    <c:set var="admin_manageAccess" value="<%=AdminAuthority.MANAGE_ACCESS%>"/>
    <c:set var="admin_editUser" value="<%=AdminAuthority.EDIT_USER%>"/>
    <c:set var="email_edit" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>
<div class="sidebar">
    <ul>
        <li>
            <%--Пользователи--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}', '${admin_finOperatorEnum}')">
                <a href="<c:url value='/2a8fy7b07dxe44/users'/>"><loc:message code="admin.users"/></a>
            </sec:authorize>
        </li>


        <li>
            <%--Администраторы--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}', '${admin_finOperatorEnum}')">
                <a href="<c:url value='/2a8fy7b07dxe44/administrators'/>"><loc:message code="admin.admins"/></a>
            </sec:authorize>
        </li>

        <li>
            <%--withdraw--%>
            <sec:authorize access="hasAnyAuthority('${admin_processWithdraw}')">
                <a href="<c:url value='/2a8fy7b07dxe44/withdrawal'/>"><loc:message code="admin.withdrawRequests"/></a>
            </sec:authorize>
        </li>

        <li>
            <%--withdraw--%>
            <sec:authorize access="hasAnyAuthority('${admin_processWithdraw}')">
                <a href="<c:url value='/2a8fy7b07dxe44/withdrawal/vouchers'/>"><loc:message code="admin.transfers"/></a>
            </sec:authorize>
        </li>

        <li>
            <%--refill--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                <a href="<c:url value='/2a8fy7b07dxe44/refill'/>"><loc:message code="admin.refillRequests"/></a>
            </sec:authorize>
        </li>
        <%--Удаление ордера--%>
        <li>

            <sec:authorize access="hasAnyAuthority('${bot_traderEnum}', '${adminEnum}', '${accountantEnum}', '${admin_userEnum}', '${admin_finOperatorEnum}')">
                <a href="<c:url value='/2a8fy7b07dxe44/removeOrder'/>"><loc:message code="manageOrder.title"/></a>
            </sec:authorize>
        </li>
        <%--free coins--%>
        <li>

            <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                <a href="#freecoinsMenu" data-toggle="collapse">Free coins<i class="fa fa-caret-down"></i></a>
                <div class="collapse" id="freecoinsMenu">
                    <ul>
                        <li><a href="<c:url value='/2a8fy7b07dxe44/free-coins/distributionListPage'/>"><loc:message code="admin.freecoins.giveaway"/></a></li>
                        <li><a href="<c:url value='/2a8fy7b07dxe44/free-coins/currencyLimitsPage'/>"><loc:message code="admin.currencyLimits.title"/></a></li>
                    </ul>
                </div>
            </sec:authorize>
        </li>
        <%--ieo--%>
        <li>
            <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                <a href="<c:url value='/2a8fy7b07dxe44/ieo/page'/>">IEO</a>
            </sec:authorize>
        </li>
        <li>
            <%--Финансисты--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_finOperatorEnum}')">
                <a href="#finMenu"  data-toggle="collapse"><loc:message code="admin.finance"/><i class="fa fa-caret-down"></i></a>
                <div class="collapse" id="finMenu">
                    <ul>
                        <li><a href="<c:url value='/2a8fy7b07dxe44/editCurrencyLimits'/>"><loc:message code="admin.currencyLimits.title"/></a></li>
                        <li><a href="<c:url value='/2a8fy7b07dxe44/commissions'/>"><loc:message code="admin.commissions"/></a></li>
                        <li><a href="<c:url value='/2a8fy7b07dxe44/comission_count'/>"><loc:message code="admin.commissionsCount"/></a></li>
                        <li><a href="<c:url value='/2a8fy7b07dxe44/merchantAccess'/>"><loc:message code="admin.merchantAccess"/></a></li>
                        <li><a href="<c:url value='/2a8fy7b07dxe44/notificatorsSettings'/>"><loc:message code="admin.notificatorsMessagesSettings"/></a></li>
<%--                        <li><a href="<c:url value='/2a8fy7b07dxe44/referral'/>"><loc:message code="admin.referral"/></a></li>--%>
                        <li><a href="<c:url value='/2a8fy7b07dxe44/externalWallets'/>"><loc:message code="admin.externalWallets.title"/></a></li>
                    </ul>
                </div>
            </sec:authorize>
        </li>

        <li>
            <%--Отчеты--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_finOperatorEnum}')">
                <a href="#reportsMenu"  data-toggle="collapse"><loc:message code="admin.reports"/><i class="fa fa-caret-down"></i></a>
                <div class="collapse" id="reportsMenu">
                    <ul>
                        <li><a href="<c:url value='/2a8fy7b07dxe44/generalStats'/>"><loc:message code="admin.generalStats.title"/></a></li>
                        <li><a href="<c:url value='/companywallet'/>"><loc:message code="admin.companyWallet"/></a></li>
                    </ul>
                </div>

            </sec:authorize>
        </li>

        <li>
            <%--crypto wallets--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}')">
                <a href="#cryptoWalletsMenu"  data-toggle="collapse">
                    <loc:message code="cryptoWallets.title"/><i class="fa fa-caret-down"></i></a>
                <div class="collapse" id="cryptoWalletsMenu">
                    <ul>
                    </ul>
                </div>
            </sec:authorize>

        </li>

        <li>
            <%--auto trading settings--%>
            <sec:authorize access="hasAnyAuthority('${bot_traderEnum}', '${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                <a href="#autoTradingMenu"  data-toggle="collapse">
                      <loc:message code="admin.autoTrading.menu"/><i class="fa fa-caret-down"></i></a>
                <div class="collapse" id="autoTradingMenu">
                    <ul>
                        <li><a href="<c:url value='/2a8fy7b07dxe44/autoTrading'/>"><loc:message code="admin.autoTrading.title"/></a></li>
                        <li><a href="<c:url value='/2a8fy7b07dxe44/candleTable'/>"><loc:message code="admin.candleTable.title"/></a></li>
                    </ul>
                </div>
            </sec:authorize>

        </li>

        <li>
            <%--session control--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}', '${admin_finOperatorEnum}')">
                <a href="<c:url value='/2a8fy7b07dxe44/sessionControl'/>"><loc:message code="admin.sessionControl"/></a>
            </sec:authorize>
        </li>

        <li>
            <%--ip addresses--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}', '${admin_finOperatorEnum}')">
                <a href="<c:url value='/2a8fy7b07dxe44/ip'/>"><loc:message code="admin.IPAdresses"/></a>
            </sec:authorize>
        </li>

        <li>
            <%--alert settings--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}', '${admin_finOperatorEnum}')">
                <a href="<c:url value='/2a8fy7b07dxe44/alerts'/>"><loc:message code="admin.alertsForUsers"/></a>
            </sec:authorize>
        </li>

        <li>
            <%--email settings--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}', '${admin_finOperatorEnum}')">
                <a href="<c:url value='/2a8fy7b07dxe44/email/page'/>"><loc:message code="admin.emailSettings"/></a>
            </sec:authorize>
        </li>

    </ul>
</div>
</div>
