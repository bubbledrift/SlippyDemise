package engine.game.components;

import engine.game.GameObject;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class DraggableComponent extends Component{

    private Vec2d holdLocation = new Vec2d(0,0);
    private Boolean dragging = false;

    public DraggableComponent() {
        super();
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "DraggableComponent";
    }

    @Override
    public void onTick(long nanosSincePreviousTick){
        Vec2d mousePos = this.gameObject.gameWorld.getMousePosition();
        Vec2d objPos = this.gameObject.getTransform().position;
        Vec2d objSize = this.gameObject.getTransform().size;

        if(this.gameObject.gameWorld.getMouseDown()){
            if(!this.dragging){

                if(mousePos.x < objPos.x || objPos.x + objSize.x < mousePos.x) return;
                if(mousePos.y < objPos.y || objPos.y + objSize.y < mousePos.y) return;

                this.holdLocation = new Vec2d(mousePos.x - objPos.x, mousePos.y - objPos.y);
                this.dragging = true;
            } else {
                this.gameObject.getTransform().position =
                        new Vec2d(mousePos.x - this.holdLocation.x, mousePos.y - this.holdLocation.y);
            }
        } else {
            this.dragging = false;
        }
    };

    @Override
    public void onLateTick(){};


    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("holdLocation", holdLocation.toString());
        component.setAttribute("dragging", Boolean.toString(dragging));
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        Vec2d holdLocation = Vec2d.fromString(attr.getNamedItem("holdLocation").getNodeValue());
        boolean dragging = Boolean.parseBoolean(attr.getNamedItem("dragging").getNodeValue());
        DraggableComponent c = new DraggableComponent();
        c.holdLocation = holdLocation;
        c.dragging = dragging;
        return c;
    }
}
