package com.mx.vise.uhf.util;

import android.util.Base64;

import java.math.BigDecimal;
import java.security.Key;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * Clase que se encarga de encriptar y desencriptar informacion por el metodo
 * AES
 *
 * @author ernestochavez
 */
public class AESencrp {

    private static final String ALGO = "AES";
    private static final byte[] keyValue = new byte[]{'V', 'I', 'S', 'E', 'T', 'I', 'S', 'c', 'r', 'e', 't',
            'K', 'e', 'y', '1', '8'};

    /**
     * Metodo que se encarga de encriptar el dato que esta en string por un string
     * encriptado
     *
     * @param data es la informacion que se quiere encriptar
     * @return un string encriptado con la informacion enviada
     * @throws Exception si el valor no se pudo encriptar
     */
    public static String encrypt(String data) throws Exception {
        if (data != null) {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(data.getBytes());
            String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
            return encryptedValue;
        }
        return null;
    }

    /**
     * Para desencriptar un string
     *
     * @param encryptedData es la informacion ya encriptada por el sistema AES
     * @return un string ya desencriptado
     * @throws Exception si el valor no fue posible desencriptar
     */
    public static String decrypt(String encryptedData) throws Exception {
        if (encryptedData != null) {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.decode(encryptedData, Base64.DEFAULT);
            byte[] decValue = c.doFinal(decordedValue);
            String decryptedValue = new String(decValue);
            return decryptedValue;
        }
        return null;
    }

    /**
     * Generador de llave
     *
     * @return la llave que encripta y desencripta
     * @throws Exception Si no es posible crear la llave
     */
    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

    /**
     * Para encriptar un valor entero tener en cuenta que lo pasa a un string
     *
     * @param integer valor en tipo Integer
     * @return regresa un string con el valor encriptado
     * @throws Exception si el valor no se pudo encriptar
     */
    public static String encrypt(Integer integer) throws Exception {
        if (integer != null) {
            return encrypt(integer.toString());
        }
        return null;
    }

    /**
     * Para poder desencriptar el valor de string a integer
     *
     * @param integer el entero en string y encriptado
     * @return un entero
     * @throws Exception si no se puede desencriptar o pasar a entero
     */
    public static Integer decryptInteger(String integer) throws Exception {
        if (integer != null) {
            return Integer.parseInt(decrypt(integer));
        }
        return null;
    }

    /**
     * Para encriptar un valor BigDecimal tener en cuenta que lo pasa a un string
     *
     * @param bigDecimal un atributo bigdecimal
     * @return un string con el bigdecimal encriptado
     * @throws Exception si no fue posible encriptar el valor
     */
    public static String encrypt(BigDecimal bigDecimal) throws Exception {
        if (bigDecimal != null) {
            return encrypt(bigDecimal.toString());
        }
        return null;
    }



    /**
     * Desencripta una lista de bytes
     *
     * @param bytes es el string que tiene los bytes encriptados
     * @return un arreglo de bytes que estaban encriptados
     * @throws Exception
     */
    @SuppressWarnings("restriction")
    public static byte[] decryptBytes(String bytes) throws Exception {
        if (bytes != null) {
            return Base64.decode(decrypt(bytes), Base64.DEFAULT);
        }
        return null;
    }

    /**
     * Encriptar un arreglo de bytes
     *
     * @param bytes son los bytes que se quieren encriptar
     * @return un string con los bystes encriptados y que primero se pasan a base64
     * para poder encriptar despues
     * @throws Exception si no puede encriptar la informacion
     */
    @SuppressWarnings("restriction")
    public static String encrypt(byte[] bytes) throws Exception {
        if (bytes != null) {
            String encryptedValue = Base64.encodeToString(bytes, Base64.DEFAULT);
            return encrypt(encryptedValue);
        }
        return null;
    }

    /**
     * para desencriptar un bigdecimal
     *
     * @param bigDecimal encriptado
     * @return un bigdecimal con el valor encriptado
     * @throws Exception si no fue posible encriptar los valores
     */
    public static BigDecimal decryptBigdecimal(String bigDecimal) throws Exception {
        if (bigDecimal != null) {
            return new BigDecimal(decrypt(bigDecimal));
        }
        return null;
    }

}
