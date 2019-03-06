package me.exrates.model.dto.kyc.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.dto.kyc.DocTypeEnum;
import me.exrates.model.exceptions.KycException;

import java.util.ArrayList;
import java.util.List;

@Builder(builderClassName = "Builder")
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestOnBoardingDto {
    private InterfaceSetting interfaceSettings;
    private List<DocumentToCapture> documentsToCapture;
    private ContactData contactData;
    private ResultHandler resultHandler;

    public static RequestOnBoardingDto createOfParams(String callBackUrl, String email, String uid, DocTypeEnum docTypeEnum, String docId) {
        InterfaceSetting interfaceSetting = new InterfaceSetting("configCISDemo", "EN");
        ResultHandler resultHandler = new ResultHandler(callBackUrl, new CisConf("demo", uid, true));
        ContactData contactData = new ContactData("EMAIL", email);

        List<DocumentToCapture> documentsToCapture = new ArrayList<>();

        switch (docTypeEnum) {
            case ID:
                documentsToCapture.add(new DocumentToCapture(docId, "Identity document", "National ID card",
                        new String[]{"ID"}));
                break;
            case P:
                documentsToCapture.add(new DocumentToCapture(
                        docId, "Identity document", "Passport",
                        new String[]{"P"}));
            default:
                throw new KycException("Error initial identity document " + docTypeEnum);
        }

        return new RequestOnBoardingDto(interfaceSetting, documentsToCapture, contactData, resultHandler);
    }
}
