import java.util.Iterator;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;


public class PointSET {

	private TreeSet<Point2D> points;

	// construct an empty set of points 
	public PointSET() {
		points = new TreeSet<>();
	}

	// is the set empty? 
	public boolean isEmpty() {
		return points.isEmpty();
	}

	// number of points in the set 
	public int size() {
		return points.size();
	}

	// add the point to the set (if it is not already in the set)
	public void insert(Point2D p) {
		if (p == null) {
			throw new NullPointerException("insert(): p should not be null");
		}
		points.add(p);
	}

	// does the set contain point p? 
	public boolean contains(Point2D p) {
		if (p == null) {
			throw new NullPointerException("contains(): p should not be null");
		}
		return points.contains(p);
	}

	// draw all points to standard draw 
	public void draw() {
		StdDraw.setPenColor();
		for (Point2D p : points) {
			StdDraw.filledCircle(p.x(), p.y(), 2);
		}
	}

	// all points that are inside the rectangle 
	public Iterable<Point2D> range(RectHV rect) {
		if (rect == null) {
			throw new NullPointerException("range(): rect should not be null");
		}
		return new Iterable<Point2D>() {

			@Override
            public Iterator<Point2D> iterator() {
	            return points.stream()
	            		.filter(p -> rect.contains(p))
	            		.iterator();
            }
		};
	}

	// a nearest neighbor in the set to point p; null if the set is empty 
	public Point2D nearest(Point2D p) {
		if (p == null) {
			throw new NullPointerException("nearest(): p should not be null");
		}
		Point2D nearest = null;
		double min = Double.POSITIVE_INFINITY;
		for (Point2D point : points) {
			if (point.distanceSquaredTo(p) < min) {
				nearest = point;
				min = point.distanceSquaredTo(p);
			}
		}
		return nearest;
	}

	// unit testing of the methods (optional) 
	public static void main(String[] args) {
	}

}
