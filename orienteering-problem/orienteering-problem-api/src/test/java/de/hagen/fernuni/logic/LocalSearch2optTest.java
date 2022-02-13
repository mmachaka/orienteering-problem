package de.hagen.fernuni.logic;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

public class LocalSearch2optTest {
	@Test
	public void TestLocalSearch() {
		Node v0 = new Node(0, 5, 0);
		Node v1 = new Node(0, 0, 1);

		Node v2 = new Node(10, 5, 2);
		Node v3 = new Node(10, 0, 3);

		Node v4 = new Node(20, 5, 4);
		Node v5 = new Node(20, 0, 5);

		Node v6 = new Node(30, 5, 6);
		Node v7 = new Node(30, 0, 7);

		ArrayList<Node> vList = new ArrayList<Node>();
		vList.add(v0);
		vList.add(v1);
		vList.add(v2);
		vList.add(v3);
		vList.add(v4);
		vList.add(v5);
		vList.add(v6);
		vList.add(v7);

		ArrayList<Edge> edges1 = new ArrayList<Edge>();
		ArrayList<Edge> edges2 = new ArrayList<Edge>();
		ArrayList<Edge> edges3 = new ArrayList<Edge>();
		ArrayList<Edge> edges4 = new ArrayList<Edge>();
		ArrayList<Edge> edges5 = new ArrayList<Edge>();
		ArrayList<Edge> edges6 = new ArrayList<Edge>();

		Edge e1_1 = new Edge(v0, v3);
		Edge e1_2 = new Edge(v3, v2);
		Edge e1_3 = new Edge(v2, v1);
		edges1.add(e1_1);
		edges1.add(e1_2);
		edges1.add(e1_3);

		Edge e2_1 = new Edge(v0, v2);
		Edge e2_2 = new Edge(v2, v5);
		Edge e2_3 = new Edge(v5, v4);
		Edge e2_4 = new Edge(v4, v3);
		Edge e2_5 = new Edge(v3, v1);
		edges2.add(e2_1);
		edges2.add(e2_2);
		edges2.add(e2_3);
		edges2.add(e2_4);
		edges2.add(e2_5);

		Edge e3_1 = new Edge(v0, v3);
		Edge e3_2 = new Edge(v3, v4);
		Edge e3_3 = new Edge(v4, v5);
		Edge e3_4 = new Edge(v5, v2);
		Edge e3_5 = new Edge(v2, v1);
		edges3.add(e3_1);
		edges3.add(e3_2);
		edges3.add(e3_3);
		edges3.add(e3_4);
		edges3.add(e3_5);

		Edge e4_1 = new Edge(v0, v2);
		Edge e4_2 = new Edge(v2, v5);
		Edge e4_3 = new Edge(v5, v7);
		Edge e4_4 = new Edge(v7, v6);
		Edge e4_5 = new Edge(v6, v4);
		Edge e4_6 = new Edge(v4, v3);
		Edge e4_7 = new Edge(v3, v1);
		edges4.add(e4_1);
		edges4.add(e4_2);
		edges4.add(e4_3);
		edges4.add(e4_4);
		edges4.add(e4_5);
		edges4.add(e4_6);
		edges4.add(e4_7);

		Edge e5_1 = new Edge(v0, v3);
		Edge e5_2 = new Edge(v3, v5);
		Edge e5_3 = new Edge(v5, v4);
		Edge e5_4 = new Edge(v4, v2);
		Edge e5_5 = new Edge(v2, v1);
		edges5.add(e5_1);
		edges5.add(e5_2);
		edges5.add(e5_3);
		edges5.add(e5_4);
		edges5.add(e5_5);

		Edge e6_1 = new Edge(v0, v2);
		Edge e6_2 = new Edge(v2, v4);
		Edge e6_3 = new Edge(v4, v5);
		Edge e6_4 = new Edge(v5, v3);
		Edge e6_5 = new Edge(v3, v1);
		edges6.add(e6_1);
		edges6.add(e6_2);
		edges6.add(e6_3);
		edges6.add(e6_4);
		edges6.add(e6_5);

		Graph graph1 = new Graph(vList, edges1);
		Graph graph2 = new Graph(vList, edges2);
		Graph graph3 = new Graph(vList, edges3);
		Graph graph4 = new Graph(vList, edges4);
		Graph graph5 = new Graph(vList, edges5);
		Graph graph6 = new Graph(vList, edges6);

		Graph result1 = LocalSearch2opt.localSearch(graph1);
		Graph result2 = LocalSearch2opt.localSearch(graph2);
		Graph result3 = LocalSearch2opt.localSearch(graph3);
		Graph result4 = LocalSearch2opt.localSearch(graph4);
		Graph result5 = LocalSearch2opt.localSearch(graph5);
		Graph result6 = LocalSearch2opt.localSearch(graph6);

		assertTrue("2Opt-Verfahren auf Graph1 erfolgreich", result1.getCostOfTour() == 25);
		assertTrue("2Opt-Verfahren auf Graph2 erfolgreich", result2.getCostOfTour() == 45);
		assertTrue("2Opt-Verfahren auf Graph3 erfolgreich", result3.getCostOfTour() == 45);
		assertTrue("2Opt-Verfahren auf Graph4 erfolgreich", result4.getCostOfTour() == 65);
		assertTrue("2Opt-Verfahren auf Graph5 erfolgreich", result5.getCostOfTour() == 45);
		assertTrue("2Opt-Verfahren auf Graph6 erfolgreich", result6.getCostOfTour() == 45);

		assertTrue("Graph1 ist zulässig",
				result1.tourHasValidDepots() && result1.tourIsUninterrupted() && result1.tourHasNoDuplicates());
		assertTrue("Graph2 ist zulässig",
				result2.tourHasValidDepots() && result2.tourIsUninterrupted() && result2.tourHasNoDuplicates());
		assertTrue("Graph3 ist zulässig",
				result3.tourHasValidDepots() && result3.tourIsUninterrupted() && result3.tourHasNoDuplicates());
		assertTrue("Graph4 ist zulässig",
				result4.tourHasValidDepots() && result4.tourIsUninterrupted() && result4.tourHasNoDuplicates());
		assertTrue("Graph5 ist zulässig",
				result5.tourHasValidDepots() && result5.tourIsUninterrupted() && result5.tourHasNoDuplicates());
		assertTrue("Graph6 ist zulässig",
				result6.tourHasValidDepots() && result6.tourIsUninterrupted() && result6.tourHasNoDuplicates());
	}
}
