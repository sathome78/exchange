<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>

<style>
    .d_footer {
        position: fixed;
        bottom: 0px;
        width: 100%;
        text-align: center;
        padding-top: 5px;
        padding-bottom: 5px;
    }
</style>
<footer class="d_footer">
    <div class="container">
        <div class="row">
            <span class="footer_link"><loc:message code="dashboard.allRightsReserved"/></span>
            <span class="footer_link"><a href="<c:url value="/termsAndConditions"/>"><loc:message
                    code="dashboard.terms"/></a></span>
            <span class="footer_link"><a href="<c:url value="/privacyPolicy"/>"><loc:message
                    code="dashboard.privacy"/></a></span>
            <span class="footer_link"><a href="<c:url value="/contacts"/>"><loc:message
                    code="dashboard.contactsAndSupport"/></a></span>
            <span class="footer_link"><a href="<c:url value="/aboutUs"/>"><loc:message
                    code="dashboard.aboutUs"/></a></span>
            <span class="footer_link"><a href="<c:url value="https://coins.exrates.me/"/>"><loc:message
                    code="partners.title"/></a></span>
        </div>
    </div>
</footer>
<script>
    $.get("/afgssr/gtag", function (data) {
        alert("success");
    })
        .done(function (data) {
            var count = data["count"];
            for (var i = 0; i < count; i++) {
                dataLayer.push({'event': 'transaction'});
            }

        })
        .fail(function () {
            alert("error");
        })
        .always(function () {
            alert("finished");
        });

    window.dataLayer = window.dataLayer || [];

    function gtag() {
        dataLayer.push(arguments);
    }

    gtag('js', new Date());

    gtag('config', 'UA-75711135-1');
</script>