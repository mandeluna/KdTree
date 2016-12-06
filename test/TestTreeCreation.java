import static org.junit.Assert.*;

import java.util.TreeSet;

import org.junit.Test;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;


public class TestTreeCreation {

	@Test
	public void testDuplicateInsert() {
		for (int range = 10; range <= 100000; range *= 10) {
			StdOut.printf("%d random general points in a %d-by-%d grid\n", range, range, range);
			KdTree tree = new KdTree();
			assertTrue(tree.size() == 0);
			TreeSet<Point2D> reference = new TreeSet<>();
			for (int i=0; i < range; i++) {
				double x = Math.floor(StdRandom.uniform() * range);
				double y = Math.floor(StdRandom.uniform() * range);
				Point2D p = new Point2D(x, y);
				tree.insert(p);
				reference.add(p);
				assertTrue(tree.size() == reference.size());
			}
		}
	}

	@Test
	public void testRandomInsert() {
		KdTree tree = new KdTree();
		assertTrue(tree.size() == 0);
		for (int i=0; i < 100000; i++) {
			Point2D p = new Point2D(StdRandom.uniform(), StdRandom.uniform());
			tree.insert(p);
			assertTrue(tree.size() == i+1);
		}
	}

	@Test
	public void testRotateRight() {
		KdTree tree = new KdTree();
		assertTrue(tree.size() == 0);

		Point2D p = new Point2D(0.5, 0.5);
		assertFalse(tree.contains(p));
		tree.insert(p);
		assertTrue(tree.size() == 1);
		assertTrue(tree.contains(p));

		Point2D q = new Point2D(0.25, 0.25);
		// adding q to the left of p so q's parent link should be red
		assertTrue(q.compareTo(p) < 0);
		assertFalse(tree.contains(q));
		tree.insert(q);
		assertTrue(tree.size() == 2);
		assertTrue(tree.contains(q));
		assertTrue(tree.contains(p));

		Point2D r = new Point2D(0.125, 0.125);
		// adding r to the left of q so r's parent link should be red
		assertTrue(r.compareTo(q) < 0);
		assertFalse(tree.contains(r));
		tree.insert(r);
		assertTrue(tree.size() == 3);
		assertTrue(tree.contains(r));
		assertTrue(tree.contains(q));
		assertTrue(tree.contains(p));
	}

	@Test
	public void testRotateLeft() {
		KdTree tree = new KdTree();
		assertTrue(tree.size() == 0);

		Point2D p = new Point2D(0.125, 0.125);
		assertFalse(tree.contains(p));
		tree.insert(p);
		assertTrue(tree.size() == 1);
		assertTrue(tree.contains(p));

		Point2D q = new Point2D(0.25, 0.25);
		// adding q to the right of p so q's parent link should be black
		assertTrue(q.compareTo(p) > 0);
		assertFalse(tree.contains(q));
		tree.insert(q);
		assertTrue(tree.size() == 2);
		assertTrue(tree.contains(q));
		assertTrue(tree.contains(p));

		Point2D r = new Point2D(0.5, 0.5);
		// adding r to the right of q so r's parent link should be black
		assertTrue(r.compareTo(q) > 0);
		assertFalse(tree.contains(r));
		tree.insert(r);
		assertTrue(tree.size() == 3);
		assertTrue(tree.contains(r));
		assertTrue(tree.contains(q));
		assertTrue(tree.contains(p));
	}

}
