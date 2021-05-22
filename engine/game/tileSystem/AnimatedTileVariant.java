package engine.game.tileSystem;

import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.components.SpriteComponent;
import engine.support.Vec2d;

public class AnimatedTileVariant extends SpriteTileVariant{

    /*
    Keeps information for single tile variant object. A single tile may have many

    Once a tile is processed it is converted into actual game objects sent to the game.
    This class is not actually used in the game.
     */
    protected int frames; //offset on sprite sheet
    protected Vec2d cropShift; //shift between crops (for when there is space between textures)

    public AnimatedTileVariant(String variantName, Vec2d cropStart, Vec2d cropSize, int frames, Vec2d cropShift) {
        super(variantName, cropStart, cropSize);
        this.frames = frames;
        this.cropShift = cropShift;
    }

    @Override
    public GameObject constructGameObject(Vec2d position, Vec2d tilesSize, String spriteSheetPath, GameWorld gameWorld, int layer){
        GameObject tile = new GameObject(gameWorld, layer);
        SpriteComponent sprite = new SpriteComponent(spriteSheetPath, new Vec2d(0,0), tilesSize, cropStart, cropSize);
        tile.addComponent(sprite);
        tile.getTransform().position = position;
        tile.getTransform().size = tilesSize;
        return tile;
    }
}
