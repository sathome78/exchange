package me.exrates.service;

import me.exrates.model.dto.QrCodeDto;
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

    public QrCodeDto getQrCodeImage(String ticker, String wallet, BigDecimal amountToWithdraw) {
        byte[] image;
        switch (ticker) {
            case BTC:
                final String qrCodeContent = String.format(btcPattern, wallet, amountToWithdraw.toString());
                image = QRCodeGeneratorUtil.generate(qrCodeContent, width, height);
                break;
            default:
                image = new byte[0];
                break;
        }
        return QrCodeDto.builder()
                .image(image)
                .build();
    }
}