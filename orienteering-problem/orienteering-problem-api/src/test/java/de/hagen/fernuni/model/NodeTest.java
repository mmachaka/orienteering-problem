package de.hagen.fernuni.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

public class NodeTest {

	@Test
	public void nodesAreEqualAndNotIdentical() {
		Node v1 = new Node(10, 20, 30);
		Node v2 = new Node(10, 20, 30);

		assertEquals("Knoten sind gleich", v1, v2);
		assertFalse("Knoten sind nicht identisch", v1 == v2);		
	}

	@Test
	public void testGetProfit() {
		Node v1 = new Node(10, 20, 30);
		Node v2 = new Node(10, 20, 0);

		assertTrue("Profit ist 30", v1.getProfit() == 30);
		assertTrue("Profit ist 0", v2.getProfit() == 0);
	}

	@Test
	public void testCompareTo() {
		Node v1 = new Node(10, 20, 30);
		Node v2 = new Node(20, 30, 10);
		Node v3 = new Node(30, 40, 10);
		Node v4 = new Node(40, 50, 0);
		
		ArrayList<Node> nodeList = new ArrayList<Node>();
		nodeList.add(v2);
		nodeList.add(v4);
		nodeList.add(v1);
		nodeList.add(v3);
		
		assertTrue("v1 ist größer als v2", v1.compareTo(v2) > 0);
		assertTrue("v2 ist kleiner als v1", v2.compareTo(v1) < 0);
		assertTrue("v2 ist gleich v3", v2.compareTo(v3) == 0);
		
		assertEquals("v1 ist profitabelster Knoten", v1, Collections.max(nodeList));
		assertEquals("v4 ist unprofitabelster Knoten", v4, Collections.min(nodeList));
	}
}
