package com.nobu_games.audio.format;

import java.io.Closeable;
import java.io.IOException;

import com.nobu_games.audio.AudioStreamDescriptor;
import com.nobu_games.audio.source.AudioDecodingException;
import com.nobu_games.audio.source.AudioSource;

/**
 * Reader for an audio container format.
 * 
 * @author ti
 */
public interface ContainerFormatReader extends Closeable {
    /**
     * Gets the audio stream descriptor for the contained audio stream.
     * 
     * @return Audio stream descriptor.
     * @throws InvalidAudioFormatException
     *             In case the audio stream format is not supported.
     */
    AudioStreamDescriptor getAudioStreamDescriptor()
            throws InvalidAudioFormatException;

    /**
     * Creates an audio source that can decode the contained audio stream on the
     * fly.
     * <p>
     * This method must only be called once on a reader object.
     * 
     * @return Audio source for the contained audio stream data.
     * 
     * @throws IOException
     *             In case there was an error reading from the input data
     *             stream.
     * @throws AudioDecodingException
     *             In case the audio source could not be properly set up due to
     *             unsupported audio stream parameters or if the audio stream
     *             format is not supported.
     */
    AudioSource createAudioSource() throws IOException, AudioDecodingException;
}
