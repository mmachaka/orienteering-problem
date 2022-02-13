package de.hagen.fernuni.logic.alns;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

public class ClusterBuilderTest {
	private ClusterBuilder cb = new ClusterBuilder();
	
	
	@Test
	public void testGetNeighbours1() {
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
		Node v4 = new Node(0, 20, 4);
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
		nodeList.add(v1);
		nodeList.add(v2);
		nodeList.add(v3);
		nodeList.add(v4);
		nodeList.add(v5);
		nodeList.add(v6);
		nodeList.add(v7);
		nodeList.add(v8);
		nodeList.add(v9);
		nodeList.add(v10);
		nodeList.add(v11);

		// Reihenfolge der Knoten darf keine Rolle spielen
		Collections.shuffle(nodeList);

		Graph g1 = new Graph(nodeList, edgeList);
		ArrayList<ArrayList<Node>> clusters = cb.getClusters(g1);
		
		assertTrue("Graph g1 enth‰lt f¸nf Cluster", clusters.size() == 5);
		
		boolean clusterIsContained = false;
		for (ArrayList<Node> cluster : clusters) {
			if (cluster.containsAll(cluster1Reference) && cluster1Reference.containsAll(cluster))
				clusterIsContained = true;
		}		
		assertTrue("Cluster #1 ist enthalten", clusterIsContained);
		
		clusterIsContained = false;
		for (ArrayList<Node> cluster : clusters) {
			if (cluster.containsAll(cluster2Reference) && cluster2Reference.containsAll(cluster))
				clusterIsContained = true;
		}		
		assertTrue("Cluster #2 ist enthalten", clusterIsContained);
		
		clusterIsContained = false;
		for (ArrayList<Node> cluster : clusters) {
			if (cluster.containsAll(cluster3Reference) && cluster3Reference.containsAll(cluster))
				clusterIsContained = true;
		}		
		assertTrue("Cluster #3 ist enthalten", clusterIsContained);
		
		clusterIsContained = false;
		for (ArrayList<Node> cluster : clusters) {
			if (cluster.containsAll(cluster4Reference) && cluster4Reference.containsAll(cluster))
				clusterIsContained = true;
		}		
		assertTrue("Cluster #4 ist enthalten", clusterIsContained);
		
		clusterIsContained = false;
		for (ArrayList<Node> cluster : clusters) {
			if (cluster.containsAll(cluster5Reference) && cluster5Reference.containsAll(cluster))
				clusterIsContained = true;
		}		
		assertTrue("Cluster #5 ist enthalten", clusterIsContained);
	}
	
	@Test
	public void testGetNeighbours2() {
		ArrayList<Node> nodeList = new ArrayList<Node>();
		ArrayList<Edge> edgeList = new ArrayList<Edge>();

		// Cluster #1
		Node v0 = new Node(0, 0, 0);
		Node v1 = new Node(90, 0, 1);
		Node v2 = new Node(91, 0, 2);
		Node v3 = new Node(92, 0, 3);
		Node v4 = new Node(93, 0, 4);
		Node v5 = new Node(94, 0, 5);
		Node v6 = new Node(95, 0, 6);
		Node v7 = new Node(96, 0, 7);
		
		nodeList.add(v0);
		nodeList.add(v1);
		nodeList.add(v2);
		nodeList.add(v3);
		nodeList.add(v4);
		nodeList.add(v5);
		nodeList.add(v6);
		nodeList.add(v7);
		
		// Reihenfolge der Knoten darf keine Rolle spielen
		Collections.shuffle(nodeList);

		Graph g1 = new Graph(nodeList, edgeList);
		ArrayList<ArrayList<Node>> clusters = cb.getClusters(g1);
		
		assertTrue("Graph g1 enth‰lt einen Cluster", clusters.size() == 1);
		assertTrue("Der einzige Cluster enth‰lt alle Knoten", clusters.get(0).size() == 8);
	}
	
	
	@Test
	public void testGetNeighbours3() {
		ArrayList<Node> nodeList = new ArrayList<Node>();
		ArrayList<Edge> edgeList = new ArrayList<Edge>();

		// Auﬂenseiter
		Node v0 = new Node(0, 0, 0);
		Node v1 = new Node(10, 0, 1);
		
		// Cluster
		Node v2 = new Node(100, 100, 2);
		Node v3 = new Node(105, 100, 3);
		Node v4 = new Node(110, 100, 4);
		Node v5 = new Node(100, 105, 2);
		Node v6 = new Node(105, 105, 3);
		Node v7 = new Node(110, 105, 4);
		Node v8 = new Node(100, 110, 2);
		Node v9 = new Node(105, 110, 3);
		Node v10 = new Node(110, 110, 4);
		
		nodeList.add(v0);
		nodeList.add(v1);
		nodeList.add(v2);
		nodeList.add(v3);
		nodeList.add(v4);
		nodeList.add(v5);
		nodeList.add(v6);
		nodeList.add(v7);
		nodeList.add(v8);
		nodeList.add(v9);
		nodeList.add(v10);
		
		// Reihenfolge der Knoten darf keine Rolle spielen
		Collections.shuffle(nodeList);

		Graph g1 = new Graph(nodeList, edgeList);
		ArrayList<ArrayList<Node>> clusters = cb.getClusters(g1);
		
		assertTrue("Graph g1 enth‰lt einen Cluster", clusters.size() == 1);
		assertTrue("Der einzige Cluster enth‰lt neun Knoten", clusters.get(0).size() == 9);
		assertFalse("Auﬂenseiter v0 ist nicht im Cluster enthalten", clusters.get(0).contains(v0));
		assertFalse("Auﬂenseiter v1 ist nicht im Cluster enthalten", clusters.get(0).contains(v1));
	}
	
	

}
