package com.libs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;


public class Encoder {
    public String create(String fmt, String name) {
        String fileName = name + "." + fmt;
        try {
            File file = new File(fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] imageData = new byte[(int) file.length()];
            fileInputStream.read(imageData);
            fileInputStream.close();
            String encodedStr = Base64.getEncoder().encodeToString(imageData);
            // delete(fileName);
            return encodedStr;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // public void delete(String target) {
    //     try {
    //         Path path = Paths.get(target);
    //         Files.delete(path);
    //     } catch(IOException e) {
    //         e.printStackTrace();
    //     }
    // }
}
