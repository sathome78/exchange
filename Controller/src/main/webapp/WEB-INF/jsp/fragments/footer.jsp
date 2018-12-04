<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>

<style>
    .d_footer
    {
        position: relative;
        bottom:0px;
        width:100%;
        text-align:center;
        padding-top:5px;
        padding-bottom:5px;
    }
</style>
<footer class="d_footer" draggable: true>
    <div class="container">
        <div class="row">
            <span class="footer_link"><loc:message code="dashboard.allRightsReserved"/></span>
            <span class="footer_link"><a href="<c:url value="/termsAndConditions"/>"><loc:message code="dashboard.terms"/></a></span>
            <span class="footer_link"><a href="<c:url value="/privacyPolicy"/>"><loc:message code="dashboard.privacy"/></a></span>
            <span class="footer_link"><a href="<c:url value="/contacts"/>"><loc:message code="dashboard.contactsAndSupport"/></a></span>
            <span class="footer_link"><a href="<c:url value="/aboutUs"/>"><loc:message code="dashboard.aboutUs"/></a></span>
            <span class="footer_link"><a href="<c:url value="https://coins.exrates.me/"/>"><loc:message code="partners.title"/></a></span>
            <span class="footer_link"><a href="https://ex-open-api.firebaseapp.com/" target="_blank"><loc:message code="apiDoc.title"/></a></span>
        </div>
    </div>
</footer>
