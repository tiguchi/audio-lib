package com.nobu_games.audio.format.wave;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.nobu_games.audio.AudioStreamDescriptor;
import com.nobu_games.audio.format.ContainerFormatReader;
import com.nobu_games.audio.format.InvalidAudioFormatException;
import com.nobu_games.audio.source.AudioDecodingException;
import com.nobu_games.audio.source.AudioSource;
import com.nobu_games.audio.stream.pcm.PCMAudioSource;
import com.nobu_games.audio.stream.pcm.PCMAudioStreamDescriptor;

/**
 * Wave file reader.
 * 
 * @author ti
 */
public class WaveFormatReader implements ContainerFormatReader {
    private static final String INVALID_WAVE_FILE_FORMAT = "Invalid WAVE file format";
    
    private AudioStreamDescriptor mAudioStreamDescriptor;
    private short mBitsPerSample;
    private short mBlockAlign;
    private final byte[] mByteArray;
    private final ByteBuffer mByteBuffer;
    private int mByteRate;
    private short mChannels;
    private int mDataSize;
    private ByteOrder mEndianness;
    private short mFormatCode;
    private final InputStream mInput;
    private int mInputFileSize;
    private int mSampleCount;
    private int mSampleRate;

    public WaveFormatReader(InputStream source)
            throws InvalidAudioFormatException, IOException {
        mInput = source;
        // Length of wave file format header
        mByteArray = new byte[44];
        mByteBuffer = ByteBuffer.wrap(mByteArray);
        readHeader();
    }

    @Override
    public void close() throws IOException {
        mInput.close();
    }

    @Override
    public AudioSource createAudioSource() throws IOException,
            AudioDecodingException {
        switch (mFormatCode) {
            case 1:
                return new PCMAudioSource(mInput, mEndianness,
                        getAudioStreamDescriptor());

            default:
                throw new AudioDecodingException(
                        "Cannot create audio source for unsupported format code "
                                + mFormatCode);
        }
    }

    @Override
    public AudioStreamDescriptor getAudioStreamDescriptor()
            throws InvalidAudioFormatException {
        if (mAudioStreamDescriptor == null) {
            switch (mFormatCode) {
                case 1:
                    mAudioStreamDescriptor = new PCMAudioStreamDescriptor(
                            mChannels, mBitsPerSample, mSampleRate,
                            mSampleCount, 0);
                    break;

                default:
                    throw new InvalidAudioFormatException(
                            "Cannot create audio stream descriptor for unsupported format code "
                                    + mFormatCode);
            }
        }

        return mAudioStreamDescriptor;
    }

    private void readBuffer(int length) throws IOException {
        int result = mInput.read(mByteArray, 0, length);
        if (result == -1)
            throw new IOException("Unexpected end of stream");
    }

    private void readHeader() throws InvalidAudioFormatException, IOException {
        validateRiff();
        mByteBuffer.order(mEndianness);
        mInputFileSize = readInt();
        validateWave();
        validateFmt();
        readInt(); // sub-chunk size
        mFormatCode = readShort();
        mChannels = readShort();
        mSampleRate = readInt();
        mByteRate = readInt();
        mBlockAlign = readShort();
        mBitsPerSample = readShort();
        validateData();
        mDataSize = readInt();
        mSampleCount = mDataSize / mBlockAlign;
    }

    private int readInt() throws IOException {
        readBuffer(4);
        mByteBuffer.rewind();
        return mByteBuffer.getInt();
    }

    private short readShort() throws IOException {
        readBuffer(2);
        mByteBuffer.rewind();
        return mByteBuffer.getShort();
    }

    private void validateData() throws InvalidAudioFormatException, IOException {
        readBuffer(4);

        if (mByteArray[0] != 'd' || mByteArray[1] != 'a'
                || mByteArray[2] != 't' || mByteArray[3] != 'a') {
            throw new InvalidAudioFormatException(INVALID_WAVE_FILE_FORMAT);
        }
    }

    private void validateFmt() throws InvalidAudioFormatException, IOException {
        readBuffer(4);

        if (mByteArray[0] != 'f' || mByteArray[1] != 'm'
                || mByteArray[2] != 't' || mByteArray[3] != ' ') {
            throw new InvalidAudioFormatException(INVALID_WAVE_FILE_FORMAT);
        }
    }

    private void validateRiff() throws InvalidAudioFormatException, IOException {
        readBuffer(4);

        if (mByteArray[0] == 'R' && mByteArray[1] == 'I'
                && mByteArray[2] == 'F') {
            if (mByteArray[3] == 'F') {
                mEndianness = ByteOrder.LITTLE_ENDIAN;
                return;
            } else if (mByteArray[3] == 'X') {
                mEndianness = ByteOrder.BIG_ENDIAN;
                return;
            }
        }

        throw new InvalidAudioFormatException(INVALID_WAVE_FILE_FORMAT);
    }

    private void validateWave() throws InvalidAudioFormatException, IOException {
        readBuffer(4);

        if (mByteArray[0] != 'W' || mByteArray[1] != 'A'
                || mByteArray[2] != 'V' || mByteArray[3] != 'E') {
            throw new InvalidAudioFormatException(
                    "Unsupported WAVE file format");
        }
    }
}
