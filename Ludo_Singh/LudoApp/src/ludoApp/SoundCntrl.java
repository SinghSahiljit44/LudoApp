package ludoApp;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public enum SoundCntrl {

    DADO("dado.wav"),
    MANGIA("mangia.wav"),
    SOUND("soundtrackSfondo.wav"),
    MOSSA("movimentoPedina.wav");

    String fileName;
    Clip clip;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private SoundCntrl(String fname) {
        this.fileName = fname;
        clip = loadSoundTrack(fname);
    }

    Clip loadSoundTrack(String fname) {
        try {
            File audioFile = new File(System.getProperty("user.dir") + "/resource/" + fname);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void play() {
        playWithDelay(0);  
    }

    public void playWithDelay(long delayMillis) {
        if (clip == null || clip.isRunning()) {
            return;  
        }

        scheduler.schedule(() -> {
            new Thread(() -> {
                clip.setFramePosition(0); 
                clip.start(); 
            }).start();
        }, delayMillis, TimeUnit.MILLISECONDS); 
    }

}
