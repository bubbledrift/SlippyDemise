package engine.game.tileSystem;

import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.Shape;
import engine.game.components.SpriteComponent;
import engine.support.Vec2d;

public class SpriteTileVariant extends TileVariant{

    protected Vec2d cropStart; //from where the crop starts on the sprite sheet
    protected Vec2d cropSize; //size of region from which to draw

    public SpriteTileVariant(String variantName, Vec2d cropStart, Vec2d cropSize){
        super(variantName);
        this.variantName = variantName;
        this.cropStart = cropStart;
        this.cropSize = cropSize;
    }

    public SpriteTileVariant(int collision_up, int collision_right, int collision_down, int collision_left,
                             String variantName, Vec2d cropStart, Vec2d cropSize){
        super(collision_up, collision_right, collision_down, collision_left, variantName);
        this.variantName = variantName;
        this.cropStart = cropStart;
        this.cropSize = cropSize;
    }
    public SpriteTileVariant(Shape shape, String variantName, Vec2d cropStart, Vec2d cropSize){
        super(shape, variantName);
        this.variantName = variantName;
        this.cropStart = cropStart;
        this.cropSize = cropSize;
    }

    public GameObject constructGameObject(Vec2d position, Vec2d tilesSize, String spriteSheetPath, GameWorld gameWorld, int layer){
        GameObject tile = new GameObject(gameWorld, layer);
        SpriteComponent sprite = new SpriteComponent(spriteSheetPath, new Vec2d(0,0), tilesSize, cropStart, cropSize);
        tile.addComponent(sprite);
        tile.getTransform().position = position;
        tile.getTransform().size = tilesSize;
        return tile;
    }
}
