package engine.game.components;

import engine.game.GameObject;
import engine.game.collisionShapes.Ray;
import engine.game.collisionShapes.Shape;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class RayComponent extends Component{


    private Ray ray;

    public GameObject colliding_with = null;
    private boolean ignore_self = false;

    public double length = -1;

    public RayComponent(Ray ray, boolean ignore_self) {
        super();
        this.ray = ray;
        this.ignore_self = ignore_self;
    }

    /**
     * Collides with other collision component and returns proper MTV
     * @param c
     * @return
     */
    public Double collide(CollisionComponent c){
        if(this.ignore_self && c.gameObject == this.gameObject) return -1.0;
        //need to set parent positions for shapes to collide properly.
        this.ray.parentPosition = this.gameObject.getTransform().position;
        c.shape.parentPosition = c.gameObject.getTransform().position;
        return c.shape.collidesRay(this.ray);
    }

    public boolean caresAboutCollision(GameObject g){
        return true; //TODO bad workaround for handling collision. Maybe something cleaner?
    }

    public Ray getRay(){
        return this.ray;
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.CollisionSystem;
    }

    @Override
    public String getTag() {
        return "RayComponent";
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("length", Double.toString(length));
        component.setAttribute("ignore_self", Boolean.toString(ignore_self));
        component.appendChild(ray.getXML(doc));
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        Node child = n.getChildNodes().item(1);
        Ray ray = Ray.loadFromXML((Element)child);

        double length = Double.parseDouble(attr.getNamedItem("length").getNodeValue());
        boolean ignore_self = Boolean.parseBoolean(attr.getNamedItem("ignore_self").getNodeValue());
        RayComponent c = new RayComponent(ray, ignore_self);
        c.length = length;
        return c;
    }
}
