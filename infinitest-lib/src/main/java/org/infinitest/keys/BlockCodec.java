package org.infinitest.keys;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encodes/Decodes a list of byte array blocks into/from a single byte array
 * 
 * @author <a href="mailto:benrady@gmail.com">Ben Rady</a>
 */
public class BlockCodec
{
    List<byte[]> blocks;

    public static BlockCodec encoder()
    {
        return new BlockCodec();
    }

    public static BlockCodec decoder(byte[] data)
    {
        return new BlockCodec(data);
    }

    private BlockCodec()
    {
        blocks = new ArrayList<byte[]>();
    }

    private BlockCodec(byte[] data)
    {
        this();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        int numBlocks = in.read();

        List<Integer> blockSizes = new ArrayList<Integer>();
        for (int i = 0; i < numBlocks; i++)
        {
            blockSizes.add(in.read());
        }

        for (Integer blockSize : blockSizes)
        {
            byte[] buffer = new byte[blockSize];
            in.read(buffer, 0, blockSize);
            blocks.add(buffer);
        }
    }

    public void addBlock(byte[] data)
    {
        blocks.add(data);
    }

    public List<byte[]> getBlocks()
    {
        return blocks;
    }

    public byte[] getEncodedBytes()
    {
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(blocks.size());
            for (byte[] block : blocks)
            {
                out.write(block.length);
            }
            for (byte[] block : blocks)
            {
                out.write(block);
            }
            out.close();
            return out.toByteArray();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
