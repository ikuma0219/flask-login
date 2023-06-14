package com;

import java.io.IOException;
import java.util.Random;

import com.domain.Constants;
import com.google.zxing.WriterException;
import com.libs.Encoder;
import com.libs.Library;
import com.rs.GF;
import com.rs.RS;

public class Main {
    public static void main(String[] args) throws WriterException, IOException {
        Random r = new Random();
        int i = r.nextInt(2);
        if (i == 1) {
            original();
        } else {
            onetime();
        }
    }

    public static int[] original() throws WriterException, IOException {
        GF gf = new GF(0x011D, 256, 0);
        RS rs = new RS(gf);
        int[] jyouhou = rs.randomR(19);
        int[] code = rs.encode(jyouhou, 8);
        int[] keiretu = rs.randomTR(code, 4);
        int[] kcode = rs.encode(keiretu, 8);
        int[] kyoutu = rs.randomTR(kcode, 4);

        String source = "ABCDEFGHIJK";
        String encoding = "UTF-8";
        String filePath = "original.png";

        Library library = new Library();
        library.generateOriginalQR(source, kyoutu, encoding, filePath);

        Encoder encoder = new Encoder();
        encoder.create(Constants.PNG, "original");
        
        // for (int i = 0; i < jyouhou.length; i++) {
        //     System.out.print(jyouhou[i] + " ");
        // }
        // System.out.println();
        // for (int i = 0; i < code.length; i++) {
        //     System.out.print(code[i] + " ");
        // }
        // System.out.println();
        for (int i = 0; i < keiretu.length; i++) {
            System.out.print(keiretu[i] + " ");
        }
        // System.out.println();
        // for (int i = 0; i < kyoutu.length; i++) {
        //     System.out.print(kyoutu[i] + " ");
        // }

        return keiretu;
    }

    public static int[] onetime() throws WriterException, IOException {
        GF gf = new GF(0x011D, 256, 0);
        RS rs = new RS(gf);
        int[] jyouhou = rs.randomR(19);
        int[] code = rs.encode(jyouhou, 8);
        int[] keiretu = rs.randomTR(code, 4);

        Library library = new Library();
        int[] onetime = library.calcurateOnetime(keiretu);

        Encoder encoder = new Encoder();
        encoder.create(Constants.PNG, "errorLocation");

        for (int i = 0; i < onetime.length; i++) {
            System.out.print(onetime[i] + " ");
        }
        return onetime;
    }
}
