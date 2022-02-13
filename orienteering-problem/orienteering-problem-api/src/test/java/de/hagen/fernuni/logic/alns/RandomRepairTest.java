package de.hagen.fernuni.logic.alns;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

public class RandomRepairTest {
	private RandomRepair randomRepair = new RandomRepair();
	
	@Test
	public void testRepair() {
		Node start = new Node(0, 0, 0);
		Node end = new Node(100, 0, 0);
		Node v1 = new Node(10, 0, 10);
		Node v2 = new Node(20, 0, 20);
		Node v3 = new Node(30, 0, 30);
		Node v4 = new Node(40, 0, 40);
		Node v5 = new Node(50, 0, 50);
		Node v6 = new Node(60, 0, 60);
		Node v7 = new Node(70, 0, 70);
		Node v8 = new Node(80, 0, 80);
		Node v9 = new Node(90, 0, 90);

		ArrayList<Node> nodeList1 = new ArrayList<Node>();
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

		ArrayList<Edge> edgeList1 = new ArrayList<Edge>();
		edgeList1.add(new Edge(start, v3));
		edgeList1.add(new Edge(v3, v5));
		edgeList1.add(new Edge(v5, end));

		Graph g1 = new Graph(nodeList1, edgeList1);
		Graph result1 = randomRepair.repair(g1, 100);

		assertTrue("Der Graph ist zulässig",
				result1.tourHasValidDepots() && result1.tourIsUninterrupted() && result1.tourHasNoDuplicates());
		
		assertTrue("Es wurden bis zu 7 Knoten hinzugefügt", result1.getUnvisitedNodes().size() <= 7);
		assertTrue("Kostenobergrenze wurde nicht überschritten", result1.getCostOfTour() <= 100);
	
		
		for (Edge e : result1.getE()) {
			assertTrue("Knoten wurden an beste Position eingefügt",
					e.getStartNode().getX() < e.getEndNode().getX());
		}

		Node v10 = new Node(20, 10, 100);
		Node v11 = new Node(40, -10, 110);
		Node v12 = new Node(60, 10, 120);
		Node v13 = new Node(80, -10, 130);
		Node v14 = new Node(100, 10, 140);

		ArrayList<Node> nodeList2 = new ArrayList<Node>();
		nodeList2.add(start);
		nodeList2.add(end);
		nodeList2.add(v10);
		nodeList2.add(v11);
		nodeList2.add(v12);
		nodeList2.add(v13);
		nodeList2.add(v14);

		ArrayList<Edge> edgeList2 = new ArrayList<Edge>();
		edgeList2.add(new Edge(start, v10));
		edgeList2.add(new Edge(v10, v11));
		edgeList2.add(new Edge(v11, end));

		Graph g2 = new Graph(nodeList2, edgeList2);
		Graph result2 = randomRepair.repair(g2, 100);
		ArrayList<Node> visitedNodes = result2.getVisitedNodes();

		assertTrue("Der Graph ist zulässig",
				result2.tourHasValidDepots() && result2.tourIsUninterrupted() && result2.tourHasNoDuplicates());
		assertTrue("Kostenobergrenze wurde nicht überschritten", result2.getCostOfTour() <= 100);
		assertTrue("Es werden nur Start- und Endknoten besucht",
				visitedNodes.size() == 2 && visitedNodes.contains(start) && visitedNodes.contains(end));

	}
}
