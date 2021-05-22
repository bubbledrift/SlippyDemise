package engine.game.components;

import engine.game.GameObject;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class RectComponent extends Component{

    private Color color;

    public RectComponent(Color color) {
        super();
        this.color = color;
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.RenderSystem;
    }

    @Override
    public String getTag() {
        return "RectComponent";
    }

    @Override
    public void onDraw(GraphicsContext g){
        Vec2d pos = this.gameObject.getTransform().position;
        Vec2d size = this.gameObject.getTransform().size;
        g.setStroke(this.color);
        g.setLineWidth(.1);
        g.strokeRect(pos.x,pos.y,size.x,size.y);
    };

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("color", color.toString());
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        //TODO essentially never use this. Need to parse color properly. For now default to red
        //Color value = Color.parseColor(attr.getNamedItem("value").getNodeValue());
        RectComponent c = new RectComponent(Color.RED);
        return c;
    }
}
