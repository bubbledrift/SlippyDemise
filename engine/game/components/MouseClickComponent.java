package engine.game.components;

import engine.game.GameObject;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class MouseClickComponent extends Component{

    public interface OnClickedFunction{
        void onClick(GameObject gameObject, Vec2d e);
    }

    private OnClickedFunction onClickedFunction;

    public MouseClickComponent() {
        super();
    }

    public void linkClickCallback(OnClickedFunction onClick){
        this.onClickedFunction = onClick;
    }

    @Override
    public void onMouseClicked(Vec2d e) {
        super.onMouseClicked(e);
        if(this.onClickedFunction != null)
            this.onClickedFunction.onClick(this.gameObject, e);
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.MouseEventSystem;
    }

    @Override
    public String getTag() {
        return "MouseClickComponent";
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        MouseClickComponent c = new MouseClickComponent();
        c.NOT_FULLY_LOADED();
        return c;
    }
}
