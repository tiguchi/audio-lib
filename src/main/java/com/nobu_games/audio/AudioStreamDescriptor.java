package com.nobu_games.audio;

public interface AudioStreamDescriptor {
    public static final int FLAG_VARIABLE_BITS_PER_SAMPLE = 1;
    public static final int FLAG_VARIABLE_SAMPLE_RATE = 2;

    /**
     * Gets the amount of audio channels available in this source.
     * 
     * @return Amount of channels (1=mono, 2=stereo...)
     */
    int getChannelCount();

    int getFlags();
    
    short getMaximumBitsPerSample(int channel);

    int getMaximumSampleRate(int channel);

    short getMinimumBitsPerSample(int channel);

    int getMinimumSampleRate(int channel);

    /**
     * Gets the total amount of samples in this stream if known.
     * 
     * @return Total sample count in stream or <code>-1</code> if unknown.
     */
    int getSampleCount();
}
