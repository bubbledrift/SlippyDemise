package engine.game.components;

import engine.UIToolKit.UIViewport;
import engine.game.SpriteLoader;
import engine.game.systems.SystemFlag;
import engine.support.FontMetrics;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class TextBoxComponent extends Component {

    /*

    //TODO instead tile edge and center images with symmetric cropping if needed
    Rectangle with text and optional images for interior, corners, and borders.
    Corners are rendered as is. Edge image stretched to match text box size along one axis.
    The interior image is stretched to match text box. Interior image is exactly the space for the text
    to be written.

    Text can be dynamically changed.
    Box can be opened and closed.
     */

    public enum TextAlignment{
        CENTER,
        LEFT,
        TOP_LEFT,
        TOP,
        TOP_RIGHT,
        RIGHT,
        BOTTOM_RIGHT,
        BOTTOM,
        BOTTOM_LEFT
    }

    private class SpriteHolder{
        public Image image;
        public String imagePath;
        public Vec2d cropStart;
        public Vec2d cropSize;
        public Vec2d drawSize;

        public SpriteHolder(String imagePath, Vec2d cropStart, Vec2d cropSize, Vec2d drawSize){
            this.image = SpriteLoader.loadImage(imagePath);
            this.imagePath = imagePath;

            this.cropStart = cropStart;
            this.cropSize = cropSize;
            this.drawSize = drawSize;
        }
    }

    private int viewport_id;
    private UIViewport viewport;

    private boolean relativeToViewport = false;
    private Vec2d position; //relative position to game object
    private boolean fitToText;
    private Vec2d textSize; //size of textbox interior (based on text)
    private Vec2d size; //Actual size of textbox (based on scaled text size)

    private SpriteHolder corner, edgeHorizontal, edgeVertical, center;

    private String text;
    private double margin;

    private enum State {OPENING, CLOSING, REWRITING}
    private State state = State.CLOSING;
    private double openTime, closeTime, rewriteTime;

    private FontMetrics fontMetrics;
    private Color textColor;
    private Font font;
    private TextAlignment alignment;

    private double currentTime;

    public TextBoxComponent(int viewport_id, boolean relativeToViewport, Vec2d position, Vec2d size,
                            String text, double margin, Color textColor, Font font, TextAlignment alignment,
                            double openTime, double closeTime, double rewriteTime){
        this.viewport_id = viewport_id;

        this.text = text;
        this.textColor = textColor == null ? Color.BLACK : textColor;
        this.font = font == null ? Font.getDefault() : font;
        this.fontMetrics = new FontMetrics(this.text, this.font);
        this.alignment = alignment;

        this.relativeToViewport = relativeToViewport;
        this.position = position;
        if(size == null){
            this.fitToText = true;
            this.textSize = new Vec2d(fontMetrics.width + margin * 2, fontMetrics.height + margin * 2);

        } else {
            this.fitToText = false;
            this.size = size;
        }
        this.margin = margin;

        this.openTime = openTime * 1000000000;
        this.closeTime = closeTime * 1000000000;
        this.rewriteTime = rewriteTime * 1000000000;
    }

    public void setCenterImage(String spritePath, Vec2d cropStart, Vec2d cropSize, Vec2d drawSize){
        this.center = new SpriteHolder(spritePath, cropStart, cropSize, drawSize);
    }

    public void setCornerImage(String spritePath, Vec2d cropStart, Vec2d cropSize, Vec2d drawSize){
        this.corner = new SpriteHolder(spritePath, cropStart, cropSize, drawSize);
    }

    public void setHorizontalEdgeImage(String spritePath, Vec2d cropStart, Vec2d cropSize, Vec2d drawSize){
        this.edgeHorizontal = new SpriteHolder(spritePath, cropStart, cropSize, drawSize);
    }

    public void setVerticalEdgeImage(String spritePath, Vec2d cropStart, Vec2d cropSize, Vec2d drawSize){
        this.edgeVertical = new SpriteHolder(spritePath, cropStart, cropSize, drawSize);
    }

    public void open(){
        if(state == State.OPENING) return;
        this.currentTime = this.openTime;
        this.state = State.OPENING;
    }

    public void close(){
        if(state == State.CLOSING) return;
        this.currentTime = this.closeTime;
        this.state = State.CLOSING;
    }

    public void rewrite(String text){
        this.currentTime = this.rewriteTime;
        this.state = State.REWRITING;

        this.text = text;
        this.fontMetrics = new FontMetrics(this.text, this.font);
        this.textSize = new Vec2d(fontMetrics.width + margin * 2, fontMetrics.height + margin * 2);
    }

    public void rewriteInstant(String text){
        this.currentTime = 0;
        this.state = State.REWRITING;
        this.text = text;
        this.fontMetrics = new FontMetrics(this.text, this.font);
        this.textSize = new Vec2d(fontMetrics.width + margin * 2, fontMetrics.height + margin * 2);
    }

    public void setText(String text){
        this.text = text;
        this.fontMetrics = new FontMetrics(this.text, this.font);
        this.textSize = new Vec2d(fontMetrics.width + margin * 2, fontMetrics.height + margin * 2);
    }

    @Override
    public void onTick(long nanosSincePreviousTick){
        if(this.currentTime >= 0) {
            this.currentTime -= nanosSincePreviousTick;
        }
        if(this.viewport == null){
            this.viewport = this.gameObject.gameWorld.getViewport(viewport_id);
        }
    }

    @Override
    public void onLateTick(){}

    @Override
    public void onDraw(GraphicsContext g){
        if(this.viewport == null) return; //no viewport to see this anyway.

        double ratio = 1; //What fraction of size should text box be drawn at.
        if(this.state == State.OPENING){
            ratio = 1 - this.currentTime / this.openTime;
        } else if(this.state == State.CLOSING){
            ratio = this.currentTime / this.closeTime;
        }
        ratio = Math.max(0,Math.min(1,ratio));

        if(this.fitToText) {
            this.size = this.textSize.smult(1 / this.viewport.getScale());
        }

        Vec2d pos;
        Vec2d offset;
        Vec2d zoomPos = new Vec2d(size.x/2,size.y/2); //TODO add option for this?
        if(this.relativeToViewport) {
            //TODO separate options for centering and relative to viewport
            pos = this.gameObject.gameWorld.getViewport(this.viewport_id).getGameWorldViewCorner();
            offset = new Vec2d(this.corner.drawSize.x,this.corner.drawSize.y);
        } else {
            pos = this.gameObject.getTransform().position;
            offset = new Vec2d(-size.x/2,-size.y);
        }
        pos = pos.plus(offset).plus(this.position);

        //center square
        g.drawImage(this.center.image, this.center.cropStart.x, this.center.cropStart.y,
                this.center.cropSize.x, this.center.cropSize.y,
                pos.x + zoomPos.x * (1 - ratio),
                pos.y + zoomPos.y * (1 - ratio),
                this.size.x * ratio, this.size.y * ratio);

        //top left corner
        g.drawImage(this.corner.image, this.corner.cropStart.x, this.corner.cropStart.y,
                this.corner.cropSize.x, this.corner.cropSize.y,
                pos.x - this.corner.drawSize.x * ratio + zoomPos.x * (1 - ratio),
                pos.y - this.corner.drawSize.y * ratio + zoomPos.y * (1 - ratio),
                this.corner.drawSize.x * ratio, this.corner.drawSize.y * ratio);
        //top right corner
        g.drawImage(this.corner.image, this.corner.cropStart.x, this.corner.cropStart.y,
                this.corner.cropSize.x, this.corner.cropSize.y,
                pos.x + (this.size.x + this.corner.drawSize.x) * ratio + zoomPos.x * (1 - ratio),
                pos.y - this.corner.drawSize.y * ratio + zoomPos.y * (1 - ratio),
                -this.corner.drawSize.x * ratio, this.corner.drawSize.y * ratio);
        //bottom right corner
        g.drawImage(this.corner.image, this.corner.cropStart.x, this.corner.cropStart.y,
                this.corner.cropSize.x, this.corner.cropSize.y,
                pos.x + (this.size.x + this.corner.drawSize.x) * ratio + zoomPos.x * (1 - ratio),
                pos.y + (this.size.y + this.corner.drawSize.y) * ratio + zoomPos.y * (1 - ratio),
                -this.corner.drawSize.x * ratio, -this.corner.drawSize.y * ratio);
        //bottom left corner
        g.drawImage(this.corner.image, this.corner.cropStart.x, this.corner.cropStart.y,
                this.corner.cropSize.x, this.corner.cropSize.y,
                pos.x - this.corner.drawSize.x * ratio + zoomPos.x * (1 - ratio),
                pos.y + (this.size.y + this.corner.drawSize.y) * ratio + zoomPos.y * (1 - ratio),
                this.corner.drawSize.x * ratio, -this.corner.drawSize.y * ratio);

        //top edge
        g.drawImage(this.edgeHorizontal.image, this.edgeHorizontal.cropStart.x, this.edgeHorizontal.cropStart.y,
                this.edgeHorizontal.cropSize.x, this.edgeHorizontal.cropSize.y,
                pos.x + zoomPos.x * (1 - ratio),
                pos.y - this.edgeHorizontal.drawSize.y * ratio + zoomPos.y * (1 - ratio),
                this.size.x * ratio, this.edgeHorizontal.drawSize.y * ratio);
        //bottom edge
        g.drawImage(this.edgeHorizontal.image, this.edgeHorizontal.cropStart.x, this.edgeHorizontal.cropStart.y,
                this.edgeHorizontal.cropSize.x, this.edgeHorizontal.cropSize.y,
                pos.x + zoomPos.x * (1 - ratio),
                pos.y + (this.edgeHorizontal.drawSize.y + this.size.y) * ratio + zoomPos.y * (1 - ratio),
                this.size.x * ratio, -this.edgeHorizontal.drawSize.y * ratio);

        //left edge
        g.drawImage(this.edgeVertical.image, this.edgeVertical.cropStart.x, this.edgeVertical.cropStart.y,
                this.edgeVertical.cropSize.x, this.edgeVertical.cropSize.y,
                pos.x - this.edgeVertical.drawSize.x * ratio + zoomPos.x * (1 - ratio),
                pos.y + zoomPos.y * (1 - ratio),
                this.edgeHorizontal.drawSize.x * ratio, this.size.y * ratio);
        //right edge
        g.drawImage(this.edgeVertical.image, this.edgeVertical.cropStart.x, this.edgeVertical.cropStart.y,
                this.edgeVertical.cropSize.x, this.edgeVertical.cropSize.y,
                pos.x + (this.edgeVertical.drawSize.x + this.size.x) * ratio + zoomPos.x * (1 - ratio),
                pos.y + zoomPos.y * (1 - ratio),
                -this.edgeVertical.drawSize.x * ratio, this.size.y * ratio);

        if(ratio == 1 && this.state == State.OPENING){
            this.state = State.REWRITING; //start rewriting once open
            this.currentTime = this.rewriteTime;
        }
        if(this.state == State.REWRITING){
            ratio = 1 - this.currentTime / this.rewriteTime;
            ratio = Math.max(0,Math.min(1,ratio));
            String partialText = this.text.substring(0, (int)(this.text.length() * ratio));
            g.save();
            g.setFill(this.textColor);
            g.setFont(this.font);
            switch(this.alignment){
                case CENTER:
                    g.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
                    g.translate(pos.x + this.size.x / 2,
                            pos.y + this.size.y / 2 - margin);
                    break;
                case LEFT:
                    g.setTextAlign(javafx.scene.text.TextAlignment.LEFT);
                    g.translate(pos.x + margin,
                            pos.y + this.size.y / 2 - margin);
                    break;
                case TOP_LEFT:
                    g.setTextAlign(javafx.scene.text.TextAlignment.LEFT);
                    g.translate(pos.x + margin,
                            pos.y + (new FontMetrics("t", this.font)).height / this.viewport.getScale() + margin);
                    break;
                case TOP:
                    g.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
                    g.translate(pos.x + this.size.x / 2,
                            pos.y + (new FontMetrics("t", this.font)).height / this.viewport.getScale() + margin);
                    break;
                default:
                    System.err.println("TextBox Alignment " + this.alignment + " is not yet implemented\nUsing default");
                    g.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
                    g.translate(pos.x + this.size.x / 2,
                            pos.y + this.size.y / 2 - margin);
            }
            g.scale(1 / this.viewport.getScale(), 1 / this.viewport.getScale());
            g.fillText(partialText,0,0);
            g.restore();
        }

    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.RenderSystem | SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "TextBoxComponent";
    }
}
