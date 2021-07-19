package com.akgarg.fileencryptiondecryption.controller;

import com.akgarg.fileencryptiondecryption.core.FileEncryptorAndDecryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@SuppressWarnings({"DuplicatedCode", "ResultOfMethodCallIgnored"})
@CrossOrigin(origins = "*")
@Controller
public class EncryptController {

    private final FileEncryptorAndDecryptor fileEncryptorAndDecryptor;

    @Autowired
    public EncryptController(FileEncryptorAndDecryptor fileEncryptorAndDecryptor) {
        this.fileEncryptorAndDecryptor = fileEncryptorAndDecryptor;
    }


    @RequestMapping(value = "/encrypt", method = RequestMethod.POST)
    public Object encrypt(@RequestParam("enc-file") MultipartFile ofile,
                          @RequestParam("eop") String key,
                          HttpServletResponse response) throws IOException {
        if (ofile.isEmpty()) {
            return ResponseEntity.ok("Please upload file to encrypt");
        }

        String originalFileName = ofile.getOriginalFilename() == null ? "" : ofile.getOriginalFilename();
        if (originalFileName.length() < 3) {
            return ResponseEntity.ok("Please upload valid file to encrypt<br><br><a href='/'>Go to home</a>");
        }

        if (originalFileName.endsWith(".enc")) {
            return ResponseEntity.ok("File is already encrypted<br><br><a href='/'>Go to home</a>");
        }

        if (key == null || key.trim().equals("")) {
            return ResponseEntity.ok("Please enter encryption key<br><br><a href='/'>Go to home</a>");
        }

        File file = convertToFile(ofile, ofile.getOriginalFilename());
        File encryptedFile = fileEncryptorAndDecryptor.encrypt(file, key);

        if (encryptedFile == null) {
            return ResponseEntity.ok("Error encrypting file<br><br><a href='/'>Go to home</a>");
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + ".enc\"");
        response.setContentLength((int) encryptedFile.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(encryptedFile));
        FileCopyUtils.copy(inputStream, response.getOutputStream());

        try {
            file.delete();
            encryptedFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }


    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }

        return tempFile;
    }
}
