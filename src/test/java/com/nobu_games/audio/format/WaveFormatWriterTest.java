package com.nobu_games.audio.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import com.nobu_games.audio.format.wave.WaveAudioDataFormat;
import com.nobu_games.audio.format.wave.WaveFormatReader;
import com.nobu_games.audio.format.wave.WaveFormatWriter;
import com.nobu_games.audio.sink.AudioEncodingException;
import com.nobu_games.audio.source.AudioDecodingException;
import com.nobu_games.audio.source.AudioSource;
import com.nobu_games.audio.source.ResampledAudioSource;
import com.nobu_games.common.io.FileTool;

public class WaveFormatWriterTest extends TestCase {
    public void testWaveFormatWriter() {
        InputStream is = getClass().getClassLoader().getResourceAsStream(
                "0.wav");
        try {
            WaveFormatReader reader = new WaveFormatReader(is);
            AudioSource as = reader.createAudioSource();
            File file = new File(FileTool.getHomeDirectory(), "waveTest.wav");// File.createTempFile("wave",
                                                                              // ".wav");
            System.out.println("Creating temp wave file at " + file);

            WaveFormatWriter writer = new WaveFormatWriter(
                    new FileOutputStream(file), WaveAudioDataFormat.LINEAR_PCM);

            ResampledAudioSource ras = new ResampledAudioSource(as, 44100);
            writer.write(ras);
            writer.close();
        } catch (InvalidAudioFormatException e) {
            fail("Audio format could not be parsed. Reason: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            fail("IOException while trying to file.");
            e.printStackTrace();
        } catch (AudioDecodingException e) {
            fail("Could not create audio source.");
            e.printStackTrace();
        } catch (AudioEncodingException e) {
            fail("Error while encoding audio: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
