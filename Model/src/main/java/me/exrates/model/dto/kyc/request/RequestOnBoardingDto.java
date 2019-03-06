package me.exrates.model.dto.kyc.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.dto.kyc.DocTypeEnum;
import me.exrates.model.exceptions.KycException;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static RequestOnBoardingDto createOfParams(String callBackUrl, String email, String uid, String docId) {
        InterfaceSetting interfaceSetting = new InterfaceSetting("configCISDemo", "EN");
        ResultHandler resultHandler = new ResultHandler(callBackUrl, new CisConf("demo", uid, true));
        ContactData contactData = new ContactData("EMAIL", email);
        List<DocumentToCapture> documentsToCapture = Arrays.asList(
                new DocumentToCapture(
                        docId, "Identity document", "National ID card or Passport",
                        new String[]{"ID", "P"}),
                new DocumentToCapture(
                        RandomStringUtils.random(18, true, false), "Selfie", "User picture",
                        new String[]{"SELFIE"}));

        return new RequestOnBoardingDto(interfaceSetting, documentsToCapture, contactData, resultHandler);
    }
}
