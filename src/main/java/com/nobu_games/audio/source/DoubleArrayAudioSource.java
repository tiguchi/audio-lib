package com.nobu_games.audio.source;

import java.io.IOException;

import com.nobu_games.audio.AudioStreamDescriptor;

/**
 * Audio source backed by an array of double precision floating point values
 * that serve as sample data.
 * 
 * @author ti
 */
public class DoubleArrayAudioSource implements SeekableAudioSource {
    private final double[] mData;
    private int mDataIndex = -1;
    private final AudioStreamDescriptor mDescriptor;

    /**
     * Creates a double array backed audio source.
     * 
     * @param descriptor
     *            Audio stream descriptor for this audio source.
     * @param data
     *            Normalized sample data. If audio source has multiple channels
     *            the samples must be interleaved like a multi-channel PCM audio
     *            stream.
     */
    public DoubleArrayAudioSource(AudioStreamDescriptor descriptor,
            double... data) {
        mDescriptor = descriptor;
        mData = data;
    }

    /**
     * No-op.
     */
    @Override
    public void close() throws IOException {
    }

    @Override
    public short getCurrentBitRate(int channel) {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    @Override
    public int getCurrentSampleRate(int channel) {
        return mDescriptor.getMaximumSampleRate(channel);
    }

    @Override
    public AudioStreamDescriptor getDescriptor() {
        return mDescriptor;
    }

    @Override
    public double getSample(int channel) {
        return mData[mDataIndex * mDescriptor.getChannelCount() + channel];
    }

    @Override
    public boolean next() throws IOException {
        return (++mDataIndex * mDescriptor.getChannelCount()) < mData.length;
    }

    @Override
    public void rewind() {
        mDataIndex = -1;
    }

    @Override
    public void seekPosition(int position) throws IOException {
        mDataIndex = position;
    }

    @Override
    public void skip(int samples) throws IOException {
        mDataIndex += samples;
    }
}
