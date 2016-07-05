package com.nobu_games.audio.stream;

import java.io.IOException;
import java.io.OutputStream;

import com.nobu_games.audio.sink.AudioEncodingException;
import com.nobu_games.audio.source.AudioDecodingException;
import com.nobu_games.audio.source.AudioSource;

/**
 * A component that can encode an audio source and write the result into an
 * output stream.
 * 
 * @author ti
 */
public interface AudioStreamWriter {
    /**
     * Writes the sample data of an audio source into the target output stream
     * using the audio encoding implemented by this writer.
     * 
     * @param source
     *            Audio source to encode and write.
     * @param target
     *            Target output stream for encoded audio data.
     * @throws IOException
     *             In case of an I/O error while reading from the audio source
     *             or writing to the target output stream.
     * @throws AudioDecodingException
     *             In case the audio source encounters an error while reading a
     *             sample.
     * @throws AudioEncodingException
     *             In case this writer encounters an error while trying to write
     *             sample data. This may be the case if the audio source
     *             descriptor settings are not compatible with this writer.
     */
    void write(AudioSource source, OutputStream target) throws IOException,
            AudioDecodingException, AudioEncodingException;
}
