package de.hagen.fernuni.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EdgeTest {
	
	@Test
	public void edgesAreEqualAndNotIdentical() {
		Node v1 = new Node(1,0,10);
		Node v2 = new Node(2,0,5);
		Node v3 = new Node(2,0,0);
		Edge e1 = new Edge(v1,v2);
		Edge e2 = new Edge(v1,v2);
		Edge e3 = new Edge(v1,v3);
		
		assertEquals("Kanten sind gleich", e1, e2);
		assertFalse("Kanten sind nicht identisch", e1 == e2);	
		
		assertEquals("Kanten sind gleich", e1, e3);
		assertFalse("Kanten sind nicht identisch", e1 == e2);	
	}
	
	@Test
	public void testGetDistance() {
		Node v1 = new Node(10,10,10);
		Node v2 = new Node(10,0,5);
		Node v3 = new Node(0,10,0);
		Edge e1 = new Edge(v1,v2);
		Edge e2 = new Edge(v1,v3);
		Edge e3 = new Edge(v1,v1);
		
		assertTrue(Edge.getDistance(v1, v2)==10);
		assertTrue(Edge.getDistance(v1, v3)==10);
		assertTrue(Edge.getDistance(v1, v1)==0);
		
		assertTrue(e1.getDistance()==10);
		assertTrue(e2.getDistance()==10);
		assertTrue(e3.getDistance()==0);	
	}
}
