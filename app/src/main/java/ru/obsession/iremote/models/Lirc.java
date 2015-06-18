package ru.obsession.iremote.models;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Arrays;

public class Lirc implements Serializable{

    protected String name;
    private String lircName;

    static {
        System.loadLibrary("androlirc");
    }

    native int parse(String filename);
    native byte[] getIrBuffer(String irDevice, String irCode);
    native String[] getDeviceList();
    native String[] getCommandList(String irDevice);

    public static AudioTrack ir;
    byte buffer[];
    private String fileName;
    public  int bufSize = AudioTrack.getMinBufferSize(48000,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_8BIT);

    public void sendSignal(String cmd) {
        if (lircName != null) {
            buffer = getIrBuffer(lircName, cmd);
            ir = new AudioTrack(AudioManager.STREAM_MUSIC, 48000,
                    AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_8BIT,
                    bufSize, AudioTrack.MODE_STATIC);


            if (bufSize < buffer.length) {
                bufSize = buffer.length;
            }

            ir.write(buffer, 0, buffer.length);
            ir.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                @Override
                public void onPeriodicNotification(AudioTrack track) {
                    // nothing to do
                }

                @Override
                public void onMarkerReached(AudioTrack track) {
                    ir.release();
                    ir = null;
                }
            });
            ir.setStereoVolume(1, 1);
            ir.play();
        }
    }

    public String[] getCommands(){
        if (lircName != null) {
            return getCommandList(lircName);
        }
        return new String[1];
    }

    public Lirc(String fileName){
        this.fileName = fileName;
    }

    public Lirc(){
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void parseFile(Context context){
        parse(fileName);
        String[] names = getDeviceList();
        if (names != null && names.length != 0) {
            lircName = names[0];
        }
        Toast.makeText(context, lircName, Toast.LENGTH_LONG).show();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
