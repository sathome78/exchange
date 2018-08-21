<%--
  User: Valk
--%>

<div id="myorders" data-submenuitemid="myhistory-button-orders" class="myorders center-frame-container hidden">
    <%----%>
    <div id="myorders-currency-pair-selector" class="currency-pair-selector dropdown">
        <c:set value="true" var="showAllPairsEnabled"/>
        <%@include file="currencyPairSelector.jsp" %>
    </div>
    <div>
        <button id="myorders-button-deal" class="myorders__button green-box margin-box"><loc:message
                code="myorders.deal"/></button>
        <button id="myorders-button-mine" class="myorders__button green-box margin-box"><loc:message
                code="myorders.title"/></button>
        <button id="myorders-button-accepted" class="myorders__button green-box margin-box"><loc:message
                code="myorders.accepted"/></button>
        <button id="myorders-button-cancelled" class="myorders__button red-box margin-box"><loc:message
                code="myorders.cancelled"/></button>
    </div>
    <%----%>

        <h4 class="h4_green"><loc:message code="orders.title"/></h4>
        <br><br>
        <table id="myHistoryOrdersTable"
               class="balance__table orders__table orders-buy-table"
               style="width:100%">
            <thead>
            <tr>
                <th class="col-2 myo_orid center blue-white"><loc:message code="myorders.id"/></th>
                <th class="col-3 myo_dcrt center blue-white"><loc:message
                        code="myorders.datecreation"/></th>
                <th class="col-2 myo_crpr center blue-white"><loc:message
                        code="myorders.currencypair"/></th>
                <th class="col-2 myo_crpr center blue-white"><loc:message code="orders.type"/></th>
                <th class="col-2 myo_amnt right blue-white"><loc:message
                        code="myorders.amount"/></th>
                <th class="col-2 myo_rate right blue-white"><loc:message code="myorders.rate"/></th>
                <th class="col-2 myo_totl right blue-white"><loc:message
                        code="myorders.total"/></th>
                <th class="col-2 myo_comm right blue-white"><loc:message
                        code="myorders.commission"/></th>
                <th class="col-2 myo_amcm right blue-white"><loc:message
                        code="myorders.amountwithcommission"/></th>
                <th class="col-4 myo_deal right blue-white"><loc:message
                        code="myorders.dealdate"/></th>
            </tr>
            </thead>
        </table>
        <br>
        <h4 class="h4_green"><loc:message code="myorders.stoporders"/></h4>
        <br>
        <ul class="pager balance__table myorders__pager">
            <li class="previous myorders-stop-table__backward"><a href="#"><loc:message code="table.backward"/></a></li>
            <div class="myorders-stop-table__page" style="display:inline-block"></div>
            <li class="next myorders-stop-table__forward"><a href="#"><loc:message code="table.forward"/></a></li>
        </ul>
        <table id="myorders-stop-table" class="balance__table myorders__table">
            <c:set value="myorders-stop-table_row" var="table_row_id"/>
            <%@include file="myStopOrders-center-tableBody.jsp" %>
        </table>

</div>

