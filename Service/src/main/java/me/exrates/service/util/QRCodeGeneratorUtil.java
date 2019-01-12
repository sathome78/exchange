package me.exrates.service.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public final class QRCodeGeneratorUtil {

    private static final String DEFAULT_IMAGE_FORMAT = "PNG";

    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 200;

    public static byte[] generate(String content) {
        return generate(content, DEFAULT_IMAGE_FORMAT, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static byte[] generate(String content, String format) {
        return generate(content, format, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static byte[] generate(String content, int width, int height) {
        return generate(content, DEFAULT_IMAGE_FORMAT, width, height);
    }

    public static byte[] generate(String content, String format, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
        } catch (WriterException ex) {
            log.error("Problem with qr code encode process, return empty bytes array", ex);
            return new byte[0];
        }

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        try {
            MatrixToImageWriter.writeToStream(bitMatrix, format, pngOutputStream);
        } catch (IOException ex) {
            log.error("Problem with write bytes to stream, return empty bytes array", ex);
            return new byte[0];
        }
        return pngOutputStream.toByteArray();
    }
}