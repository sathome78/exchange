<%--
  Created by IntelliJ IDEA.
  User: Valk
  Date: 01.06.2016
  Time: 12:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="left-sider cols-md-2">
    <sec:authorize access="isAuthenticated()">
        <h4 class="h4_green"><loc:message code="usermenu.referral"/></h4>
        <hr class="under_h4">
        <div class="refferal clearfix">
            <div id="refferal-reference" class="refferal__reference"></div>
            <div class="refferal__button-wrapper">
                <button id="refferal-generate" class="refferal__button">
                    <loc:message code="refferal.generate"/>
                </button>
                <button id="refferal-copy" class="refferal__button">
                    <loc:message code="refferal.copy"/>
                </button>
            </div>
        </div>
        <h4 class="h4_green"><loc:message code="mywallets.abalance"/></h4>
        &#160;
            <span class="glyphicon glyphicon-search"></span>
            <input id="my-wallets-filter" size="20%"
                   placeholder=<loc:message code="currency.search"/>>
        <div class="parent">
            <div class="total_usd_child">
                <label for="exclude-zero-statbalances"><loc:message code="userWallets.excludeZero"/></label>
            </div>
            <input type='checkbox' id='exclude-zero-statbalances'>
        </div>
        <hr class="under_h4">
        <div class="parent">
            <div class="total_usd_child">
                <h6><loc:message code="mywallets.balanceTotal"/></h6>
            </div>
            <div class="total_usd_child">
                <h6><div id="total-sum-usd" class="total-sum-usd" align="right"></div></h6>
            </div>
        </div>
        <hr class="under_h4">
        <div id="mywallets_table_wrapper">
            <table id="mywallets_table" class="table mywallets_table">
                <tbody>
                <tr>
                    <th><loc:message code="mywallets.currency"/></th>
                    <th><loc:message code="mywallets.amountTotal"/></th>
                    <th><loc:message code="mywallets.amount"/></th>
                </tr>
                <script type="text/template" id="mywallets_table_row">
                    <tr>
                        <td><@=currencyName@><br/>
                            <span class="text-muted"><@=description@></span>
                        </td>
                        <td class="right balanceTotal_<@=currencyName@>"><@=(Math.trunc(totalBalance * Math.pow(10, 5)) / Math.pow(10, 5)).toFixed(5)@></td>
                        <td class="right balance_<@=currencyName@>" hidden><@=activeBalance@></td>
                        <td class="right balanceCut_<@=currencyName@>"><@=(Math.trunc(activeBalance * Math.pow(10, 5)) / Math.pow(10, 5)).toFixed(5)@></td>
                    </tr>
                </script>
                </tbody>
            </table>
        </div>
    </sec:authorize>

    <div class="table-header-wrapper">

    <h4 class="h4_green"><loc:message code="currency.pairs"/></h4>
        <div id="trade_market_filter" class="table-filter__wrapper">
            <span class="glyphicon glyphicon-search"></span>
            <input id="pair-filter"  class="table-filter__input"
                   placeholder=<loc:message code="currency.search"/>>
        </div>
    </div>


    <hr class="under_h4">
    <div id="currency_table_wrapper">
    <table id="currency_table" class="table currency_table">
        <tr>
            <th><loc:message code="currency.pair"/></th>
            <th><loc:message code="currency.rate"/></th>
            <th>+/-</th>
        </tr>
        <script type="text/template" id="currency_table_row">
            <@var c = lastOrderRate == predLastOrderRate ? "black" : percentChange[0] == '-' ? "red" :
            "green";@>
            <tr id="stat_<@=currencyPairName@>">
                <td><@=currencyPairName@></td>
                <td class="right <@=c@> last_rate"><@=lastOrderRate@></td>
                <td class="right <@=c@> percent"><@=percentChange@></td>
            </tr>
        </script>
    </table>
    </div>
    <br>
    <br>
    <br>
    <%--TODO disable ATB banner--%>
    <%--<div style="text-align: center; height: 1000px; vertical-align: top">
        <a href="https://atbcoin.com/" target="_blank">
            <img src="/client/img/ATB_banner_new.gif" style="width: 185px; height: 600px; "/>
        </a>
    </div>--%>
</div>
