package engine.game.components;

import engine.game.GameObject;
import engine.game.SpriteLoader;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class SpriteComponent extends Component{

    protected Image sprite;
    protected String spriteSheetPath;

    protected Vec2d position; //relative position to game object
    protected Vec2d size; //size of sprite

    protected Vec2d offset; //offset on sprite sheet
    protected Vec2d cropSize; //size of region from which to draw

    protected boolean horizontalFlip = false;


    public SpriteComponent(String spritePath, Vec2d position, Vec2d size) {
        super();
        this.sprite = SpriteLoader.loadImage(spritePath);
        this.spriteSheetPath = spritePath;
        this.position = position;
        this.size = size;

        this.offset = new Vec2d(0,0);
        this.cropSize = new Vec2d(this.sprite.getWidth(),this.sprite.getHeight());
    }

    public SpriteComponent(String spritePath,
                           Vec2d position, Vec2d size, Vec2d offset, Vec2d cropSize) {
        super();
        this.sprite = SpriteLoader.loadImage(spritePath);
        this.spriteSheetPath = spritePath;
        this.position = position;
        this.size = size;

        this.offset = offset;
        this.cropSize = cropSize;
    }

    public void resetSprite(String spritePath, Vec2d position, Vec2d size, Vec2d offset, Vec2d cropSize){
        this.sprite = SpriteLoader.loadImage(spritePath);
        this.spriteSheetPath = spritePath;
        this.position = position;
        this.size = size;

        this.offset = offset;
        this.cropSize = cropSize;
    }

    public void flipHorizontally(){
        this.horizontalFlip = !this.horizontalFlip;
    }

    @Override
    public void onDraw(GraphicsContext g){

        Vec2d pos = this.gameObject.getTransform().position;


        if(this.horizontalFlip){
            g.drawImage(this.sprite, this.offset.x, this.offset.y, this.cropSize.x, this.cropSize.y,
                    pos.x + this.position.x + this.size.x, pos.y + this.position.y, -this.size.x, this.size.y);
        } else {
            g.drawImage(this.sprite, this.offset.x, this.offset.y, this.cropSize.x, this.cropSize.y,
                    pos.x + this.position.x, pos.y + this.position.y, this.size.x, this.size.y);
        }
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.RenderSystem;
    }

    @Override
    public String getTag() {
        return "SpriteComponent";
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("spriteSheetPath", spriteSheetPath);
        component.setAttribute("position", position.toString());
        component.setAttribute("size", size.toString());
        component.setAttribute("offset", offset.toString());
        component.setAttribute("cropSize", cropSize.toString());
        component.setAttribute("horizontalFlip", Boolean.toString(horizontalFlip));
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        String path = attr.getNamedItem("spriteSheetPath").getNodeValue();
        Vec2d position = Vec2d.fromString(attr.getNamedItem("position").getNodeValue());
        Vec2d size = Vec2d.fromString(attr.getNamedItem("size").getNodeValue());

        Vec2d offset = Vec2d.fromString(attr.getNamedItem("offset").getNodeValue());
        Vec2d cropSize = Vec2d.fromString(attr.getNamedItem("cropSize").getNodeValue());

        Boolean horizontalFlip = Boolean.parseBoolean(attr.getNamedItem("horizontalFlip").getNodeValue());
        SpriteComponent c = new SpriteComponent(path, position, size, offset, cropSize);
        c.horizontalFlip = horizontalFlip;
        return c;
    }
}
