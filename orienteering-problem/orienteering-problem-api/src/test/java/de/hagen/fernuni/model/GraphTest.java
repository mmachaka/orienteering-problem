package de.hagen.fernuni.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class GraphTest {
	private ArrayList<Node> nodeList1, nodeList2, nodeList3;
	private ArrayList<Edge> edgeList1, edgeList2, edgeList3, edgeList4, edgeList5, edgeList6;
	private Node start, end, v1, v2, v3, v4, v5, v6, v7, v8, v9;

	@Before
	public void init() throws Exception {
		nodeList1 = new ArrayList<Node>();
		nodeList2 = new ArrayList<Node>();
		nodeList3 = new ArrayList<Node>();
		edgeList1 = new ArrayList<Edge>();
		edgeList2 = new ArrayList<Edge>();
		edgeList3 = new ArrayList<Edge>();
		edgeList4 = new ArrayList<Edge>();
		edgeList5 = new ArrayList<Edge>();
		edgeList6 = new ArrayList<Edge>();

		start = new Node(0, 0, 0);
		end = new Node(100, 0, 0);

		v1 = new Node(10, 0, 1);
		v2 = new Node(20, 0, 2);
		v3 = new Node(30, 0, 3);
		v4 = new Node(40, 0, 4);
		v5 = new Node(50, 0, 5);
		v6 = new Node(60, 0, 6);
		v7 = new Node(70, 0, 7);
		v8 = new Node(80, 0, 8);
		v9 = new Node(90, 0, 9);
	}


	@SuppressWarnings("unchecked")
	@Test
	public void graphsAreEqualAndNotIdentical() {
		nodeList1.add(start);
		nodeList1.add(end);
		nodeList1.add(v1);
		edgeList1.add(new Edge(start, end));
		Graph g1 = new Graph(nodeList1, edgeList1);

		nodeList2.add(start);
		nodeList2.add(end);
		nodeList2.add(v1);
		edgeList2.add(new Edge(start, end));
		Graph g2 = new Graph(nodeList2, edgeList2);

		assertEquals("Graphen sind gleich", g1, g2);
		assertFalse("Graphen sind nicht identisch", g1 == g2);

		nodeList3 = (ArrayList<Node>) nodeList1.clone();
		edgeList3 = (ArrayList<Edge>) edgeList1.clone();
		Graph g3 = new Graph(nodeList3, edgeList3);

		assertEquals("Graphen sind gleich", g1, g3);
		assertFalse("Graphen sind nicht identisch", g1 == g3);

		Graph g4 = new Graph(g1);
		Graph g5 = new Graph(g1);

		assertEquals("Graphen sind gleich", g4, g5);
		assertFalse("Graphen sind nicht identisch", g4 == g5);
	}

	@Test
	public void testTourIsUninterrupted() {
		nodeList1.add(start);
		nodeList1.add(end);
		nodeList1.add(v1);
		nodeList1.add(v2);
		nodeList1.add(v3);
		nodeList1.add(v4);
		edgeList1.add(new Edge(start, v1));
		edgeList1.add(new Edge(v1, v2));
		edgeList1.add(new Edge(v3, v4));
		edgeList1.add(new Edge(v2, v3));
		edgeList1.add(new Edge(v4, end));
		Graph g1 = new Graph(nodeList1, edgeList1);

		assertFalse("Tour ist unterbrochen", g1.tourIsUninterrupted());

		edgeList2.add(new Edge(start, v1));
		edgeList2.add(new Edge(v1, v2));
		edgeList2.add(new Edge(v2, v3));
		edgeList2.add(new Edge(v3, v4));
		edgeList2.add(new Edge(v4, end));
		Graph g2 = new Graph(nodeList1, edgeList2);

		assertTrue("Tour ist nicht unterbrochen", g2.tourIsUninterrupted());
	}

	@Test
	public void testTourHasValidDepots() {
		nodeList1.add(start);
		nodeList1.add(end);
		nodeList1.add(v1);
		nodeList1.add(v2);
		nodeList1.add(v3);
		nodeList1.add(v4);
		edgeList1.add(new Edge(start, v1));
		edgeList1.add(new Edge(v1, v2));
		edgeList1.add(new Edge(v2, v3));
		edgeList1.add(new Edge(v3, v4));
		edgeList1.add(new Edge(v4, end));
		Graph g1 = new Graph(nodeList1, edgeList1);

		assertTrue("Start- und Endknoten sind korrekt in der Tour eingebunden", g1.tourHasValidDepots());

		edgeList2.add(new Edge(v1, start));
		edgeList2.add(new Edge(start, v2));
		edgeList2.add(new Edge(v2, v3));
		edgeList2.add(new Edge(v3, v4));
		edgeList2.add(new Edge(v4, end));
		Graph g2 = new Graph(nodeList1, edgeList2);

		assertFalse("Tour startet nicht mit Startknoten", g2.tourHasValidDepots());

		edgeList3.add(new Edge(start, end));
		edgeList3.add(new Edge(end, v2));
		edgeList3.add(new Edge(v2, v3));
		edgeList3.add(new Edge(v3, v4));
		edgeList3.add(new Edge(v4, v1));
		Graph g3 = new Graph(nodeList1, edgeList3);

		assertFalse("Tour endet nicht mit Endknoten", g3.tourHasValidDepots());

		nodeList2.add(start);
		nodeList2.add(end);
		Graph g4 = new Graph(nodeList2, edgeList4);

		assertTrue("Leere Tour, keine Depots notwendig", g4.tourHasValidDepots());

		Node start2 = new Node(10, 10, 0);
		Node end2 = new Node(10, 10, 0);
		nodeList3.add(start2);
		nodeList3.add(end2);
		edgeList5.add(new Edge(start2, end2));
		Graph g5 = new Graph(nodeList3, edgeList5);

		assertTrue("Start- und Endknoten sind gleich, Tour hat nur eine Kante", g5.tourHasValidDepots());

		edgeList6.add(new Edge(end2, start2));
		Graph g6 = new Graph(nodeList3, edgeList6);

		assertFalse("Start- und Endknoten sind gleich, Tour beginnt mit Endknoten", g6.tourHasValidDepots());

	}
	
	@Test
	public void testTourHasNoDuplicates(){
		nodeList1.add(start);
		nodeList1.add(end);
		nodeList1.add(v1);
		nodeList1.add(v2);
		nodeList1.add(v3);
		nodeList1.add(v4);
		
		start = new Node(0, 0, 0);
		end = new Node(0, 0, 0);
		
		edgeList1.add(new Edge(start, v1));
		edgeList1.add(new Edge(v1, v2));
		edgeList1.add(new Edge(v2, v3));
		edgeList1.add(new Edge(v3, end));
		
		edgeList2.add(new Edge(start, v1));
		edgeList2.add(new Edge(v1, v2));
		edgeList2.add(new Edge(v2, v3));
		edgeList2.add(new Edge(v3, v4));
		edgeList2.add(new Edge(v4, v1));
		edgeList2.add(new Edge(v1, end));
		
		Graph g1 = new Graph(nodeList1,edgeList1);
		Graph g2 = new Graph(nodeList1,edgeList2);
		
		assertTrue("Graph g1 besucht keinen Knoten mehrfach",g1.tourHasNoDuplicates());
		assertFalse("Graph g2 besucht Knoten mehrfach",g2.tourHasNoDuplicates());		
	}
	

	@Test
	public void testGetProfitOfTour() {
		nodeList1.add(start);
		nodeList1.add(end);
		nodeList1.add(v1);
		nodeList1.add(v2);
		nodeList1.add(v3);
		nodeList1.add(v4);
		Graph g1 = new Graph(nodeList1, edgeList1);

		assertTrue("Leere Tour liefert keinen Profit", g1.getProfitOfTour() == 0);

		edgeList1.add(new Edge(start, end));
		Graph g2 = new Graph(nodeList1, edgeList1);

		assertTrue("Tour mit einer Kante zwischen Start- und Endknoten liefert keinen Profit",
				g2.getProfitOfTour() == 0);

		edgeList2.add(new Edge(start, v1));
		edgeList2.add(new Edge(v1, v2));
		edgeList2.add(new Edge(v2, v3));
		edgeList2.add(new Edge(v3, end));
		Graph g3 = new Graph(nodeList1, edgeList2);
		assertTrue("Tour mit Profit > 0", g3.getProfitOfTour() == 6.0);
	}

	@Test
	public void testGetTotalProfit() {
		nodeList1.add(start);
		nodeList1.add(end);
		nodeList1.add(v1);
		nodeList1.add(v2);
		nodeList1.add(v3);
		nodeList1.add(v4);
		Graph g1 = new Graph(nodeList1, edgeList1);

		assertTrue("TotalProfit eines Graphen mit mehreren Knoten", g1.getTotalProfit() == 10);

		nodeList2.add(start);
		nodeList2.add(end);
		Graph g2 = new Graph(nodeList2, edgeList2);

		assertTrue("TotalProfit eines Graphen, der nur Depots enthält", g2.getProfitOfTour() == 0);

		Graph g3 = new Graph(nodeList3, edgeList3);
		assertTrue("TotalProfit eines leeren Graphen", g3.getProfitOfTour() == 0);
	}

	@Test
	public void testGetCostOfTour() {
		nodeList1.add(start);
		nodeList1.add(end);
		nodeList1.add(v1);
		nodeList1.add(v2);
		nodeList1.add(v3);
		nodeList1.add(v4);
		edgeList1.add(new Edge(start, v1));
		edgeList1.add(new Edge(v1, v2));
		edgeList1.add(new Edge(v2, v3));
		edgeList1.add(new Edge(v3, v4));
		edgeList1.add(new Edge(v4, end));
		Graph g1 = new Graph(nodeList1, edgeList1);

		assertTrue("Kosten der Tour mit mehreren Zwischenstopps", g1.getCostOfTour() == 100.0);
		assertTrue(g1.getCostOfTour() == Graph.getCostOfTour(edgeList1));

		nodeList2.add(start);
		nodeList2.add(end);
		edgeList2.add(new Edge(start, end));
		Graph g2 = new Graph(nodeList2, edgeList2);

		assertTrue("Kosten der Tour ohne Zwischenstopps", g2.getCostOfTour() == 100.0);
		assertTrue(g2.getCostOfTour() == Graph.getCostOfTour(edgeList2));

		Node start2 = new Node(10, 10, 0);
		Node end2 = new Node(10, 10, 0);
		nodeList3.add(start2);
		nodeList3.add(end2);
		edgeList3.add(new Edge(start2, end2));
		Graph g3 = new Graph(nodeList3, edgeList3);

		assertTrue("Kosten der Tour - Start und Endknoten sind gleich", g3.getCostOfTour() == 0);
		assertTrue(g3.getCostOfTour() == Graph.getCostOfTour(edgeList3));
	}

	@Test
	public void testGetVisitedNodes() {
		ArrayList<Node> nodetListReference = new ArrayList<Node>();
		nodetListReference.add(start);
		nodetListReference.add(end);
		nodetListReference.add(v4);
		nodetListReference.add(v3);
		nodetListReference.add(v2);
		nodetListReference.add(v1);

		nodeList1.add(start);
		nodeList1.add(end);
		nodeList1.add(v1);
		nodeList1.add(v2);
		nodeList1.add(v3);
		nodeList1.add(v4);
		nodeList1.add(v5);
		nodeList1.add(v6);
		nodeList1.add(v7);
		nodeList1.add(v8);
		nodeList1.add(v9);
		edgeList1.add(new Edge(start, v1));
		edgeList1.add(new Edge(v1, v2));
		edgeList1.add(new Edge(v2, v3));
		edgeList1.add(new Edge(v3, v4));
		edgeList1.add(new Edge(v4, end));
		Graph g1 = new Graph(nodeList1, edgeList1);

		assertTrue("Alle besuchten Knoten der Tour sind enthalten",
				nodetListReference.containsAll(g1.getVisitedNodes()));
		assertTrue("Es sind nur nur besuchte Knoten enthalten",
				g1.getVisitedNodes().containsAll(nodetListReference));
		assertEquals(g1.getVisitedNodes(), Graph.getVisitedNodes(edgeList1));
		assertTrue(g1.getVisitedNodes().size() == 6);

		Graph g2 = new Graph(nodeList1, edgeList2);
		assertTrue("Graph ohne Tour enthält keine besuchten Knoten", g2.getVisitedNodes().size() == 0);
	}

	@Test
	public void testGetUnvisitedNodes() {
		ArrayList<Node> nodetListReference = new ArrayList<Node>();
		nodetListReference.add(v5);
		nodetListReference.add(v6);
		nodetListReference.add(v7);
		nodetListReference.add(v8);
		nodetListReference.add(v9);

		nodeList1.add(start);
		nodeList1.add(end);
		nodeList1.add(v1);
		nodeList1.add(v2);
		nodeList1.add(v3);
		nodeList1.add(v4);
		nodeList1.add(v5);
		nodeList1.add(v6);
		nodeList1.add(v7);
		nodeList1.add(v8);
		nodeList1.add(v9);
		edgeList1.add(new Edge(start, v1));
		edgeList1.add(new Edge(v1, v2));
		edgeList1.add(new Edge(v2, v3));
		edgeList1.add(new Edge(v3, v4));
		edgeList1.add(new Edge(v4, end));
		Graph g1 = new Graph(nodeList1, edgeList1);

		assertTrue("Alle unbesuchten Knoten der Tour sind enthalten",
				nodetListReference.containsAll(g1.getUnvisitedNodes()));
		assertTrue("Es sind nur nur unbesuchte Knoten enthalten",
				g1.getUnvisitedNodes().containsAll(nodetListReference));
		assertTrue(g1.getUnvisitedNodes().size() == 5);

		Graph g2 = new Graph(nodeList1, edgeList2);
		assertTrue("Alle Knoten des Graphen ohne Tour sind enthalten", g2.getUnvisitedNodes().size() == 11);
	}

	@Test
	public void testInsertionIsViable() {
		edgeList1.add(new Edge(start, v1));
		edgeList1.add(new Edge(v1, v2));
		edgeList1.add(new Edge(v2, v3));
		edgeList1.add(new Edge(v3, v4));
		edgeList1.add(new Edge(v4, end));

		assertTrue("Einfügen des Knotens ist möglich", Graph.insertionIsViable(v5, new Edge(v4, end), edgeList1, 100));
		assertFalse("Einfügen des Knotens ist nicht möglich",
				Graph.insertionIsViable(v5, new Edge(v1, v2), edgeList1, 100));
	}

	@Test
	public void testRestorefeasibility() {
		v1 = new Node(25, 25, 10);
		v2 = new Node(50, 50, 10);
		v3 = new Node(-50, -50, 10);
		v4 = new Node(75, 25, 10);
		v5 = new Node(75, -25, 10);
		nodeList1.add(start);
		nodeList1.add(end);
		nodeList1.add(v1);
		nodeList1.add(v2);
		nodeList1.add(v3);
		nodeList1.add(v4);
		nodeList1.add(v5);

		edgeList1.add(new Edge(start, v1));
		edgeList1.add(new Edge(v1, v2));
		edgeList1.add(new Edge(v2, v3));
		edgeList1.add(new Edge(v3, v4));
		edgeList1.add(new Edge(v4, v5));
		edgeList1.add(new Edge(v5, end));

		ArrayList<Edge> oneEdgeRemovedReference = new ArrayList<Edge>();
		oneEdgeRemovedReference.add(new Edge(start, v1));
		oneEdgeRemovedReference.add(new Edge(v1, v2));
		oneEdgeRemovedReference.add(new Edge(v2, v4));
		oneEdgeRemovedReference.add(new Edge(v4, v5));
		oneEdgeRemovedReference.add(new Edge(v5, end));

		ArrayList<Edge> twoEdgesRemovedReference = new ArrayList<Edge>();
		twoEdgesRemovedReference.add(new Edge(start, v1));
		twoEdgesRemovedReference.add(new Edge(v1, v2));
		twoEdgesRemovedReference.add(new Edge(v2, v4));
		twoEdgesRemovedReference.add(new Edge(v4, end));

		ArrayList<Edge> result1 = Graph.restorefeasibility(edgeList1, 440);
		Graph g1 = new Graph(nodeList1, result1);

		assertTrue("Eine Kante entfernt - Kosten sind gleich",
				Graph.getCostOfTour(result1) == Graph.getCostOfTour(oneEdgeRemovedReference));
		assertTrue("Eine Kante entfernt - Besuchte Knoten sind gleich",
				(Graph.getVisitedNodes(result1).containsAll(Graph.getVisitedNodes(oneEdgeRemovedReference))
						&& Graph.getVisitedNodes(oneEdgeRemovedReference)
								.containsAll(Graph.getVisitedNodes(result1))));
		assertTrue("Erzeugter Graph ist gültig", g1.tourHasValidDepots() && g1.tourIsUninterrupted());

		ArrayList<Edge> result2 = Graph.restorefeasibility(edgeList1, 190);
		Graph g2 = new Graph(nodeList1, result2);

		assertTrue("Zwei Kanten entfernt - Kosten sind gleich",
				Graph.getCostOfTour(result2) == Graph.getCostOfTour(twoEdgesRemovedReference));
		assertTrue("Zwei Kanten entfernt - Besuchte Knoten sind gleich",
				(Graph.getVisitedNodes(result2).containsAll(Graph.getVisitedNodes(twoEdgesRemovedReference))
						&& Graph.getVisitedNodes(twoEdgesRemovedReference)
								.containsAll(Graph.getVisitedNodes(result2))));
		assertTrue("Erzeugter Graph ist gültig", g2.tourHasValidDepots() && g2.tourIsUninterrupted());

		ArrayList<Edge> result3 = Graph.restorefeasibility(edgeList1, 500);
		Graph g3 = new Graph(nodeList1, result3);

		assertTrue("Keine Kante entfernt - Kosten sind unverändert",
				Graph.getCostOfTour(result3) == Graph.getCostOfTour(edgeList1));
		assertTrue("Keine Kanten entfernt - Besuchte Knoten sind unverändert",
				(Graph.getVisitedNodes(result3).containsAll(Graph.getVisitedNodes(edgeList1))
						&& Graph.getVisitedNodes(edgeList1).containsAll(Graph.getVisitedNodes(result3))));
		assertTrue("Erzeugter Graph ist gültig", g3.tourHasValidDepots() && g3.tourIsUninterrupted());
	}
}
