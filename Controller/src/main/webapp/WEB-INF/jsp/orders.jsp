<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>  
<head> 
<title><loc:message code="orders.title" /></title>
<style>  
body {  
 font-size: 20px;  
 color: teal;  
 font-family: Calibri;  
}  
  
td {  
 font-size: 15px;  
 color: black;  
 width: 100px;  
 height: 22px;  
 text-align: left;  
}  
  
.heading {  
 font-size: 18px;  
 color: white;  
 font: bold;  
 background-color: orange;  
 border: thick;  
}  
</style>  
</head>  
<body>  
<table>
	<tr>
		<td colspan=2><%@include file='header.jsp'%></td>
	</tr>
	<tr>
	<td><%@include file='usermenu.jsp'%></td>
	<td nowrap style="padding-left:30px">
		<h4><a href="order/sell/new"><loc:message code="orders.createordersell"/></a>
		<br>
		<a href="newordertobuy"><loc:message code="orders.createorderbuy"/></a></h4>		 
		<br><br><br>
		<loc:message code="orders.listtosell"/>
		<p>
				<table border=1>
					<tr>
						<td><loc:message code="orders.currsell"/></td>
						<td><loc:message code="orders.amountsell"/></td>
						<td><loc:message code="orders.currbuy"/></td>
						<td><loc:message code="orders.amountbuy"/></td>
						<td><loc:message code="orders.commission"/></td>
						<td><loc:message code="orders.amountwithcommission"/></td>
						<td><loc:message code="orders.datecreation"/></td>
						<td></td>
					</tr>
				 <c:forEach var="order" items="${orderMap.sell}">
					<tr>
						<td>
							${order.currencySellString}
						</td>
						<td>
							<fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountSell}"/>
						</td>
						<td>
							${order.currencyBuyString}
						</td>
						<td>
							<fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountBuy}"/>
						</td>
						<td>
							${order.commission}
						</td>
						<td>
							<fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountBuyWithCommission}"/>
						</td>
						<td>
							${order.dateCreation}
						</td>
	   					<td><a href="orders/accept?id=${order.id}"><loc:message code="orders.accept"/></a></td>  
					</tr>
				</c:forEach>
				</table>
				</p>
		<loc:message code="orders.listtobuy"/>
	</td>
	<tr>
		<td colspan=2 align=center><%@include file='footer.jsp'%></td>
	</tr>
</table>
</body>  
</html>  
