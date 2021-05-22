package engine.game.components;

import engine.game.GameObject;
import engine.game.collisionShapes.Shape;
import engine.game.systems.CollisionSystem;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Vector;

public class PhysicsComponent extends CollisionComponent{

    protected double mass = 1;

    protected Vec2d velocity = new Vec2d(0,0);
    protected Vec2d impulse = new Vec2d(0,0);
    protected Vec2d force = new Vec2d(0,0);

    protected Vec2d deltaV = new Vec2d(0,0);

    double restitution = 1;

    public PhysicsComponent(Shape shape, double mass, double restitution, int collisionLayer, int collisionMask) {
        super(shape, false, true, collisionLayer, collisionMask);
        this.mass = mass;
        this.restitution = restitution;
        hasPhysics = true;
        assert(mass != 0);
    }

    public PhysicsComponent(Shape shape, double mass, double restitution, boolean isStatic,
                            int collisionLayer, int collisionMask) {
        super(shape, isStatic, true, collisionLayer, collisionMask);
        this.mass = mass;
        this.restitution = restitution;
        hasPhysics = true;
        assert(mass != 0);
    }

    public PhysicsComponent(Vec2d position, Shape shape, double mass, double restitution, int collisionLayer, int collisionMask) {
        super(position, shape, false, true, collisionLayer, collisionMask);
        this.mass = mass;
        this.restitution = restitution;
        hasPhysics = true;
        assert(mass != 0);
    }

    public PhysicsComponent(Vec2d position, Shape shape, double mass, double restitution, boolean isStatic,
                            int collisionLayer, int collisionMask) {
        super(position, shape, isStatic, true, collisionLayer, collisionMask);
        this.mass = mass;
        this.restitution = restitution;
        hasPhysics = true;
        assert(mass != 0);
    }

    public void linkCollisionCallback(OnCollisionFunction onCollisionFunction){
        this.onCollisionFunction = onCollisionFunction;
    }


    public void applyForce(Vec2d f){
        this.force = this.force.plus(f);
    }

    public void applyImpulse(Vec2d i){
        if(Double.isNaN(i.x) || Double.isNaN(i.y)){
            new Exception().printStackTrace();
            System.exit(0);
        }
        this.impulse = this.impulse.plus(i);
    }

    public void applyDeltaV(Vec2d v){
        this.deltaV = this.deltaV.plus(v);
    }

    @Override
    public void onTick(long nanosSincePreviousTick){
        double t = nanosSincePreviousTick / 1000000000.0;
        if(!this.isStatic()) {
            this.velocity = this.velocity.plus(this.force.smult(t / this.mass));
            this.velocity = this.velocity.plus(this.impulse.smult(1 / this.mass));
            this.gameObject.getTransform().position = this.gameObject.getTransform().position.plus(this.velocity.smult(t));
            this.gameObject.getTransform().position = this.gameObject.getTransform().position.plus(this.deltaV);
            this.force = new Vec2d(0, 0);
            this.impulse = new Vec2d(0, 0);
            this.deltaV = new Vec2d(0, 0);
        }
    }

    @Override
    public void onLateTick(){

    };

    @Override
    public void onCollision(CollisionSystem.CollisionInfo collisionInfo){
        if(!this.isStatic && this.isSolid) {
            Vec2d pos = this.gameObject.getTransform().position;
            this.gameObject.getTransform().position = this.gameObject.getTransform().position.plus(collisionInfo.MTV);
        }
        if(this.onCollisionFunction == null) return;
        this.onCollisionFunction.onCollision(collisionInfo);
    }

    public Vec2d getVelocity(){
        return this.velocity.plus(this.deltaV);
    }

    public void setVelocity(Vec2d v){
        this.velocity = v;
    }


    public double getRestitution(){
        return this.restitution;
    }

    public double getMass(){
        return this.mass;
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.CollisionSystem | SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "PhysicsComponent";
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("collisionLayer", Integer.toString(collisionLayer));
        component.setAttribute("collisionMask", Integer.toString(collisionMask));
        component.setAttribute("isStatic", Boolean.toString(isStatic));
        component.setAttribute("isSolid", Boolean.toString(isSolid));
        component.setAttribute("hasPhysics", Boolean.toString(hasPhysics));
        component.setAttribute("mass", Double.toString(mass));
        component.setAttribute("velocity", velocity.toString());
        component.setAttribute("impulse", impulse.toString());
        component.setAttribute("force", force.toString());
        component.setAttribute("deltaV", deltaV.toString());
        component.setAttribute("restitution", Double.toString(restitution));
        component.appendChild(shape.getXML(doc));
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        Node child = n.getChildNodes().item(1);
        Shape shape = Shape.loadFromXML((Element)child);

        int collisionLayer = Integer.parseInt(attr.getNamedItem("collisionLayer").getNodeValue());
        int collisionMask = Integer.parseInt(attr.getNamedItem("collisionMask").getNodeValue());
        boolean isStatic = Boolean.parseBoolean(attr.getNamedItem("isStatic").getNodeValue());
        boolean isSolid = Boolean.parseBoolean(attr.getNamedItem("isSolid").getNodeValue());
        boolean hasPhysics = Boolean.parseBoolean(attr.getNamedItem("hasPhysics").getNodeValue());
        Double mass = Double.parseDouble(attr.getNamedItem("mass").getNodeValue());
        Vec2d velocity = Vec2d.fromString(attr.getNamedItem("velocity").getNodeValue());
        Vec2d impulse = Vec2d.fromString(attr.getNamedItem("impulse").getNodeValue());
        Vec2d force = Vec2d.fromString(attr.getNamedItem("force").getNodeValue());
        Vec2d deltaV = Vec2d.fromString(attr.getNamedItem("deltaV").getNodeValue());
        Double restitution = Double.parseDouble(attr.getNamedItem("restitution").getNodeValue());
        PhysicsComponent c = new PhysicsComponent(shape, mass, restitution, isStatic, collisionLayer, collisionMask);
        c.isSolid = isSolid;
        c.hasPhysics = hasPhysics;
        c.velocity = velocity;
        c.impulse = impulse;
        c.force = force;
        c.deltaV = deltaV;
        c.NOT_FULLY_LOADED();
        return c;
    }
}
