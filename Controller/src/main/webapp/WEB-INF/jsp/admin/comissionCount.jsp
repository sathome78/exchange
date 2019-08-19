
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
</head>

<body>
<%@include file='../fragments/header-simple.jsp'%>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="row">
            <div class="col-md-6 col-md-offset-2 content admin-container">
                <div class="text-center">
                    <h4 class="modal-title"><loc:message code="ordersearch.title"/></h4>
                </div>

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
                        </tr>
                        </thead>
                    </table>

            </div>
        </div>

    </div>
</main>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>

