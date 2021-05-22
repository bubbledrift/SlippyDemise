package engine.game.components;

import engine.UIToolKit.UIViewport;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


public class CameraComponent extends Component{



    private int viewport_id;
    private Vec2d offset;
    private UIViewport viewport;

    private Vec2d horizontalRange;
    private Vec2d verticalRange;

    private boolean rangeDisabled = false;

    //TODO add smoothness
    //TODO add option for level bounds (camera cant go past certain point but player can).

    public CameraComponent(int viewport_id, Vec2d offset) {
        super();
        this.viewport_id = viewport_id;
        this.offset = offset;
    }

    public CameraComponent(int viewport_id, Vec2d offset, Vec2d horizontalRange, Vec2d verticalRange) {
        super();
        this.viewport_id = viewport_id;
        this.offset = offset;
        this.horizontalRange = horizontalRange;
        this.verticalRange = verticalRange;
    }

    public void enableRange(){
        this.rangeDisabled = false;
    }

    public void disableRange(){
        this.rangeDisabled = true;
    }

    public void setHorizontalRange(Vec2d horizontalRange){
        this.horizontalRange = horizontalRange;
    }

    public void setVerticalRange(Vec2d verticalRange){
        this.verticalRange = verticalRange;
    }

    @Override
    public void onTick(long nanosSincePreviousTick){
        if(this.viewport == null){
            this.viewport = this.gameObject.gameWorld.getViewport(viewport_id);
        }
    }

    @Override
    public void onLateTick(){
        if(this.viewport == null){
            System.err.println("Viewport not found. Maybe you forgot to link the viewport with the gameworld.");
            return;
        }
        Vec2d pos = this.gameObject.getTransform().position;
        Vec2d view_size = this.viewport.getGameWorldViewSize();

        double camera_x = pos.x + this.offset.x/2;
        double camera_y = pos.y + this.offset.y/2;
        if(horizontalRange != null && !this.rangeDisabled)
            camera_x = Math.max(this.horizontalRange.x+view_size.x/2, Math.min(this.horizontalRange.y-view_size.x/2,camera_x));
        if(verticalRange != null && !this.rangeDisabled)
            camera_y = Math.max(this.verticalRange.x+view_size.y/2, Math.min(this.verticalRange.y-view_size.y/2,camera_y));
        this.viewport.setGamePosition(new Vec2d(camera_x, camera_y));
    };

    @Override
    public int getSystemFlags() {
        return SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "CameraComponent";
    }


    //TODO need to load and save boundary constraints
    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("viewport_id", Integer.toString(viewport_id));
        component.setAttribute("offset", this.offset.toString());
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        int viewport_id = Integer.parseInt(attr.getNamedItem("viewport_id").getNodeValue());
        Vec2d offset = Vec2d.fromString(attr.getNamedItem("offset").getNodeValue());
        CameraComponent c = new CameraComponent(viewport_id, offset);
        return c;
    }
}
