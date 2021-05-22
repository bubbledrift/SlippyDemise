package engine.game.components;

import engine.game.systems.SystemFlag;
import engine.support.Vec2d;

public class ShakeComponent extends Component{

    private double magnitude;
    private double duration;

    private Vec2d orig_pos;

    public ShakeComponent(double magnitude, double duration){
        this.magnitude = magnitude;
        this.duration = duration;

    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
        if(this.orig_pos == null) this.orig_pos = this.gameObject.getTransform().position;

        this.duration -= nanosSincePreviousTick/1000000000.0;
        double x = (Math.random()*2 - 1)*this.magnitude;
        double y = (Math.random()*2 - 1)*this.magnitude;
        this.gameObject.getTransform().position = this.orig_pos.plus(new Vec2d(x,y));

        if(duration <= 0){
            this.gameObject.getTransform().position = this.orig_pos;
            this.gameObject.removeComponent(this);
        }
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "ShakeComponent";
    }
}
