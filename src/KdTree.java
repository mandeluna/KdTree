import java.util.Iterator;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;


public class KdTree {

	private Node root;

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
		return size(root);
	}

	// return the size of the subtree rooted at the node 
	private int size(Node node) {
		if (node == null) {
			return 0;
		}
		else {
			return 1 + size(node.lb) + size(node.rt) ;
		}
	}

	// add the point to the set (if it is not already in the set)
	public void insert(Point2D p) {
		if (p == null) {
			throw new NullPointerException("insert(): p should not be null");
		}
		double xmin, ymin, xmax, ymax;
		boolean isHorizontal = true;	// initial orientation is left-to-right
//						// TODO correct all these assumptions
//		xmin = 0.0;		// XXX leftmost point within smallest rectangle containing p
//		xmax = p.x();	// XXX assuming p is the top-most left-to-right entry
//		ymax = p.y();	// XXX this is the largest rectangle possible
//		ymin = 0.0;		// XXX assuming the top boundary is 0
//		insert(root, p, new RectHV(xmin, ymax, xmax, ymin), isHorizontal);
		Node node = insert(root, p, null, isHorizontal);
		if (root == null) {
			root = node;
		}
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
		Node node = root;
		boolean isLeftRight = true;

		while (node != null) {

			if (isLeftRight) {
				StdDraw.setPenColor(StdDraw.BOOK_RED);
			}
			else {
				StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
			}

			// Draw Point
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledCircle(node.point.x(), node.point.y(), 2);

			isLeftRight = !isLeftRight;
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
	            return new Iterator<Point2D>() {

					@Override
                    public boolean hasNext() {
	                    // TODO Auto-generated method stub
	                    return false;
                    }

					@Override
                    public Point2D next() {
	                    // TODO Auto-generated method stub
	                    return null;
                    }
	            };
            }
		};
	}

	// a nearest neighbor in the set to point p; null if the set is empty 
	public Point2D nearest(Point2D p) {
		if (p == null) {
			throw new NullPointerException("nearest(): p should not be null");
		}
		Node x = nearestNodeTo(p);
		if (x == null) {
			return null;
		}
		else {
			return x.point;
		}
	}

	private Node nearestNodeTo(Point2D p) {
		double min = Double.POSITIVE_INFINITY;
		Node nearest = null;
		Node x = root;
		boolean isLeftRight = true;
		while (x != null) {
			double dist = p.distanceSquaredTo(nearest.point);
			if (dist < min) {
				min = dist;
				nearest = x;
				x = x.lb;
			}
			else if (dist >= min) {
				x = x.rt;
			}
			isLeftRight = !isLeftRight;
		}
		return nearest;
	}

	// unit testing of the methods (optional) 
	public static void main(String[] args) {
	}

	/* --- Private API (Red/Black BST implementation --- */

	private static class Node {
		Point2D point;
		RectHV rect;
		Node lb;	// the left/bottom subtree
		Node rt;	// the right/top subtree
		boolean isRed;	// color of parent link

		private Node(Point2D key, RectHV value, boolean isRed) {
			this.point = key;
			this.rect = value;
			this.isRed = isRed;
		}
	}

	private boolean isRed(Node x) {
		if (x == null) {
			return false;	// null links are black
		}
		return x.isRed;
	}

	// Left rotation. Orient a (temporarily) right-leaning link to lean left
	private Node rotateLeft(Node h) {
		assert (isRed(h.rt));
		Node x = h.rt;
		h.rt = x.lb;
		x.lb = h;
		x.isRed = h.isRed;
		h.isRed = true;
		return x;
	}

	// Right rotation. Orient a left-leaning link to (temporarily) lean right
	private Node rotateRight(Node h) {
		assert (isRed(h.lb));
		Node x = h.lb;
		h.lb = x.rt;
		x.rt = h;
		x.isRed = h.isRed;
		h.isRed = true;
		return x;
	}

	// Color flip. Recolor to split a (temporary) 4-node
	private void flipColors(Node h) {
		assert !isRed(h);
		assert isRed(h.lb);
		assert isRed(h.rt);
		h.isRed = true;
		h.lb.isRed = false;
		h.rt.isRed = false;
	}

	// retrieval
	private RectHV get(Point2D key) {
		Node x = root;
		while (x != null) {
			int cmp = key.compareTo(x.point);
			if (cmp < 0) {
				x = x.lb;
			}
			else if (cmp > 0) {
				x = x.rt;
			}
			else {
				return x.rect;
			}
		}
		return null;
	}

	// Insertion.
	private Node insert(Node h, Point2D key, RectHV value, boolean orientation) {
		if (h == null) {
			return new Node(key, value, true);
		}
		int cmp = key.compareTo(h.point);
		if (cmp < 0) {
			h.lb = insert(h.lb, key, value, !orientation);
		}
		else if (cmp > 0) {
			h.rt = insert(h.rt, key, value, !orientation);
		}
		else {
			h.rect = value;
		}
		if (isRed(h.rt) && !isRed(h.lb)){
			h = rotateLeft(h);
		}
		if (isRed(h.lb) && isRed(h.lb.lb)) {
			h = rotateRight(h);
		}
		if (isRed(h.lb) && isRed(h.rt)) {
			flipColors(h);
		}
		return h;
	}

	private Node lookup(Point2D key) {
		Node x = root;
		while (x != null) {
			int cmp = key.compareTo(x.point);
			if (cmp < 0) {
				x = x.lb;
			}
			else if (cmp > 0) {
				x = x.rt;
			}
			else {
				return x;
			}
		}
		return null;
	}
}
