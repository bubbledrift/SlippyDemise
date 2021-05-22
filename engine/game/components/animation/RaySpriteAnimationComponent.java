package engine.game.components.animation;

import engine.game.SpriteLoader;
import engine.game.components.Component;
import engine.game.components.RayComponent;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class RaySpriteAnimationComponent extends AnimationComponent {

    protected Image spriteSheet;
    protected String spriteSheetPath;

    protected Vec2d position, size;
    protected RayComponent rayComponent;

    //3 parts to sprite source, length (stretched), destination
    protected boolean src_image = true;
    protected Vec2d srcCropStart;
    protected Vec2d srcCropSize;

    protected boolean length_image = true;
    protected Vec2d lengthCropStart;
    protected Vec2d lengthCropSize;

    protected boolean dst_image = true;
    protected Vec2d dstCropStart;
    protected Vec2d dstCropSize;

    public RaySpriteAnimationComponent(RayComponent rayComponent, String spriteSheetPath, Vec2d position, Vec2d size,
                                       Vec2d srcCropStart, Vec2d srcCropSize, Vec2d lengthCropStart, Vec2d lengthCropSize,
                                       Vec2d dstCropStart, Vec2d dstCropSize, int frames, double frameDuration){
        super(frames, frameDuration);
        this.spriteSheet = SpriteLoader.loadImage(spriteSheetPath);
        this.spriteSheetPath = spriteSheetPath;

        this.position = position;
        this.size = size;
        this.rayComponent = rayComponent;

        if(srcCropStart == null || srcCropSize == null) src_image=false;
        this.srcCropStart = srcCropStart;
        this.srcCropSize = srcCropSize;

        if(lengthCropStart == null || lengthCropSize == null) length_image=false;
        this.lengthCropStart = lengthCropStart;
        this.lengthCropSize = lengthCropSize;

        if(dstCropStart == null || dstCropSize == null) dst_image=false;
        this.dstCropStart = dstCropStart;
        this.dstCropSize = dstCropSize;
    }

    public void resetAnimation(RayComponent rayComponent, String spriteSheetPath, Vec2d position, Vec2d size,
                               Vec2d srcCropStart, Vec2d srcCropSize, Vec2d lengthCropStart, Vec2d lengthCropSize,
                               Vec2d dstCropStart, Vec2d dstCropSize, int frames, double frameDuration){
        this.spriteSheet = SpriteLoader.loadImage(spriteSheetPath);
        this.spriteSheetPath = spriteSheetPath;

        this.position = position;
        this.size = size;
        this.rayComponent = rayComponent;

        if(srcCropStart == null || srcCropSize == null) src_image=false;
        this.srcCropStart = srcCropStart;
        this.srcCropSize = srcCropSize;

        if(lengthCropStart == null || lengthCropSize == null) length_image=false;
        this.lengthCropStart = lengthCropStart;
        this.lengthCropSize = lengthCropSize;

        if(dstCropStart == null || dstCropSize == null) dst_image=false;
        this.dstCropStart = dstCropStart;
        this.dstCropSize = dstCropSize;

    }

    public void setRayComponent(RayComponent rayComponent){
        this.rayComponent = rayComponent;
    }


    @Override
    public void onDraw(GraphicsContext g){
        if(this.rayComponent == null) return;
        double length = this.rayComponent.length;
        if(this.rayComponent.length == -1) length = 10000;

        Vec2d pos = this.gameObject.getTransform().position;

        g.save();
        g.translate(this.rayComponent.getRay().src.x,this.rayComponent.getRay().src.y); //center on y for proper rotation
        g.rotate(180*this.rayComponent.getRay().dir.angle()/Math.PI);

        if(this.length_image)
            g.drawImage(this.spriteSheet,
                    this.lengthCropStart.x + this.lengthCropSize.x * this.currentStep, this.lengthCropStart.y,
                    this.lengthCropSize.x, this.lengthCropSize.y, 0, -this.size.y/2,
                    length, this.size.y);

        if(this.src_image)
            g.drawImage(this.spriteSheet,
                    this.srcCropStart.x + this.srcCropSize.x * this.currentStep, this.srcCropStart.y,
                    this.srcCropSize.x, this.srcCropSize.y, 0, -this.size.y/2,
                    this.size.x, this.size.y);

        if(this.dst_image && rayComponent.length != -1)
            g.drawImage(this.spriteSheet,
                    this.dstCropStart.x + this.dstCropSize.x * this.currentStep, this.dstCropStart.y,
                    this.dstCropSize.x, this.dstCropSize.y, length - this.size.x, -this.size.y/2,
                    this.size.x, this.size.y);

        g.restore();
    }


    @Override
    public int getSystemFlags() {
        return SystemFlag.RenderSystem | SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "RaySpriteAnimationComponent";
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("spriteSheetPath", spriteSheetPath);
        component.setAttribute("position", position.toString());
        component.setAttribute("size", size.toString());

        component.setAttribute("src_image", Boolean.toString(src_image));
        component.setAttribute("srcCropStart", srcCropStart.toString());
        component.setAttribute("srcCropSize", srcCropSize.toString());

        component.setAttribute("length_image", Boolean.toString(length_image));
        component.setAttribute("lengthCropStart", lengthCropStart.toString());
        component.setAttribute("lengthCropSize", lengthCropSize.toString());

        component.setAttribute("dst_image", Boolean.toString(dst_image));
        component.setAttribute("dstCropStart", dstCropStart.toString());
        component.setAttribute("dstCropSize", dstCropSize.toString());

        component.setAttribute("frames", Integer.toString(this.steps));
        component.setAttribute("frameDuration", Double.toString(frameDuration / 1000000000));
        component.setAttribute("currentTime", Long.toString(currentTime));
        component.setAttribute("currentFrame", Integer.toString(currentStep));
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        String path = attr.getNamedItem("spriteSheetPath").getNodeValue();
        Vec2d position = Vec2d.fromString(attr.getNamedItem("position").getNodeValue());
        Vec2d size = Vec2d.fromString(attr.getNamedItem("size").getNodeValue());

        boolean src_image = Boolean.parseBoolean(attr.getNamedItem("src_image").getNodeValue());
        Vec2d srcCropStart = Vec2d.fromString(attr.getNamedItem("srcCropStart").getNodeValue());
        Vec2d srcCropSize = Vec2d.fromString(attr.getNamedItem("srcCropSize").getNodeValue());

        boolean length_image = Boolean.parseBoolean(attr.getNamedItem("length_image").getNodeValue());
        Vec2d lengthCropStart = Vec2d.fromString(attr.getNamedItem("lengthCropStart").getNodeValue());
        Vec2d lengthCropSize = Vec2d.fromString(attr.getNamedItem("lengthCropSize").getNodeValue());

        boolean dst_image = Boolean.parseBoolean(attr.getNamedItem("dst_image").getNodeValue());
        Vec2d dstCropStart = Vec2d.fromString(attr.getNamedItem("dstCropStart").getNodeValue());
        Vec2d dstCropSize = Vec2d.fromString(attr.getNamedItem("dstCropSize").getNodeValue());

        int frames = Integer.parseInt(attr.getNamedItem("frames").getNodeValue());

        double frameDuration = Double.parseDouble(attr.getNamedItem("frameDuration").getNodeValue());
        long currentTime = Long.parseLong(attr.getNamedItem("currentTime").getNodeValue());
        int currentFrame = Integer.parseInt(attr.getNamedItem("currentFrame").getNodeValue());
        RaySpriteAnimationComponent c = new RaySpriteAnimationComponent(null, path, position, size,
                srcCropStart, srcCropSize, lengthCropStart, lengthCropSize, dstCropStart, dstCropSize, frames, frameDuration);
        c.src_image = src_image;
        c.length_image = length_image;
        c.dst_image = dst_image;
        c.currentTime = currentTime;
        c.currentStep = currentFrame;
        c.NOT_FULLY_LOADED();
        return c;
    }
}
