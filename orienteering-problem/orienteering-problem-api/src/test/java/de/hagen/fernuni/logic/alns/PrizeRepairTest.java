package de.hagen.fernuni.logic.alns;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

public class PrizeRepairTest {
	private PrizeRepair prizeRepair = new PrizeRepair();

	@Test
	public void testRepair() {
		Node start = new Node(0, 0, 0);
		Node end = new Node(100, 0, 0);
		Node v1 = new Node(50, 50, 10);
		Node v2 = new Node(50, -50, 20);

		ArrayList<Node> nodeList1 = new ArrayList<Node>();
		nodeList1.add(start);
		nodeList1.add(end);
		nodeList1.add(v1);
		nodeList1.add(v2);

		ArrayList<Edge> edgeList1 = new ArrayList<Edge>();
		edgeList1.add(new Edge(start, end));

		Graph g1 = new Graph(nodeList1, edgeList1);
		Graph result1 = prizeRepair.repair(g1, 142);

		assertTrue("Der Graph ist zulässig",
				result1.tourHasValidDepots() && result1.tourIsUninterrupted() && result1.tourHasNoDuplicates());
		assertTrue("Es wurde max. 1 Knoten hinzugefügt", result1.getVisitedNodes().size() <= 3);
		assertTrue("Kostenobergrenze wurde nicht überschritten", result1.getCostOfTour() <= 142);
		if (result1.getVisitedNodes().size() == 3)
			assertTrue("Der Knoten v2 wurde hinzugefügt, falls ein Knoten hinzugefügt wurde",
					result1.getProfitOfTour() == 20);

		Graph result2 = prizeRepair.repair(g1, 120);
		assertTrue("Der Graph ist zulässig",
				result2.tourHasValidDepots() && result2.tourIsUninterrupted() && result2.tourHasNoDuplicates());
		assertTrue("Es wurde kein Knoten hinzugefügt", result2.getVisitedNodes().size() == 2);
		assertTrue("Kostenobergrenze wurde nicht überschritten", result2.getCostOfTour() <= 120);
	}
}
