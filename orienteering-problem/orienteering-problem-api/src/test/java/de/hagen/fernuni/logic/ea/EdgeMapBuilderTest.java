package de.hagen.fernuni.logic.ea;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.junit.Test;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

public class EdgeMapBuilderTest {

	@Test
	public void TestGetEdgeMap() {
		ArrayList<Node> vList = new ArrayList<Node>();

		Node vStart = new Node(10, 15, 1);
		Node v1End = new Node(10, 15, 1);
		Node v2 = new Node(15, 20, 2);
		Node v3 = new Node(30, 20, 3);
		Node v4 = new Node(15, 15, 4);
		Node v5 = new Node(20, 15, 5);
		Node v6 = new Node(25, 15, 6);
		Node v7 = new Node(30, 15, 7);
		Node v8 = new Node(5, 10, 8);
		Node v9 = new Node(10, 10, 9);
		Node v10 = new Node(15, 10, 10);
		Node v11 = new Node(20, 10, 11);
		Node v12 = new Node(25, 10, 12);
		Node v13 = new Node(35, 10, 13);
		Node v14 = new Node(15, 5, 14);
		Node v15 = new Node(10, 0, 15);
		Node v16 = new Node(25, 5, 16);

		vList.add(vStart);
		vList.add(v1End);
		vList.add(v2);
		vList.add(v3);
		vList.add(v4);
		vList.add(v5);
		vList.add(v6);
		vList.add(v7);
		vList.add(v8);
		vList.add(v9);
		vList.add(v10);
		vList.add(v11);
		vList.add(v12);
		vList.add(v13);
		vList.add(v14);
		vList.add(v15);
		vList.add(v16);

		ArrayList<Edge> edges1 = new ArrayList<Edge>();
		edges1.add(new Edge(vStart, v2));
		edges1.add(new Edge(v2, v4));
		edges1.add(new Edge(v4, v10));
		edges1.add(new Edge(v10, v14));
		edges1.add(new Edge(v14, v11));
		edges1.add(new Edge(v11, v6));
		edges1.add(new Edge(v6, v3));
		edges1.add(new Edge(v3, v7));
		edges1.add(new Edge(v7, v12));
		edges1.add(new Edge(v12, v16));
		edges1.add(new Edge(v16, v15));
		edges1.add(new Edge(v15, v9));
		edges1.add(new Edge(v9, v1End));

		ArrayList<Edge> edges2 = new ArrayList<Edge>();
		edges2.add(new Edge(vStart, v4));
		edges2.add(new Edge(v4, v5));
		edges2.add(new Edge(v5, v6));
		edges2.add(new Edge(v6, v7));
		edges2.add(new Edge(v7, v13));
		edges2.add(new Edge(v13, v12));
		edges2.add(new Edge(v12, v11));
		edges2.add(new Edge(v11, v10));
		edges2.add(new Edge(v10, v9));
		edges2.add(new Edge(v9, v8));
		edges2.add(new Edge(v8, v1End));

		Graph graph1 = new Graph(vList, edges1);
		Graph graph2 = new Graph(vList, edges2);
		Graph[] parents = { graph1, graph2 };

		ArrayList<Node> visitedNodes1 = parents[0].getVisitedNodes();
		ArrayList<Node> visitedNodes2 = parents[1].getVisitedNodes();

		ArrayList<Node> commonNodes = (ArrayList<Node>) visitedNodes1.stream().filter(visitedNodes2::contains)
				.collect(Collectors.toList());
		commonNodes.remove(parents[0].getV().get(1));

		ArrayList<EdgeMapEntry> edgeMap = EdgeMapBuilder.getEdgeMap(parents, commonNodes);

		assertTrue("Edgemap hat acht Einträge", edgeMap.size() == 8);
		int counter = 0;
		Node commonNode;
		ArrayList<Node> connectedNodes;
		int degree;
		ArrayList<ArrayList<Edge>> intermediatePaths;

		boolean connectedNodesCorrect, degreeCorrect, pathsCorrect;
		for (EdgeMapEntry entry : edgeMap) {
			commonNode = entry.getCommonNode();
			connectedNodes = entry.getConnectedNodes();
			degree = entry.getDegree();
			intermediatePaths = entry.getIntermediatePaths();
			if (commonNode.equals(vStart)) {
				connectedNodesCorrect = connectedNodes.size() == 2 && connectedNodes.contains(v4)
						&& connectedNodes.contains(v9);
				degreeCorrect = (degree == 2);
				pathsCorrect = (intermediatePaths.size() == 4);
				assertTrue("Entry für Startknoten ist korrekt", connectedNodesCorrect && degreeCorrect && pathsCorrect);
				counter++;
			}
			if (commonNode.equals(v4)) {
				connectedNodesCorrect = connectedNodes.size() == 3 && connectedNodes.contains(vStart)
						&& connectedNodes.contains(v6) && connectedNodes.contains(v10);
				degreeCorrect = (degree == 3);
				pathsCorrect = (intermediatePaths.size() == 4);
				assertTrue("Entry für v4 ist korrekt", connectedNodesCorrect && degreeCorrect && pathsCorrect);
				counter++;
			}
			if (commonNode.equals(v6)) {
				connectedNodesCorrect = connectedNodes.size() == 3 && connectedNodes.contains(v4)
						&& connectedNodes.contains(v7) && connectedNodes.contains(v11);
				degreeCorrect = (degree == 3);
				pathsCorrect = (intermediatePaths.size() == 4);
				assertTrue("Entry für v6 ist korrekt", connectedNodesCorrect && degreeCorrect && pathsCorrect);
				counter++;
			}
			if (commonNode.equals(v7)) {
				connectedNodesCorrect = connectedNodes.size() == 2 && connectedNodes.contains(v6)
						&& connectedNodes.contains(v12);
				degreeCorrect = (degree == 2);
				pathsCorrect = (intermediatePaths.size() == 4);
				assertTrue("Entry für v7 ist korrekt", connectedNodesCorrect && degreeCorrect && pathsCorrect);
				counter++;
			}
			if (commonNode.equals(v9)) {
				connectedNodesCorrect = connectedNodes.size() == 3 && connectedNodes.contains(vStart)
						&& connectedNodes.contains(v10) && connectedNodes.contains(v12);
				degreeCorrect = (degree == 3);
				pathsCorrect = (intermediatePaths.size() == 4);
				assertTrue("Entry für v9 ist korrekt", connectedNodesCorrect && degreeCorrect && pathsCorrect);
				counter++;
			}
			if (commonNode.equals(v10)) {
				connectedNodesCorrect = connectedNodes.size() == 3 && connectedNodes.contains(v4)
						&& connectedNodes.contains(v9) && connectedNodes.contains(v11);
				degreeCorrect = (degree == 3);
				pathsCorrect = (intermediatePaths.size() == 4);
				assertTrue("Entry für v10 ist korrekt", connectedNodesCorrect && degreeCorrect && pathsCorrect);
				counter++;
			}
			if (commonNode.equals(v11)) {
				connectedNodesCorrect = connectedNodes.size() == 3 && connectedNodes.contains(v6)
						&& connectedNodes.contains(v10) && connectedNodes.contains(v12);
				degreeCorrect = (degree == 3);
				pathsCorrect = (intermediatePaths.size() == 4);
				assertTrue("Entry für v11 ist korrekt", connectedNodesCorrect && degreeCorrect && pathsCorrect);
				counter++;
			}
			if (commonNode.equals(v12)) {
				connectedNodesCorrect = connectedNodes.size() == 3 && connectedNodes.contains(v7)
						&& connectedNodes.contains(v9) && connectedNodes.contains(v11);
				degreeCorrect = (degree == 3);
				pathsCorrect = (intermediatePaths.size() == 4);
				assertTrue("Entry für v12 ist korrekt", connectedNodesCorrect && degreeCorrect && pathsCorrect);
				counter++;
			}
		}
		assertTrue("Alle gemeinsamen Knoten haben einen Eintrag in der Edge Map", counter == 8);
	}

	@Test
	public void testRemoveNodeFromConnectedNodes() {
		Node v1 = new Node(1, 1, 1);
		Node v2 = new Node(2, 2, 2);
		Node v3 = new Node(3, 3, 3);
		Node v4 = new Node(4, 4, 4);
		Node v5 = new Node(5, 5, 5);

		ArrayList<Node> connectedNodes1 = new ArrayList<Node>();
		connectedNodes1.add(v2);
		connectedNodes1.add(v3);
		connectedNodes1.add(v4);
		connectedNodes1.add(v5);
		EdgeMapEntry entry1 = new EdgeMapEntry(v1, connectedNodes1, connectedNodes1.size(), null);

		ArrayList<Node> connectedNodes2 = new ArrayList<Node>();
		connectedNodes2.add(v1);
		connectedNodes2.add(v4);
		connectedNodes2.add(v3);
		connectedNodes2.add(v4);
		connectedNodes2.add(v5);
		EdgeMapEntry entry2 = new EdgeMapEntry(v2, connectedNodes2, connectedNodes2.size(), null);

		ArrayList<Node> connectedNodes3 = new ArrayList<Node>();
		connectedNodes3.add(v1);
		connectedNodes3.add(v2);
		connectedNodes3.add(v4);
		connectedNodes3.add(v5);
		connectedNodes3.add(v4);
		EdgeMapEntry entry3 = new EdgeMapEntry(v3, connectedNodes3, connectedNodes3.size(), null);

		ArrayList<Node> connectedNodes4 = new ArrayList<Node>();
		connectedNodes4.add(v1);
		connectedNodes4.add(v3);
		connectedNodes4.add(v2);
		connectedNodes4.add(v5);
		EdgeMapEntry entry4 = new EdgeMapEntry(v4, connectedNodes4, connectedNodes4.size(), null);

		ArrayList<EdgeMapEntry> edgeMap = new ArrayList<EdgeMapEntry>();
		edgeMap.add(entry1);
		edgeMap.add(entry2);
		edgeMap.add(entry3);
		edgeMap.add(entry4);

		for (EdgeMapEntry e : edgeMap) {
			if (e.getCommonNode().equals(v1)) {
				assertTrue("Eintrag v1 hat 4 Connected Nodes", e.getConnectedNodes().size() == 4);
				assertTrue("Eintrag v1 den Grad 4", e.getDegree() == 4);
			}
			if (e.getCommonNode().equals(v2)) {
				assertTrue("Eintrag v2 hat 5 Connected Nodes", e.getConnectedNodes().size() == 5);
				assertTrue("Eintrag v2 den Grad 5", e.getDegree() == 5);
			}
			if (e.getCommonNode().equals(v3)) {
				assertTrue("Eintrag v3 hat 5 Connected Nodes", e.getConnectedNodes().size() == 5);
				assertTrue("Eintrag v3 den Grad 5", e.getDegree() == 5);
			}
			if (e.getCommonNode().equals(v4)) {
				assertTrue("Eintrag v4 hat 4 Connected Nodes", e.getConnectedNodes().size() == 4);
				assertTrue("Eintrag v4 den Grad 4", e.getDegree() == 4);
			}
		}

		edgeMap = EdgeMapBuilder.removeNodeFromConnectedNodes(edgeMap, v4);

		for (EdgeMapEntry e : edgeMap) {
			if (e.getCommonNode().equals(v1)) {
				assertTrue("Eintrag v1 hat 3 Connected Nodes", e.getConnectedNodes().size() == 3);
				assertTrue("Eintrag v1 den Grad 3", e.getDegree() == 3);
			}
			if (e.getCommonNode().equals(v2)) {
				assertTrue("Eintrag v2 hat 3 Connected Nodes", e.getConnectedNodes().size() == 3);
				assertTrue("Eintrag v2 den Grad 3", e.getDegree() == 3);
			}
			if (e.getCommonNode().equals(v3)) {
				assertTrue("Eintrag v3 hat 3 Connected Nodes", e.getConnectedNodes().size() == 3);
				assertTrue("Eintrag v3 den Grad 3", e.getDegree() == 3);
			}
			if (e.getCommonNode().equals(v4)) {
				assertTrue("Eintrag v4 hat 4 Connected Nodes", e.getConnectedNodes().size() == 4);
				assertTrue("Eintrag v4 den Grad 4", e.getDegree() == 4);
			}
		}
	}
}
