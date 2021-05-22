package engine.game.tileSystem;

import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.AABShape;
import engine.game.collisionShapes.PolygonShape;
import engine.game.collisionShapes.Shape;
import engine.game.components.CollisionComponent;
import engine.game.components.Component;
import engine.support.Vec2d;

import java.util.ArrayList;
import java.util.List;

public abstract class TileVariant {

    /*
    Keeps information for single tile variant object. A single tile may have many

    Once a tile is processed it is converted into actual game objects sent to the game.
    This class is not actually used in the game.
     */
    public String variantName;

    public double thickness;

    public boolean collision_up, collision_right, collision_down, collision_left;

    public Shape collisionShape;

    public TileVariant(String variantName){
        this.variantName = variantName;
        this.collision_up = false;
        this.collision_right = false;
        this.collision_down = false;
        this.collision_left = false;
    }

    public TileVariant(boolean collision_up, boolean collision_right, boolean collision_down, boolean collision_left,
                       double thickness, String variantName){
        this.variantName = variantName;
        this.collision_up = collision_up;
        this.collision_right = collision_right;
        this.collision_down = collision_down;
        this.collision_left = collision_left;
        this.thickness = thickness;
    }

    public TileVariant(boolean collision_up, boolean collision_right, boolean collision_down, boolean collision_left, String variantName){
        this.variantName = variantName;
        this.collision_up = collision_up;
        this.collision_right = collision_right;
        this.collision_down = collision_down;
        this.collision_left = collision_left;
        this.thickness = 0;
    }

    public TileVariant(int collision_up, int collision_right, int collision_down, int collision_left,
                       double thickness, String variantName){
        this.variantName = variantName;
        this.collision_up = collision_up==1;
        this.collision_right = collision_right==1;
        this.collision_down = collision_down==1;
        this.collision_left = collision_left==1;
        this.thickness = thickness;
    }

    public TileVariant(int collision_up, int collision_right, int collision_down, int collision_left, String variantName){
        this.variantName = variantName;
        this.collision_up = collision_up==1;
        this.collision_right = collision_right==1;
        this.collision_down = collision_down==1;
        this.collision_left = collision_left==1;
        this.thickness = 0;
    }

    public TileVariant(Shape shape, String variantName){
        this.variantName = variantName;
        this.collisionShape = shape;
        this.thickness = 0;
    }

    public List<Component> getCollisionComponents(GameObject gameObject, Vec2d tileSize, int collisionLayer, int collisionMask){
        ArrayList<Component> ret = new ArrayList();

        if(this.collisionShape != null){
            ret.add(new CollisionComponent(this.collisionShape, true, true, collisionLayer, collisionMask));
            return ret;
        }
        //if no shape is specified use booleans
        if(this.collision_up){
            ret.add(new CollisionComponent(new AABShape(new Vec2d(0,0), new Vec2d(tileSize.x,this.thickness)),
                    true, true, collisionLayer, collisionMask));
        }
        if(this.collision_right){
            ret.add(new CollisionComponent(new AABShape(new Vec2d(tileSize.x - this.thickness,0), new Vec2d(this.thickness, tileSize.y)),
                    true, true, collisionLayer, collisionMask));
        }
        if(this.collision_down){
            ret.add(new CollisionComponent(new AABShape(new Vec2d(0, tileSize.y-this.thickness), new Vec2d(tileSize.x,this.thickness)),
                    true, true, collisionLayer, collisionMask));
        }
        if(this.collision_left){
            ret.add(new CollisionComponent(new AABShape(new Vec2d(0,0), new Vec2d(this.thickness, tileSize.y)),
                    true, true, collisionLayer, collisionMask));
        }
        return ret;
    }

    public abstract GameObject constructGameObject(Vec2d position, Vec2d tileSize, String spriteSheetPath, GameWorld gameWorld, int layer);
}
