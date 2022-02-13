package de.hagen.fernuni.logic.alns;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

public class ClusterRepairTest {
	private ClusterRepair clusterRepair = new ClusterRepair();

	@Test
	public void testRepair() {
		ArrayList<Node> nodeList = new ArrayList<Node>();
		ArrayList<Edge> edgeList = new ArrayList<Edge>();

		// Cluster #1
		Node v0 = new Node(0, 0, 0);
		Node v1 = new Node(5, 0, 1);
		ArrayList<Node> cluster1Reference = new ArrayList<Node>();
		cluster1Reference.add(v0);
		cluster1Reference.add(v1);

		// Cluster #2
		Node v2 = new Node(20, 0, 2);
		Node v3 = new Node(25, 0, 3);
		ArrayList<Node> cluster2Reference = new ArrayList<Node>();
		cluster2Reference.add(v2);
		cluster2Reference.add(v3);

		// Cluster #3
		Node v4 = new Node(0, 20, 0);
		Node v5 = new Node(5, 20, 5);
		ArrayList<Node> cluster3Reference = new ArrayList<Node>();
		cluster3Reference.add(v4);
		cluster3Reference.add(v5);

		// Cluster #4
		Node v6 = new Node(20, 20, 6);
		Node v7 = new Node(25, 20, 7);
		ArrayList<Node> cluster4Reference = new ArrayList<Node>();
		cluster4Reference.add(v6);
		cluster4Reference.add(v7);

		// Cluster #5
		Node v8 = new Node(11, 8, 8);
		Node v9 = new Node(15, 8, 9);
		Node v10 = new Node(11, 12, 10);
		Node v11 = new Node(15, 12, 11);
		ArrayList<Node> cluster5Reference = new ArrayList<Node>();
		cluster5Reference.add(v8);
		cluster5Reference.add(v9);
		cluster5Reference.add(v10);
		cluster5Reference.add(v11);

		nodeList.add(v0);
		nodeList.add(v4);
		nodeList.add(v1);
		nodeList.add(v2);
		nodeList.add(v3);
		nodeList.add(v5);
		nodeList.add(v6);
		nodeList.add(v7);
		nodeList.add(v8);
		nodeList.add(v9);
		nodeList.add(v10);
		nodeList.add(v11);

		edgeList.add(new Edge(v0, v8));
		edgeList.add(new Edge(v8, v4));

		Graph g1 = new Graph(nodeList, edgeList);

		Graph result1 = clusterRepair.repair(g1, 1000);
		ArrayList<Node> visitedNodes = result1.getVisitedNodes();
		int visitedNodesSize = visitedNodes.size();

		assertTrue("Der Graph ist zulässig",
				result1.tourHasValidDepots() && result1.tourIsUninterrupted() && result1.tourHasNoDuplicates());
		assertTrue("Es wurden bis zu 3 Knoten hinzugefügt", visitedNodesSize <= 6);

		boolean onlyOneClusterAdded = false;
		// Cluster 1 ausgewählt - nur ein Knoten wurde hinzugefügt
		if (visitedNodes.containsAll(cluster1Reference) && visitedNodesSize == 4) {
			onlyOneClusterAdded = true;
		}
		// Cluster 2 ausgewählt - nur ein Knoten wurde hinzugefügt
		if (visitedNodes.containsAll(cluster2Reference) && visitedNodesSize == 5) {
			onlyOneClusterAdded = true;
		}
		// Cluster 3 ausgewählt - nur ein Knoten wurde hinzugefügt
		if (visitedNodes.containsAll(cluster3Reference) && visitedNodesSize == 4) {
			onlyOneClusterAdded = true;
		}
		// Cluster 4 ausgewählt - nur ein Knoten wurde hinzugefügt
		if (visitedNodes.containsAll(cluster4Reference) && visitedNodesSize == 5) {
			onlyOneClusterAdded = true;
		}
		// Cluster 6 ausgewählt - nur ein Knoten wurde hinzugefügt
		if (visitedNodes.containsAll(cluster5Reference) && visitedNodesSize == 6) {
			onlyOneClusterAdded = true;
		}

		assertTrue("Nur ein Cluster wurde hinzugefügt", onlyOneClusterAdded);

		Graph result2 = clusterRepair.repair(g1, 30);

		assertTrue("Der Graph ist zulässig",
				result2.tourHasValidDepots() && result2.tourIsUninterrupted() && result2.tourHasNoDuplicates());
		assertTrue("Die Kostenobergrenze wurde eingehalten", result2.getCostOfTour() <= 30);
	}
}
