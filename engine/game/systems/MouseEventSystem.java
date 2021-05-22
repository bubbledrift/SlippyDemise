package engine.game.systems;

import engine.game.GameObject;
import engine.game.components.Component;
import engine.support.Vec2d;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class MouseEventSystem extends GeneralSystem{

    public int getSystemFlag(){
        return SystemFlag.MouseEventSystem;
    }

    public void onMouseClicked(Vec2d e) {
        for(Component o : this.components){
            if(o.isDisabled()) continue;
            o.onMouseClicked(e);
        }
    }

    public void onMousePressed(Vec2d e) {
        for(Component o : this.components){
            if(o.isDisabled()) continue;
            o.onMousePressed(e);
        }
    }


    public void onMouseReleased(Vec2d e) {
        for(Component o : this.components){
            if(o.isDisabled()) continue;
            o.onMouseReleased(e);
        }
    }


    public void onMouseDragged(Vec2d e) {
        for(Component o : this.components){
            if(o.isDisabled()) continue;
            o.onMouseDragged(e);
        }
    }


    public void onMouseMoved(Vec2d e) {
        for(Component o : this.components){
            if(o.isDisabled()) continue;
            o.onMouseMoved(e);
        }
    }

    public void onMouseWheelMoved(ScrollEvent e) {
        for(Component o : this.components){
            if(o.isDisabled()) continue;
            o.onMouseWheelMoved(e);
        }
    }
}
