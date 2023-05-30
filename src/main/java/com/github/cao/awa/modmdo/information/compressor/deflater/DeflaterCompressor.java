package com.github.cao.awa.modmdo.information.compressor.deflater;

import com.github.cao.awa.modmdo.information.compressor.*;
import com.github.cao.awa.modmdo.utils.io.*;

import java.io.*;
import java.util.zip.*;

public class DeflaterCompressor implements InformationCompressor {
    public static final DeflaterCompressor BEST_INSTANCE = new DeflaterCompressor(Deflater.BEST_COMPRESSION);
    public static final DeflaterCompressor INSTANCE = new DeflaterCompressor(Deflater.DEFAULT_COMPRESSION);
    private final int level;

    public DeflaterCompressor(int level) {
        this.level = level;
    }

    /**
     * Compress using deflater with best compression
     *
     * @param bytes
     *         Data source
     * @return Compress result
     */
    public byte[] compress(byte[] bytes) {
        if (bytes.length == 0) {
            return EMPTY_BYTES;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IOUtil.write(
                    new DeflaterOutputStream(
                            out,
                            new Deflater(level)
                    ),
                    bytes
            );
            return out.toByteArray();
        } catch (Exception e) {
            return bytes;
        }
    }

    /**
     * Decompress using inflater
     *
     * @param bytes
     *         Data source
     * @return Decompress result
     */
    public byte[] decompress(byte[] bytes) {
        if (bytes.length == 0) {
            return EMPTY_BYTES;
        }
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            InflaterInputStream inflater = new InflaterInputStream(new ByteArrayInputStream(bytes));
            IOUtil.write(
                    result,
                    inflater
            );
            return result.toByteArray();
        } catch (Exception ex) {
            return bytes;
        }
    }
}
