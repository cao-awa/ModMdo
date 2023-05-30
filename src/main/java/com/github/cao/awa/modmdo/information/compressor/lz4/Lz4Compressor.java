package com.github.cao.awa.modmdo.information.compressor.lz4;

import com.github.cao.awa.modmdo.information.compressor.*;
import net.jpountz.lz4.*;

public class Lz4Compressor implements InformationCompressor {
    public static final Lz4Compressor FAST_INSTANCE = new Lz4Compressor(
            LZ4Factory.safeInstance(),
            true
    );
    public static final Lz4Compressor FASTEST_INSTANCE = new Lz4Compressor(
            LZ4Factory.fastestInstance(),
            true
    );
    public static final Lz4Compressor HIGH_INSTANCE = new Lz4Compressor(
            LZ4Factory.safeInstance(),
            false
    );
    public static final Lz4Compressor HIGH_FASTEST_INSTANCE = new Lz4Compressor(
            LZ4Factory.fastestInstance(),
            false
    );
    private final LZ4Factory factory;
    private final boolean fast;

    public Lz4Compressor(LZ4Factory factory, boolean fast) {
        this.factory = factory;
        this.fast = fast;
    }

    /**
     * Compress using lz4 with the fastest compression
     *
     * @param bytes
     *         Data source
     * @return Compress result
     */
    public byte[] compress(byte[] bytes) {
        return (fast ? factory.fastCompressor() : factory.highCompressor()).compress(bytes);
    }

    /**
     * Decompress using lz4
     *
     * @param bytes
     *         Data source
     * @return Decompress result
     */
    public byte[] decompress(byte[] bytes) {
        return factory.fastDecompressor()
                      .decompress(
                              bytes,
                              bytes.length
                      );
    }
}