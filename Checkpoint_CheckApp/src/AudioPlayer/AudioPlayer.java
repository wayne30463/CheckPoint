package AudioPlayer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class AudioPlayer {
   
    String fileName;
    Context contex;
    MediaPlayer mp;
    Timer timer = new Timer(true);
    long second = 2000;

    //Constructor
    public AudioPlayer(String name, Context context) {
        fileName = name;
        contex = context;
    }

    //Set fileName
    public void setFileName(String name) {
        fileName = name;
    }

    //Set PlaySecond
    public void setPlaySecond(long second) {
        this.second = second;
    }

    //Play Audio
    public void playAudio() {
        mp = new MediaPlayer();
        try {
            AssetFileDescriptor descriptor = contex.getAssets()
                    .openFd(fileName);
            mp.setDataSource(descriptor.getFileDescriptor(),
                    descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            mp.prepare();
            mp.setLooping(false);
            mp.start();
            mp.setVolume(3, 3);
            timer.schedule(new timerTask(), second);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Stop Audio
    public void stop() {
        mp.stop();
    }
    
    public class timerTask extends TimerTask
    {
      public void run()
      {
    	  mp.stop();
      }
    }
}
