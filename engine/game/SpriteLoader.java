package engine.game;


import javafx.scene.image.Image;

import java.util.HashMap;

public class SpriteLoader {

    private static HashMap<String,Image> loadedResources = new HashMap<String,Image>();

    /**
     * Loads image from given path. If image has already been loaded returns copy.
     * @return Image of the given name in the given path.
     */
    public static Image loadImage(String path) {
        if(SpriteLoader.loadedResources.containsKey(path)){
            return SpriteLoader.loadedResources.get(path);
        }
        Image img = new Image(path);
        SpriteLoader.loadedResources.put(path, img);
        return img;
    }
}
