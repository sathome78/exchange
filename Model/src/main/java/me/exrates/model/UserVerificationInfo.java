package me.exrates.model;

import me.exrates.model.dto.kyc.DocTypeEnum;

import java.util.Objects;

public class UserVerificationInfo {
    private Integer userId;
    private DocTypeEnum docTypeEnum;
    private String docId;

    public UserVerificationInfo() {
    }

    public UserVerificationInfo(Integer userId, DocTypeEnum docTypeEnum, String docId) {
        this.userId = userId;
        this.docTypeEnum = docTypeEnum;
        this.docId = docId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public DocTypeEnum getDocTypeEnum() {
        return docTypeEnum;
    }

    public void setDocTypeEnum(DocTypeEnum docTypeEnum) {
        this.docTypeEnum = docTypeEnum;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserVerificationInfo that = (UserVerificationInfo) o;
        return Objects.equals(userId, that.userId) &&
                docTypeEnum == that.docTypeEnum &&
                Objects.equals(docId, that.docId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, docTypeEnum, docId);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserVerificationDoc{");
        sb.append("userId=").append(userId);
        sb.append(", docTypeEnum=").append(docTypeEnum);
        sb.append(", docId='").append(docId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
