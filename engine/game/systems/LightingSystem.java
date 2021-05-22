package engine.game.systems;

import engine.game.components.LightComponent;
import engine.support.Vec2d;


//Keeps track of all light sources and calculates the brightness at different points.
public class LightingSystem extends GeneralSystem {

    public int getSystemFlag(){
        return SystemFlag.LightingSystem;
    }


    //TODO there may be a way to optimize this. probably saving computations in a smart way
    public double getBrightnessAt(Vec2d location){
        double totalBrightness = 0;
        for(int i =0; i < this.components.size(); i++) {
            if (this.components.get(i).isDisabled()) continue;
            LightComponent lightComponent = (LightComponent)this.components.get(i);
            totalBrightness += lightComponent.getBrightnessAtLocation(location);
        }
        return Math.min(totalBrightness,1);
    }

}
