package engine.game.components;

import engine.game.GameObject;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class VelocityComponent extends Component{

    public Vec2d velocity;
    public double friction;

    public VelocityComponent(Vec2d velocity) {
        super();
        this.velocity = velocity;
        friction = 1;
    }

    public VelocityComponent(Vec2d velocity, double friction) {
        super();
        this.velocity = velocity;
        this.friction = friction;
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "VelocityComponent";
    }

    @Override
    public void onTick(long nanosSincePreviousTick){
        double dt = nanosSincePreviousTick/1000000000.0;
        Vec2d position = this.gameObject.getTransform().position;
        this.gameObject.getTransform().position =
                new Vec2d(position.x + dt*this.velocity.x,position.y + dt*this.velocity.y);
        this.velocity = this.velocity.smult(this.friction);
    };

    @Override
    public void onLateTick(){};

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("velocity", velocity.toString());
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        VelocityComponent c = new VelocityComponent(Vec2d.fromString(attr.getNamedItem("velocity").getNodeValue()));
        return c;
    }
}
