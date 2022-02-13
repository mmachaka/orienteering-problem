package de.hagen.fernuni.logic.alns;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

public class ClusterRemoveTest {
	private ClusterBuilder cb = new ClusterBuilder();
	private ClusterRemove clusterRemove = new ClusterRemove();

	@Test
	public void testDestroy() {
		ArrayList<Node> nodeList = new ArrayList<Node>();
		ArrayList<Edge> edgeList = new ArrayList<Edge>();

		// Cluster #1
		Node v0 = new Node(0, 0, 0);
		Node v1 = new Node(5, 0, 1);

		// Cluster #2
		Node v2 = new Node(20, 0, 2);
		Node v3 = new Node(25, 0, 3);

		// Cluster #3
		Node v4 = new Node(0, 20, 4);
		Node v5 = new Node(5, 20, 5);

		// Cluster #4
		Node v6 = new Node(20, 20, 6);
		Node v7 = new Node(25, 20, 7);

		// Cluster #5
		Node v8 = new Node(11, 8, 8);
		Node v9 = new Node(15, 8, 9);
		Node v10 = new Node(11, 12, 10);
		Node v11 = new Node(15, 12, 11);

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

		edgeList.add(new Edge(v0, v1));
		edgeList.add(new Edge(v1, v8));
		edgeList.add(new Edge(v8, v9));
		edgeList.add(new Edge(v9, v2));
		edgeList.add(new Edge(v2, v3));
		edgeList.add(new Edge(v3, v7));
		edgeList.add(new Edge(v7, v6));
		edgeList.add(new Edge(v6, v11));
		edgeList.add(new Edge(v11, v10));
		edgeList.add(new Edge(v10, v5));
		edgeList.add(new Edge(v5, v4));

		Graph g1 = new Graph(nodeList, edgeList);
		ArrayList<ArrayList<Node>> clusters = cb.getClusters(g1);

		assertTrue("Graph g1 enthält fünf Cluster", clusters.size() == 5);

		Graph result1 = clusterRemove.destroy(g1, 1);

		ArrayList<Node> visitedNodes = result1.getVisitedNodes();

		assertTrue("Lösungsgraph ist gültig",
				result1.tourHasValidDepots() && result1.tourIsUninterrupted() && result1.tourHasNoDuplicates());
		if (!visitedNodes.contains(v1) || !visitedNodes.contains(v5)) {
			assertTrue("Cluster mit Start- oder Endknoten gewählt. Nur ein Knoten wurde entfernt",
					result1.getE().size() == 10);
		} else {
			if (!visitedNodes.contains(v8) || !visitedNodes.contains(v9) || !visitedNodes.contains(v10)
					|| !visitedNodes.contains(v11)) {
				assertTrue("Großer Cluster gewählt. Vier Knoten wurden entfernt", result1.getE().size() == 7);
				assertTrue("Kein Knoten des großen Cluster ist in der Tour enthalten",
						!visitedNodes.contains(v8) && !visitedNodes.contains(v9) && !visitedNodes.contains(v10)
								&& !visitedNodes.contains(v11));
				assertTrue("Es werden nur noch acht Knoten besucht", visitedNodes.size() == 8);
			} else {
				assertTrue("Kleiner Cluster ohne Depots gewählt. Zwei Knoten wurden entfernt",
						result1.getE().size() == 9);
			}
		}

	}
}
