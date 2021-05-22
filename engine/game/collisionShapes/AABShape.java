package engine.game.collisionShapes;

import engine.support.Vec2d;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AABShape extends Shape{
    protected Vec2d position; //relative to parent component *not absolute*
    protected Vec2d size;
    
    
    public AABShape(Vec2d pos, Vec2d size){
        this.position = pos;
        this.size = size;
    }

    public Vec2d getPosition(){
        //return actual position not position relative to parent component
        return new Vec2d(this.position.x + this.parentPosition.x, this.position.y + this.parentPosition.y);
    }

    public Vec2d getRelativePosition(){
        return this.position;
    }

    public Vec2d getSize(){
        return this.size;
    }

    @Override
    public Vec2d getXRange() {
        return new Vec2d(this.getPosition().x,this.getPosition().x + this.getSize().x);
    }

    @Override
    public Vec2d getYRange() {
        return new Vec2d(this.getPosition().y,this.getPosition().y + this.getSize().y);
    }

    @Override
    public Vec2d collides(Shape o) {
        return o.collidesAAB(this);
    }

    @Override
    public Vec2d collidesAAB(AABShape o) {
        double left = o.getPosition().x - this.getPosition().x - this.getSize().x;
        double right = o.getPosition().x + o.getSize().x - this.getPosition().x;
        double up = o.getPosition().y - this.getPosition().y - this.getSize().y;
        double down = o.getPosition().y + o.getSize().y - this.getPosition().y;
        if(left >= 0 || right <= 0 || up >= 0 || down <= 0){
            return null;
        }
        if(Math.abs(left) > Math.abs(right)){
            left = right;
        }
        if(Math.abs(up) > Math.abs(down)){
            up = down;
        }
        if(Math.abs(left)<Math.abs(up)) {
            return new Vec2d(left, 0);
        }
        return new Vec2d(0,up);
    }

    @Override
    public Vec2d collidesCircle(CircleShape o) {
        if(this.getPosition().x <= o.getCenter().x && o.getCenter().x <= this.getPosition().x + this.getSize().x &&
                this.getPosition().y <= o.getCenter().y && o.getCenter().y <= this.getPosition().y + this.getSize().y){

            double x = o.getCenter().x - this.getPosition().x;
            double y = o.getCenter().y - this.getPosition().y;

            if(y*this.getSize().y > x * this.getSize().x){
                //Left Bottom side
                if(y*this.getSize().y > (this.getSize().x - x) * this.getSize().x){
                    //Bottom side
                    return new Vec2d(0,y - this.getSize().x - o.getRadius());
                } else{
                    //Left side
                    return new Vec2d(x + o.getRadius(),0);
                }
            } else{
                //Right Top side
                if(y*this.getSize().y > (this.getSize().x - x) * this.getSize().x){
                    //Right side
                    return new Vec2d(x - this.getSize().x - o.getRadius(),0);
                } else{
                    //Top side
                    return new Vec2d(0,y + o.getRadius());
                }
            }
        }

        double x = Math.max(Math.min(o.getCenter().x,this.getPosition().x + this.getSize().x),this.getPosition().x);
        double y = Math.max(Math.min(o.getCenter().y,this.getPosition().y + this.getSize().y),this.getPosition().y);
        double dx = o.getCenter().x - x;
        double dy = o.getCenter().y - y;
        double D = Math.sqrt(dx*dx + dy*dy);
        if(D > o.getRadius()){
            return null;
        }

        double L = o.getRadius() - D;
        if(L <= 0){
            return null;
        }

        Vec2d r = new Vec2d(-dx * L / D, -dy * L / D);

        if(Double.isNaN(r.x) || Double.isNaN(r.y)){
            new Exception().printStackTrace();
            System.exit(0);
        }
        return new Vec2d(-dx * L / D, -dy * L / D);
    }

    @Override
    public Vec2d collidesPolygon(PolygonShape o) {

        double smallestOverlap = Double.MAX_VALUE;
        Vec2d axis = null;
        {
            Vec2d proj = new Vec2d(0, 1);
            Vec2d range1 = this.project(this, proj);
            Vec2d range2 = this.project(o, proj);
            double overlap = this.getOverlap(range1, range2);
            if (overlap == 0) return null;
            if (Math.abs(overlap) < Math.abs(smallestOverlap)) {
                smallestOverlap = overlap;
                axis = proj;
            }
        }
        {
            Vec2d proj = new Vec2d(1, 0);
            Vec2d range1 = this.project(this, proj);
            Vec2d range2 = this.project(o, proj);
            double overlap = getOverlap(range1, range2);
            if (overlap == 0) return null;
            if (Math.abs(overlap) < Math.abs(smallestOverlap)) {
                smallestOverlap = overlap;
                axis = proj;
            }
        }
        for(int i = 0; i < o.getNumPoints(); i++){
            Vec2d proj = o.getPoints()[(i+1)%o.getNumPoints()].minus(o.getPoints()[i]).perpendicular();
            Vec2d range1 = this.project(this,proj);
            Vec2d range2 = this.project(o,proj);
            double overlap = getOverlap(range1, range2);
            if(overlap == 0) return null;
            if (Math.abs(overlap) < Math.abs(smallestOverlap)) {
                smallestOverlap = overlap;
                axis = proj;
            }
        }
        Vec2d r = axis.normalize().smult(smallestOverlap);
        if(Double.isNaN(r.x) || Double.isNaN(r.y)){
            new Exception().printStackTrace();
            System.exit(0);
        }
        return axis.normalize().smult(smallestOverlap);
    }

    @Override
    public Vec2d collidesPoint(Vec2d o) {
        if(this.getPosition().x > o.x || o.x > this.getPosition().x + this.getSize().x ||
                this.getPosition().y > o.y || o.y > this.getPosition().y + this.getSize().y){
            return null;
        }
        double x = o.x - this.getPosition().x;
        double y = o.y - this.getPosition().y;

        if(y*this.getSize().y > x * this.getSize().x){
            //Left Bottom side
            if(y*this.getSize().y > (this.getSize().x - x) * this.getSize().x){
                //Bottom side
                return new Vec2d(this.getSize().y - y,0);
            } else{
                //Left side
                return new Vec2d(0,- x);
            }
        } else{
            //Right Top side
            if(y*this.getSize().y > (this.getSize().x - x) * this.getSize().x){
                //Right side
                return new Vec2d(0,this.getSize().x - x);
            } else{
                //Top side
                return new Vec2d(- y,0);
            }
        }
    }

    @Override
    public double collidesRay(Ray r) {
        Vec2d a = this.getPosition();
        Vec2d b = this.getPosition().plus(new Vec2d(this.getSize().x,0));
        Vec2d c = this.getPosition().plus(this.getSize());
        Vec2d d = this.getPosition().plus(new Vec2d(0,this.getSize().y));
        double min_dist = Double.MAX_VALUE;
        {
            double t = this.raycast(a, b, r);
            if(t > 0) min_dist = Math.min(t,min_dist);
        }
        {
            double t = this.raycast(b, c, r);
            if(t > 0) min_dist = Math.min(t,min_dist);
        }
        {
            double t = this.raycast(c, d, r);
            if(t > 0) min_dist = Math.min(t,min_dist);
        }
        {
            double t = this.raycast(d, a, r);
            if(t > 0) min_dist = Math.min(t,min_dist);
        }
        if(min_dist == Double.MAX_VALUE) return -1;
        return (float)min_dist;
    }

    public Element getXML(Document doc){
        Element AABShape = doc.createElement(this.getClass().getName());
        AABShape.setAttribute("position", this.position.toString());
        AABShape.setAttribute("size", this.size.toString());
        return AABShape;
    }


}
