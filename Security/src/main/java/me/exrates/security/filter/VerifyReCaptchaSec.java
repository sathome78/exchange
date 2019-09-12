package me.exrates.security.filter;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

@Component
@PropertySource("classpath:/captcha.properties")
@Log4j2
public class VerifyReCaptchaSec {

    public static final String URL = "https://www.google.com/recaptcha/api/siteverify";
    private
    @Value("${captcha.skey}")
    String SECRET_KEY;

    public boolean verify(String recapchaResponse) {
        if (StringUtils.isEmpty(recapchaResponse)) {
            return false;
        }

        boolean result = false;

        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(URL).openConnection();
            /**/
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes("secret=" + SECRET_KEY + "&response=" + recapchaResponse);
            dos.flush();
            dos.close();
            /**/
            BufferedReader isbr = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            StringBuilder jsonStringResponse = new StringBuilder();

            while ((inputLine = isbr.readLine()) != null) {
                jsonStringResponse.append(inputLine);
            }
            isbr.close();

            JsonReader jsonReader = Json.createReader(new StringReader(jsonStringResponse.toString()));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();
            result = jsonObject.getBoolean("success");
        } catch (Exception e) {
            log.error("error while response of reCapcha verifying " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return result;
    }


}
