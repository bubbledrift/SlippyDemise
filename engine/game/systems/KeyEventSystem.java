package engine.game.systems;

import engine.game.GameObject;
import engine.game.components.Component;
import javafx.scene.input.KeyEvent;

/**
 * Forwards Key events to relevant game objects.
 */
public class KeyEventSystem extends GeneralSystem{

    public int getSystemFlag(){
        return SystemFlag.KeyEventSystem;
    }

    public void onKeyTyped(KeyEvent e) {
        for(Component o : this.components){
            if(o.isDisabled()) continue;
            o.onKeyTyped(e);
        }
    }

    public void onKeyPressed(KeyEvent e) {
        for(Component o : this.components){
            if(o.isDisabled()) continue;
            o.onKeyPressed(e);
        }
    }

    public void onKeyReleased(KeyEvent e) {
        for(Component o : this.components){
            if(o.isDisabled()) continue;
            o.onKeyReleased(e);
        }
    }

}
