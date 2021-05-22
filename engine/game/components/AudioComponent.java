package engine.game.components;

import engine.game.GameObject;
import engine.game.systems.SystemFlag;
import javax.sound.sampled.*;
import engine.support.Vec2d;

import java.io.*;

public class AudioComponent extends Component {

    protected String filePath;
    protected FloatControl gainControl;
    protected GameObject source;
    protected Clip clip;
    protected boolean loop = false;

    public AudioComponent(String filePath, GameObject source, boolean loop) {
        super();
        this.filePath = "projects/final_project/assets/sounds/" + filePath;
        this.source = source;
        this.loop = loop;
    }

    public AudioComponent(String filePath, GameObject source) {
        super();
        this.filePath = "projects/final_project/assets/sounds/" + filePath;
        this.source = source;
    }

    public AudioComponent(String filePath) {
        super();
        this.filePath = "projects/final_project/assets/sounds/" + filePath;
    }

    public AudioComponent(String filePath, boolean loop) {
        super();
        this.filePath = "projects/final_project/assets/sounds/" + filePath;
        this.loop = loop;
    }

    //Stops the clip
    public void stop() {
        if(clip != null) {
            clip.stop();
        }
    }

    //Starts the clip from the very beginning
    public void start() {
        if(clip != null && !clip.isRunning()) {
            clip = null;
        }




        if(clip == null) {
            clip = setup();

            gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            gainControl.setValue(-20.0f); //TODO ALEX CHANGE


            if(source != null) {
                gainControl =
                        (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                gainControl.setValue(-80.0f);
            }

            if(loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }

        if(loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }

        clip.start();
    }



    @Override
    public void onTick(long nanosSincePreviousTick){

        if(source != null && gainControl != null) {
            //Based on the game object's position from the source, reduce volume
            float dist = (float)gameObject.getTransform().position.dist(source.getTransform().position);
            if(-0.8f*dist < -80.0f) {
                gainControl.setValue(-80.0f);
            }
            else {
                gainControl.setValue(-0.8f*dist);
            }
        }

    }


    public Clip setup() {
        File file = new File(filePath);
        InputStream in =
                null;
        try {
            in = new BufferedInputStream(
                    new FileInputStream(file)
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AudioInputStream stream = null;

        try {
            stream = AudioSystem
                    .getAudioInputStream(in);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        try {
            clip.open(stream);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clip;
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "AudioComponent";
    }

}
