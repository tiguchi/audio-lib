# Java Audio Lib #

This is a simple audio transcoding library specifically for changing the sample rate of PCM wave streams on the fly.

I originally created this library a while ago for a single purpose: doubling the sample rate of PCM wave files from 22 KHZ to 44KHZ so they could
be played back correctly on Google Chromecast devices.

More info here: http://blog.nobu-games.com/2015/04/24/chromecast-development-blues

This project is not actively maintained. If you find errors then feel free to send me a pull request.

## Resampling Usage Example ##
```
        final int CD_QUALITY_SAMPLE_RATE = 44100;
        FileInputStream fis = new FileInputStream(audiofile);

        try {
            final WaveFormatReader reader = new WaveFormatReader(fis);

            try {
                final AudioStreamDescriptor descr = reader.getAudioStreamDescriptor();
                AudioSource source = reader.createAudioSource();

                if (descr.getMaximumSampleRate(0) < CD_QUALITY_SAMPLE_RATE) {
                    source = new ResampledAudioSource(source, CD_QUALITY_SAMPLE_RATE);

                    if (descr.getChannelCount() > 1) {
                        log.warn(
                                "PCM audio source has %d channels. Mixing down to mono for faster delivery...",
                                descr.getChannelCount());
                        source = new MonoDownmixAudioSource(source);
                    }
                } else {
                    // TODO This is to forcibly go to the according catch block and deliver the audio file verbatim without resampling. It could be done more elegantly, I suppose.
                    throw new InvalidAudioFormatException(
                            "PCM data already has a sufficiently high sample rate.");
                }

                final int fileSize = WaveFormatWriter.calculateFileSize(source);

                if (fileSize > -1) {
                    log.info("Delivering %d bytes...", fileSize);
                    response.setContentLength(fileSize);
                }

                WaveFormatWriter writer = new WaveFormatWriter(os, WaveAudioDataFormat.LINEAR_PCM);

                try {
                    writer.write(source);
                } catch (AudioEncodingException | AudioDecodingException e) {
                    log.error("Could not transcode audio.", e);
                    // TODO This here is a serious error condition since this may yield corrupted audio data that can break the speakers of the TV!
                }
            } finally {
                reader.close();
            }
        } catch (InvalidAudioFormatException e) {
            log.warn(String.format(Locale.US,
                    "Generated speech file %s has either an unsupported WAVE file format or already the correct sample rate. Delivering unprocessed audio file.",
                    file), e);
            // Deliver unprocessed audio file...
        } catch (AudioDecodingException e) {
            log.warn("Could not decode audio. Sending 'file not found'.");
            // Send 404
        }
```
