package com.nobu_games.audio.source;

import java.io.IOException;

import com.nobu_games.audio.stream.pcm.PCMAudioStreamDescriptor;

import junit.framework.TestCase;

public class SampleWindowTest extends TestCase {

    public void testSampleWindowDataIntegrity() {
        double[] samples = new double[64];

        for (int i = 0; i < samples.length; ++i) {
            samples[i] = i + 1;
        }

        PCMAudioStreamDescriptor descriptor = new PCMAudioStreamDescriptor(1,
                (short) 16, 22050, 64, 0);

        DoubleArrayAudioSource source = new DoubleArrayAudioSource(descriptor,
                samples);

        final int lookAheadSize = 2;
        final int lookBehindSize = 2;
        SampleWindow window = new SampleWindow(source, lookAheadSize,
                lookBehindSize);

        try {
            int readIndex = 0;

            for (; readIndex < samples.length; ++readIndex) {
                assertTrue(
                        "Could not iterate window at readIndex=" + readIndex,
                        window.next(0));
                double centerSample = window.getSample(0, 0);
                assertEquals("Wrong sample value at readIndex=" + readIndex,
                        samples[readIndex], centerSample);
                double beforeSample1 = window.getSample(0, -1);
                double beforeSample2 = window.getSample(0, -2);
                double afterSample1 = window.getSample(0, 1);
                double afterSample2 = window.getSample(0, 2);

                if (Double.isNaN(beforeSample1)) {
                    assertTrue(
                            "beforeSample1 is unexpectedly NaN at readIndex="
                                    + readIndex, readIndex < lookBehindSize);
                } else {
                    assertTrue(
                            "beforeSample1 is not less than centerSample at readIndex="
                                    + readIndex, beforeSample1 < centerSample);
                }

                if (Double.isNaN(beforeSample2)) {
                    assertTrue(
                            "beforeSample2 is unexpectedly NaN at readIndex="
                                    + readIndex, readIndex < lookBehindSize);
                } else {
                    assertTrue(
                            "beforeSample2 is not less than centerSample at readIndex="
                                    + readIndex, beforeSample2 < centerSample);

                    assertTrue(
                            "beforeSample2 is not less than beforeSample1 at readIndex="
                                    + readIndex, beforeSample2 < beforeSample1);
                }

                if (Double.isNaN(afterSample1)) {
                    assertTrue("afterSample1 is unexpectedly NaN at readIndex="
                            + readIndex, readIndex >= samples.length
                            - lookAheadSize);
                } else {
                    assertTrue(
                            "afterSample1 is not greater than centerSample at readIndex="
                                    + readIndex, afterSample1 > centerSample);
                }

                if (Double.isNaN(afterSample2)) {
                    assertTrue("afterSample2 is unexpectedly NaN at readIndex="
                            + readIndex, readIndex >= samples.length
                            - lookAheadSize);
                } else {
                    assertTrue(
                            "afterSample2 is not greater than centerSample at readIndex="
                                    + readIndex, afterSample2 > centerSample);

                    assertTrue(
                            "afterSample2 is not greater than afterSample1 at readIndex="
                                    + readIndex, afterSample2 > afterSample1);
                }
            }

            assertEquals(
                    "Sample window iteration did not reach the end of the array",
                    samples.length, readIndex);
        } catch (IOException e) {
            fail("Unexpected IOException " + e.getMessage());
            e.printStackTrace();
        }
    }
}
