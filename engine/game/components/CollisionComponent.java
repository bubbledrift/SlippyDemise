package engine.game.components;

import engine.game.DebugFlags;
import engine.game.GameObject;
import engine.game.Utils;
import engine.game.collisionShapes.AABShape;
import engine.game.collisionShapes.CircleShape;
import engine.game.collisionShapes.PolygonShape;
import engine.game.collisionShapes.Shape;
import engine.game.components.animation.AnimationField;
import engine.game.systems.CollisionSystem;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Component To detect collisions and trigger response
 */
public class CollisionComponent extends Component{

    public interface OnCollisionFunction{
        void onCollision(CollisionSystem.CollisionInfo collisionInfo);
    }

    protected OnCollisionFunction onCollisionFunction;

    public AnimationField<Vec2d> position;
    protected Shape shape;

    protected int collisionLayer = 0;
    protected int collisionMask = 0;

    protected boolean isStatic = false; //should never move do to collisions
    protected boolean isSolid = true; //if false, passes through other objects but, does get full collision notification.

    public boolean hasPhysics = false;


    public CollisionComponent(Shape shape, boolean isStatic, boolean isSolid,
                              int collisionLayer, int collisionMask) {
        this.position = new AnimationField<Vec2d>(new Vec2d(0,0));
        this.shape = shape;
        this.isStatic = isStatic;
        this.isSolid = isSolid;
        this.collisionLayer = collisionLayer;
        this.collisionMask = collisionMask;
    }

    public CollisionComponent(Vec2d position, Shape shape, boolean isStatic, boolean isSolid,
                              int collisionLayer, int collisionMask) {
        this.position = new AnimationField<Vec2d>(position);
        this.shape = shape;
        this.isStatic = isStatic;
        this.isSolid = isSolid;
        this.collisionLayer = collisionLayer;
        this.collisionMask = collisionMask;
    }

    public void linkCollisionCallback(OnCollisionFunction onCollisionFunction){
        this.onCollisionFunction = onCollisionFunction;
    }

    /**
     * Collides with other collision component and returns proper MTV
     * @param c
     * @return
     */
    public Vec2d collide(CollisionComponent c){
        //need to set parent positions for shapes to collide properly.
        this.shape.parentPosition = this.gameObject.getTransform().position.plus(this.position.value);
        c.shape.parentPosition = c.gameObject.getTransform().position.plus(c.position.value);
        return this.shape.collides(c.shape);
    }


    /**
     * Take s collision info and performs collision.
     * @param collisionInfo
     */
    public void onCollision(CollisionSystem.CollisionInfo collisionInfo){
        if(!this.isStatic && this.isSolid) {
            Vec2d pos = this.gameObject.getTransform().position;
            this.gameObject.getTransform().position = pos.plus(collisionInfo.MTV);
        }
        if(this.onCollisionFunction == null) return;
        this.onCollisionFunction.onCollision(collisionInfo);

    }

    public boolean caresAboutCollision(GameObject g){
        return true; //TODO bad workaround for handling collision. Maybe something cleaner?
    }

    public Shape getShape(){
        return this.shape;
    }

    public boolean isStatic(){
        return this.isStatic;
    }

    public boolean isSolid(){
        return this.isSolid;
    }

    public int getCollisionLayer(){return this.collisionLayer;}

    public int getCollisionMask(){return this.collisionMask;}

    public void setCollisionLayer(int collisionLayer){this.collisionLayer = collisionLayer;}

    public void setCollisionMask(int collisionMask){this.collisionMask = collisionMask;}

    @Override
    public int getSystemFlags() {
        if(DebugFlags.COLLISION_DEBUG){
            return SystemFlag.CollisionSystem | SystemFlag.RenderSystem;
        }
        return SystemFlag.CollisionSystem;
    }

    @Override
    public String getTag() {
        return "CollisionComponent";
    }

    @Override
    public void onLateDraw(GraphicsContext g){
        Vec2d pos = this.gameObject.getTransform().position.plus(this.position.value);
        Color color = this.isSolid ? Color.RED : Color.BLUE;
        if(this.shape instanceof AABShape){
            g.setStroke(color);
            g.setLineWidth(.05);
            g.strokeRect(pos.x + ((AABShape) this.shape).getRelativePosition().x,
                    pos.y + ((AABShape) this.shape).getRelativePosition().y,
                    ((AABShape) this.shape).getSize().x,((AABShape) this.shape).getSize().y);
        } else if(this.shape instanceof CircleShape){
            g.setStroke(color);
            g.setLineWidth(.05);
            double R = ((CircleShape) this.shape).getRadius();
            g.strokeOval(pos.x + ((CircleShape) this.shape).getRelativeCenter().x - R,
                    pos.y + ((CircleShape) this.shape).getRelativeCenter().y - R, 2*R, 2*R);
        } else if(this.shape instanceof PolygonShape){
            g.setStroke(color);
            g.setLineWidth(.05);
            PolygonShape poly = (PolygonShape)this.shape;
            double[] xpoints = new double[poly.getNumPoints()];
            double[] ypoints = new double[poly.getNumPoints()];
            for(int i = 0; i < poly.getNumPoints(); i++){
                Vec2d p = poly.getRelativePoints()[i].plus(pos);
                xpoints[i] = p.x;
                ypoints[i] = p.y;
            }
            g.strokePolygon(xpoints, ypoints, poly.getNumPoints());
        }

    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("collisionLayer", Integer.toString(collisionLayer));
        component.setAttribute("collisionMask", Integer.toString(collisionMask));
        component.setAttribute("isStatic", Boolean.toString(isStatic));
        component.setAttribute("isSolid", Boolean.toString(isSolid));
        component.setAttribute("hasPhysics", Boolean.toString(hasPhysics));
        component.appendChild(shape.getXML(doc));
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        Node child = n.getChildNodes().item(0);
        Shape shape = Shape.loadFromXML((Element)child);

        int collisionLayer = Integer.parseInt(attr.getNamedItem("collisionLayer").getNodeValue());
        int collisionMask = Integer.parseInt(attr.getNamedItem("collisionMask").getNodeValue());
        boolean isStatic = Boolean.parseBoolean(attr.getNamedItem("isStatic").getNodeValue());
        boolean isSolid = Boolean.parseBoolean(attr.getNamedItem("isSolid").getNodeValue());
        boolean hasPhysics = Boolean.parseBoolean(attr.getNamedItem("hasPhysics").getNodeValue());
        CollisionComponent c = new CollisionComponent(shape, isStatic, isSolid, collisionLayer, collisionMask);
        c.hasPhysics = hasPhysics;
        c.NOT_FULLY_LOADED();
        return c;
    }
}
