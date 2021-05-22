package engine.game.systems;

import engine.game.GameObject;
import engine.game.components.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class GeneralSystem {
    protected List<Component> components;

    public GeneralSystem(){
        components = new ArrayList<Component>();
    }

    public void addComponent(Component c){
        this.components.add(c);
    }

    public void removeComponent(Component c) {
        this.components.remove(c);
    }

    public boolean hasComponent(Component c) {
        return this.components.contains(c);
    }

    public abstract int getSystemFlag();
}
