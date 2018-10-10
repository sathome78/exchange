<%--
  Created by IntelliJ IDEA.
  User: Valk
  Date: 01.06.2016
  Time: 12:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%----%>

<div id="trading" data-menuitemid="menu-traiding" class="dashboard center-frame-container hidden">

    <div class="graphInfo__wrapper clearfix">
        <div id="dashboard-currency-pair-selector" class="currency-pair-selector dropdown">
            <%@include file="currencyPairSelector.jsp" %>
        </div>


        <div class="graphInfo">
            <div id="lastOrderAmountBase" class="graphInfo__item lightblue"><loc:message
                    code="dashboard.lastOrder"/><span class="green">USD</span></div>
            <div id="sumBase" class="graphInfo__item lightblue"><loc:message code="dashboard.volume"/><span
                    class="green">BTC</span></div>
            <div id="sumConvert" class="graphInfo__item"><span class="red">USD</span></div>
            <div id="firstOrderRate" class="graphInfo__item lightblue"><loc:message
                    code="dashboard.priceStart"/><span
                    class="green">USD</span></div>
            <div id="lastOrderRate" class="graphInfo__item lightblue"><loc:message code="dashboard.priceEnd"/><span
                    class="green">USD</span></div>
            <div id="percentChange" class="graphInfo__item lightblue"><loc:message code="dashboard.percentChange"/><span></span></div>
        </div>
    </div>

    <div id='graphics-container' style='position: relative;' class="clearfix">
        <%--<img class="loading" src="/client/img/loading-circle.gif" alt=""--%>
        <%--style='position: absolute;--%>
        <%--top: -100px;--%>
        <%--bottom: 0;--%>
        <%--left: 0;--%>
        <%--right: 0;--%>
        <%--margin: auto;--%>
        <%--z-index: 999;'/>--%>
        <%@include file="amcharts-graphics2.jsp" %>
    </div>
    <div class="row">
        <c:if test="${roleSettings.orderAcceptionSameRoleOnly}">
            <div id="order-filter-selector">
                <div class="col-md-3"><span><strong><loc:message code="orders.roleFilter"/> </strong></span></div>
                <div class="col-md-1 blue-switch"><input id="order-row-filter-box" type="checkbox"/></div>
            </div>
        </c:if>
    </div>

    <div class="row">


        <div class="cols-md-4">
            <div class="lightblue em-08">
                <span class="green margin-right"><loc:message code="dashboard.BUY"/></span>
                <span class="currencyBaseName red margin-right"></span>
                <loc:message code="dashboard.lowestPrice"/>
                <span id="minRate" class="green"></span>
            </div>
            <div class="buyBTC">
                <%--BUY FORM ...--%>
                    <sec:authorize access="isAuthenticated()">
                        <div class="buyBTC__item item">
                            <span class="item__span"><loc:message code="dashboard.yourBalance"/>
                        <span class="currencyConvertName item__span"></span>
                    </span>
                            <span id="currentConvertBalance" class="item__span trading-current-balance"></span>
                        </div>
                    </sec:authorize>
                <form:form id="dashboard-buy-form" class="dashboard-sell-buy__form" action="/order/submitnew/BUY"
                           method="post"
                           modelAttribute="orderCreateDto">

                    <div class="buyBTC__item item">
                    <span class="item__span"><loc:message code="dashboard.amount"/>
                        <span class="currencyBaseName item__span"></span>
                    </span>
                        <div class="dark_blue_area"><span class="currencyBaseName"></span></div>
                        <form:input id="amountBuy" path="amount" type="text" class="item__input numericInputField"/>
                    </div>

                    <div class="buyBTC__item item">
                    <span class="item__span"><loc:message code="dashboard.price"/>
                        <span class="currencyBaseName"></span>
                    </span>
                        <div class="dark_blue_area"><span class="currencyConvertName"></span></div>
                        <form:input id="exchangeRateBuy" path="exchangeRate" type="text"
                                    class="buyBTC__input usd_green numericInputField"/>
                    </div>

                    <div class="buyBTC__item item">
                        <span class="item__span"><loc:message code="dashboard.total"/></span>
                        <div class="dark_blue_area"><span class="currencyConvertName"></span></div>
                        <input id="totalForBuy" class="item__input numericInputField"/>
                            <%--<div id="totalForBuy" class="blue_area">
                            <span></span>
                        </div>--%>
                    </div>

                    <div class="buyBTC__item item">
                        <span class="item__span"><loc:message code="dashboard.commission"/></span>
                        <div class="dark_blue_area"><span class="currencyConvertName"></span></div>
                        <div id="calculatedCommissionForBuy" class="blue_area">
                            <span></span>
                        </div>
                    </div>

                    <div class="buyBTC__item item">
                        <span class="item__span"><loc:message code="dashboard.amountwithcommission"/></span>
                        <div class="dark_blue_area"><span class="currencyConvertName"></span></div>
                        <div id="totalWithCommissionForBuy" class="blue_area">
                            <span></span>
                        </div>
                    </div>

                    <div class="row">
                        <c:if test="${accessToOperationForUser==false}">
                            <input id="accessToOperationForUser" hidden value='${accessToOperationForUser}'/>
                            <input id="accessToOperationForUserTextError" hidden value='<loc:message code="merchant.operationNotAvailable"/>'/>
                        </c:if>
                        <sec:authorize access="isAuthenticated()">
                            <button id="dashboard-buy" class="dashboard-sell-buy__button">
                                <loc:message code="dashboard.buy"/>
                            </button>
                            <button id="dashboard-buy-accept"
                                    class="dashboard-sell-buy__button dashboard-accept__button hidden">
                                <loc:message code="orders.accept"/>
                            </button>
                            <button id="dashboard-buy-accept-reset"
                                    class="dashboard-sell-buy__button dashboard-accept-reset__button hidden">
                                X
                            </button>
                        </sec:authorize>
                    </div>
                </form:form>
                <%--... BUY FORM--%>
            </div>
            <!-- end buyBTC -->
            <span class="red marginTop-25"><loc:message code="dashboard.sellOrders"/></span>
            <div id="orders-sell-table-wrapper">
            <table class="table_middle table_middle2">
                <table id="dashboard-orders-sell-table" class="dashboard-order__table table_middle">
                    <tbody>
                    <tr class="ht__theader">
                        <th class="left"><loc:message code="dashboard.orderPrice"/></th>
                        <th class="center currencyBaseName"></th>
                        <th class="center currencyConvertName"></th>
                    </tr>
                    <script type="text/template" id="dashboard-orders-sell-table_row">
                        <tr class="dashboard-order__tr">
                            <@ var symbolsLimit = 14; @>
                            <td class="order_exrate left" title="<@=exrate@>"><@=exrate.length > symbolsLimit ?
                                exrate.substring(0, symbolsLimit) + '...' : exrate@></td>
                            <td class="order_amount right" title="<@=amountBase@>"><@=amountBase.length > symbolsLimit ?
                                amountBase.substring(0, symbolsLimit) + '...' : amountBase@></td>
                            <td class="right" title="<@=amountConvert@>"><@=amountConvert.length > symbolsLimit ?
                                amountConvert.substring(0, symbolsLimit) + '...' : amountConvert@></td>
                            <td class="order_id" hidden><@=ordersIds@></td>
                            <td class="order_type" hidden><@=orderType@></td>
                        </tr>
                    </script>
                    </tbody>
                </table>
            </table>
            </div>

        </div>
        <!-- end cols-md-4 -->
        <div class="cols-md-4">
            <div class="lightblue em-08">
                <span class="red margin-right"><loc:message code="dashboard.SELL"/></span>
                <span class="currencyBaseName red margin-right"></span>
                <loc:message code="dashboard.highestPrice"/>
                <span id="maxRate" class="red"></span>
            </div>

            <div class="buyBTC">
                <%--SELL FORM ... --%>
                    <sec:authorize access="isAuthenticated()">
                        <div class="buyBTC__item item">
                    <span class="item__span"><loc:message code="dashboard.yourBalance"/>
                        <span class="currencyBaseName item__span"></span>
                    </span>
                            <span id="currentBaseBalance" class="item__span trading-current-balance"></span>
                        </div>
                    </sec:authorize>
                <form:form id="dashboard-sell-form" class="dashboard-sell-buy__form" action="/order/submitnew/SELL"
                           method="post"
                           modelAttribute="orderCreateDto">
                    <div class="buyBTC__item item">
                    <span class="item__span"><loc:message code="dashboard.amount"/>
                        <span class="currencyBaseName item__span"></span>
                    </span>
                        <div class="dark_blue_area"><span class="currencyBaseName"></span></div>
                        <form:input id="amountSell" path="amount" type="text" class="item__input numericInputField"/>
                    </div>

                    <div class="buyBTC__item item">
                    <span class="item__span"><loc:message code="dashboard.price"/>
                        <span class="currencyBaseName"></span>
                    </span>
                        <div class="dark_blue_area"><span class="currencyConvertName"></span></div>
                        <form:input id="exchangeRateSell" path="exchangeRate" type="text"
                                    class="buyBTC__input usd_green numericInputField"/>
                    </div>

                    <div class="buyBTC__item item">
                        <span class="item__span"><loc:message code="dashboard.total"/></span>
                        <div class="dark_blue_area"><span class="currencyConvertName"></span></div>
                        <input id="totalForSell" class="item__input numericInputField"/>
                       <%-- <div id="totalForSell" class="blue_area">
                            <span></span>
                        </div>--%>
                    </div>

                    <div class="buyBTC__item item">
                        <span class="item__span"><loc:message code="dashboard.commission"/></span>
                        <div class="dark_blue_area"><span class="currencyConvertName"></span></div>
                        <div id="calculatedCommissionForSell" class="blue_area">
                            <span></span>
                        </div>
                    </div>

                    <div class="buyBTC__item item">
                        <span class="item__span"><loc:message code="dashboard.amountwithcommission"/></span>
                        <div class="dark_blue_area"><span class="currencyConvertName"></span></div>
                        <div id="totalWithCommissionForSell" class="blue_area">
                            <span></span>
                        </div>
                    </div>

                    <div class="row">
                        <sec:authorize access="isAuthenticated()">
                            <button id="dashboard-sell" class="dashboard-sell-buy__button">
                                <loc:message code="dashboard.sell"/>
                            </button>
                            <button id="dashboard-sell-accept"
                                    class="dashboard-sell-buy__button dashboard-accept__button hidden">
                                <loc:message code="orders.accept"/>
                            </button>
                            <button id="dashboard-sell-accept-reset"
                                    class="dashboard-sell-buy__button dashboard-accept-reset__button hidden">
                                X
                            </button>
                        </sec:authorize>
                    </div>
                </form:form>
                <%--... SELL FORM--%>
            </div>
            <!-- end buyBTC -->
            <span class="green marginTop-25"><loc:message code="dashboard.buyOrders"/></span>
            <div id="orders-buy-table-wrapper">
            <table class="table_middle table_middle2">
                <tbody class="table_middle2">
                <table id="dashboard-orders-buy-table" class="dashboard-order__table table_middle">
                    <tbody>
                    <tr class="ht__theader">
                        <th class="left"><loc:message code="dashboard.orderPrice"/></th>
                        <th class="center currencyBaseName"></th>
                        <th class="center currencyConvertName"></th>
                    </tr>
                    <script type="text/template" id="dashboard-orders-buy-table_row">
                        <tr class="dashboard-order__tr">
                            <@ var symbolsLimit = 14; @>
                            <td class="order_exrate left" title="<@=exrate@>"><@=exrate.length > symbolsLimit ?
                                exrate.substring(0, symbolsLimit) + '...' : exrate@></td>
                            <td class="order_amount right" title="<@=amountBase@>"><@=amountBase.length > symbolsLimit ?
                                amountBase.substring(0, symbolsLimit) + '...' : amountBase@></td>
                            <td class="right" title="<@=amountConvert@>"><@=amountConvert.length > symbolsLimit ?
                                amountConvert.substring(0, symbolsLimit) + '...' : amountConvert@></td>
                            <td class="order_id" hidden><@=ordersIds@></td>
                            <td class="order_type" hidden><@=orderType@></td>
                        </tr>
                    </script>
                    </tbody>
                </table>
                </tbody>
            </table>
            </div>
        </div>

        <div class="cols-md-4">
            <div class="lightblue em-08">
                <span class="green margin-right">Stop-Limit</span>
            </div>
            <div class="buyBTC">
                <%--stop-limit ...--%>
                <sec:authorize access="isAuthenticated()">
                    <div class="buyBTC__item item">
                            <span class="item__span"><loc:message code="dashboard.yourBalance"/>
                        <span class="currencyConvertName item__span"></span>
                    </span>
                        <span id="currentConvertBalance" class="item__span trading-current-balance currentConvertBalance"></span>
                    </div>
                </sec:authorize>
                <form:form id="dashboard-stop-order-form" class="dashboard-sell-buy__form" action="/order/submitnew/BUY"
                           method="post"
                           modelAttribute="orderCreateDto">

                    <div class="buyBTC__item stop item">
                    <span class="item__span"><loc:message code="dashboard.amount"/>
                        <span class="currencyBaseName item__span"></span>
                    </span>
                        <div class="dark_blue_area"><span class="currencyBaseName"></span></div>
                        <form:input id="amount-stop" path="amount" type="text" class="item__input numericInputField"/>
                    </div>

                    <div class="buyBTC__item stop item">
                    <span class="item__span">Stop
                        <%--<span class="currencyBaseName"></span>--%>
                    </span>
                        <div class="dark_blue_area"><span class="currencyConvertName"></span></div>
                        <form:input id="stop" path="stop" type="text"
                                    class="buyBTC__input usd_green numericInputField"/>
                    </div>

                    <div class="buyBTC__item stop item">
                    <span class="item__span">Limit
                        <%--<span class="currencyBaseName"></span>--%>
                    </span>
                        <div class="dark_blue_area"><span class="currencyConvertName"></span></div>
                        <form:input id="limit-stop" path="exchangeRate" type="text"
                                    class="buyBTC__input usd_green numericInputField"/>
                    </div>


                    <div class="buyBTC__item stop item">
                        <span class="item__span"><loc:message code="dashboard.total"/></span>
                        <div class="dark_blue_area"><span class="currencyConvertName"></span></div>
                        <input id="totalForStop" class="item__input numericInputField"/>
                            <%--<div id="totalForBuy" class="blue_area">
                            <span></span>
                        </div>--%>
                    </div>

                    <div class="row">
                        <sec:authorize access="isAuthenticated()">
                            <button id="dashboard-stop-buy" data-action="BUY" class="dashboard-stop-sell-buy__button">
                                <loc:message code="dashboard.buy"/>
                            </button>
                            <button id="dashboard-stop-sell" data-action="SELL" class="dashboard-stop-sell-buy__button">
                                <loc:message code="dashboard.sell"/>
                            </button>
                        </sec:authorize>
                    </div>
                </form:form>
                <%--... stop limit FORM--%>
            </div>
            <!-- end stop limit -->

           <div id="orders-history-table-wrapper" class="margin-top-10">
           <div class="deals-scope-switcher__wrapper">
               <div id="all-deals" class="deals-scope-switcher__button ht ht-active"
                    data-tableId="orders-history-table">
                   <loc:message code="dashboard.dealshistory"/>
               </div>
               <sec:authorize access="isAuthenticated()">
                   <div id="my-deals" class="deals-scope-switcher__button ht"
                        data-tableId="orders-history-table__my-deals">
                       <loc:message code="dashboard.transactions"/>
                   </div>
               </sec:authorize>
           </div>
          <%-- ALL TRADES TABLE--%>

           <table id="orders-history-table" class="orders-history-table table_middle default-skin">
               <tbody>
               <tr class="ht__theader">
                   <th class="center"><loc:message code="dashboard.time"/></th>
                   <th class="center"><loc:message code="dashboard.price"/></th>
                   <th class="center currencyBaseName"></th>
               </tr>
               <script type="text/template" id="orders-history-table_row">
                   <tr>
                       <td><@=dateAcceptionTime@></td>
                       <td><@=rate@></td>
                       <@var c = operationType == 'BUY' ? 'green' : 'red';@>
                       <td class=<@=c@>><@=amountBase@></td>
                   </tr>
               </script>
               </tbody>
           </table>
           <%--MY TRADES ONLY TABLE--%>
           <table id="orders-history-table__my-deals" class="orders-history-table table_middle hidden">
               <tbody>
               <tr class="ht__theader">
                   <th class="center"><loc:message code="dashboard.time"/></th>
                   <th class="center"><loc:message code="dashboard.price"/></th>
                   <th class="center currencyBaseName"></th>
               </tr>
               <script type="text/template" id="orders-history-table_row__my-deals">
                   <tr>
                       <td><@=dateAcceptionTime@></td>
                       <td><@=rate@></td>
                       <@var c = operationType == 'BUY' ? 'green' : 'red';@>
                       <td class=<@=c@>><@=amountBase@></td>
                   </tr>
               </script>
               </tbody>
           </table>
           </div>

        </div>
    </div>
</div>
<%--MODAL--%>
<%@include file="modal/order_create_confirm_modal.jsp" %>
<%--#order-create-confirm__modal--%>
