package com.nobu_games.audio.format.wave;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.nobu_games.audio.AudioStreamDescriptor;
import com.nobu_games.audio.format.ContainerFormatWriter;
import com.nobu_games.audio.sink.AudioEncodingException;
import com.nobu_games.audio.source.AudioDecodingException;
import com.nobu_games.audio.source.AudioSource;
import com.nobu_games.audio.stream.AudioStreamWriter;
import com.nobu_games.audio.stream.pcm.PCMAudioStreamWriter;

/**
 * Wave file writer.
 * 
 * @author ti
 */
public class WaveFormatWriter implements ContainerFormatWriter {
    private final byte[] mByteArray;
    private final ByteBuffer mByteBuffer;
    private final WaveAudioDataFormat mFormat;
    private OutputStream mOutput;

    public WaveFormatWriter(OutputStream output, WaveAudioDataFormat format) {
        mOutput = output;
        mFormat = format;
        mByteArray = new byte[44];
        mByteBuffer = ByteBuffer.wrap(mByteArray);
        mByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Calculates the final wave file size in bytes for the specified audio
     * source if the sample count is known.
     * 
     * @param source
     *            Audio source to calculate final wave file size for.
     * @return Wave file size in bytes for audio source on success. -1 if the
     *         source does not specify the total amount of samples.
     */
    // TODO I just realize how non-sensical this method is, since it would only
    // work correctly for PCM wave files.
    public static int calculateFileSize(AudioSource source) {
        final AudioStreamDescriptor descriptor = source.getDescriptor();
        final int sampleCount = descriptor.getSampleCount();
        if (sampleCount == -1)
            return -1;

        int bitsPerSample = descriptor.getMaximumBitsPerSample(0);
        int channels = descriptor.getChannelCount();
        int dataSize = sampleCount * channels * (bitsPerSample / 8);
        int fileSize = 44 + dataSize;

        return fileSize;
    }

    @Override
    public void close() throws IOException {
        mOutput.close();
    }

    @Override
    public void write(AudioSource source) throws IOException,
            AudioDecodingException, AudioEncodingException {
        writeHeader(source.getDescriptor());
        AudioStreamWriter writer;

        switch (mFormat) {
            case LINEAR_PCM:
                writer = new PCMAudioStreamWriter(ByteOrder.LITTLE_ENDIAN);
                break;

            default:
                throw new IllegalStateException(
                        "Cannot handle wave audio data format " + mFormat);
        }

        writer.write(source, mOutput);
    }

    private void writeHeader(AudioStreamDescriptor descriptor)
            throws IOException {
        mByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        mByteBuffer.put(new byte[] { 'R', 'I', 'F', 'F' });
        int sampleCount = descriptor.getSampleCount();
        int bitsPerSample = descriptor.getMaximumBitsPerSample(0);
        int channels = descriptor.getChannelCount();
        int sampleRate = descriptor.getMaximumSampleRate(0);
        int byteRate = sampleRate * channels * (bitsPerSample / 8);
        int blockAlign = channels * (bitsPerSample / 8);
        int dataSize = sampleCount * channels * (bitsPerSample / 8);
        int fileSize = 44 + dataSize;
        mByteBuffer.putInt(fileSize - 8);
        mByteBuffer.put(new byte[] { 'W', 'A', 'V', 'E' });
        mByteBuffer.put(new byte[] { 'f', 'm', 't', ' ' });
        // sub chunk size... this is currently hard coded for linear PCM
        // http://csserver.evansville.edu/~blandfor/EE356/WavFormatDocs.pdf
        mByteBuffer.putInt(16);
        mByteBuffer.putShort(mFormat.getFormatCode());
        mByteBuffer.putShort((short) channels);
        mByteBuffer.putInt(sampleRate);
        mByteBuffer.putInt(byteRate);
        mByteBuffer.putShort((short) blockAlign);
        mByteBuffer.putShort((short) bitsPerSample);
        mByteBuffer.put(new byte[] { 'd', 'a', 't', 'a' });
        mByteBuffer.putInt(dataSize);
        mOutput.write(mByteArray, 0, 44);
    }
}
