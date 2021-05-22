package engine.game.components.screenEffects;

import javafx.scene.canvas.GraphicsContext;

public class ShakeEffect extends ScreenEffectComponent{

    private double magnitude;
    private double duration;

    public ShakeEffect(double magnitude, double duration){
        this.magnitude = magnitude;
        this.duration = duration;
    }

    @Override
    public void preEffect(GraphicsContext g) {
        double x = (Math.random()*2 - 1)*this.magnitude;
        double y = (Math.random()*2 - 1)*this.magnitude;
        g.translate(x,y);
    }

    @Override
    public void postEffect(GraphicsContext g) {

    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
        this.duration -= nanosSincePreviousTick/1000000000.0;
        if(duration <= 0){
            this.gameObject.removeComponent(this);
        }
    }

    @Override
    public String getTag() {
        return "ShakeEffect";
    }
}
