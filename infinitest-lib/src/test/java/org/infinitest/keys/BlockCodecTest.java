package org.infinitest.keys;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class BlockCodecTest
{
    @Test
    public void shouldEncodeASingleBlock()
    {
        BlockCodec encoder = BlockCodec.encoder();
        byte[] data = new byte[] { 1, 2, 3, 4 };
        encoder.addBlock(data);

        BlockCodec decoder = BlockCodec.decoder(encoder.getEncodedBytes());
        List<byte[]> blocks = decoder.getBlocks();
        assertEquals(1, blocks.size());
        assertArrayEquals(data, blocks.get(0));
    }

    @Test
    public void shouldEncodeTwoBlocks()
    {
        BlockCodec encoder = BlockCodec.encoder();
        byte[] data1 = new byte[] { 1, 2, 3, 4 };
        byte[] data2 = new byte[] { 5, 6, 7, 8 };
        encoder.addBlock(data1);
        encoder.addBlock(data2);

        BlockCodec decoder = BlockCodec.decoder(encoder.getEncodedBytes());
        List<byte[]> blocks = decoder.getBlocks();
        assertEquals(2, blocks.size());
        assertArrayEquals(data1, blocks.get(0));
        assertArrayEquals(data2, blocks.get(1));
    }
}
