<%--
  User: Valk
--%>
<section id="files-upload">
    <h4 class="h4_green">
        <loc:message code="admin.uploadFiles"/>
    </h4>
    <h4 class="under_h4_margin"></h4>

    <div class="container">
        <div class="row">
            <div class="col-sm-8 content">
                <c:choose>
                    <c:when test="${userFiles.size() != 0}">
                        <h5><loc:message code="admin.yourFiles"/></h5>

                        <div class="row usr_doc_row">
                            <div class="col-md-offset-0 col-md-10">
                                <c:forEach var="image" items="${userFiles}">
                                    <div class="img-container">
                                        <img src="${image.path}" data-toggle="lightbox" class="col-sm-4">
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:when>
                </c:choose>
                <c:choose>
                    <c:when test="${userFiles.size() < 3}">
                        <form method="post" id="upload" action="/settings/uploadFile" enctype="multipart/form-data" class="form-horizontal">
                            <c:forEach var="i" varStatus="vs" begin="1" end="${3 - userFiles.size()}">
                                <c:choose>
                                    <c:when test="${i == 1}">
                                        <input class="settings-upload-files" required type="file" name="file"/>
                                    </c:when>
                                    <c:otherwise>
                                        <input class="settings-upload-files" type="file" name="file"/>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                            <div class="confirm-button-wrapper">
                                <button type="submit" class="confirm-button"><loc:message
                                        code="admin.upload"/>
                                </button>
                            </div>
                        </form>
                    </c:when>
                </c:choose>
                <br/>
                <br/>
                <br/>
            </div>
        </div>
    </div>
</section>
