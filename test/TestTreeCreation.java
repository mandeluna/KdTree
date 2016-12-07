import static org.junit.Assert.*;

import java.util.TreeSet;

import org.junit.Test;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;


public class TestTreeCreation {

//	Test 3a: Insert n non-degenerate points and call contains() with random query points
//	  *  5000 random non-degenerate points in a 10000-by-10000 grid
	@Test
	public void testRandomContains() {
		KdTree tree = new KdTree();
		TreeSet<Point2D> reference = new TreeSet<>();
		int range = 10000;
		for (int i = 0; i < 10000; i++) {
			double x = Math.floor(StdRandom.uniform() * range);
			double y = Math.floor(StdRandom.uniform() * range);
			Point2D p = new Point2D(x, y);
			tree.insert(p);
			reference.add(p);
			if (!tree.contains(p)) {
				System.out.println("Tree does not contain " + p);
			}
			assertTrue(tree.contains(p) == true);
		}
		assertTrue(tree.size() == reference.size());
		for (int i = 0; i < 10000; i++) {
			double x = Math.floor(StdRandom.uniform() * range);
			double y = Math.floor(StdRandom.uniform() * range);
			Point2D p = new Point2D(x, y);
			if (tree.contains(p) && !reference.contains(p)) {
				System.out.println("Tree contains " + p);
			}
			assertTrue(tree.contains(p) == reference.contains(p));
		}
	}

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
}
