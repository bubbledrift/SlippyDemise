package engine.game.collisionShapes;

import engine.support.Vec2d;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CircleShape extends Shape{

    protected Vec2d center; //relative to parent component *not absolute*
    protected double radius;

    public CircleShape(Vec2d center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public Vec2d getCenter(){
        //return actual position not position relative to parent component
        return new Vec2d(this.center.x + this.parentPosition.x, this.center.y + this.parentPosition.y);
    }
    public Vec2d getRelativeCenter(){
        return this.center;
    }

    public double getRadius(){
        return this.radius;
    }

    @Override
    public Vec2d getXRange() {
        return new Vec2d(this.getCenter().x - this.radius/2,this.getCenter().x + this.radius/2);
    }

    @Override
    public Vec2d getYRange() {
        return new Vec2d(this.getCenter().y - this.radius/2,this.getCenter().y + this.radius/2);
    }

    @Override
    public Vec2d collides(Shape o) {
        return o.collidesCircle(this);
    }

    @Override
    public Vec2d collidesAAB(AABShape o) {
        Vec2d f = o.collidesCircle(this);
        return f == null ? null : f.reflect();
    }

    @Override
    public Vec2d collidesCircle(CircleShape o) {
        double dx = this.getCenter().x - o.getCenter().x;
        double dy = this.getCenter().y - o.getCenter().y;
        double D = Math.sqrt(dx*dx + dy*dy);
        if(D == 0){
            return new Vec2d(this.getRadius() + o.getRadius(),0);
        }
        double L = this.getRadius() + o.getRadius() - D;
        if(L < 0){
            return null;
        }
        return new Vec2d(dx*L/D, dy*L/D);
    }

    @Override
    public Vec2d collidesPolygon(PolygonShape o) {
        double smallestOverlap;
        Vec2d axis = null;
        {
            Vec2d closestPoint = o.getPoints()[0];
            double closestDist = Double.MAX_VALUE;

            for (int i = 0; i < o.getNumPoints(); i++) {
                double dist = o.getPoints()[i].dist(this.getCenter());
                if (dist < closestDist) {
                    closestDist = dist;
                    closestPoint = o.getPoints()[i];
                }
            }
            axis = closestPoint.minus(this.getCenter());
            Vec2d range2 = project(o, axis);
            double point = (this.getCenter()).dot(axis) / axis.mag();
            smallestOverlap = getOverlap(new Vec2d(point - this.radius, point + this.radius), range2);
            if (smallestOverlap == 0) return null;
        }

        for(int i = 0; i < o.getNumPoints(); i++){
            Vec2d proj = o.getPoints()[(i+1)%o.getNumPoints()].minus(o.getPoints()[i]).perpendicular();
            double point = (this.getCenter()).dot(proj) / proj.mag();
            Vec2d range2 = project(o,proj);
            double overlap = getOverlap(new Vec2d(point - this.radius, point + this.radius),range2);
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
        double dx = this.getCenter().x - o.x;
        double dy = this.getCenter().y - o.y;
        double D = Math.sqrt(dx*dx + dy*dy);
        if(D == 0){
            return new Vec2d(this.getRadius(),0);
        }
        double L = this.getRadius() - D;
        if(L < 0){
            return null;
        }
        return new Vec2d(dx*L/D, dy*L/D);
    }

    @Override
    public double collidesRay(Ray r) {
        Vec2d pnt = this.getCenter().minus(r.src).projectOnto(r.dir);
        if(pnt.dot(r.dir) < 0) return -1;
        double l = pnt.mag();
        double d = pnt.dist(this.getCenter().minus(r.src));
        if(d > this.getRadius()) return -1;
        if( pnt.minus(this.getCenter().minus(r.src)).mag() > this.getRadius())
            return (float)(l + Math.sqrt(this.getRadius()*this.getRadius() - d*d)); //outside
        return l -Math.sqrt(this.getRadius()*this.getRadius() - d*d); //inside
    }

    public Element getXML(Document doc){
        Element circle = doc.createElement(this.getClass().getName());
        circle.setAttribute("center", this.center.toString());
        circle.setAttribute("radius", Double.toString(this.radius));
        return circle;
    }

}
