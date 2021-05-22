package engine.game.systems;

import engine.game.GameObject;
import engine.game.components.CollisionComponent;
import engine.game.components.Component;
import engine.game.components.PhysicsComponent;
import engine.game.components.RayComponent;
import engine.support.Vec2d;

import java.util.ArrayList;
import java.util.List;

//TODO 2d tree for more efficient collisions.



public class CollisionSystem extends GeneralSystem {

    private List<RayComponent> rays;

    public CollisionSystem(){
        super();
        rays = new ArrayList<RayComponent>();
    }

    public int getSystemFlag(){
        return SystemFlag.CollisionSystem;
    }

    @Override
    public void addComponent(Component c){
        if(c instanceof RayComponent){
            this.rays.add((RayComponent)c);
        } else {
            this.components.add(c);
        }
    }

    @Override
    public void removeComponent(Component c) {
        if(c instanceof RayComponent){
            this.rays.remove((RayComponent)c);
        } else {
            this.components.remove(c);
        }
    }

    public void onTick(long nanosSincePreviousTick) {
        for(int i = 0; i < this.components.size()-1; i++){
            CollisionComponent c1 = (CollisionComponent) components.get(i);
            if(c1 == null || c1.isDisabled()) continue; //something was deleted so we shouldn't do collision for it
            for(int j = i+1; j < this.components.size(); j++){
                CollisionComponent c2 = (CollisionComponent) components.get(j);
                if(c2 == null || c2.isDisabled()) continue; //something was deleted so we shouldn't do collision for it


                //Booleans for both directions of collision
                //true if collision should matter for that object
                boolean c1_cares = 0 != (c1.getCollisionMask() & c2.getCollisionLayer());
                boolean c2_cares = 0 != (c2.getCollisionMask() & c1.getCollisionLayer());

                //take into account that solid things only care about solid things.
                c1_cares = c1_cares && (!c1.isSolid() || c2.isSolid());
                c2_cares = c2_cares && (!c2.isSolid() || c1.isSolid());

                if(!c1_cares && !c2_cares){
                    continue;
                }

                //TODO painful work around need better way of handling height based collision
                if(!c1.caresAboutCollision(c2.getGameObject()) || !c2.caresAboutCollision(c1.getGameObject())){
                    continue;
                }

                Vec2d MTV = c2.collide(c1);

                if(MTV == null) continue;

                if(Double.isNaN(MTV.x) || Double.isNaN(MTV.y)){
                    new Exception().printStackTrace();
                    System.exit(0);
                }

                if((c1.isStatic() && c2.isStatic())){
                    if(c1_cares)
                        c1.onCollision(new CollisionInfo(c1, c2, true, new Vec2d(MTV.x/2,MTV.y/2)));
                    if(c2_cares)
                        c2.onCollision(new CollisionInfo(c2, c1, false, new Vec2d(-MTV.x/2,-MTV.y/2)));
                    continue;
                }
                //TODO maybe better way to check this
                if(c1.hasPhysics && c2.hasPhysics){
                    PhysicsComponent p1 = (PhysicsComponent)c1;
                    PhysicsComponent p2 = (PhysicsComponent)c2;
                    this.applyImpulses(MTV, p1, p2);
                }

                if(c1.isStatic()) {
                    if(c1_cares)
                        c1.onCollision(new CollisionInfo(c1, c2, true, MTV));
                    if(c2_cares)
                        c2.onCollision(new CollisionInfo(c2, c1, false, MTV.smult(-1)));
                } else if (c2.isStatic()){
                    if(c1_cares)
                        c1.onCollision(new CollisionInfo(c1, c2, true, MTV));
                    if(c2_cares)
                        c2.onCollision(new CollisionInfo(c2, c1, false, MTV.smult(-1)));
                } else {
                    if(c1_cares)
                        c1.onCollision(new CollisionInfo(c1, c2, true, new Vec2d(MTV.x/2,MTV.y/2)));
                    if(c2_cares)
                        c2.onCollision(new CollisionInfo(c2, c1, false, new Vec2d(-MTV.x/2,-MTV.y/2)));
                }

            }
        }

        //Perform Ray Collisions.
        for(int i = 0; i < this.rays.size(); i++){
            RayComponent ray = this.rays.get(i);
            if(ray == null || ray.isDisabled()) continue; //something was deleted so we shouldn't do collision for it
            Double dist = Double.MAX_VALUE;
            for(int j = 0; j < this.components.size(); j++){
                CollisionComponent c = (CollisionComponent) components.get(j);
                if(c == null || c.isDisabled()) continue; //something was deleted so we shouldn't do collision for it

                if(!ray.caresAboutCollision(c.getGameObject())) return;

                double hit = ray.collide(c);
                if(hit == -1) continue;
                if( hit < dist) {
                    dist = hit;
                    ray.colliding_with = c.getGameObject();
                }

            }
            ray.length = dist;
            if(dist == Double.MAX_VALUE){
                ray.colliding_with = null;
                ray.length = -1;
            }
        }
    }

    public void onLateTick(){
        //TODO
    }

    private void applyImpulses(Vec2d MTV, PhysicsComponent p1, PhysicsComponent p2){

        double COR = Math.sqrt(p1.getRestitution() * p2.getRestitution());
        Vec2d axis = MTV.normalize();
        if(Double.isNaN(axis.x) || Double.isNaN(axis.y)){
            new Exception().printStackTrace();
            System.exit(0);
        }
        double du = p2.getVelocity().dot(axis) - p1.getVelocity().dot(axis);
        if(p1.isStatic()){
            double I = -p2.getMass() * du * (1 + COR);
            p2.applyImpulse(axis.smult(I));
        } else if(p2.isStatic()){
            double I = p1.getMass() * du * (1 + COR);
            p1.applyImpulse(axis.smult(I));
        } else {
            double M = p1.getMass() * p2.getMass() / (p1.getMass() + p2.getMass());
            double I1 = M * du * (1 + COR);
            double I2 = -M * du * (1 + COR);
            p1.applyImpulse(axis.smult(I1));
            p2.applyImpulse(axis.smult(I2));
        }
    }

    public class CollisionInfo{
        public boolean isParent;
        public GameObject gameObjectSelf;
        public GameObject gameObjectOther;
        public CollisionComponent collisionComponentSelf;
        public CollisionComponent collisionComponentOther;
        public PhysicsComponent physicsComponent;
        public Vec2d MTV;
        public CollisionInfo(CollisionComponent s, CollisionComponent other, boolean isParent, Vec2d MTV){
            this.gameObjectSelf = s.getGameObject();
            this.gameObjectOther = other.getGameObject();
            this.collisionComponentSelf = s;
            this.collisionComponentOther = other;
            this.isParent = isParent;
            this.MTV = MTV;
        }
        public CollisionInfo(PhysicsComponent other, boolean isParent, Vec2d MTV){
            this.gameObjectOther = other.getGameObject();
            this.physicsComponent = other;
            this.isParent = isParent;
            this.MTV = MTV;
        }
    }

    public static class CollisionMask{
        public static final int NUM_LAYERS = 8;
        public static final int ALL = (1<<NUM_LAYERS)-1;
        public static final int NONE = 0;
        public static final int layer0 = 1;
        public static final int layer1 = 2;
        public static final int layer2 = 4;
        public static final int layer3 = 8;
        public static final int layer4 = 16;
        public static final int layer5 = 32;
        public static final int layer6 = 64;
        public static final int layer7 = 128;

        public static boolean hasLayer(int mask, int layer){
            return 0 != (mask & (1<<layer));
        }
    }

}
