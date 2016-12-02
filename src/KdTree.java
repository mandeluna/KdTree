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
		// if root is initially null we need to set that node to black
		boolean special = root == null;
		// initial orientation is left-right and box is entire unit square
		root = insert(root, p, 0.0, 1.0, 0.0, 1.0, true);
		if (special) {
			root.isRed = false;
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
		drawNode(root, true);
	}

	private void drawNode(Node node, boolean isLeftRight) {
		if (node == null) {
			return;
		}

		// Draw Point
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledCircle(node.point.x(), node.point.y(), 0.005);

		// Draw separating line
		if (isLeftRight) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.line(node.point.x(), node.ymin, node.point.x(), node.ymax);
		}
		else {
			StdDraw.setPenColor(StdDraw.BLUE);
			StdDraw.line(node.xmin, node.point.y(), node.xmax, node.point.y());
		}

		drawNode(node.lb, !isLeftRight);
		drawNode(node.rt, !isLeftRight);
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

	/* --- Private API (Red/Black BST 2d Tree) --- */

	private static class Node {
		Point2D point;
		double xmin, xmax, ymin, ymax;
		Node lb;	// the left/bottom subtree
		Node rt;	// the right/top subtree
		boolean isRed;	// color of parent link

		private Node(Point2D key, double xmin, double xmax, double ymin, double ymax, boolean isRed) {
			this.point = key;
			this.xmin = xmin;
			this.xmax = xmax;
			this.ymin = ymin;
			this.ymax = ymax;
			this.isRed = isRed;
		}

		@Override
		public String toString() {
			return String.format("%s [%.6f, %.6f, %.6f, %.6f]", point.toString(), xmin, xmax, ymin, ymax);
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
				return new RectHV(x.xmin, x.xmax, x.ymin, x.ymax);
			}
		}
		return null;
	}

	// Insertion.
	private Node insert(Node h, Point2D point, double xmin, double xmax, double ymin, double ymax, boolean leftToRight) {
		if (h == null) {
			return new Node(point, xmin, xmax, ymin, ymax, true);
		}
		int cmp = leftToRight ? Double.compare(point.x(), h.point.x()) : Double.compare(point.y(), h.point.y());
		if (cmp < 0) {
			// inserting to the left of the current node
			if (leftToRight) {
				xmax = point.x();
			}
			// inserting below the current node
			else {
				ymax = point.y();
			}
			h.lb = insert(h.lb, point, xmin, xmax, ymin, ymax, !leftToRight);
		}
		else if (cmp > 0) {
			// inserting to the right of the current node
			if (leftToRight) {
				xmin = point.x();
			}
			// inserting above the current node
			else {
				ymin = point.y();
			}
			h.rt = insert(h.rt, point, xmin, xmax, ymin, ymax, !leftToRight);
		}
		else {
			h.xmin = xmin;
			h.xmax = xmax;
			h.ymin = ymin;
			h.ymax = ymax;
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
