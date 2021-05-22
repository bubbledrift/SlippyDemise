package engine.game.collisionShapes;


import engine.support.Vec2d;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class Ray{
/*
IMPORTANT NOTE
Does not have parent position like shapes. Needs to be created with absolute world position.
Subject to change.
 */
    public Vec2d parentPosition;
    public Vec2d src;
    public Vec2d dir;

    public Ray(Vec2d src, Vec2d dir) {
        this.src = src;
        this.dir = dir;
    }

    public Vec2d getPosition(){
        //return actual position not position relative to parent component
        return new Vec2d(this.src.x + this.parentPosition.x, this.src.y + this.parentPosition.y);
    }

    public Element getXML(Document doc){
        Element ray = doc.createElement(this.getClass().getName());
        ray.setAttribute("src", this.src.toString());
        ray.setAttribute("dir", this.dir.toString());
        return ray;
    }

    public static Ray loadFromXML(Element n){
        NamedNodeMap attr = n.getAttributes();
        Vec2d src = Vec2d.fromString(attr.getNamedItem("src").getNodeValue());
        Vec2d dir = Vec2d.fromString(attr.getNamedItem("dir").getNodeValue());
        return new Ray(src,dir);
    }

}
