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
    <div class="row">
        <div class="cols-md-4">

            <div class="ht ht-active"><loc:message code="dashboard.dealshistory"/></div>
            <div class="ht"><loc:message code="dashboard.transactions"/></div>

            <table id="orders-history-table" class="table_middle">
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
        </div>
        <!-- end cols-md-2 -->

        <div class="cols-md-4">
            <div class="lightblue em-08">
                <span class="green margin-right"><loc:message code="dashboard.BUY"/></span>
                <span class="currencyBaseName red margin-right"></span>
                <loc:message code="dashboard.lowestPrice"/>
                <span id="minRate" class="green"></span>
            </div>
            <div class="buyBTC">
                <%--BUY FORM ...--%>
                <form:form id="dashboard-buy-form" class="dashboard-sell-buy__form" action="/order/submitnew/BUY"
                           method="post"
                           modelAttribute="orderCreateDto">
                    <div class="buyBTC__item item">
                    <span class="item__span"><loc:message code="dashboard.amount"/>
                        <span class="currencyBaseName item__span"></span>
                    </span>
                        <form:input id="amountBuy" path="amount" type="text" class="item__input numericInputField"/>
                    </div>

                    <div class="buyBTC__item item">
                    <span class="item__span"><loc:message code="dashboard.price"/>
                        <span class="currencyBaseName"></span>
                    </span>
                        <form:input id="exchangeRateBuy" path="exchangeRate" type="text"
                                    class="buyBTC__input usd_green numericInputField"/>
                    </div>

                    <div class="buyBTC__item item">
                        <span class="item__span"><loc:message code="dashboard.total"/></span>

                        <div id="totalForBuy" class="blue_area">
                            <span></span>
                            <span class="currencyConvertName white_right"></span>
                        </div>
                    </div>

                    <div class="buyBTC__item item">
                        <span class="item__span"><loc:message code="dashboard.commission"/></span>

                        <div id="calculatedCommissionForBuy" class="blue_area">
                            <span></span>
                            <span class="currencyConvertName white_right"></span>
                        </div>
                    </div>

                    <div class="buyBTC__item item">
                        <span class="item__span"><loc:message code="dashboard.amountwithcommission"/></span>

                        <div id="totalWithCommissionForBuy" class="blue_area">
                            <span></span>
                            <span class="currencyConvertName white_right"></span>
                        </div>
                    </div>

                    <div class="row">
                        <sec:authorize access="isAuthenticated()">
                            <button id="dashboard-buy" class="dashboard-sell-buy__button">
                                <loc:message code="dashboard.buy"/>
                            </button>
                            <button id="dashboard-buy-accept"
                                    class="dashboard-sell-buy__button dashboard-accept__button hidden">
                                <loc:message code="orders.accept"/>
                            </button>
                        </sec:authorize>
                    </div>
                </form:form>
                <%--... BUY FORM--%>
            </div>
            <!-- end buyBTC -->
            <span class="green marginTop-15"><loc:message code="dashboard.sellOrders"/></span>
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
                            <td class="order_exrate left"><@=exrate@></td>
                            <td class="order_amount right"><@=amountBase@></td>
                            <td class="right"><@=amountConvert@></td>
                            <td class="order_id" hidden><@=id@></td>
                            <td class="order_type" hidden><@=orderType@></td>
                        </tr>
                    </script>
                    </tbody>
                </table>
            </table>
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
                <form:form id="dashboard-sell-form" class="dashboard-sell-buy__form" action="/order/submitnew/SELL"
                           method="post"
                           modelAttribute="orderCreateDto">
                    <div class="buyBTC__item item">
                    <span class="item__span"><loc:message code="dashboard.amount"/>
                        <span class="currencyBaseName item__span"></span>
                    </span>
                        <form:input id="amountSell" path="amount" type="text" class="item__input numericInputField"/>
                    </div>

                    <div class="buyBTC__item item">
                    <span class="item__span"><loc:message code="dashboard.price"/>
                        <span class="currencyBaseName"></span>
                    </span>
                        <form:input id="exchangeRateSell" path="exchangeRate" type="text"
                                    class="buyBTC__input usd_green numericInputField"/>
                    </div>

                    <div class="buyBTC__item item">
                        <span class="item__span"><loc:message code="dashboard.total"/></span>

                        <div id="totalForSell" class="blue_area">
                            <span></span>
                            <span class="currencyConvertName white_right"></span>
                        </div>
                    </div>

                    <div class="buyBTC__item item">
                        <span class="item__span"><loc:message code="dashboard.commission"/></span>

                        <div id="calculatedCommissionForSell" class="blue_area">
                            <span></span>
                            <span class="currencyConvertName white_right"></span>
                        </div>
                    </div>

                    <div class="buyBTC__item item">
                        <span class="item__span"><loc:message code="dashboard.amountwithcommission"/></span>

                        <div id="totalWithCommissionForSell" class="blue_area">
                            <span></span>
                            <span class="currencyConvertName white_right"></span>
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
                        </sec:authorize>
                    </div>
                </form:form>
                <%--... SELL FORM--%>
            </div>
            <!-- end buyBTC -->
            <span class="green marginTop-15"><loc:message code="dashboard.buyOrders"/></span>
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
                            <td class="order_exrate left"><@=exrate@></td>
                            <td class="order_amount right"><@=amountBase@></td>
                            <td class="right"><@=amountConvert@></td>
                            <td class="order_id" hidden><@=id@></td>
                            <td class="order_type" hidden><@=orderType@></td>
                        </tr>
                    </script>
                    </tbody>
                </table>
                </tbody>
            </table>
        </div>
    </div>
</div>
<%--MODAL--%>
<%@include file="modal/order_create_confirm_modal.jsp" %>
<%--#order-create-confirm__modal--%>