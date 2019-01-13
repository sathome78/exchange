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

    @Value("${qr.btc.pattern}")
    private String btcPattern;

    public QRCodeDto getQrCodeImage(String ticker, String wallet, BigDecimal amountToWithdraw) {
        if (BTC.equals(ticker)) {
            final String url = String.format(btcPattern, wallet, amountToWithdraw.toString());

            return QRCodeDto.builder()
                    .url(url)
                    .image(QRCodeGeneratorUtil.generate(url, width, height))
                    .build();
        }
        return QRCodeDto.builder()
                .image(new byte[0])
                .build();
    }
}