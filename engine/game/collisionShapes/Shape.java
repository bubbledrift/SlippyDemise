package engine.game.collisionShapes;

import engine.support.Vec2d;
import org.w3c.dom.*;

public abstract class Shape {
    public Vec2d parentPosition;
    public abstract Vec2d getXRange();
    public abstract Vec2d getYRange();

    public abstract Vec2d collides(Shape o);
    public abstract Vec2d collidesAAB(AABShape o);
    public abstract Vec2d collidesCircle(CircleShape o);
    public abstract Vec2d collidesPolygon(PolygonShape o);
    public abstract Vec2d collidesPoint(Vec2d o);
    public abstract double collidesRay(Ray r);

    public abstract Element getXML(Document doc);

    protected double getOverlap(Vec2d range1, Vec2d range2){
        double left = range2.x - range1.y;
        double right = range2.y - range1.x;
        if(left >= 0 || right <= 0) return 0;
        return Math.abs(left) < Math.abs(right) ? left : right;
    }

    protected double getOverlap(Vec2d range1, double val){
        double left = val - range1.y;
        double right = val - range1.x;
        if(left >= 0 || right <= 0) return 0;
        return Math.abs(left) < Math.abs(right) ? left : right;
    }

    protected Vec2d project(PolygonShape s, Vec2d axis){
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        double L = axis.mag();
        for(Vec2d p : s.getPoints()){
            double projection = p.dot(axis)/L;
            min = Math.min(projection,min);
            max = Math.max(projection,max);
        }
        return new Vec2d(min,max);
    }

    protected Vec2d project(AABShape s, Vec2d axis){
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        double L = axis.mag();
        double projection;
        projection = (s.getPosition()).dot(axis)/L;
        min = Math.min(projection,min);
        max = Math.max(projection,max);

        projection = (s.getPosition().plus(new Vec2d(s.getSize().x,0))).dot(axis)/L;
        min = Math.min(projection,min);
        max = Math.max(projection,max);

        projection = (s.getPosition().plus(new Vec2d(0,s.getSize().y))).dot(axis)/L;
        min = Math.min(projection,min);
        max = Math.max(projection,max);

        projection = (s.getPosition().plus(s.getSize())).dot(axis)/L;
        min = Math.min(projection,min);
        max = Math.max(projection,max);

        return new Vec2d(min,max);
    }

    protected double raycast(Vec2d a, Vec2d b, Ray s2){
        Vec2d b_minus_p = a.minus(s2.src);
        Vec2d a_minus_p = b.minus(s2.src);
        if(b_minus_p.cross(s2.dir)*a_minus_p.cross(s2.dir) > 0) return -1;
        Vec2d n = b.minus(a).perpendicular();
        double t = b_minus_p.dot(n) / s2.dir.dot(n);
        if(t < 0) return -1;
        return t;
    }



    public static Shape loadFromXML(Element n){
        String name = n.getTagName();
        NamedNodeMap attr = n.getAttributes();
        if(name.endsWith("AABShape")){
            Vec2d pos = Vec2d.fromString(attr.getNamedItem("position").getNodeValue());
            Vec2d size = Vec2d.fromString(attr.getNamedItem("size").getNodeValue());
            return new AABShape(pos,size);
        } else if(name.endsWith("CircleShape")) {
            Vec2d center = Vec2d.fromString(attr.getNamedItem("center").getNodeValue());
            Double r = Double.parseDouble(attr.getNamedItem("radius").getNodeValue());
            return new CircleShape(center,r);
        } else if(name.endsWith("PolygonShape")){
            NodeList elements = n.getElementsByTagName("point");
            Vec2d[] points = new Vec2d[elements.getLength()];
            for(int i = 0; i < elements.getLength(); i++){
                Node child = elements.item(i);
                if(child.getNodeType() == Node.ELEMENT_NODE)
                    points[i] = Vec2d.fromString(child.getAttributes().getNamedItem("point").getNodeValue());
            }
            return new PolygonShape(points);
        }
        return null;
    }
}
