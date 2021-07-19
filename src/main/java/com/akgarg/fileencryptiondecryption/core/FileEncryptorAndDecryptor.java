package com.akgarg.fileencryptiondecryption.core;

import com.akgarg.fileencryptiondecryption.model.ResponseMessage;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@SuppressWarnings({"DuplicatedCode", "unused", "ResultOfMethodCallIgnored", "ConstantConditions"})
public class FileEncryptorAndDecryptor {
    private File destinationFile;

    boolean areHashesEqual(File file, String keyHash) throws IOException {
        BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
        //reading key hash from file
        StringBuilder keyHashFromFile = new StringBuilder(128);

        for (int i = 0; i < 128; i++) {
            keyHashFromFile.append((char) fileReader.read());
        }

        fileReader.close();
        return keyHashFromFile.toString().equals(keyHash);
    }

    private byte[] getHashInBytes(String key) throws NoSuchAlgorithmException {
        byte[] keyHash;
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        keyHash = md.digest(key.getBytes());
        StringBuilder sb = new StringBuilder();

        for (byte hash : keyHash) {
            sb.append(Integer.toString((hash & 0xff) + 0x100, 16).substring(1));
        }

        String hashOfPassword = sb.toString();

        return hashOfPassword.getBytes();
    }

    private String getHashInString(String key) throws NoSuchAlgorithmException {
        byte[] keyHash;
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        keyHash = md.digest(key.getBytes());
        StringBuilder sb = new StringBuilder();

        for (byte hash : keyHash) {
            sb.append(Integer.toString((hash & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }


    public File encrypt(File file, String key) {
        byte[] keyHash;
        boolean isExceptionOccurred = false;

        if (!file.isDirectory()) {
            try {
                keyHash = getHashInBytes(key);

                destinationFile = new File(file.getAbsolutePath().concat(".enc"));
                if (destinationFile.exists()) {
                    destinationFile.delete();
                    destinationFile = new File(file.getAbsolutePath().concat(".enc"));
                }

                BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
                FileOutputStream fileWriter = new FileOutputStream(destinationFile, true);

                //writing key hash to file
                fileWriter.write(keyHash, 0, 128);

                //encrypting content & writing
                byte[] buffer = new byte[262144];
                int bufferSize = buffer.length;
                int keySize = key.length();

                while (fileReader.available() > 0) {
                    int bytesCopied = fileReader.read(buffer);
                    for (int i = 0, keyCounter = 0; i < bufferSize; i++, keyCounter %= keySize) {
                        buffer[i] += key.toCharArray()[keyCounter];
                    }

                    fileWriter.write(buffer, 0, bytesCopied);
                    long fileLength = file.length();
                }

                fileReader.close();
                fileWriter.close();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                isExceptionOccurred = true;
                Logger.getLogger(FileEncryptorAndDecryptor.class.getName()).log(Level.SEVERE, null, e);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.getLogger(FileEncryptorAndDecryptor.class.getName()).log(Level.SEVERE, null, e);
                isExceptionOccurred = true;
            }
        }

        return isExceptionOccurred ? null : destinationFile;
    }


    public Object decrypt(File file, String key) {
        String keyHash;
        boolean isExceptionOccurred = false;

        if (!file.isDirectory()) {
            try {
                keyHash = getHashInString(key);

                if (areHashesEqual(file, keyHash)) {
                    destinationFile = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4));

                    BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
                    FileOutputStream fileWriter = new FileOutputStream(destinationFile);

                    //decrypting content & writing
                    byte[] buffer = new byte[262144];
                    int bufferSize = buffer.length;
                    int keySize = key.length();

                    for (int i = 0; i < 128; i++) {
                        if (fileReader.available() > 0) {
                            fileReader.read();
                        }
                    }
                    while (fileReader.available() > 0) {
                        int bytesCopied = fileReader.read(buffer);
                        for (int i = 0, keyCounter = 0; i < bufferSize; i++, keyCounter %= keySize) {
                            buffer[i] -= key.toCharArray()[keyCounter];
                        }

                        fileWriter.write(buffer, 0, bytesCopied);
                    }

                    fileReader.close();
                    fileWriter.close();
                } else if (!areHashesEqual(file, keyHash)) {
                    System.out.println("File password and input password mismatch");
                    return new ResponseMessage("Password entered is invalid");
                }
            } catch (NoSuchAlgorithmException e) {
                isExceptionOccurred = true;
                Logger.getLogger(FileEncryptorAndDecryptor.class.getName()).log(Level.SEVERE, null, e);
            } catch (Exception e) {
                isExceptionOccurred = true;
            }
        }

        return isExceptionOccurred ? null : destinationFile;
    }
}