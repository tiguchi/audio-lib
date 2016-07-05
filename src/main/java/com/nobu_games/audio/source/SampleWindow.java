package com.nobu_games.audio.source;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

import com.nobu_games.common.util.ArrayTool;

/**
 * Sliding window that provides a buffered data view into a range of samples of
 * an {@link AudioSource}.
 * <p>
 * Useful for online algorithms that need to look ahead or look past a few
 * samples of an audio source.
 * 
 * @author ti
 */
public class SampleWindow implements Closeable {
    private int mBufferFillIndex;
    private final int mBufferSize;
    private int mLookAheadSize;
    private int mLookBehindSize;
    private final double[][] mSampleBuffer;
    private final AudioSource mSource;

    /**
     * Creates a sample window object for an audio source.
     * 
     * @param source
     *            Audio source to provide window for.
     * @param lookAheadSize
     *            The maximum amount of future samples that can be accessed at a
     *            time in this window (must be >= 0).
     * @param lookBehindSize
     *            The maximum amount of past samples that can be accessed at a
     *            time in this window (must be >= 0).
     * 
     * @throws IllegalArgumentException
     *             If <code>lookAheadSize</code> or <code>lookBehindSize</code>
     *             are negative, or if both values are 0.
     * @throws NullPointerException
     *             If <code>source</code> is <code>null</code>.
     */
    public SampleWindow(AudioSource source, int lookAheadSize,
            int lookBehindSize) {
        if (lookAheadSize < 0)
            throw new IllegalArgumentException("lookAheadSize must be >= 0");
        if (lookBehindSize < 0)
            throw new IllegalArgumentException("lookBehindSize must be >= 0");
        if (lookAheadSize + lookBehindSize == 0)
            throw new IllegalArgumentException(
                    "lookAheadSize + lookBehindSize must be > 0");
        mSource = source;
        mLookAheadSize = lookAheadSize;
        mLookBehindSize = lookBehindSize;
        mBufferSize = mLookBehindSize + mLookAheadSize + 1;
        mSampleBuffer = new double[source.getDescriptor().getChannelCount()][mBufferSize];

        for (int i = 0; i < mSampleBuffer.length; ++i) {
            Arrays.fill(mSampleBuffer[i], Double.NaN);
        }

        // +1 because of algorithm implemented in next() method, which always
        // shifts the window by at least 1
        mBufferFillIndex = mLookBehindSize + 1;
    }

    /**
     * Closes the underlying audio source.
     */
    @Override
    public void close() throws IOException {
        mSource.close();
    }

    /**
     * Gets the underlying audio source for this window.
     * 
     * @return Audio source.
     */
    public AudioSource getAudioSource() {
        return mSource;
    }

    /**
     * Gets a sample from the window.
     * <p>
     * Make sure to call {@link #next(int)} at least once before calling this
     * method.
     * 
     * @param channel
     *            Channel index number to get sample from.
     * @param offset
     *            Relative offset. 0 meaning the current center position of the
     *            window, negative values look behind, positive values look
     *            ahead.
     * @return Normalized sample value (-1.0 to +1.0) or <code>Double.NaN</code>
     *         in case the offset value points to a non-existing sample in this
     *         window. This would be the case while the window is near the
     *         beginning or near the end of the underlying audio stream. It is
     *         guaranteed that offset 0 (window center) always returns a valid
     *         sample from the stream.
     * 
     * @see #next(int)
     * @throws ArrayIndexOutOfBoundsException
     *             In case the relative offset value points out of bounds of
     *             this window. That is the case when
     *             <code>abs(offset) > "look-behind size"</code> or when
     *             <code>offset > "look-ahead size"</code>.
     */
    public double getSample(int channel, int offset) {
        return mSampleBuffer[channel][mLookBehindSize + offset];
    }

    /**
     * Gets the total amount of samples buffered by this window at a time.
     * <p>
     * <code>look-behind size + look-ahead size + 1</code>
     * 
     * @return Window size in samples.
     */
    public int getWindowSize() {
        return mBufferSize;
    }

    /**
     * Slides the window to the next position in the audio stream.
     * 
     * @param skip
     *            Optional amount of audio stream samples to skip. A value of 0
     *            means that the window slides only by 1 spot through the sample
     *            stream, without skipping any samples.
     * 
     * @return <code>true</code> if there is at least 1 more sample left in the
     *         audio stream to reach the center location of this window,
     *         <code>false</code> if the end of stream has been reached.
     * @throws IOException
     *             In case there was an I/O error while reading samples from the
     *             underlying audio source.
     * @see {@link #getSample(int, int)}
     */
    public boolean next(int skip) throws IOException {
        boolean didLoad = false;

        for (int channel = 0; channel < mSampleBuffer.length; ++channel) {
            double[] samples = mSampleBuffer[channel];
            ArrayTool.shift(samples, -(skip + 1), Double.NaN);
        }

        mBufferFillIndex -= skip + 1;
        if (mBufferFillIndex < 0)
            mBufferFillIndex = 0;

        for (int i = 0; i < skip; ++i) {
            if (!mSource.next())
                break;
        }

        for (; mBufferFillIndex < mBufferSize; ++mBufferFillIndex) {
            if (mSource.next()) {
                didLoad = true;
            } else {
                break;
            }

            for (int channel = 0; channel < mSampleBuffer.length; ++channel) {
                double[] samples = mSampleBuffer[channel];
                samples[mBufferFillIndex] = mSource.getSample(channel);
            }
        }

        return didLoad || mBufferFillIndex > mLookBehindSize;
    }
}
