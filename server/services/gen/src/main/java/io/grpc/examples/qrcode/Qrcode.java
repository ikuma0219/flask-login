package io.grpc.examples.qrcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;
import java.util.Base64;
import java.io.FileInputStream;
import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * Hello world!
 *
 */

public class Qrcode {
    public String createoriginal() {
        try {
            Random rand = new Random();
            String contents = String.format("%04d", rand.nextInt(10000));
            BarcodeFormat format = BarcodeFormat.QR_CODE;
            int width = 160;
            int height = 160;

            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(contents, format, width, height, hints);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ImageIO.write(image, "png", new File("test.png"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }

        String filePath = "test.png";
        try {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] imageData = new byte[(int) file.length()];
            fileInputStream.read(imageData);
            fileInputStream.close();
            String encodedStr = Base64.getEncoder().encodeToString(imageData);
            return encodedStr;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createerror(String name) {
        try {
            String contents = name;
            BarcodeFormat format = BarcodeFormat.QR_CODE;
            int width = 160;
            int height = 160;

            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(contents, format, width, height, hints);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ImageIO.write(image, "png", new File("test.png"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }

        String filePath = "test.png";
        try {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] imageData = new byte[(int) file.length()];
            fileInputStream.read(imageData);
            fileInputStream.close();
            String encodedStr = Base64.getEncoder().encodeToString(imageData);
            return encodedStr;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
