<%@taglib uri="http://www.springframework.org/tags/form" prefix="registrationform"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Title</title>
</head>
<body>
<%@include file='header.jsp'%><br>
<h2><loc:message code="${error}"/></h2>
<a href="/merchants/"><loc:message code="merchants.merchants"/></a>
</body>
</html>