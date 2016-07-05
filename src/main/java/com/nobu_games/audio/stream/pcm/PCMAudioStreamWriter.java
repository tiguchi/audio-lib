package com.nobu_games.audio.stream.pcm;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.nobu_games.audio.AudioStreamDescriptor;
import com.nobu_games.audio.sink.AudioEncodingException;
import com.nobu_games.audio.source.AudioDecodingException;
import com.nobu_games.audio.source.AudioSource;
import com.nobu_games.audio.stream.AudioStreamWriter;

/**
 * Component for encoding an {@link AudioSource} and writing it as uncompressed
 * linear PCM audio data into an output stream.
 * 
 * @author ti
 */
public class PCMAudioStreamWriter implements AudioStreamWriter {
    private final byte[] mByteArray;
    private final ByteBuffer mByteBuffer;

    /**
     * Creates a new PCM audio stream writer for the specified byte order.
     * 
     * @param endianness
     *            Byte order for the audio encoding (typically it is little
     *            endian).
     */
    public PCMAudioStreamWriter(ByteOrder endianness) {
        mByteArray = new byte[8];
        mByteBuffer = ByteBuffer.wrap(mByteArray);
        mByteBuffer.order(endianness);
    }

    private static byte toByte(double value) {
        if (value < 0) {
            return (byte) Math.round(-value * Byte.MIN_VALUE);
        } else {
            return (byte) Math.round(value * Byte.MAX_VALUE);
        }
    }

    private static int toInt(double value) {
        if (value < 0) {
            return (int) Math.round(-value * Integer.MIN_VALUE);
        } else {
            return (int) Math.round(value * Integer.MAX_VALUE);
        }
    }

    private static long toLong(double value) {
        if (value < 0) {
            return (long) Math.round(-value * Long.MIN_VALUE);
        } else {
            return (long) Math.round(value * Long.MAX_VALUE);
        }
    }

    private static short toShort(double value) {
        if (value < 0) {
            return (short) Math.round(-value * Short.MIN_VALUE);
        } else {
            return (short) Math.round(value * Short.MAX_VALUE);
        }
    }

    @Override
    public void write(AudioSource source, OutputStream target)
            throws IOException, AudioDecodingException, AudioEncodingException {
        int flags = source.getDescriptor().getFlags();

        if ((flags & AudioStreamDescriptor.FLAG_VARIABLE_SAMPLE_RATE) != 0) {
            throw new IllegalArgumentException(
                    "Cannot write audio source with variable sample rate.");
        }

        final AudioStreamDescriptor descriptor = source.getDescriptor();
        int channelCount = descriptor.getChannelCount();
        short bitsPerSample = descriptor.getMaximumBitsPerSample(descriptor
                .getMaximumBitsPerSample(0));

        switch (bitsPerSample) {
            case 8:
                write8Bit(source, target, channelCount);
                break;

            case 16:
                write16Bit(source, target, channelCount);
                break;

            case 32:
                write32Bit(source, target, channelCount);
                break;

            case 64:
                write64Bit(source, target, channelCount);
                break;

            default:
                throw new AudioEncodingException(
                        "Cannot write unsupported sample bit depth "
                                + bitsPerSample);
        }
    }

    private void write16Bit(AudioSource source, OutputStream target,
            int channelCount) throws IOException {
        while (source.next()) {
            for (int channel = 0; channel < channelCount; ++channel) {
                double sample = source.getSample(channel);
                short intSample = toShort(sample);
                mByteBuffer.rewind();
                mByteBuffer.putShort(intSample);
                target.write(mByteArray, 0, 2);
            }
        }
    }

    private void write32Bit(AudioSource source, OutputStream target,
            int channelCount) throws IOException {
        while (source.next()) {
            for (int channel = 0; channel < channelCount; ++channel) {
                double sample = source.getSample(channel);
                int intSample = toInt(sample);
                mByteBuffer.rewind();
                mByteBuffer.putInt(intSample);
                target.write(mByteArray, 0, 4);
            }
        }
    }

    private void write64Bit(AudioSource source, OutputStream target,
            int channelCount) throws IOException {
        while (source.next()) {
            for (int channel = 0; channel < channelCount; ++channel) {
                double sample = source.getSample(channel);
                long intSample = toLong(sample);
                mByteBuffer.rewind();
                mByteBuffer.putLong(intSample);
                target.write(mByteArray, 0, 8);
            }
        }
    }

    private void write8Bit(AudioSource source, OutputStream target,
            int channelCount) throws IOException {
        while (source.next()) {
            for (int channel = 0; channel < channelCount; ++channel) {
                double sample = source.getSample(channel);
                byte intSample = toByte(sample);
                target.write(intSample);
            }
        }
    }
}
