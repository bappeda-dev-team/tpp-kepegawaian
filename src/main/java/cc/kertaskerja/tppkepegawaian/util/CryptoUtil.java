package cc.kertaskerja.tppkepegawaian.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtil {

    private static final String PREFIX = "{ENC}:";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private static final String SECRET = System.getenv()
            .getOrDefault("APP_SECRET_KEY", "default-secret-key-change-me");

    private static final SecretKeySpec KEY =
            new SecretKeySpec(padKey().getBytes(), ALGORITHM);

    public static String encrypt(String data) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, KEY, new GCMParameterSpec(TAG_LENGTH, iv));

            byte[] encrypted = cipher.doFinal(data.getBytes());

            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return PREFIX + Base64.getEncoder().encodeToString(combined);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            if (!isEncrypted(encryptedData)) {
                return encryptedData;
            }

            String value = encryptedData.substring(PREFIX.length());
            byte[] decoded = Base64.getDecoder().decode(value);

            byte[] iv = new byte[IV_LENGTH];
            byte[] ciphertext = new byte[decoded.length - IV_LENGTH];

            System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);
            System.arraycopy(decoded, IV_LENGTH, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, KEY, new GCMParameterSpec(TAG_LENGTH, iv));

            return new String(cipher.doFinal(ciphertext));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isEncrypted(String data) {
        return data != null && data.startsWith(PREFIX);
    }

    private static String padKey() {
        if (CryptoUtil.SECRET.length() >= 32) {
            return CryptoUtil.SECRET.substring(0, 32);
        }
        return String.format("%-32s", CryptoUtil.SECRET).replace(' ', '0');
    }
}
