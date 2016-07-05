package com.nobu_games.audio.format;

import java.io.Closeable;
import java.io.IOException;

import com.nobu_games.audio.sink.AudioEncodingException;
import com.nobu_games.audio.source.AudioDecodingException;
import com.nobu_games.audio.source.AudioSource;

/**
 * Audio container format writer.
 * 
 * @author ti
 */
public interface ContainerFormatWriter extends Closeable {
    /**
     * Writes an audio source into the container format file.
     * 
     * @param source
     *            Audio source that provides the sample stream to encode and
     *            write.
     * @throws IOException
     * @throws AudioDecodingException
     * @throws AudioEncodingException
     */
    void write(AudioSource source) throws IOException, AudioDecodingException,
            AudioEncodingException;
}
