
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title><loc:message code="manageOrder.title"/></title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <%@include file='links_scripts.jsp'%>
    <script type="text/javascript" src="<c:url value='/client/js/order/comissionsCount.js'/>"></script>

    <c:set var="admin_manualBalanceChange" value="<%=AdminAuthority.MANUAL_BALANCE_CHANGE%>"/>

</head>

<body>
<%@include file='../fragments/header-simple.jsp'%>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="row">
            <div class="col-md-6 col-md-offset-2 content admin-container">
                <div class="text-center">
                    <h4 class="modal-title"><loc:message code="admin.commissionsCount"/></h4>
                </div>

                <sec:authorize access="(hasAuthority('${admin_manualBalanceChange}'))">
                    <hr/>
                    <div class="text-center"><h4><loc:message code="admin.manualBalanceChange.title"/></h4>
                    </div>
                    <div class="col-md-12">
                        <form id="commission_withdraw_form" action="/2a8fy7b07dxe44/withdrawCommission/submit"
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
                                <label for="amount"><loc:message code="dashboard.amount"/> </label>
                                <input id="amount" name="amount" type="number" step="any" class="form-control"/>
                            </div>
                            <div class="form-item form-group">
                                <label for="amount"><loc:message code="admin.email"/> </label>
                                <input id="email" name="email" type="email" step="any" class="form-control"/>
                            </div>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            <div class="form-item form-group">
                                <label for="comment"><loc:message code="mywallets.comment"/> </label>
                                <textarea id="comment" name="comment"
                                          class="form-control"
                                          placeholder="Enter your comment..." required="required"></textarea>
                            </div>

                            <button id="commission_withdraw_submit" type="button" class="blue-box"><loc:message
                                    code="admin.submit"/></button>
                        </form>
                    </div>

                </sec:authorize>

                <form id="dates-form-info" class="form_full_height_width">

                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ordersearch.date" />
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input id="datetimepicker_start" type="text" name="from">
                                <input id="datetimepicker_end" type="text" name="to">
                            </div>
                            <div for="datetimepicker_start" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                                <label for="datetimepicker_start" class="input-block-wrapper__input"><loc:message
                                        code="ordersearch.errordatetime"/></label>
                            </div>
                            <div for="datetimepicker_end" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                                <label for="datetimepicker_end" class="input-block-wrapper__input"><loc:message
                                        code="ordersearch.errordatetime"/></label>
                            </div>
                        </div>

                    </form>

                <div class="info__button-wrapper">
                    <button id="submit-info__search" class="blue-box"
                            type="button"><loc:message code="ordersearch.submit"/></button>
                </div>

                    <table id="comissions-count-table">
                        <thead>
                        <tr>
                            <th><loc:message code="refill.currency"/></th>
                            <th><loc:message code="comission.refill"/></th>
                            <th><loc:message code="comission.withdraw"/></th>
                            <th><loc:message code="comission.transfer"/></th>
                            <th><loc:message code="comission.orders"/></th>
                            <th><loc:message code="referral.payments"/></th>
                            <th><loc:message code="comission.total"/></th>
                            <th><loc:message code="comission.withdrawed_to_users"/></th>
                            <th><loc:message code="companywallet.commission_balance"/></th>
                        </tr>
                        </thead>
                    </table>

            </div>
        </div>

    </div>
</main>

<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>

