package de.hagen.fernuni.logic.alns;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

public class GreedyRepairTest {
	private GreedyRepair greedyRepair = new GreedyRepair();

	@Test
	public void testRepair() {
		Node start = new Node(30, 50, 0);
		Node end = new Node(50, 30, 0);
		Node v1 = new Node(50, 70, 10);
		Node v2 = new Node(70, 70, 10);
		Node v3 = new Node(90, 50, 10);
		Node v4 = new Node(70, 30, 10);

		Node v5 = new Node(85, 35, 15);
		Node v6 = new Node(85, 65, 20);
		Node v7 = new Node(65, 75, 20);
		Node v8 = new Node(60, 65, 25);
		Node v9 = new Node(60, 75, 30);

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
		edgeList1.add(new Edge(start, v1));
		edgeList1.add(new Edge(v1, v2));
		edgeList1.add(new Edge(v2, v3));
		edgeList1.add(new Edge(v3, v4));
		edgeList1.add(new Edge(v4, end));

		Graph g1 = new Graph(nodeList1, edgeList1);
		Graph result1 = greedyRepair.repair(g1, 132);

		assertTrue("Der Graph ist zulässig",
				result1.tourHasValidDepots() && result1.tourIsUninterrupted() && result1.tourHasNoDuplicates());
		assertTrue("Es wurden 3 Knoten hinzugefügt", result1.getVisitedNodes().size() == 9);
		assertTrue("Es wurden die 3 Knoten mit bestem Score hinzugefügt", result1.getProfitOfTour() == 110);
		assertTrue("Kostenobergrenze wurde nicht überschritten", result1.getCostOfTour() <= 132);

		Graph result2 = greedyRepair.repair(g1, 129);

		assertTrue("Der Graph ist zulässig",
				result2.tourHasValidDepots() && result2.tourIsUninterrupted() && result2.tourHasNoDuplicates());
		assertTrue("Es wurden 2 Knoten hinzugefügt", result2.getVisitedNodes().size() == 8);
		assertTrue("Es wurden die 2 Knoten mit bestem Score hinzugefügt", result2.getProfitOfTour() == 90);
		assertTrue("Kostenobergrenze wurde nicht überschritten", result2.getCostOfTour() <= 129);

		@SuppressWarnings("unchecked")
		ArrayList<Node> nodeList2 = (ArrayList<Node>) nodeList1.clone();
		Node v10 = new Node(60, 100, 1000);
		nodeList2.add(v10);
		Graph g2 = new Graph(nodeList2, edgeList1);
		Graph result3 = greedyRepair.repair(g2, 169);

		assertTrue("Der Graph ist zulässig",
				result3.tourHasValidDepots() && result3.tourIsUninterrupted() && result2.tourHasNoDuplicates());
		assertTrue("Es wurde nur ein Knoten hinzugefügt", result3.getVisitedNodes().size() == 7);
		assertTrue("Es wurde der Knoten mit bestem Score hinzugefügt", result3.getProfitOfTour() == 1040);
		assertTrue("Kostenobergrenze wurde nicht überschritten", result3.getCostOfTour() <= 169);
	}
}
