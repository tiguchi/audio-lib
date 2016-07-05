package com.nobu_games.audio.format;

import com.nobu_games.audio.source.AudioDecodingException;

public class InvalidAudioFormatException extends AudioDecodingException {
    public InvalidAudioFormatException(String message) {
        super(message);
    }

}
