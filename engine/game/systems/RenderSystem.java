package engine.game.systems;

import engine.game.components.Component;
import javafx.scene.canvas.GraphicsContext;

import java.util.*;


//TODO sort by layer / other criteria
//TODO store objects intelligently

public class RenderSystem extends GeneralSystem {

    private Map<Integer,List<Component>> layers = new HashMap<Integer,List<Component>>();

    public int getSystemFlag(){
        return SystemFlag.RenderSystem;
    }

    private class RenderOrdering implements Comparator<Component>{
        @Override
        public int compare(Component c1, Component c2) {
            return Double.compare(c1.getRenderOrdering(),c2.getRenderOrdering());
        }
    }

    public void onDraw(GraphicsContext g){
        List<Integer> orderedLayers = new ArrayList<Integer>(layers.keySet());
        Collections.sort(orderedLayers);
        for(int layer : orderedLayers){
            List<Component> components = layers.get(layer);
            components.sort(new RenderOrdering());
            for(Component c : layers.get(layer)){
                if(c.isDisabled()) continue;
                c.onDraw(g);
            }
        }
    }

    public void onLateDraw(GraphicsContext g) {
        List<Integer> orderedLayers = new ArrayList<Integer>(layers.keySet());
        Collections.sort(orderedLayers);
        for(int layer : orderedLayers){
            List<Component> components = layers.get(layer);
            components.sort(new RenderOrdering());
            for(Component c : layers.get(layer)){
                if(c.isDisabled()) continue;
                c.onLateDraw(g);
            }
        }
    }

    @Override
    public void addComponent(Component o){
        super.addComponent(o);
        if(layers.containsKey(o.getGameObject().getLayer())) {
            layers.get(o.getGameObject().getLayer()).add(o);
        } else {
            List<Component> layerList = new ArrayList<Component>();
            layerList.add(o);
            layers.put(o.getGameObject().getLayer(),layerList);
        }
    }
    @Override
    public void removeComponent(Component o) {
        super.removeComponent(o);
        layers.get(o.getGameObject().getLayer()).remove(o);
    }

}
