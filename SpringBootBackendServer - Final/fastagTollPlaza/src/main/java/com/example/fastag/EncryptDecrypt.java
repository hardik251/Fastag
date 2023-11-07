package com.example.fastag;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class EncryptDecrypt{
    // These are kept private as they are highly sensitive information
    // They are kept final as they are not supposed to be changed
    // These are used to encrypt and decrypt the password
    // static is used so that they can be accessed in the method without creating an object of the class
    private static final String ALGORITHM = "AES";
    private static final String keyT = "HkZGE9WPDeX2Tr5Q";
    private static final String keyV = "HqZPE3WPDeXrTr5s";

    // this encrypt method is used to encrypt the password entered by the user
    // the password entered by the user is encrypted using the AES algorithm
    // the encrypted password is returned by the encrypt method
    // the encrypted password is stored in the database
    // the encrypted password is compared with the password stored in the database
    // if the encrypted password matches with the password stored in the database then the password entered by the user is correct
    // so the user will be allowed to login

    public static byte[] encrypt(String input) throws Exception {
    try {
        SecretKeySpec keySpec = new SecretKeySpec(keyT.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(input.getBytes());
    } catch (Exception e) {
        // Handle exceptions here or rethrow them
        throw new Exception("Encryption failed: " + e.getMessage(), e);
    }
}

    public static String decrypt(byte[] encrypted) throws Exception 
    {
        SecretKeySpec keySpec = new SecretKeySpec(keyV.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedBytes = cipher.doFinal(encrypted);
        return new String(decryptedBytes);
    }
}
