package com.nobu_games.audio.source;

import com.nobu_games.audio.AudioStreamDescriptor;

/**
 * Audio stream descriptor that passes on the information provided by another
 * audio stream descriptor.
 * <p>
 * This class is useful as a parent class if you want to override individual
 * values returned by an audio stream descriptor object.
 * 
 * @author ti
 */
public class ProxyAudioStreamDescriptor implements AudioStreamDescriptor {
    private final AudioStreamDescriptor mDescriptor;

    public ProxyAudioStreamDescriptor(AudioStreamDescriptor descriptor) {
        mDescriptor = descriptor;
    }

    @Override
    public int getChannelCount() {
        return mDescriptor.getChannelCount();
    }

    @Override
    public int getFlags() {
        return mDescriptor.getFlags();
    }

    @Override
    public short getMaximumBitsPerSample(int channel) {
        return mDescriptor.getMaximumBitsPerSample(channel);
    }

    @Override
    public int getMaximumSampleRate(int channel) {
        return mDescriptor.getMaximumSampleRate(channel);
    }

    @Override
    public short getMinimumBitsPerSample(int channel) {
        return mDescriptor.getMinimumBitsPerSample(channel);
    }

    @Override
    public int getMinimumSampleRate(int channel) {
        return mDescriptor.getMinimumSampleRate(channel);
    }

    @Override
    public int getSampleCount() {
        return mDescriptor.getSampleCount();
    }
}
