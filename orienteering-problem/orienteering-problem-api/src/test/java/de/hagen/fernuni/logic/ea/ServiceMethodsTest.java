package de.hagen.fernuni.logic.ea;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.junit.Test;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

public class ServiceMethodsTest {

	@Test
	public void TestCrossover() {
		Node start = new Node(420.0, 242.0, 0.0);
		Node end = new Node(405.0, 237.0, 0.0);
		Node v1 = new Node(444.0, 359.0, 72.0);
		Node v2 = new Node(363.0, 397.0, 62.0);
		Node v3 = new Node(313.0, 476.0, 7.0);
		Node v4 = new Node(364.0, 540.0, 52.0);
		Node v5 = new Node(336.0, 605.0, 49.0);
		Node v6 = new Node(265.0, 630.0, 37.0);
		Node v7 = new Node(152.0, 610.0, 25.0);
		Node v8 = new Node(154.0, 560.0, 33.0);
		Node v9 = new Node(261.0, 586.0, 16.0);
		Node v10 = new Node(302.0, 543.0, 78.0);
		Node v11 = new Node(148.0, 494.0, 59.0);
		Node v12 = new Node(231.0, 506.0, 24.0);
		Node v13 = new Node(71.0, 428.0, 40.0);
		Node v14 = new Node(58.0, 377.0, 65.0);
		Node v15 = new Node(158.0, 446.0, 5.0);
		Node v16 = new Node(244.0, 461.0, 92.0);
		Node v17 = new Node(210.0, 395.0, 11.0);
		Node v18 = new Node(100.0, 368.0, 53.0);
		Node v19 = new Node(81.0, 295.0, 84.0);
		Node v20 = new Node(193.0, 362.0, 11.0);
		Node v21 = new Node(325.0, 383.0, 85.0);
		Node v22 = new Node(128.0, 224.0, 35.0);
		Node v23 = new Node(150.0, 263.0, 91.0);
		Node v24 = new Node(177.0, 184.0, 20.0);
		Node v25 = new Node(223.0, 119.0, 94.0);
		Node v26 = new Node(358.0, 153.0, 38.0);
		Node v27 = new Node(257.0, 198.0, 26.0);
		Node v28 = new Node(228.0, 247.0, 22.0);
		Node v29 = new Node(329.0, 214.0, 76.0);
		Node v30 = new Node(281.0, 310.0, 3.0);
		Node v31 = new Node(231.0, 294.0, 83.0);
		Node v32 = new Node(259.0, 372.0, 45.0);
		Node v33 = new Node(340.0, 302.0, 87.0);

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
		nodeList.add(v8);
		nodeList.add(v9);
		nodeList.add(v10);
		nodeList.add(v11);
		nodeList.add(v12);
		nodeList.add(v13);
		nodeList.add(v14);
		nodeList.add(v15);
		nodeList.add(v16);
		nodeList.add(v17);
		nodeList.add(v18);
		nodeList.add(v19);
		nodeList.add(v20);
		nodeList.add(v21);
		nodeList.add(v22);
		nodeList.add(v23);
		nodeList.add(v24);
		nodeList.add(v25);
		nodeList.add(v26);
		nodeList.add(v27);
		nodeList.add(v28);
		nodeList.add(v29);
		nodeList.add(v30);
		nodeList.add(v31);
		nodeList.add(v32);
		nodeList.add(v33);

		ArrayList<Edge> edgeList1 = new ArrayList<Edge>();
		edgeList1.add(new Edge(start, v1));
		edgeList1.add(new Edge(v1, v2));
		edgeList1.add(new Edge(v2, v21));
		edgeList1.add(new Edge(v21, v3));
		edgeList1.add(new Edge(v3, v10));
		edgeList1.add(new Edge(v10, v4));
		edgeList1.add(new Edge(v4, v5));
		edgeList1.add(new Edge(v5, v6));
		edgeList1.add(new Edge(v6, v9));
		edgeList1.add(new Edge(v9, v7));
		edgeList1.add(new Edge(v7, v8));
		edgeList1.add(new Edge(v8, v11));
		edgeList1.add(new Edge(v11, v15));
		edgeList1.add(new Edge(v15, v12));
		edgeList1.add(new Edge(v12, v16));
		edgeList1.add(new Edge(v16, v32));
		edgeList1.add(new Edge(v32, v17));
		edgeList1.add(new Edge(v17, v20));
		edgeList1.add(new Edge(v20, v18));
		edgeList1.add(new Edge(v18, v13));
		edgeList1.add(new Edge(v13, v14));
		edgeList1.add(new Edge(v14, v19));
		edgeList1.add(new Edge(v19, v23));
		edgeList1.add(new Edge(v23, v22));
		edgeList1.add(new Edge(v22, v24));
		edgeList1.add(new Edge(v24, v25));
		edgeList1.add(new Edge(v25, v27));
		edgeList1.add(new Edge(v27, v28));
		edgeList1.add(new Edge(v28, v31));
		edgeList1.add(new Edge(v31, v30));
		edgeList1.add(new Edge(v30, v33));
		edgeList1.add(new Edge(v33, v29));
		edgeList1.add(new Edge(v29, v26));
		edgeList1.add(new Edge(v26, end));

		ArrayList<Edge> edgeList2 = new ArrayList<Edge>();
		edgeList2.add(new Edge(start, v1));
		edgeList2.add(new Edge(v1, v2));
		edgeList2.add(new Edge(v2, v21));
		edgeList2.add(new Edge(v21, v3));
		edgeList2.add(new Edge(v3, v4));
		edgeList2.add(new Edge(v4, v5));
		edgeList2.add(new Edge(v5, v10));
		edgeList2.add(new Edge(v10, v9));
		edgeList2.add(new Edge(v9, v6));
		edgeList2.add(new Edge(v6, v7));
		edgeList2.add(new Edge(v7, v8));
		edgeList2.add(new Edge(v8, v12));
		edgeList2.add(new Edge(v12, v11));
		edgeList2.add(new Edge(v11, v15));
		edgeList2.add(new Edge(v15, v13));
		edgeList2.add(new Edge(v13, v14));
		edgeList2.add(new Edge(v14, v18));
		edgeList2.add(new Edge(v18, v19));
		edgeList2.add(new Edge(v19, v23));
		edgeList2.add(new Edge(v23, v22));
		edgeList2.add(new Edge(v22, v24));
		edgeList2.add(new Edge(v24, v25));
		edgeList2.add(new Edge(v25, v27));
		edgeList2.add(new Edge(v27, v28));
		edgeList2.add(new Edge(v28, v31));
		edgeList2.add(new Edge(v31, v20));
		edgeList2.add(new Edge(v20, v17));
		edgeList2.add(new Edge(v17, v16));
		edgeList2.add(new Edge(v16, v32));
		edgeList2.add(new Edge(v32, v30));
		edgeList2.add(new Edge(v30, v33));
		edgeList2.add(new Edge(v33, v29));
		edgeList2.add(new Edge(v29, v26));
		edgeList2.add(new Edge(v26, end));

		Graph parent1 = new Graph(nodeList, edgeList1);
		Graph parent2 = new Graph(nodeList, edgeList2);
		ArrayList<Node> commonNodes = (ArrayList<Node>) parent1.getVisitedNodes().stream()
				.filter(parent2.getVisitedNodes()::contains).collect(Collectors.toList());

		Graph[] parents = { parent1, parent2 };
		Graph child = ServiceMethods.crossover(parents);

		assertTrue("Der Graph ist zulässig",
				child.tourHasValidDepots() && child.tourIsUninterrupted() && child.tourHasNoDuplicates());

		assertTrue("Alle gemeinsamen Knoten sind in der Tour enthalten",
				commonNodes.containsAll(child.getVisitedNodes()) && child.getVisitedNodes().containsAll(commonNodes));

	}

	@Test
	public void TestAddOperator() {
		ArrayList<Node> nodeList = new ArrayList<Node>();

		Node start = new Node(1, 5, 0.0);
		Node end = new Node(10, 5, 0.0);
		Node v1 = new Node(3, 7, 5);
		Node v2 = new Node(4, 3, 5);
		Node v3 = new Node(5, 5, 5);
		Node v4 = new Node(6, 6, 5);
		Node v5 = new Node(7, 4, 5);
		Node v6 = new Node(9, 3, 5);
		Node v7 = new Node(9, 7, 5);
		Node v8 = new Node(8, 5, 5);

		nodeList.add(start);
		nodeList.add(end);
		nodeList.add(v1);
		nodeList.add(v2);
		nodeList.add(v3);
		nodeList.add(v4);
		nodeList.add(v5);
		nodeList.add(v6);
		nodeList.add(v7);
		nodeList.add(v8);

		ArrayList<Edge> edgeList1 = new ArrayList<Edge>();
		edgeList1.add(new Edge(start, v1));
		edgeList1.add(new Edge(v1, v2));
		edgeList1.add(new Edge(v2, v4));
		edgeList1.add(new Edge(v4, v5));
		edgeList1.add(new Edge(v5, v6));
		edgeList1.add(new Edge(v6, v7));
		edgeList1.add(new Edge(v7, end));

		Graph g1 = new Graph(nodeList, edgeList1);
		Graph res1 = ServiceMethods.addOperator(g1, 22);
		assertTrue("v3 wurde hinzugefügt", res1.getVisitedNodes().contains(v3));
		assertTrue("Es werden 9 Knoten besucht", res1.getVisitedNodes().size() == 9);
		assertFalse("v8 wurde nicht hinzugefügt", res1.getVisitedNodes().contains(v8));
		assertTrue("Der Graph ist zulässig",
				res1.tourHasValidDepots() && res1.tourIsUninterrupted() && res1.tourHasNoDuplicates());

		g1 = new Graph(nodeList, edgeList1);
		Graph res2 = ServiceMethods.addOperator(g1, 20);
		assertTrue("Kein Knoten wurde hinzugefügt", res2.getVisitedNodes().size() == 8);
		assertTrue("Der Graph ist zulässig",
				res2.tourHasValidDepots() && res2.tourIsUninterrupted() && res2.tourHasNoDuplicates());

		g1 = new Graph(nodeList, edgeList1);
		Graph res3 = ServiceMethods.addOperator(g1, 100);
		assertTrue("Beide Knoten wurden hinzugefügt", res3.getVisitedNodes().size() == 10);
		assertTrue("v3 wurde zwischen (v2,v4) eingefügt",
				res3.getE().contains(new Edge(v2, v3)) && res3.getE().contains(new Edge(v3, v4)));
		assertTrue("v8 wurde zwischen (v4,v5) eingefügt",
				res3.getE().contains(new Edge(v4, v8)) && res3.getE().contains(new Edge(v8, v5)));
		assertTrue("Der Graph ist zulässig",
				res3.tourHasValidDepots() && res3.tourIsUninterrupted() && res3.tourHasNoDuplicates());
	}
}
