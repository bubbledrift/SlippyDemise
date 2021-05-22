package engine.game.components;

import engine.game.GameObject;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


public class ConstantForceComponent extends Component{

    private PhysicsComponent physicsComponent = null;
    private Vec2d force;

    public ConstantForceComponent(Vec2d force) {
        super();
        this.force = force;
    }

    public ConstantForceComponent(Vec2d force, PhysicsComponent physicsComponent) {
        super();
        this.physicsComponent = physicsComponent;
        this.force = force;
    }

    @Override
    public void onTick(long nanosSincePreviousTick){
        if(this.physicsComponent == null) {
            Component c = this.gameObject.getComponent("PhysicsComponent");
            if(c != null) {
                this.physicsComponent = (PhysicsComponent)c;
            } else {
                return;
            }
        }
        this.physicsComponent.applyForce(force);
    }

    @Override
    public void onLateTick(){}

    @Override
    public int getSystemFlags() {
        return SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "ConstantForceComponent";
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("force", force.toString());
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        Vec2d force = Vec2d.fromString(attr.getNamedItem("force").getNodeValue());
        ConstantForceComponent c = new ConstantForceComponent(force);
        return c;
    }
}
