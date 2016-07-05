package com.nobu_games.audio.stream.pcm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.nobu_games.audio.AudioStreamDescriptor;
import com.nobu_games.audio.source.AudioDecodingException;
import com.nobu_games.audio.source.AudioSource;
import com.nobu_games.audio.source.ValidationHelper;

public class PCMAudioSource implements AudioSource {
    private final byte[] mBuffer;
    private final ByteBuffer mByteBuffer;
    private final AudioStreamDescriptor mDescriptor;
    private final InputStream mInput;

    public PCMAudioSource(InputStream data, ByteOrder order,
            AudioStreamDescriptor descriptor) throws AudioDecodingException {
        int flags = descriptor.getFlags();

        if ((flags & AudioStreamDescriptor.FLAG_VARIABLE_BITS_PER_SAMPLE) != 0
                || (flags & AudioStreamDescriptor.FLAG_VARIABLE_SAMPLE_RATE) != 0) {
            throw new IllegalArgumentException(
                    "Linear PCM audio stream cannot have variable bit rate or variable sample rate");
        }

        ValidationHelper.validateIntegrity(descriptor);
        short bitsPerSample = descriptor.getMaximumBitsPerSample(0);

        if (bitsPerSample != 8 && bitsPerSample != 16 && bitsPerSample != 32
                && bitsPerSample != 64) {
            throw new IllegalArgumentException("Unsupported bit rate "
                    + bitsPerSample);
        }

        mInput = data;
        mDescriptor = descriptor;
        mBuffer = new byte[(bitsPerSample / 8) * descriptor.getChannelCount()];
        mByteBuffer = ByteBuffer.wrap(mBuffer);
        mByteBuffer.order(order);
    }

    private static double normalizeByte(byte value) {
        if (value < 0) {
            return (double) -value / (double) Byte.MIN_VALUE;
        } else {
            return (double) value / (double) Byte.MAX_VALUE;
        }
    }

    private static double normalizeInt(int value) {
        if (value < 0) {
            return (double) -value / (double) Integer.MIN_VALUE;
        } else {
            return (double) value / (double) Integer.MAX_VALUE;
        }
    }

    private static double normalizeLong(long value) {
        if (value < 0) {
            return (double) -value / (double) Long.MIN_VALUE;
        } else {
            return (double) value / (double) Long.MAX_VALUE;
        }
    }

    private static double normalizeShort(short value) {
        if (value < 0) {
            return (double) -value / (double) Short.MIN_VALUE;
        } else {
            return (double) value / (double) Short.MAX_VALUE;
        }
    }

    @Override
    public void close() throws IOException {
        mInput.close();
    }

    @Override
    public short getCurrentBitRate(int channel) {
        return mDescriptor.getMaximumBitsPerSample(0);
    }

    @Override
    public int getCurrentSampleRate(int channel) {
        return mDescriptor.getMaximumSampleRate(0);
    }

    @Override
    public AudioStreamDescriptor getDescriptor() {
        return mDescriptor;
    }

    @Override
    public double getSample(int channel) {
        switch (mDescriptor.getMaximumBitsPerSample(0)) {
            case 8:
                return normalizeByte(mByteBuffer.get(channel));

            case 16:
                return normalizeShort(mByteBuffer.getShort(channel * 2));

            case 32:
                return normalizeInt(mByteBuffer.getInt(channel * 4));

            case 64:
                return normalizeLong(mByteBuffer.getLong(channel * 8));

            default:
                throw new IllegalStateException("Unexpected bits per sample "
                        + mDescriptor.getMaximumBitsPerSample(0));
        }
    }

    @Override
    public boolean next() throws IOException {
        int result = mInput.read(mBuffer, 0, mBuffer.length);
        if (result != mBuffer.length)
            return false;

        return true;
    }
}
