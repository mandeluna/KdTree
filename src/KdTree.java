import java.util.Iterator;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;


public class KdTree {

	private Node<Point2D, Point2D> root;
	private int count = 0;

	/* --- Public API (required for assignment) --- */

	// construct an empty set of points 
	public KdTree() {
	}

	// is the set empty? 
	public boolean isEmpty() {
		return root == null;
	}

	// number of points in the set 
	public int size() {
		return count;
	}

	// add the point to the set (if it is not already in the set)
	public void insert(Point2D p) {
		if (p == null) {
			throw new NullPointerException("insert(): p should not be null");
		}
		insert(root, p, p);
	}

	// does the set contain point p? 
	public boolean contains(Point2D p) {
		if (p == null) {
			throw new NullPointerException("contains(): p should not be null");
		}
		return this.lookup(p) != null;
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

	/* --- Private API (Red/Black BST implementation --- */

	private class Node<Key extends Comparable<Key>, Value> {
		Node<Key, Value> left;
		Node<Key, Value> right;
		Key key;
		Value value;
		boolean isRed;	// color of parent link

		private Node(Key key, Value value, boolean isRed) {
			this.key = key;
			this.value = value;
			this.isRed = isRed;
		}

		private boolean isRed(Node<Key, Value> x) {
			if (x == null) {
				return false;	// null links are black
			}
			return x.isRed;
		}

		// Left rotation. Orient a (temporarily) right-leaning link to lean left
		private Node<Key, Value> rotateLeft(Node<Key, Value> h) {
			assert (isRed(h.right));
			Node<Key, Value> x = h.right;
			h.right = x.left;
			x.left = h;
			x.isRed = h.isRed;
			h.isRed = true;
			return x;
		}

		// Right rotation. Orient a left-leaning link to (temporarily) lean right
		private Node<Key, Value> rotateRight(Node<Key, Value> h) {
			assert (isRed(h.left));
			Node<Key, Value> x = h.left;
			h.left = x.right;
			x.right = h;
			x.isRed = h.isRed;
			h.isRed = true;
			return x;
		}

		// Color flip. Recolor to split a (temporary) 4-node
		private void flipColors(Node<Key, Value> h) {
			assert !isRed(h);
			assert isRed(h.left);
			assert isRed(h.right);
			h.isRed = true;
			h.left.isRed = false;
			h.right.isRed = false;
		}

		// retrieval
		private Value get(Key key) {
			Node<Key, Value> x = root;
			while (x != null) {
				int cmp = key.compareTo(x.key);
				if (cmp < 0) {
					x = x.left;
				}
				else if (cmp > 0) {
					x = x.right;
				}
				else {
					return x.value;
				}
			}
			return null;
		}

		// Insertion.
		private Node<Key, Value> insert(Node<Key, Value> h, Key key, Value value) {
			if (h == null) {
				return new Node<Key, Value>(key, value, true);
			}
			int cmp = key.compareTo(h.key);
			if (cmp < 0) {
				h.left = insert(h.left, key, value);
			}
			else if (cmp > 0) {
				h.right = insert(h.right, key, value);
			}
			else {
				h.value = value;
			}
			if (isRed(h.right) && !isRed(h.left)){
				h = rotateLeft(h);
			}
			if (isRed(h.left) && isRed(h.left.left)) {
				h = rotateRight(h);
			}
			if (isRed(h.left)&& isRed(h.right)) {
				flipColors(h);
			}
			return h;
		}
	}

	private Point2D lookup(Point2D key) {
		Node<Point2D, Point2D> x = root;
		while (x != null) {
			int cmp = key.compareTo(x.key);
			if (cmp < 0) {
				x = x.left;
			}
			else if (cmp > 0) {
				x = x.right;
			}
			else {
				return x.value;
			}
		}
		return null;
	}
}
