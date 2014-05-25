/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Point;

public class Vertex implements Comparable<Vertex>
{
    public final String name;
    public Edge[] adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;
    
    public boolean ignore;
     
    private Point point;
    private double rot;
                
    public Vertex(String argName, Point point, double rot) {
        ignore = false;
        name = argName; 
        this.point = point;
        this.rot = rot;
    }
    
    public Vertex(String argName, Point point) {
        ignore = false;
        name = argName; 
        this.point = point;
        this.rot = Double.NaN;
    }
    
    public Point getPoint() {
        return point;
    }
    
    public double getRot() {
        return rot;
    }
    
    public String toString() { 
        return name; 
    }
    
    public int compareTo(Vertex other)
    {
        return Double.compare(minDistance, other.minDistance);
    }

}
