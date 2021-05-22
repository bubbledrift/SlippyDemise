package engine.UIToolKit;

import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class UIImage extends UIElement{

    private Image image;

    protected Vec2d offset; //offset on sprite sheet
    protected Vec2d cropSize; //size of region from which to draw

    public UIImage(Image image, Vec2d position, Vec2d size) {
        super(position, size);
        this.image = image;
        this.size = size;
        this.offset = new Vec2d(0,0);
        this.cropSize = new Vec2d(this.image.getWidth(), this.image.getHeight());
        this.size = size;
    }

    public UIImage(Image image, Vec2d position, Vec2d size, Vec2d offset, Vec2d cropSize){
        super(position, size);
        this.image = image;
        this.position = position;
        this.size = size;
        this.offset = offset;
        this.cropSize = cropSize;
    }

    @Override
    public void onDraw(GraphicsContext g) {
        Vec2d pos = this.getOffset();
        g.drawImage(this.image, this.offset.x, this.offset.y, this.cropSize.x, this.cropSize.y,
                pos.x, pos.y, this.size.x, this.size.y);
        super.onDraw(g);
    }
}
