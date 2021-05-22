package engine.game.components.animation;

import engine.game.SpriteLoader;
import engine.game.components.Component;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GeneralSpriteAnimationComponent extends AnimationComponent {

    //TODO Frames should be fully custom (spacing and offset)
    //TODO handle multiple animation sequences

    //TODO handle animating other components (moving hitbox)

    protected Image spriteSheet;
    protected String spriteSheetPath;

    protected Vec2d[] position; //relative position to game object
    protected Vec2d[] size; //size of sprite

    protected Vec2d[] cropStart; //from where the crop starts on the sprite sheet
    protected Vec2d[] cropSize; //size of region from which to draw

    protected boolean[] horizontalFlip;


    public GeneralSpriteAnimationComponent(String spriteSheetPath, Vec2d position[], Vec2d size[],
                                           Vec2d[] cropStart, Vec2d[] cropSize, double frameDuration) {
        super(position.length, frameDuration);
        this.spriteSheet = SpriteLoader.loadImage(spriteSheetPath);
        this.spriteSheetPath = spriteSheetPath;
        this.position = position;
        this.size = size;

        this.cropStart = cropStart;
        this.cropSize = cropSize;
        this.frameDuration = frameDuration * 1000000000;

        horizontalFlip = new boolean[this.position.length];
        assert(this.position.length == this.size.length);
        assert(this.position.length == this.cropStart.length);
        assert(this.position.length == this.cropSize.length);
    }

    public void resetAnimation(String spriteSheetPath, Vec2d position[], Vec2d size[],
                               Vec2d cropStart[], Vec2d cropSize[], double frameDuration){

        this.spriteSheet = SpriteLoader.loadImage(spriteSheetPath);
        this.spriteSheetPath = spriteSheetPath;
        this.position = position;
        this.size = size;


        this.cropStart = cropStart;
        this.cropSize = cropSize;


        horizontalFlip = new boolean[this.position.length];
        assert(this.position.length == this.size.length);
        assert(this.position.length == this.cropStart.length);
        assert(this.position.length == this.cropSize.length);

    }

    public void setHorizontalFlip(boolean[] horizontalFlip){
        this.horizontalFlip = horizontalFlip;
    }

    @Override
    public void onDraw(GraphicsContext g){
        Vec2d pos = this.gameObject.getTransform().position;
        Vec2d size = this.gameObject.getTransform().size;

        if(this.horizontalFlip[this.currentStep]) {
            g.drawImage(this.spriteSheet,
                    this.cropStart[currentStep].x,this.cropStart[currentStep].y,
                    this.cropSize[currentStep].x, this.cropSize[currentStep].y,
                    pos.x + this.position[currentStep].x + this.size[currentStep].x, pos.y + this.position[currentStep].y,
                    -this.size[currentStep].x, this.size[currentStep].y);
        } else {
            g.drawImage(this.spriteSheet,
                    this.cropStart[currentStep].x, this.cropStart[currentStep].y,
                    this.cropSize[currentStep].x, this.cropSize[currentStep].y,
                    pos.x + this.position[currentStep].x, pos.y + this.position[currentStep].y,
                    this.size[currentStep].x, this.size[currentStep].y);
        }
    }

    @Override
    public String getTag() {
        return "SpriteAnimationComponent";
    }

    //TODO proper saving and loading
    // not being used yet so no need.

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
//        component.setAttribute("spriteSheetPath", spriteSheetPath);
//        component.setAttribute("position", position.toString());
//        component.setAttribute("size", size.toString());
//
//        component.setAttribute("frames", Integer.toString(frames));
//        component.setAttribute("cropStart", cropStart.toString());
//        component.setAttribute("cropSize", cropSize.toString());
//
//        component.setAttribute("frameDuration", Double.toString(frameDuration / 1000000000));
//        component.setAttribute("currentTime", Long.toString(currentTime));
//        component.setAttribute("currentFrame", Integer.toString(currentFrame));
        return component;
    }

    public static Component loadFromXML(Element n) {
        return null;
//        NamedNodeMap attr = n.getAttributes();
//        String path = attr.getNamedItem("spriteSheetPath").getNodeValue();
//        Vec2d position = Vec2d.fromString(attr.getNamedItem("position").getNodeValue());
//        Vec2d size = Vec2d.fromString(attr.getNamedItem("size").getNodeValue());
//
//        int frames = Integer.parseInt(attr.getNamedItem("frames").getNodeValue());
//        Vec2d cropSize = Vec2d.fromString(attr.getNamedItem("cropSize").getNodeValue());
//        Vec2d cropStart = Vec2d.fromString(attr.getNamedItem("cropStart").getNodeValue());
//        Vec2d cropShift = Vec2d.fromString(attr.getNamedItem("cropShift").getNodeValue());
//
//        double frameDuration = Double.parseDouble(attr.getNamedItem("frameDuration").getNodeValue());
//        long currentTime = Long.parseLong(attr.getNamedItem("currentTime").getNodeValue());
//        int currentFrame = Integer.parseInt(attr.getNamedItem("currentFrame").getNodeValue());
//
//        GeneralSpriteAnimationComponent c = new GeneralSpriteAnimationComponent(path, position, size, frames, cropStart, cropSize, cropShift, frameDuration);
//        c.currentTime = currentTime;
//        c.currentFrame = currentFrame;
//        return c;
    }
}
