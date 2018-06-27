<%--
  User: Valk
--%>

<div id="orders" data-menuitemid="menu-orders" class="orders center-frame-container hidden">
    <%----%>
    <div id="orders-currency-pair-selector" class="currency-pair-selector dropdown">
        <c:set value="true" var="showAllPairsEnabled"/>
        <%@include file="currencyPairSelector.jsp" %>
    </div>
        <br><br>
    <h4 class="h4_green"><loc:message code="orders.title"/></h4>
        <br><br>
        <table id="myOrdersTable"
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
                <th class="col-2 myo_delt center blue-white"></th>
            </tr>
            </thead>
        </table>
        <br><br><br>
        <h4 class="h4_green"><loc:message code="myorders.stoporders"/></h4>
        </br>
        <ul class="pager balance__table orders__pager">
            <li class="previous stop_orders-table__backward"><a href="#"><loc:message code="table.backward"/></a></li>
            <div class="stop_orders-table__page" style="display:inline-block"></div>
            <li class="next stop_orders-table__forward"><a href="#"><loc:message code="table.forward"/></a></li>
        </ul>
        <table id="stop-orders-table" class="balance__table orders__table orders-buy-table">
            <c:set value="stop-orders-table_row" var="table_row_id"/>
            <%@include file="stopOrders-center-tableBody.jsp" %>
        </table>
</div>
<script type="text/javascript">
    var localDelete = '<loc:message code="myorders.delete"/>';
</script>
<%--MODAL--%>
<%@include file="modal/order_delete_confirm_modal.jsp" %>
<%--#order-delete-confirm__modal--%>

