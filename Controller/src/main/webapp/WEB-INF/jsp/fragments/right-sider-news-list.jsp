<%--
  User: Valk
--%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<sec:authorize access="<%=AdminController.adminAnyAuthority%>">
    <a id="showAllNews" href="#"> <h4 class="h4_green"><loc:message code="news.title"/></h4></a>
</sec:authorize>

<sec:authorize access="<%=AdminController.adminAnyAuthority%>">
    <h4 class="h4_green"><loc:message code="news.title"/></h4>
</sec:authorize>
<hr class="under_h4">
<div id="news_table_wrapper">
<div id="news-table" class="news">

    <script type="text/template" id="news_table_row">
        <div class="news_item">
            <a class="news_item__ref" href="<@=ref+'newstopic'@>">
                <h5 class="news_item__header"><@=title@></h5>

                <p class="news_item__brief"><@=brief@></p>
            </a>
            <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                <div class="news__admin-section clearfix">
                    <button class="news__admin__delete-news-variant news__admin-section-button"><loc:message
                            code="news.deletevariant"/></button>
                    <button class="news__admin__add-news-variant news__admin-section-button"><loc:message
                            code="news.addvariant"/></button>
                </div>
            </sec:authorize>
        </div>
    </script>
</div>
</div>
<sec:authorize access="<%=AdminController.adminAnyAuthority%>">
    <button id="add-news-button" class="send_button"><loc:message code="news.addnews"/></button>
    <%--MODAL--%>
    <%@include file='modal/news_add_modal.jsp' %>
    <%@include file='modal/news_delete_modal.jsp' %>
    <%@include file='modal/news_list_modal.jsp' %>
    <%--#news-add-modal--%>
</sec:authorize>

