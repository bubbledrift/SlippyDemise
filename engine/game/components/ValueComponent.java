package engine.game.components;

import engine.game.systems.SystemFlag;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class ValueComponent extends Component{

    public double value;

    public ValueComponent(double value) {
        super();
        this.value = value;
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.None;
    }

    @Override
    public String getTag() {
        return "ValueComponent";
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("value", Double.toString(value));
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        Double value = Double.parseDouble(attr.getNamedItem("value").getNodeValue());
        ValueComponent c = new ValueComponent(value);
        return c;
    }
}
