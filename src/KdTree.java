import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;


public class KdTree {

	private Node root;
	private int depth = 0;
	private boolean isRootOrientationLeftToRight = false;   // will be true after we add the first node

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
		// initial orientation is left-right and box is entire unit square
		root = insert(root, p, 0.0, 1.0, 0.0, 1.0, isRootOrientationLeftToRight);
		// if the root turns red, it means that either a new node was added to a null tree,
        // or the root was split into two nodes by a flipColors() operation
		if (isRed(root)) {
            root.isRed = false;
            isRootOrientationLeftToRight = !isRootOrientationLeftToRight;
            String orientation = (isRootOrientationLeftToRight) ? "Left to Right" : "Top to Bottom";
            depth++;
            StdOut.printf("Tree depth is now %d, orientation is %s root node is %s\n", depth, orientation, root);
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

        StdDraw.setPenRadius(0.001);

        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        double hw = (node.xmax - node.xmin);
        double hh = (node.ymax - node.ymin);
        StdDraw.rectangle(node.xmin, node.ymin, hw, hh);

		// Draw separating line
		if (isLeftRight) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.line(node.point.x(), node.ymin, node.point.x(), node.ymax);
		}
		else {
			StdDraw.setPenColor(StdDraw.BLUE);
			StdDraw.line(node.xmin, node.point.y(), node.xmax, node.point.y());
		}

        // Draw Point
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledCircle(node.point.x(), node.point.y(), 0.005);

        drawNode(node.lb, !isLeftRight);
		drawNode(node.rt, !isLeftRight);
	}

	private void drawSubtree(Node node) {
	    StdDraw.setPenColor(StdDraw.BLACK);
	    StdDraw.setPenRadius(0.002);
	    StdDraw.setFont();
        StdDraw.textLeft(node.point.x() + 0.03, node.point.y(), node.point.toString());
        drawSubtree(node, node.point.x(), node.point.y());
    }

    private void drawSubtree(Node node, double x, double y) {
        if (node == null) {
            return;
        }
        StdDraw.line(x, y, x - 0.03, y - 0.05);
        drawSubtree(node.lb, x - 0.03, y - 0.05);
        StdDraw.line(x, y, x + 0.03, y - 0.05);
        drawSubtree(node.rt, x + 0.03, y - 0.05);
    }

	// all points that are inside the rectangle 
	public Iterable<Point2D> range(RectHV rect) {
		if (rect == null) {
			throw new NullPointerException("range(): rect should not be null");
		}
		List<Point2D> results = new ArrayList<>();
		findPointsInRange(root, rect, results);
		return results;
	}

	private void findPointsInRange(Node node, RectHV rect, List results) {
	    if (node == null) {
	        return;
        }
        if (rect.contains(node.point)) {
	        results.add(node.point);
        }
        RectHV that = node.getRect();
	    if (rect.intersects(that)) {
	        findPointsInRange(node.lb, rect, results);
	        findPointsInRange(node.rt, rect, results);
        }
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
		    drawSubtree(x);
			return x.point;
		}
	}

	private Node nearestNodeTo(Point2D p) {
		double min = Double.POSITIVE_INFINITY;

		Node x = root;
        Node nearest = x;
        boolean isLeftRight = true;
		while (x != null) {
			double dist = p.distanceSquaredTo(x.point);
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

	public void printNodes() {
		printNodes(root, 0);
	}

	private void printNodes(Node node, int indent) {
        char[] chars = new char[indent];
        Arrays.fill(chars, ' ');
        System.out.print(new String(chars));

		if (node == null) {
			System.out.println(".");
			return;
		}
		System.out.println(node);
        System.out.print(new String(chars));
        System.out.println("left/below: ");
		printNodes(node.lb, indent + 1);
        System.out.print(new String(chars));
        System.out.println("right/above: ");
        printNodes(node.rt, indent + 1);
	}

	// unit testing of the methods (optional) 
	public static void main(String[] args) {
		KdTree tree = new KdTree();
		Point2D p0 = new Point2D(0.7, 0.2);
		Point2D p1 = new Point2D(0.5, 0.4);
		Point2D p2 = new Point2D(0.9, 0.6);
		Point2D p3 = new Point2D(0.2, 0.3);
		Point2D p4 = new Point2D(0.4, 0.7);
        tree.insert(p0);
		tree.insert(p1);
        tree.insert(p4);
        tree.insert(p2);
        tree.insert(p3);
		tree.printNodes();
        StdDraw.enableDoubleBuffering();
        tree.draw();
        StdDraw.show();
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

		private RectHV getRect() {
		    return new RectHV(xmin, xmax, ymin, ymax);
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
	private Node rotateLeft(Node a, boolean leftToRight) {
		assert (isRed(a.rt));
		Node b = a.rt;
		a.rt = b.lb;
		b.lb = a;

		Point2D p0 = b.point;

        // preserve rect for top node
        RectHV r1 = new RectHV(a.xmin, a.xmax, a.ymin, a.ymax);

		if (leftToRight) {
		    // r1 <- (r00, (p0x, r01y))
            a.xmin = b.xmin;    // r00x
            a.ymin = b.ymin;    // r00y
		    a.xmax = p0.x();    // p0x
		    a.ymax = b.ymax;    // r01y
        }
        else {
		    // r1 <- ((r00x, p0y), r11)
		    a.xmin = b.xmin;    // r00x
		    a.ymin = p0.y();    // p0y
            a.xmax = b.xmax;    // r11x
            a.ymax = b.ymax;    // r11y
        }

        // r0 <- r1
        b.xmin = r1.xmin();
        b.xmax = r1.xmax();
        b.ymin = r1.ymin();
        b.ymax = r1.ymax();

		b.isRed = a.isRed;
		a.isRed = true;
		return b;
	}

	// Right rotation. Orient a left-leaning link to (temporarily) lean right
	private Node rotateRight(Node node, boolean leftToRight) {
		assert (isRed(node.lb));
		Node x = node.lb;
		node.lb = x.rt;
		x.rt = node;

        // preserve rect for top node
        RectHV temp = new RectHV(node.xmin, node.xmax, node.ymin, node.ymax);
        x.xmin = temp.xmin();
        x.xmax = temp.xmax();
        x.ymin = temp.ymin();
        x.ymax = temp.ymax();

        if (leftToRight) {
        }
        else {
        }
		x.isRed = node.isRed;
		node.isRed = true;
		return x;
	}

	// Color flip. Recolor to split a (temporary) 4-node
	private void flipColors(Node node) {
		assert !isRed(node);
		assert isRed(node.lb);
		assert isRed(node.rt);
		node.isRed = true;
		node.lb.isRed = false;
		node.rt.isRed = false;
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
			    xmin = h.xmin;
				xmax = h.point.x();
			}
			// inserting below the current node
			else {
			    ymin = h.ymin;
				ymax = h.point.y();
			}
			h.lb = insert(h.lb, point, xmin, xmax, ymin, ymax, !leftToRight);
		}
		else if (cmp > 0) {
			// inserting to the right of the current node
			if (leftToRight) {
				xmin = h.point.x();
				xmax = h.xmax;
			}
			// inserting above the current node
			else {
				ymin = h.point.y();
				ymax = h.ymax;
			}
			h.rt = insert(h.rt, point, xmin, xmax, ymin, ymax, !leftToRight);
		}
		else {
			h.xmin = xmin;
			h.xmax = xmax;
			h.ymin = ymin;
			h.ymax = ymax;
		}

		// TODO maintain balance of tree

//		if (isRed(h.rt) && !isRed(h.lb)){
//			h = rotateLeft(h, leftToRight);
//		}
//		if (isRed(h.lb) && isRed(h.lb.lb)) {
//			h = rotateRight(h, leftToRight);
//		}
//		if (isRed(h.lb) && isRed(h.rt)) {
//			flipColors(h);
//		}
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
