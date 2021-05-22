package engine.game.systems;

import engine.game.GameObject;

public class TickSystem extends GeneralSystem {

    public int getSystemFlag(){
        return SystemFlag.TickSystem;
    }

    public void onTick(long nanosSincePreviousTick){
        for(int i =0; i < this.components.size(); i++){
            if(this.components.get(i).isDisabled()) continue;
            this.components.get(i).onTick(nanosSincePreviousTick);
        }
    }
    public void onLateTick(){
        for(int i =0; i < this.components.size(); i++){
            if(this.components.get(i).isDisabled()) continue;
            this.components.get(i).onLateTick();
        }
    }
}
