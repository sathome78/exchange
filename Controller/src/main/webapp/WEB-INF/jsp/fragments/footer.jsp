<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>

<footer>
    <div class="container">
        <div class="row">
            <span class="footer_link"><loc:message code="dashboard.allRightsReserved"/></span>
            <span class="footer_link"><a href="<c:url value="/termsAndConditions"/>"><loc:message code="dashboard.terms"/></a></span>
            <span class="footer_link"><a href="<c:url value="/privacyPolicy"/>"><loc:message code="dashboard.privacy"/></a></span>
            <span class="footer_link"><a href="<c:url value="/contacts"/>"><loc:message code="dashboard.contactsAndSupport"/></a></span>
            <span class="footer_link"><a href="<c:url value="/aboutUs"/>"><loc:message code="dashboard.aboutUs"/></a></span>
        </div>
    </div>
</footer>