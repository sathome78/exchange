<%@ attribute name="methodsInfo" required="true" type="java.util.List" %>
<%@ attribute name="baseUrl" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>

<div class="apidoc_methods_container">
    <c:forEach items="${methodsInfo}" var="method">
        <h5><strong><loc:message code="${method.nameCode}"/></strong></h5>
        <p>URL: <c:out value='${baseUrl.concat(method.relativeUrl)}'/></p>
        <p><loc:message code="apiDoc.request.method"/>: <strong>${method.httpMethod}</strong> </p>
        <c:if test="${not empty method.requestParams}">
            <p><loc:message code="apiDoc.requestParams"/></p>
            <div class="well">
                <table class="apidoc_table">
                    <tbody>
                    <c:forEach items="${method.requestParams}" var="reqParam">
                        <tr>
                            <td><i>${reqParam.name}</i></td> <td><loc:message code="${reqParam.descriptionCode}"/>
                            <c:if test="${reqParam.optional}">(<loc:message code="apidoc.params.optional"/>)</c:if></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
        <p><loc:message code="apiDoc.response"/>: <loc:message code="${method.responseDescriptionCode}"/></p>
        <div class="well">
            <table class="apidoc_table">
                <tbody>
                <c:forEach items="${method.responseFields}" var="field">
                    <tr>
                        <td><i>${field.name}</i></td> <td><loc:message code="${field.descriptionCode}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

        </div>

    </c:forEach>
</div>