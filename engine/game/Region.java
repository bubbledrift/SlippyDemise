package engine.game;

import java.util.ArrayList;
import java.util.List;

public class Region {
    /*
    Class that holds some set of game objects to be added and removed jointly from the game world.
    ###########################################

    The gameworld has an option to load in a region. This loads in all the gameobjects in the region at once.

    While the Region is linked to the gameworld, gameobjects that are added to the gameworld are added to
    the region as well. When gameobjects are removed they are removed from the region (if the region has this game object)

    Once the region is unloaded only the gameobjects originally in the gameworld remain (excluding those that were removed).
    Any gameobjects that were added while the Region is loaded will stay with the region unless a special flag is passed.
     */

    private List<GameObject> gameObjects;

    public Region(){
        this.gameObjects = new ArrayList<GameObject>();
    }

    public void addGameObject(GameObject g){
        this.gameObjects.add(g);
    }

    public void removeGameObject(GameObject g){
        this.gameObjects.remove(g);
    }

    public List<GameObject> getGameObjects(){
        return gameObjects;
    }
}
