package com.nobu_games.audio.source;

import java.io.IOException;

import com.nobu_games.audio.AudioStreamDescriptor;

/**
 * Audio source that mixes down multiple channels to a single one on the fly.
 * 
 * @author ti
 */
public class MonoDownmixAudioSource implements AudioSource {
    private AudioStreamDescriptor mDescriptor;
    private final AudioSource mSource;

    /**
     * Creates a new mono downmix audio source for the specified audio source.
     * 
     * @param source
     *            Audio source to downmix.
     */
    public MonoDownmixAudioSource(AudioSource source) {
        mSource = source;
        final AudioStreamDescriptor descr = source.getDescriptor();
        mDescriptor = new ProxyAudioStreamDescriptor(descr) {
            @Override
            public int getChannelCount() {
                return 1;
            }
        };
    }

    @Override
    public void close() throws IOException {
        mSource.close();
    }

    @Override
    public short getCurrentBitRate(int channel) {
        return mSource.getCurrentBitRate(channel);
    }

    @Override
    public int getCurrentSampleRate(int channel) {
        return mSource.getCurrentSampleRate(channel);
    }

    @Override
    public AudioStreamDescriptor getDescriptor() {
        return mDescriptor;
    }

    @Override
    public double getSample(int channel) {
        final int sourceChannelCount = mSource.getDescriptor()
                .getChannelCount();
        if (sourceChannelCount == 1)
            return mSource.getSample(channel);
        double sample = 0;

        for (int sourceChannel = 0; sourceChannel < sourceChannelCount; ++sourceChannel) {
            sample += mSource.getSample(sourceChannel);
        }

        sample /= sourceChannelCount;

        return sample;
    }

    @Override
    public boolean next() throws IOException {
        return mSource.next();
    }
}
