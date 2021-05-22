package engine.game.collisionShapes;


import engine.support.Vec2d;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PolygonShape extends Shape{

    private Vec2d[] points;

    private Vec2d xRange, yRange;

    public PolygonShape(Vec2d... points) {
        this.points = points;

        double xmin = Double.MAX_VALUE;
        double xmax = -Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double ymax = -Double.MAX_VALUE;
        for(Vec2d p : points){
            xmin = Math.min(xmin, p.x);
            xmax = Math.min(xmax, p.x);
            ymin = Math.min(ymin, p.y);
            ymax = Math.min(ymax, p.y);
        }
        this.xRange = new Vec2d(xmin,xmax);
        this.yRange = new Vec2d(ymin, ymax);
    }

    @Override
    public Vec2d getXRange() {
        return this.xRange.plus(new Vec2d(this.parentPosition.x,this.parentPosition.x));
    }

    @Override
    public Vec2d getYRange() {
        return this.yRange.plus(new Vec2d(this.parentPosition.y,this.parentPosition.y));
    }

    public int getNumPoints(){
        return this.points.length;
    }

    public Vec2d[] getPoints(){
        Vec2d[] p = new Vec2d[this.points.length];
        for(int i = 0; i < this.points.length; i++){
            p[i] = this.points[i].plus(this.parentPosition);
        }
        return p;
    }

    public Vec2d[] getRelativePoints(){
        return this.points;
    }

    @Override
    public Vec2d collides(Shape o) {
        return o.collidesPolygon(this);
    }

    @Override
    public Vec2d collidesAAB(AABShape o) {
        Vec2d f = o.collidesPolygon(this);
        return f == null ? null : f.reflect();
    }

    @Override
    public Vec2d collidesCircle(CircleShape o) {
        Vec2d f = o.collidesPolygon(this);
        return f == null ? null : f.reflect();
    }

    @Override
    public Vec2d collidesPolygon(PolygonShape o) {
        double smallestOverlap = Double.MAX_VALUE;
        Vec2d axis = null;
        for(int i = 0; i < this.getNumPoints(); i++){
            Vec2d proj = this.getPoints()[(i+1)%this.getNumPoints()].minus(this.getPoints()[i]).perpendicular();
            Vec2d range1 = project(this,proj);
            Vec2d range2 = project(o,proj);
            double overlap = getOverlap(range1, range2);
            if(overlap == 0) return null;
            if (Math.abs(overlap) < Math.abs(smallestOverlap)) {
                smallestOverlap = overlap;
                axis = proj;
            }
        }
        for(int i = 0; i < o.getNumPoints(); i++){
            Vec2d proj = o.getPoints()[(i+1)%o.getNumPoints()].minus(o.getPoints()[i]).perpendicular();
            Vec2d range1 = project(this,proj);
            Vec2d range2 = project(o,proj);
            double overlap = getOverlap(range1, range2);
            if(overlap == 0) return null;
            if (Math.abs(overlap) < Math.abs(smallestOverlap)) {
                smallestOverlap = overlap;
                axis = proj;
            }
        }

        return axis.normalize().smult(smallestOverlap);
    }

    @Override
    public Vec2d collidesPoint(Vec2d o) {
        double smallestOverlap = Double.MAX_VALUE;
        Vec2d axis = null;
        for(int i = 0; i < this.getNumPoints(); i++) {
            Vec2d proj = this.getPoints()[(i + 1) % this.getNumPoints()].minus(this.getPoints()[i]).perpendicular();
            Vec2d range1 = project(this, proj);
            double point = o.dot(proj)/proj.mag();
            double overlap = getOverlap(range1, point);
            if(overlap == 0) return null;
            if (Math.abs(overlap) < Math.abs(smallestOverlap)) {
                smallestOverlap = overlap;
                axis = proj;
            }
        }
        return axis.normalize().smult(smallestOverlap);
    }

    @Override
    public double collidesRay(Ray r) {
        double min_dist = Double.MAX_VALUE;
        for(int i = 0; i < this.getNumPoints(); i++){
            double t = this.raycast(this.getPoints()[i], this.getPoints()[(i+1)%this.getNumPoints()], r);
            if(t < 0){
                continue;
            }
            min_dist = Math.min(t,min_dist);
        }
        return (float)min_dist;
    }

    public Element getXML(Document doc){
        Element polygon = doc.createElement(this.getClass().getName());
        for(int i = 0; i < this.points.length; i++){
            Element p = doc.createElement("point");
            p.setAttribute("point", this.points[i].toString());
            polygon.appendChild(p);
        }
        return polygon;
    }
}
