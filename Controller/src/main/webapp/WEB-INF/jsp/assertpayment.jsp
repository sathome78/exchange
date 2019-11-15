<%@page language="java"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="paymentForm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title><loc:message code="merchants.merchants"/></title>
    <style type="text/css">
        .error {
            color: #ff0000;
        }
        .errorblock {
            color: #000;
            background-color: #ffEEEE;
            border: 3px solid #ff0000;
            padding: 8px;
            margin: 16px;
        }
    </style>
</head>
<body>
<%@include file='header.jsp'%><br>
<c:url value="/merchants/yandexmoney/token/authorization" var="submitUrl" />
<paymentForm:form method="post" action="${submitUrl}" modelAttribute="payment">
    <table>
        <tr>
            <td>
                <loc:message code="merchants.amountToBeCredited"/>
            </td>
            <td>
                <input type="text" name="amount" value="${paymentPrepareData.amount}" readonly="true"/>
            </td>
        </tr>
        <tr>
            <td>
                <loc:message code="merchants.commission"/>
            </td>
            <td>
                <input type="text" name="commission" value="${paymentPrepareData.commission}" readonly="true"/>
            </td>
        </tr>
        <tr>
           <td>
               <loc:message code="merchants.sumToPay"/>
           </td>
            <td>
                <input type="text" value="${paymentPrepareData.sumToPay}">
            </td>
                <h4>${paymentPrepareData.merchant}</h4>
        </tr>
        <tr>
            <td>
                <input type="submit" value="<loc:message code='merchants.submitPayment'/>">
            </td>
            <td>
                <a href="<c:url value='/merchants'/>"><loc:message code="merchants.cancelPayment"/></a>
            </td>
        </tr>
    </table>
</paymentForm:form>
</body>
</html>