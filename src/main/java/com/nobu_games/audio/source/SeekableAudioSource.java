package com.nobu_games.audio.source;

import java.io.IOException;

/**
 * Audio source that is seekable, meaning that the audio stream position can be
 * arbitrarily changed.
 * 
 * @author ti
 */
public interface SeekableAudioSource extends AudioSource {
    /**
     * Rewinds to the beginning of the stream.
     * 
     * @throws IOException
     */
    void rewind() throws IOException;

    /**
     * Seeks the audio stream to the specified sample position.
     * 
     * @param position
     *            Position index in samples.
     * @throws IOException
     */
    void seekPosition(int position) throws IOException;

    /**
     * Skips an amount of samples forward through the stream.
     * 
     * @param samples
     *            Amount of samples to skip.
     * @throws IOException
     */
    void skip(int samples) throws IOException;
}
