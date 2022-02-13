package de.hagen.fernuni.logic.alns;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

public class RandomRemoveTest {
	private RandomRemove randomRemove = new RandomRemove();
	
	@Test
	public void testDestroy() {
		Node start = new Node(0, 0, 0);
		Node end = new Node(0, 0, 0);
		Node v1 = new Node(10, 0, 10);
		Node v2 = new Node(20, 0, 20);
		Node v3 = new Node(30, 0, 30);
		Node v4 = new Node(40, 0, 40);
		Node v5 = new Node(50, 0, 50);
		Node v6 = new Node(60, 0, 50);
		Node v7 = new Node(70, 0, 50);

		ArrayList<Node> nodeList = new ArrayList<Node>();
		nodeList.add(start);
		nodeList.add(end);
		nodeList.add(v1);
		nodeList.add(v2);
		nodeList.add(v3);
		nodeList.add(v4);
		nodeList.add(v5);
		nodeList.add(v6);
		nodeList.add(v7);

		ArrayList<Edge> edgeList1 = new ArrayList<Edge>();
		edgeList1.add(new Edge(start, v1));
		edgeList1.add(new Edge(v1, v2));
		edgeList1.add(new Edge(v2, v3));
		edgeList1.add(new Edge(v3, v4));
		edgeList1.add(new Edge(v4, v5));
		edgeList1.add(new Edge(v5, end));

		Graph g1 = new Graph(nodeList, edgeList1);

		Graph result1 = randomRemove.destroy(g1, 1);
		assertTrue("Alle Knoten bis auf Depots wurden aus der Tour entfernt", result1.getVisitedNodes().size() == 2);
		assertTrue("Der erzielte Profit wurde verringert", result1.getProfitOfTour() < g1.getProfitOfTour());
		assertTrue("Der Graph ist zulässig", result1.tourHasValidDepots() && result1.tourIsUninterrupted()&& result1.tourHasNoDuplicates());

		Graph result2 = randomRemove.destroy(g1, 0.5);
		assertTrue("Es wurden zwei Knoten aus der Tour entfernt", result2.getVisitedNodes().size() == 5);
		assertTrue("Der erzielte Profit wurde verringert", result2.getProfitOfTour() < g1.getProfitOfTour());
		assertTrue("Der Graph ist zulässig", result2.tourHasValidDepots() && result2.tourIsUninterrupted()&& result2.tourHasNoDuplicates());

		ArrayList<Edge> edgeList2 = new ArrayList<Edge>();
		edgeList2.add(new Edge(start, end));
		Graph g2 = new Graph(nodeList, edgeList2);
		Graph result3 = randomRemove.destroy(g2, 1);
		assertTrue("Keine Knoten wurden aus der Tour entfernt", result3.getVisitedNodes().size() == 2);
		assertTrue("Der Graph ist zulässig", result3.tourHasValidDepots() && result3.tourIsUninterrupted()&& result3.tourHasNoDuplicates());
	}
}
