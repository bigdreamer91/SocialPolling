package edu.iastate.cs510.company2.utility;

import java.security.Key;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * This class generates salt and encrypts the password for security purpose
 *
 * Created by Dhaval on 10/25/2016.
 */

public class Password {


    public static String generateSalt(){
        byte[] salt = new byte[16];
        new Random().nextBytes(salt);
        return new String(salt);
     }

    /**
     * Uses AES encrpyption to encrpt the salted password
     *
     * @param pass
     * @param salt
     * @return
     */
    public static String encryptPassword(String pass, String salt) throws Exception{

            String saltedPassword = pass + salt;
            final String symmetricKey = "A1b2C3d4E5f6LmOp";  //key (16 bytes)
            Key aesKey = new SecretKeySpec(symmetricKey.getBytes(), "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, aesKey);

            return  new String(c.doFinal(saltedPassword.getBytes()));

    }
}
