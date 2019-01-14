package me.exrates.service;

import me.exrates.model.dto.QRCodeDto;
import me.exrates.service.util.QRCodeGeneratorUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@PropertySource(value = {"classpath:/qr_code.properties"})
public class QRCodeService {

    private static final String BTC = "BTC";

    @Value("${qr.width}")
    private int width;
    @Value("${qr.height}")
    private int height;

    @Value("${qr.btc.url-pattern}")
    private String btcUrlPattern;
    @Value("${qr.btc.query-pattern}")
    private String btcQueryPattern;

    public QRCodeDto getQrCodeImage(String ticker, String wallet, BigDecimal amountToWithdraw) {
        if (BTC.equals(ticker)) {
            final String url = String.format(btcUrlPattern, wallet, amountToWithdraw.toString());
            final String query = String.format(btcQueryPattern, wallet, amountToWithdraw.toString());

            return QRCodeDto.builder()
                    .url(url)
                    .image(QRCodeGeneratorUtil.generate(query, width, height))
                    .build();
        }
        return QRCodeDto.builder()
                .image(new byte[0])
                .build();
    }
}