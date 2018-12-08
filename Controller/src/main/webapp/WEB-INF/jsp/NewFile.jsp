<%--<%@page language="java"%>--%>
<%--<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>--%>
<%--<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>--%>
<%--<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>--%>
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">--%>
<%--<html>  --%>
<%--<head> --%>
<%--<title><loc:message code="mywallets.title"/></title>--%>
<%--<style>  --%>
<%--body {  --%>
 <%--font-size: 20px;  --%>
 <%--color: teal;  --%>
 <%--font-family: Calibri;  --%>
<%--}  --%>
  <%----%>
<%--td {  --%>
 <%--font-size: 15px;  --%>
 <%--color: black;  --%>
 <%--width: 100px;  --%>
 <%--height: 22px;  --%>
 <%--text-align: left;  --%>
<%--}  --%>
  <%----%>
<%--.heading {  --%>
 <%--font-size: 18px;  --%>
 <%--color: white;  --%>
 <%--font: bold;  --%>
 <%--background-color: orange;  --%>
 <%--border: thick;  --%>
<%--}  --%>
<%--</style>  --%>
<%--</head>  --%>
<%--<body>  --%>
<%--<table>--%>
	<%--<tr>--%>
		<%--<td colspan=2><%@include file='header.jsp'%></td>--%>
	<%--</tr>--%>
	<%--<tr>--%>
	<%--<td><%@include file='usermenu.jsp'%></td>--%>
	<%--<td>--%>
	<%--<c:forEach var="wallet" items="${walletList}">--%>
		<%--<p>--%>
		<%--<table width=100%>--%>
			<%--<tr>--%>
				<%--<td colspan=4>${wallet.name}</td>--%>
			<%--</tr>--%>
			<%--<tr>--%>
				<%--<td nowrap  width=200>--%>
				<%--<loc:message code="mywallets.abalance"/>: --%>
				<%--<fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.activeBalance}"/></td>--%>
				<%--<td>--%>
					<%--<form action="input">--%>
						<%--<loc:message code="mywallets.input" var="inputButton"/>--%>
						<%--<input type="submit" value="${inputButton}">--%>
					<%--</form>--%>
				<%--</td>--%>
				<%--<td>--%>
					<%--<form action="output">--%>
						<%--<loc:message code="mywallets.output" var="outputButton"/>--%>
						<%--<input type="submit" value="${outputButton}">--%>
					<%--</form>--%>
				<%--</td>--%>
				<%--<td>--%>
					<%--<form action="orders">--%>
						<%--<loc:message code="mywallets.createorder" var="createorderButton"/>--%>
						<%--<input type="submit" value="${createorderButton}">--%>
					<%--</form>--%>
				<%--</td>--%>
			<%--</tr>--%>
			<%--<tr>--%>
				<%--<td colspan=4>--%>
					<%--<loc:message code="mywallets.rbalance"/>: --%>
					<%--<fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.reservedBalance}"/>--%>
				<%--</td>--%>
			<%--</tr>--%>
		<%--</table>--%>
		<%--</p>--%>
	<%--</c:forEach>--%>


	 <%--</td>--%>
	<%--<tr>--%>
		<%--<td colspan=2 align=center><%@include file='footer.jsp'%></td>--%>
	<%--</tr>--%>
<%--</table>--%>
<%--</body>  --%>
<%--</html>  --%>


