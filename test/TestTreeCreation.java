import static org.junit.Assert.*;

import org.junit.Test;

import edu.princeton.cs.algs4.Point2D;


public class TestTreeCreation {

	@Test
	public void testAddPoints() {
		KdTree tree = new KdTree();
		Point2D p = new Point2D(0.5, 0.5);
		tree.insert(p);
		assertTrue(tree.contains(p));
		Point2D q = new Point2D(0.25, 0.25);
		assertFalse(tree.contains(q));
		tree.insert(q);
		assertTrue(tree.contains(q));
		assertTrue(tree.contains(p));
		Point2D r = new Point2D(0.125, 0.125);
		assertFalse(tree.contains(r));
		tree.insert(r);
		assertTrue(tree.contains(r));
		assertTrue(tree.contains(q));
		assertTrue(tree.contains(p));
	}

}
