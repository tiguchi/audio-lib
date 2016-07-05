package com.nobu_games.audio.source;

import java.io.IOException;

import com.nobu_games.audio.stream.pcm.PCMAudioStreamDescriptor;

import junit.framework.TestCase;

public class ResampledAudioSourceTest extends TestCase {
    public void testResampleCorrectness() {
        double[] samples = new double[64];

        for (int i = 0; i < samples.length; ++i) {
            samples[i] = Math.round(Math.random() * 100);
        }

        PCMAudioStreamDescriptor descriptor = new PCMAudioStreamDescriptor(1,
                (short) 16, 22050, 64, 0);

        DoubleArrayAudioSource source = new DoubleArrayAudioSource(descriptor,
                samples);

        ResampledAudioSource resampled = new ResampledAudioSource(source, 44100);

        final int expectedTargetLength = samples.length * 2;
        int sampleIndex = 0;
        
        try {
            for (; sampleIndex < expectedTargetLength; ++sampleIndex) {
                assertTrue(
                        "Resampled audio stream is prematurely out of samples",
                        resampled.next());
                int originalIndex = sampleIndex / 2;
                double originalSample = samples[originalIndex];
                double sample = resampled.getSample(0);
                
                System.out.println(sample);
            }

            assertEquals(
                    "Resampled audio stream does not have enough samples.",
                    expectedTargetLength, sampleIndex);
        } catch (IOException e) {
            fail("Unexpected IOException " + e.getMessage());
            e.printStackTrace();
        }
    }
}
