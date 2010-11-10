package org.infinitest.keys;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class KeyGenerator
{
    public static void main(String[] args) throws Exception
    {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        KeyPair keyPair = generator.generateKeyPair();

        // No, this isn't backwards. We want to encrypt keys, not sign them.
        writeKey("secret.key", keyPair.getPublic());
        writeKey("product.key", keyPair.getPrivate());
        System.out.println("Generated keys");
    }

    private static void writeKey(String filename, Key key) throws FileNotFoundException, IOException
    {
        FileOutputStream outputStream = new FileOutputStream(filename);
        outputStream.write(key.getEncoded());
        outputStream.close();
    }
}
