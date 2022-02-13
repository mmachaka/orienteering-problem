package de.hagen.fernuni.logic.alns;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

public class ALNSTest {

	@Test
	public void testSolve() {
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

		Graph g1 = new Graph(nodeList1, new ArrayList<Edge>());
		Graph result1 = new ALNS(g1, 99, 100, 0.5, 0.5, 0).solve();

		assertTrue("Es wurde kein Knoten hinzugefügt, da Tmax zu klein ist", result1.getVisitedNodes().isEmpty());

		ArrayList<Node> nodeList2 = new ArrayList<Node>();
		nodeList2.add(start);
		nodeList2.add(end);
		nodeList2.add(v1);
		nodeList2.add(v2);
		nodeList2.add(v3);
		nodeList2.add(v4);
		nodeList2.add(v5);
		nodeList2.add(v6);
		nodeList2.add(v7);
		nodeList2.add(v8);
		nodeList2.add(v9);

		Graph g2 = new Graph(nodeList2, new ArrayList<Edge>());
		Graph result2 = new ALNS(g2, 200, 100, 0.5, 0.5, 0).solve();

		assertTrue("ALNS konnte alle Knoten hinzufügen", result2.getProfitOfTour() == 450);
		assertTrue("Graph ist gültig",
				result2.tourHasNoDuplicates() && result2.tourHasValidDepots() && result2.tourIsUninterrupted());

	}
}
