package engine.game.systems;

import engine.game.components.screenEffects.ScreenEffectComponent;
import javafx.scene.canvas.GraphicsContext;

public class ScreenEffectSystem extends GeneralSystem{

    public int getSystemFlag(){
        return SystemFlag.ScreenEffectSystem;
    }

    public void preEffect(GraphicsContext g){
        for(int i = 0; i < this.components.size(); i++){
            ScreenEffectComponent effect = (ScreenEffectComponent)this.components.get(i);
            if(effect == null || effect.isDisabled()) continue;
            effect.preEffect(g);
        }
    }

    public void postEffect(GraphicsContext g){
        for(int i = 0; i < this.components.size(); i++){
            ScreenEffectComponent effect = (ScreenEffectComponent)this.components.get(i);
            if(effect == null || effect.isDisabled()) continue;
            effect.postEffect(g);
        }
    }

    public void onTick(long nanosSincePreviousTick){
        for(int i = 0; i < this.components.size(); i++){
            ScreenEffectComponent effect = (ScreenEffectComponent)this.components.get(i);
            if(effect == null || effect.isDisabled()) continue;
            effect.onTick(nanosSincePreviousTick);
        }
    }
}
