package engine.game.components;

import engine.game.GameObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class IDComponent extends Component{
    private String id;

    public IDComponent(String id) {
        super();
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    @Override
    public int getSystemFlags() {
        return 0;
    }

    @Override
    public String getTag() {
        return "IDComponent";
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("id", this.id);
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        IDComponent c = new IDComponent(attr.getNamedItem("id").getNodeValue());
        return c;
    }
}
