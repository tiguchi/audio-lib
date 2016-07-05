package com.nobu_games.audio.format.wave;

public enum WaveAudioDataFormat {
    LINEAR_PCM(1);

    private short mCode;

    private WaveAudioDataFormat(int code) {
        mCode = (short) code;
    }

    public short getFormatCode() {
        return mCode;
    }
}
