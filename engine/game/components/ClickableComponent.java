package engine.game.components;

import engine.game.GameObject;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class ClickableComponent extends Component{

    public interface OnClickedFunction{
        void onClick(GameObject g);
    }

    private OnClickedFunction onClickedFunction;

    private String className;
    private String classMethod;

    private boolean dragging = false;

    public ClickableComponent() {
        super();
    }

    public void linkClickCallback(OnClickedFunction onClickedFunction){
        this.onClickedFunction = onClickedFunction;
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "ClickableComponent";
    }

    @Override
    public void onTick(long nanosSincePreviousTick){
        Vec2d mousePos = this.gameObject.gameWorld.getMousePosition();
        Vec2d objPos = this.gameObject.getTransform().position;
        Vec2d objSize = this.gameObject.getTransform().size;

        if(mousePos.x < objPos.x || objPos.x + objSize.x < mousePos.x) return;
        if(mousePos.y < objPos.y || objPos.y + objSize.y < mousePos.y) return;

        if(this.gameObject.gameWorld.getMouseDown()){
            if(!this.dragging) {
                this.onClickedFunction.onClick(this.gameObject);
                this.dragging = true;
            }
        } else {
            this.dragging = false;
        }
    }

    @Override
    public void onLateTick(){};

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("onClickedFunctionClass", this.onClickedFunction.getClass().getName());
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        ClickableComponent c = new ClickableComponent();
        c.NOT_FULLY_LOADED();
        return c;
    }
}
