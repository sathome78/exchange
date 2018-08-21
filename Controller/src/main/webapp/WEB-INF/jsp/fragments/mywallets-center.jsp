<%--
  User: Valk
--%>

<script type="text/javascript" src="<c:url value="/client/js/inputOutput/voucher_reedem.js"/>"></script>

<div id="balance-page" data-menuitemid="menu-mywallets" class="balance center-frame-container hidden">
    <h4 class="h4_green"><loc:message code="mywallets.title"/></h4>
    <a class="btn btn-default pull-right" id="voucher_reedem_dialog_button"><loc:message code="voucher.enter.code"/></a>
    <input size="30%" type="text" id="myInputTextField" onkeyup="mySearchFunction()" placeholder="Search for currency..">
    &#160; &#160;&#160;
    <input type='checkbox' id='exclude-zero-mybalances'>
    <label for="exclude-zero-mybalances"><loc:message code="userWallets.excludeZero"/></label>
    <table id="balance-table" class="balance__table">
        <tbody>
        <tr>
            <th class="left blue-white"><a href="#" class="white"><loc:message code="mywallets.currency"/></a></th>
            <th class="blue-white"></th>
            <th class="right blue-white"><a href="#" class="white"><loc:message code="mywallets.balance"/></a></th>
            <th class="right blue-white"><a href="#" class="white"><loc:message code="mywallets.onconfirmation"/></a></th>
            <th class="right blue-white"><a href="#" class="white"><loc:message code="mywallets.rbalance"/></a></th>
            <th class="right blue-white"><a href="#" class="white"><loc:message code="mywallets.reservedonorders"/></a></th>
            <th class="right blue-white"><a href="#" class="white"><loc:message code="mywallets.reservedonwithdraw"/></a></th>
            <th class="right blue-white"></th>
        </tr>
        <script type="text/template" id="balance-table_row">
            <tr class="balance-table__row">
                <td class="left blue-white"><@=currencyName@></td>
                <td class="left blue-white"><@=currencyDescription@></td>
                <td class="right"><@=activeBalance@></td>
                <td class="right"><@=onConfirmation@><%--<div class="on-confirmation-detail">
                    <@=
                    (function(){
                    if((+onConfirmationCount)==1){
                        return '('+onConfirmationStage+'/4)';
                    }else if((+onConfirmationCount)>1){
                        return '<span class="glyphicon glyphicon-search mywallet-item-detail" data-walletid='+id+'></span>';
                    }
                    })()
                    @></div>--%></td>
                <td class="right"><@=reservedBalance@></td>
                <td class="right"><@=reservedByOrders@></td>
                <td class="right"><@=reservedByMerchant@></td>
                <td class="table-button-block table-button-block--wallets">
                    <form class="table-button-block__form" action="/merchants/input" target="_blank">
                        <loc:message code="mywallets.input" var="inputButton"/>
                        <input type="text" hidden value=<@=currencyName@> name="currency" >
                        <button class="table-button-block__button btn btn-success" type="submit">${inputButton}</button>
                    </form>
                    <form class="table-button-block__form" action="<c:url value="/merchants/output"/>" target="_blank">
                        <loc:message code="mywallets.output" var="outputButton"/>
                        <input type="text" hidden value=<@=currencyName@> name="currency" >
                        <button class="table-button-block__button btn btn-danger" type="submit">${outputButton}</button>
                    </form>
                    <form class="table-button-block__form" action="<c:url value="#"/>">
                        <loc:message code="mywallets.history" var="historyButton"/>
                        <@=
                        '<button data-walletid='+id+' class="wallet-mystatement-button table-button-block__button btn btn-primary" type="submit">${historyButton}</button>'
                        @>
                    </form>
                    <form class="table-button-block__form" action="<c:url value="/merchants/transfer"/>" target="_blank">
                        <loc:message code="mywallets.transfer" var="transferButton"/>
                        <input type="text" hidden value=<@=currencyName@> name="currency" >
                        <button class="table-button-block__button btn btn-info" type="submit">${transferButton}</button>
                    </form>
                </td>
            </tr>
        </script>
        </tbody>
    </table>
</div>
<%@include file="../fragments/modal/voucher_code_modal.jsp" %>



