package org.infinitest.keys;

import static org.apache.commons.codec.binary.Base64.*;
import static org.apache.commons.lang.StringUtils.*;
import static org.infinitest.keys.BlockCodec.*;
import static org.infinitest.keys.LicenseUtils.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class LicenseKeyDecrypter
{
    private byte[] productSideKey;

    public LicenseKeyDecrypter()
    {
        try
        {
            productSideKey = getKeyBytes("product.key");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public InputStream decrypt(String licenseKey)
    {
        String rawKeyString = stripWrapper(licenseKey);
        Cipher cipher = createRSACipher();
        byte[] encryptedBytes = decodeBase64(rawKeyString.getBytes());
        return decryptBytes(cipher, encryptedBytes);
    }

    private InputStream decryptBytes(Cipher cipher, byte[] encryptedBytes)
    {
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BlockCodec decoder = decoder(encryptedBytes);
            for (byte[] block : decoder.getBlocks())
                out.write(cipher.doFinal(block));
            out.close();

            return new ByteArrayInputStream(out.toByteArray());
        }
        catch (IllegalBlockSizeException e)
        {
            throw new LicenseKeyException(e);
        }
        catch (BadPaddingException e)
        {
            throw new LicenseKeyException(e);
        }
        catch (IOException e)
        {
            throw new LicenseKeyException(e);
        }
    }

    private Cipher createRSACipher()
    {
        try
        {

            Cipher cipher = Cipher.getInstance("RSA");
            // CHECKSTYLE:OFF
            // Need this to create a key from a byte stream. Don't know another way to do it
            RSAPrivateKey key = sun.security.rsa.RSAPrivateCrtKeyImpl.newKey(productSideKey);
            // CHECKSTYLE:ON
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher;
        }
        catch (InvalidKeyException e)
        {
            throw new RuntimeException("Please re-install Infinitest", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        catch (NoSuchPaddingException e)
        {
            throw new RuntimeException("Please re-install Infinitest", e);
        }
    }

    public static void main(String[] args) throws IOException
    {
        LicenseKeyDecrypter decrypter = new LicenseKeyDecrypter();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String key = "";
        for (String line = reader.readLine(); !isBlank(line);)
        {
            key += line;
            line = reader.readLine();
        }
        Properties properties = new Properties();
        properties.load(decrypter.decrypt(key));
        License license = new License();
        System.out.println(properties);

        license.setKey(key);
    }
}
