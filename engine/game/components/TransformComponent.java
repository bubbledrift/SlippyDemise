package engine.game.components;

import engine.game.GameObject;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class TransformComponent extends Component {
    public Vec2d position;
    public Vec2d size;

    public TransformComponent(){
        super();
        this.position = new Vec2d(0,0);
        this.size = new Vec2d(0,0);
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.None;
    }

    @Override
    public String getTag() {
        return "TransformComponent";
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("position", position.toString());
        component.setAttribute("size", size.toString());
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        TransformComponent c = new TransformComponent();
        c.position = Vec2d.fromString(attr.getNamedItem("position").getNodeValue());
        c.size = Vec2d.fromString(attr.getNamedItem("size").getNodeValue());
        return c;
    }

}
