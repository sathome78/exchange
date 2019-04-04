<%@ page import="me.exrates.model.enums.invoice.InvoiceOperationPermission" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title><loc:message code="admin.title"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <%@include file='links_scripts.jsp' %>

    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value="/client/css/ekko-lightbox.min.css"/>" rel="stylesheet">
    <script type="text/javascript" src="<c:url value='/client/js/app.js'/>"></script>

    <%----------%>
    <script type="text/javascript" src="<c:url value="/client/js/ekko-lightbox.min.js"/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery-ui.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery-ui.js'/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/ieo_page.js'/>"></script>

<body>
    <%@include file='../fragments/header-simple.jsp' %>

    <main class="container">
        <div class="row">
            <%@include file='left_side_menu.jsp' %>
            <div class="col-md-8 content admin-container">

                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

                <div class="text-center">
                    <h4 class="modal-title">IEO</h4>
                </div>
                <br><div class="col-md-12 content">
                <button id="ieo_create" class="btn btn-default"><loc:message code="ieo.create_new"/></button>
                <br>
                <div id="create_ieo" class="collapse" hidden>
                    <form id="create_ieo_form" class="form_full_height_width">

                        <%--Description--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ieo.coinDescription"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="text"  name="description">
                            </div>
                        </div>
                        <%--coin name--%>
                         <div class="input-block-wrapper">
                             <div class="col-md-3 input-block-wrapper__label-wrapper">
                                 <label class="input-block-wrapper__label">
                                     <loc:message code="ieo.curName"/>
                                 </label>
                             </div>
                             <div class="col-md-9 input-block-wrapper__input-wrapper">
                                 <input type="text" name="currencyName">
                             </div>
                         </div>
                            <%--currency pair--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.currencytopairwith"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input type="text" id="currencyToPairWith" name="currencyToPairWith" disabled>
                                </div>
                            </div>
                            <%--maker email--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.makerEmail"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input type="text" name="makerEmail">
                                </div>
                            </div>
                            <%--rate--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.price"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input type="number" name="rate">
                                </div>
                            </div>
                            <%--amount--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.amount"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input type="number" name="amount">
                                </div>
                            </div>
                            <%--available balance--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.availableBalance"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input type="number" name="availableBalance">
                                </div>
                            </div>
                            <%--min amount--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.minamount"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input type="number" name="minAmount">
                                </div>
                            </div>
                            <%--max amount per user--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.maxAmountPerUser"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input type="number" name="maxAmountPerUser">
                                </div>
                            </div>
                            <%--max amount per claim--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.maxAmountPerClime"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input type="number" name="maxAmountPerClaim">
                                </div>
                            </div>
                            <%--start date--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.startTime"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input id="start_date_create" name="startDate"  autocomplete="off">
                                </div>
                            </div>
                            <%--end date--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.endTime"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input id="end_date_create" name="endDate" autocomplete="off">
                                </div>
                            </div>

                        <a id="ieo_create_send" class="blue-box"><loc:message
                                code="ieo.create_new"/></a>
                        <a id="ieo_create_close" class="blue-box"><loc:message
                                code="ieo.close"/></a>

                    </form>

                </div>
                <br>
                <table id="ieoTable" style="width:100%">
                    <thead>
                        <tr>
                          <%--  <th class="col-2 center blue-white"><loc:message code="ieo.coins"/></th>--%>
                            <th class="col-2 center blue-white"><loc:message code="ieo.curName"/></th>
                            <th class="col-2 center blue-white"><loc:message code="ieo.status"/></th>
                            <th class="col-2 center blue-white"><loc:message code="ieo.contributors"/></th>
                            <th class="col-3 center blue-white"><loc:message code="ieo.price"/></th>
                            <th class="col-2 center blue-white"><loc:message code="ieo.availableBalance"/></th>
                            <th class="col-3 center blue-white"><loc:message code="ieo.startTime"/></th>
                            <th class="col-3 center blue-white"><loc:message code="ieo.endTime"/></th>
                            <th class="col-3 center blue-white"><loc:message code="ieo.amount"/></th>
                            <th class="col-3 right blue-white"><loc:message code="ieo.minamount"/></th>
                            <th class="col-3 right blue-white"><loc:message code="ieo.maxAmountPerClime"/></th>
                            <th class="col-3 right blue-white"><loc:message code="ieo.maxAmountPerUser"/></th>
                        </tr>
                    </thead>
                </table>
                <br>

                <div id="update_ieo" class="collapse" hidden>

                    <form id="update_ieo-form" class="form_full_height_width">

                        <input type="text" id="id_upd" name="id" disabled hidden>

                        <%--coin name--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ieo.curName"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="text" id="currencyName" name="currencyName" disabled>
                            </div>
                        </div>
                        <%--Description--%>
                       <%-- <div class="input-block-wrapper" hidden>
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ieo.coinDescription"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="text" id="description" name="description">
                            </div>
                        </div>--%>
                        <%--maker email--%>
                       <%-- <div class="input-block-wrapper" hidden>
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ieo.makerEmail"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="text" id="makerEmail" name="makerEmail">
                            </div>
                        </div>--%>
                            <%--status--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.status"/>
                                    </label>
                                </div>
                                <div class="col-md-3 input-block-wrapper__input-wrapper">
                                    <select id="status" class="input-block-wrapper__input admin-form-input" name="status">
                                        <c:forEach items="${statuses}" var="status">
                                            <option value="${status}">${status}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                        <%--rate--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ieo.price"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="number" id="rate" name="rate">
                            </div>
                        </div>
                        <%--amount--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ieo.amount"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="number" id="amount" name="amount">
                            </div>
                        </div>
                        <%--available balance--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ieo.availableBalance"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="number" id="available_balance" name="availableBalance" required>
                            </div>
                        </div>
                        <%--min amount--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ieo.minamount"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="number" id="minAmount" name="minAmount">
                            </div>
                        </div>
                        <%--max amount per user--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ieo.maxAmountPerUser"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="number" id="maxAmountPerUser" name="maxAmountPerUser">
                            </div>
                        </div>
                        <%--max amount per claim--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ieo.maxAmountPerClime"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input type="number" id="maxAmountPerClaim" name="maxAmountPerClaim">
                            </div>
                        </div>
                        <%--start date--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ieo.startTime"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input id="start_date_upd" name="startDate" autocomplete="off">
                            </div>
                        </div>
                        <%--end date--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="ieo.endTime"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input id="end_date_upd" name="endDate" autocomplete="off">
                            </div>
                        </div>
                          <%--  &lt;%&ndash;maker email&ndash;%&gt;
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.makerEmail"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input type="text" id="makerEmail" name="makerEmail" disabled>
                                </div>
                            </div>--%>
                            <%--create date--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.creationDate"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input type="text" id="createdAt" name="createdAt" disabled>
                                </div>
                            </div>
                            <%--created by--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.maxAmountPerClime"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input type="text" id="createdBy" name="createdBy" disabled>
                                </div>
                            </div>
                            <%--version--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="ieo.version"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input type="number" id="version" name="version" disabled>
                                </div>
                            </div>

                            <a id="ieo_update_send" class="blue-box"><loc:message
                                code="button.update"/></a>
                        <a id="ieo_update_close" class="blue-box"><loc:message
                                code="ieo.close"/></a>
                        <a id="ieo_revert_send" class="blue-box"><loc:message
                                code="ieo.revert_ieo"/></a>

                    </form>

                </div>
            </div>



        </div>
    </main>

</body>
</html>
