<%--
  User: Sasha
  Date: 7/9/2018
  Time: 12:37 PM
--%>
<section id="files-upload">
    <h4 class="h4_green">
        KYC settings
    </h4>
    <h4 class="under_h4_margin"></h4>

    <div class="container">
        <div class="row">
            <div class="col-sm-8 content">
                <div class="tab-content">
                    <div class="input-block-wrapper clearfix">
                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                            <input id="individual__box"
                                   data-tabid="tab__individual"
                                   type="radio" name="kyc__tab__change"
                                   value="individual"><loc:message code="kyc.Individual"/>
                            <input id="entity__box"
                                   data-tabid="tab__individual"
                                   type="radio" name="kyc__tab__change"
                                   value="legal_entity"><loc:message code="kyc.LegalEntity"/><br>
                        </div>
                    </div>

                    <!-- Individual-->
                    <div class="tab-pane" id="tab__individual">

                        <form:form class="form-horizontal" id="kyc_individual_form"
                                   method="post"
                                   enctype="multipart/form-data"
                                   modelAttribute="kyc">
                            <input type="hidden"  class="csrfC" name="_csrf" value="${_csrf.token}"/>

                            <form:input type="hidden" path="person.confirmDocumentPath"/>
                            <form:input id="kyc_type" class="kyc__type" type="hidden" path="kycType"/>

                            <h4>Personal info</h4>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Name</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.name" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Surename</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.surname" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Middle name</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.middleName"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Sex</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                    <form:select class="form-control" path="person.gender">
                                        <form:options itemLabel="val" />
                                    </form:select>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Date of birth</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input id="date_of_birth" type="text" class="form-control input-block-wrapper__input" path="person.birthDate" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Phone</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.phone" required="true"/>
                                </div>
                                <div class="col-md-8 input-block-wrapper__error-wrapper__kyc">
                                    <form:errors path="person.phone"/>
                                </div>
                            </div>

                            <h4>Address</h4>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Country</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="address.country" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">City</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="address.city" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Street</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="address.street" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Zip code</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="address.zipCode" required="true"/>
                                </div>
                            </div>

                            <h4>Nationality</h4>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Nationality</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.nationality" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">ID card</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.idNumber" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <c:choose>
                                    <c:when test="${not empty kyc.person.confirmDocumentPath}">
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">Confirmation document</label>
                                        </div>
                                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                            <a target="_blank" href="/kyc/docs/${kyc.person.confirmDocumentPath}" class="alert-danger settings-upload-files">
                                                    ${kyc.person.confirmDocumentPath}</a>
                                        </div>
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label"></label>
                                        </div>
                                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                            <form:input type="file" class="settings-upload-files" path="person.confirmDocument"/>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">Confirmation document</label>
                                        </div>
                                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                            <form:input type="file" class="settings-upload-files" path="person.confirmDocument" required="true"/>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <div class="col-md-8 input-block-wrapper__error-wrapper__kyc">
                                    <form:errors path="person.confirmDocument"/>
                                </div>
                            </div>

                            <c:if test="${empty kyc.kycStatus || kyc.kycStatus == \"IN_PROGRESS\"}">
                                <div class="confirm-button-wrapper">
                                    <button formaction="/kyc/save" class="btn btn-info" type="submit">Save</button>
                                </div>
                                <div class="confirm-button-wrapper">
                                    <button formaction="/kyc/sendForApprove" class="btn btn-success" type="submit">Send</button>
                                </div>
                            </c:if>
                        </form:form>
                    </div>

                    <!-- Legal entity-->
                    <div class="tab-pane" id="tab__entity">
                        <form:form class="form-horizontal" id="kyc_entity_form"
                                   action="/kyc/saveLegalEntity"
                                   method="post"
                                   enctype="multipart/form-data"
                                   modelAttribute="kyc">
                            <h4>Company info</h4>

                            <form:input id="kyc_type" class="kyc__type" type="hidden" path="kycType"/>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Company name</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="companyName" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Registration country</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="regCountry" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Registration number</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="regNumber" required="true"/>
                                </div>
                            </div>

                            <h4>Registration address</h4>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Country</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="address.country" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">City</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="address.city" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Street</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="address.street" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Zip code</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="address.zipCode" required="true"/>
                                </div>
                            </div>

                            <h4>Authority person info</h4>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Position</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.position" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Name</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.name" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Surname</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.surname" required="true"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Middle name</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.middleName"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">Phone</label>
                                </div>
                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                    <form:input type="text" class="form-control input-block-wrapper__input" path="person.phone" required="true"/>
                                </div>
                                <div class="col-md-8 input-block-wrapper__error-wrapper__kyc">
                                    <form:errors path="person.phone"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <c:choose>
                                    <c:when test="${not empty kyc.person.confirmDocumentPath}">
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">Confirmation document</label>
                                        </div>
                                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                            <a target="_blank" href="/kyc/docs/${kyc.person.confirmDocumentPath}" class="alert-danger settings-upload-files">
                                                    ${kyc.person.confirmDocumentPath}</a>
                                        </div>
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label"></label>
                                        </div>
                                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                            <form:input type="file" class="settings-upload-files" path="person.confirmDocument"/>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">Confirmation document</label>
                                        </div>
                                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                            <form:input type="file" class="settings-upload-files" path="person.confirmDocument" required="true"/>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <div class="col-md-8 input-block-wrapper__error-wrapper__kyc">
                                    <form:errors path="person.confirmDocument"/>
                                </div>
                            </div>

                            <h4>Company documents</h4>

                            <div class="input-block-wrapper clearfix">
                                <c:choose>
                                    <c:when test="${not empty kyc.commercialRegistryPath}">
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">Commercial registry</label>
                                        </div>
                                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                            <a target="_blank" href="/kyc/docs/${kyc.commercialRegistryPath}" class="alert-danger settings-upload-files">
                                                    ${kyc.commercialRegistryPath}</a>
                                        </div>
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label"></label>
                                        </div>
                                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                            <form:input type="file" class="settings-upload-files" path="commercialRegistry"/>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">Commercial registry</label>
                                        </div>
                                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                            <form:input type="file" class="settings-upload-files" path="commercialRegistry" required="true"/>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <div class="col-md-8 input-block-wrapper__error-wrapper__kyc">
                                    <form:errors path="commercialRegistry"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper clearfix">
                                <c:choose>
                                    <c:when test="${not empty kyc.commercialRegistryPath}">
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">Company charter</label>
                                        </div>
                                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                            <a target="_blank" href="/kyc/docs/${kyc.companyCharterPath}" class="alert-danger settings-upload-files">
                                                    ${kyc.companyCharterPath}</a>
                                        </div>
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label"></label>
                                        </div>
                                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                            <form:input type="file" class="settings-upload-files" path="companyCharter"/>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">Company charter</label>
                                        </div>
                                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="overflow:visible;">
                                            <form:input type="file" class="settings-upload-files" path="companyCharter" required="true"/>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <div class="col-md-8 input-block-wrapper__error-wrapper__kyc">
                                    <form:errors path="companyCharter"/>
                                </div>
                            </div>

                            <c:if test="${empty kyc.kycStatus || kyc.kycStatus == \"IN_PROGRESS\"}">
                                <div class="confirm-button-wrapper">
                                    <button formaction="/kyc/save" class="btn btn-info" type="submit">Save</button>
                                </div>
                                <div class="confirm-button-wrapper">
                                    <button formaction="/kyc/sendForApprove" class="btn btn-success" type="submit">Send</button>
                                </div>
                            </c:if>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<span hidden id="successNoty">${successNoty}</span>

<link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
<script type="text/javascript" src="<c:url value='/client/js/kyc.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
