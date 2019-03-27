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
    <table id="balance-grid" class="balance__table">
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
                <td class="mywallet-item-id" hidden><@=id@></td>
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
                <td class="table-button-block--balance">
                    <span>
                        <% double numR = Math.random(); if (numR > 0.5) {  // to prevent outer code interception by xpath %>
                            <span>
                                <a href="/merchants/input?currency=<@=currencyName@>" target="_blank" class="btn btn-success butto-block">
                                    <loc:message code="mywallets.input"/>
                                </a>
                            </span>
                        <%  } else { %>
                            <span>
                                <a href="/merchants/input?currency=<@=currencyName@>" target="_blank" class="btn btn-success butto-block">
                                    <loc:message code="mywallets.input"/>
                                </a>
                            </span>
                        <% } %>
                        <% double numW = Math.random(); if (numW > 0.5) {  %>
                            <span>
                                <a href="/merchants/output?currency=<@=currencyName@>" target="_blank" class="butto-block btn btn-danger">
                                    <loc:message code="mywallets.output"/>
                                </a>
                            </span>
                        <%  } else { %>
                            <span>
                                <a href="/merchants/output?currency=<@=currencyName@>" target="_blank" class="butto-block btn btn-danger">
                                    <loc:message code="mywallets.output"/>
                                </a>
                            </span>
                        <% } %>
                        <% double numH = Math.random(); if (numH > 0.5) {  %>
                            <span>
                                <a style="cursor:pointer" target="_blank" class="wallet-mystatement-button butto-block btn btn-primary">
                                    <loc:message code="mywallets.history"/>
                                </a>
                            </span>
                        <%  } else { %>
                            <span>
                                <a style="cursor:pointer" target="_blank" class="wallet-mystatement-button butto-block btn btn-primary">
                                    <loc:message code="mywallets.history"/>
                                </a>
                            </span>
                        <% } %>
                        <% double numT = Math.random(); if (numT > 0.5) {  %>
                            <span>
                                <a href="/merchants/transfer?currency=<@=currencyName@>" target="_blank" class="butto-block btn btn-info">
                                    <loc:message code="mywallets.transfer"/>
                                </a>
                            </span>
                        <%  } else { %>
                            <span>
                                <a href="/merchants/transfer?currency=<@=currencyName@>" target="_blank" class="butto-block btn btn-info">
                                    <loc:message code="mywallets.transfer"/>
                                </a>
                            </span>
                        <% } %>
                    </span>
                </td>
            </tr>
        </script>
        </tbody>
    </table>
</div>
<%@include file="../fragments/modal/voucher_code_modal.jsp" %>



