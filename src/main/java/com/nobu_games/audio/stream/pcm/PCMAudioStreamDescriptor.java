package com.nobu_games.audio.stream.pcm;

import com.nobu_games.audio.AudioStreamDescriptor;

public class PCMAudioStreamDescriptor implements AudioStreamDescriptor {
    private final short mBitsPerSample;
    private final int mChannels;
    private final int mFlags;
    private final int mSampleCount;
    private final int mSampleRate;

    public PCMAudioStreamDescriptor(int channels, short bitsPerSample,
            int sampleRate, int sampleCount, int flags) {
        mChannels = channels;
        mBitsPerSample = bitsPerSample;
        mSampleRate = sampleRate;
        mSampleCount = sampleCount;
        mFlags = flags;
    }

    @Override
    public int getChannelCount() {
        return mChannels;
    }

    @Override
    public int getFlags() {
        return mFlags;
    }

    @Override
    public short getMaximumBitsPerSample(int channel) {
        return mBitsPerSample;
    }

    @Override
    public int getMaximumSampleRate(int channel) {
        return mSampleRate;
    }

    @Override
    public short getMinimumBitsPerSample(int channel) {
        return mBitsPerSample;
    }

    @Override
    public int getMinimumSampleRate(int channel) {
        return mSampleRate;
    }

    @Override
    public int getSampleCount() {
        return mSampleCount;
    }
}
