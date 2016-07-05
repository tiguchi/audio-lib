package com.nobu_games.audio.source;

import java.io.Closeable;
import java.io.IOException;

import com.nobu_games.audio.AudioStreamDescriptor;

/**
 * Audio sample data source.
 * <p>
 * Provides a decoded stream of normalized double precision floating point
 * sample values ranging from <code>-1.0</code> to <code>+1.0</code>.
 * 
 * @author ti
 */
public interface AudioSource extends Closeable {
    /**
     * Gets the applicable bit rate for the currently buffered sample of the
     * specified channel.
     * <p>
     * This method will only return different values in case the bit rate is
     * variable.
     * 
     * @param channel
     *            Channel index number to get current bit rate for (offset = 0).
     * 
     * @return Bit rate for sample.
     */
    short getCurrentBitRate(int channel);

    /**
     * Gets the applicable sample rate at the current position in the audio
     * stream.
     * 
     * @param channel
     *            Channel to get sample rate for (offset = 0).
     * 
     * @return Sample rate for specified channel.
     */
    int getCurrentSampleRate(int channel);

    /**
     * Gets the audio stream descriptor for this audio source.
     * 
     * @return Descriptor object for this audio source.
     */
    AudioStreamDescriptor getDescriptor();

    /**
     * Gets the normalized sample value of the specified audio channel.
     * <p>
     * Make sure to call {@link #next()} before calling this method to
     * initialize the sample reading buffer.
     * 
     * @param channel
     *            Channel index number to get sample from (offset = 0).
     * 
     * @return Normalized sample value ranging from -1.0 to +1.0.
     * 
     * @throws IllegalArgumentException
     *             if the channel index is out of range
     * @throws IllegalStateException
     *             if {@link #next()} was not called prior calling this method.
     */
    double getSample(int channel);

    /**
     * Reads and buffers the next sample(s) of all available audio channels of
     * this source, if available.
     * 
     * @return True if there is at least one more sample available, false if the
     *         end of the stream has been reached.
     * @throws IOException
     *             In case of an I/O error while reading the next sample(s) from
     *             the underlying data source.
     */
    boolean next() throws IOException;
}
