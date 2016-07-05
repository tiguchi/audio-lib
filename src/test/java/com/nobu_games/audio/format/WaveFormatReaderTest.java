package com.nobu_games.audio.format;

import java.io.IOException;
import java.io.InputStream;

import com.nobu_games.audio.AudioStreamDescriptor;
import com.nobu_games.audio.format.wave.WaveFormatReader;
import com.nobu_games.audio.source.AudioDecodingException;
import com.nobu_games.audio.source.AudioSource;

import junit.framework.TestCase;

public class WaveFormatReaderTest extends TestCase {
    public void testWaveFormatReader() {
        InputStream is = getClass().getClassLoader().getResourceAsStream(
                "0.wav");
        try {
            WaveFormatReader reader = new WaveFormatReader(is);
            AudioSource as = reader.createAudioSource();
            AudioStreamDescriptor descriptor = as.getDescriptor();
            assertEquals(1, descriptor.getChannelCount());
            assertEquals(16, descriptor.getMaximumBitsPerSample(0));
            assertEquals(22500, descriptor.getMaximumSampleRate(0));
            
            while(as.next()) {
                double sample = as.getSample(0);
                assertTrue(sample >= -1.0 && sample <= 1.0);
            }
            
            as.close();
        } catch (InvalidAudioFormatException e) {
            fail("Audio format could not be parsed. Reason: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            fail("IOException while trying to file.");
            e.printStackTrace();
        } catch (AudioDecodingException e) {
            fail("Could not create audio source.");
            e.printStackTrace();
        }
    }
}
