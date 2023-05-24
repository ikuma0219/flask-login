package com.example.helloworld;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
/**
 * Hello world!
 *
 */


public class Qrcode 
{
    public void publicMethod()
    {
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
          }
          catch( IOException e ) {
            e.printStackTrace();
          }
          catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
