package com.nobu_games.audio.source;

import java.io.IOException;

import com.nobu_games.audio.AudioStreamDescriptor;

/**
 * Audio source that changes the sample rate of another audio source on the fly.
 * 
 * @author ti
 */
public class ResampledAudioSource implements AudioSource {
    private final int mResampleRate;
    private final AudioStreamDescriptor mDescriptor;
    private int mSampleCount;
    private final float mPullSampleIncrement;
    private float mSampleIndex = -1f;
    private final double[] mSampleBuffer;
    private boolean mEOS;
    private SampleWindow mWindow;
    private int mWindowSize;

    /**
     * Creates a resampled audio source for the specified audio source.
     * 
     * @param source
     *            Audio source to resample.
     * @param resampleRate
     *            The target sample rate.
     */
    public ResampledAudioSource(AudioSource source, int resampleRate) {
        mResampleRate = resampleRate;
        final AudioStreamDescriptor descriptor = source.getDescriptor();

        if ((descriptor.getFlags() & AudioStreamDescriptor.FLAG_VARIABLE_SAMPLE_RATE) != 0) {
            throw new IllegalArgumentException(
                    "Cannot resample an audio source with variable sample rate.");
        }

        float resampleFactor = (float) resampleRate
                / (float) descriptor.getMaximumSampleRate(0);
        mPullSampleIncrement = 1f / resampleFactor;
        mSampleCount = descriptor.getSampleCount();
        if (mSampleCount > -1)
            mSampleCount = Math.round(mSampleCount * resampleFactor);
        mDescriptor = new ProxyAudioStreamDescriptor(descriptor) {
            @Override
            public int getMaximumSampleRate(int channel) {
                return mResampleRate;
            }

            @Override
            public int getMinimumSampleRate(int channel) {
                return mResampleRate;
            }

            @Override
            public int getSampleCount() {
                return mSampleCount;
            }
        };

        mWindowSize = Math.round(resampleFactor) - 1;
        if (mWindowSize == 0)
            mWindowSize = 1;
        mWindow = new SampleWindow(source, mWindowSize, 0);
        mSampleBuffer = new double[descriptor.getChannelCount()];
    }

    @Override
    public void close() throws IOException {
        mWindow.close();
    }

    @Override
    public short getCurrentBitRate(int channel) {
        throw new UnsupportedOperationException("Not implemented, yet.");
    }

    @Override
    public int getCurrentSampleRate(int channel) {
        return mResampleRate;
    }

    @Override
    public AudioStreamDescriptor getDescriptor() {
        return mDescriptor;
    }

    @Override
    public double getSample(int channel) {
        return mSampleBuffer[channel];
    }

    @Override
    public boolean next() throws IOException {
        if (mEOS)
            return false;
        boolean didLoad = false;

        if (mSampleIndex < 0) {
            mSampleIndex = 0;

            if (!mWindow.next(0)) {
                mEOS = true;
            } else {
                didLoad = true;
            }
        } else {
            mSampleIndex += mPullSampleIncrement;

            if (mSampleIndex >= 1f) {
                int toSkip = ((int) mSampleIndex) - 1;
                if (!mWindow.next(toSkip)) {
                    mEOS = true;
                } else {
                    didLoad = true;
                }
                mSampleIndex -= toSkip + 1;
            }
        }

        int range = mWindowSize * 2;

        for (int channel = 0; channel < mSampleBuffer.length; ++channel) {
            double sample = 0;
            int winPos = Math.round(mSampleIndex * range);
            int start = winPos;
            int end = mWindowSize + 1;
            int div = 0;

            for (int offset = start; offset < end; ++offset) {
                double sourceSample = mWindow.getSample(channel, offset);
                if (Double.isNaN(sourceSample))
                    continue;
                div++;
                sample += sourceSample;
            }

            if (div == 0)
                continue;
            sample /= div;
            mSampleBuffer[channel] = sample;
        }

        return didLoad || !mEOS;
    }
}
