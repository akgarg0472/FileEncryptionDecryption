package com.akgarg.fileencryptiondecryption.controller;

import com.akgarg.fileencryptiondecryption.core.FileEncryptorAndDecryptor;
import com.akgarg.fileencryptiondecryption.model.ResponseMessage;
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
public class DecryptController {

    private final FileEncryptorAndDecryptor fileEncryptorAndDecryptor;

    @Autowired
    public DecryptController(FileEncryptorAndDecryptor fileEncryptorAndDecryptor) {
        this.fileEncryptorAndDecryptor = fileEncryptorAndDecryptor;
    }


    @RequestMapping(value = "/decrypt", method = RequestMethod.POST)
    public Object decrypt(@RequestParam("dec-file") MultipartFile ofile,
                          @RequestParam("dop") String key,
                          HttpServletResponse response) throws IOException {
        if (ofile.isEmpty()) {
            return ResponseEntity.ok("Please upload file to decrypt<br><br><a href='/'>Go to home</a>");
        }

        String originalFileName = ofile.getOriginalFilename() == null ? "" : ofile.getOriginalFilename();
        if (originalFileName.length() < 3) {
            return ResponseEntity.ok("Please upload valid file<br><br><a href='/'>Go to home</a>");
        }

        if (!originalFileName.endsWith(".enc")) {
            return ResponseEntity.ok("File is already decrypted<br><br><a href='/'>Go to home</a>");
        }

        File file = convertToFile(ofile, ofile.getOriginalFilename());
        Object decryptionResponse = fileEncryptorAndDecryptor.decrypt(file, key);

        if (decryptionResponse instanceof ResponseMessage) {
            return ResponseEntity.ok("Password mismatched. Please enter valid password to decrypt file<br><br><a href='/'>Go to home</a>");
        }

        File decryptedFile = (File) decryptionResponse;

        if (decryptedFile == null) {
            return ResponseEntity.ok("Error decrypting file. Please try again later<br><br><a href='/'>Go to home</a>");
        }

        String decryptedFileName = file.getName();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "inline; filename=\"" + decryptedFileName.substring(0, decryptedFileName.length() - 4) + "\"");
        response.setContentLength((int) decryptedFile.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(decryptedFile));
        FileCopyUtils.copy(inputStream, response.getOutputStream());

        try {
            file.delete();
            decryptedFile.delete();
        } catch (Exception e) {
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
