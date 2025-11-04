package com.family.agent.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtil {

    private static final String AES_ALGO = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    private static final String KEY_FILE = "agent.key";

    public static class EncryptedData {
        public byte[] cipherText;
        public byte[] iv;
    }

    /** 🔹 Mã hóa chuỗi văn bản bằng AES/GCM với key đọc từ agent.key */
    public static EncryptedData encrypt(String plainText) throws Exception {
        byte[] keyBytes = getOrCreateAES(KEY_FILE);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_ALGO);
        GCMParameterSpec gcm = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcm);

        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        EncryptedData result = new EncryptedData();
        result.cipherText = cipherText;
        result.iv = iv;
        return result;
    }

    /** 🔹 Đọc hoặc tạo file agent.key (nếu chưa có) */
    private static byte[] getOrCreateAES(String fileName) throws IOException {
        File file = new File(fileName);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String base64 = br.readLine();
                return Base64.getDecoder().decode(base64.trim());
            }
        } else {
            byte[] key = new byte[16];
            new SecureRandom().nextBytes(key);
            String base64 = Base64.getEncoder().encodeToString(key);
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(base64);
            }
            return key;
        }
    }
}
