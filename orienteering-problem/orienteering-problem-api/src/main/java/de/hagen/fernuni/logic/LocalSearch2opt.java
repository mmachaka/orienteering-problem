package de.hagen.fernuni.logic;

import java.util.ArrayList;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * LocalSearch2opt stellt das lokale Suchverfahren "2OptFill" bereit, mit
 * welcher die �berkreuzung von Kanten aufgel�st und eine Tour optimiert werden
 * kann.
 * 
 * @author Mahmoud
 *
 */
public class LocalSearch2opt {

	/**
	 * Wendet das 2OptFill-Verfahren auf den �bergebenen Graphen an.
	 * 
	 * @param g Graph, auf den das 2OptFill-Verfahren angewendet werden soll.
	 * @return Optimierter Graph
	 */
	public static Graph localSearch(Graph g) {
		g = new Graph(g);
		ArrayList<Edge> existingTour = g.getE();
		if (existingTour.size() < 2)
			return g;

		double bestDistance = g.getCostOfTour();
		int visitedNodes = g.getVisitedNodes().size();
		boolean hasImproved = true;
		while (hasImproved) {
			hasImproved = false;
			for (int i = 1; i < visitedNodes - 2; i++) {
				for (int k = i + 1; k < visitedNodes - 1; k++) {
					ArrayList<Edge> newRoute = twoOptSwap(existingTour, i, k);
					double newDistance = Graph.getCostOfTour(newRoute);
					if (newDistance < bestDistance) {
						existingTour = newRoute;
						bestDistance = newDistance;
						hasImproved = true;
					}
				}
			}
		}

		return new Graph(g.getV(), existingTour);
	}

	/**
	 * Kehrt aus einem Teil der �bergebenen Tour die Ausrichtung der Kanten um
	 * (2OptSwap).
	 * 
	 * @param currentTour Kantenfolge, auf die der 2OptSwap-Algorithmus angewendet
	 *                    werden soll.
	 * @param i           Startindex des Bereichs, dessen Kanten umgekehrt werden
	 *                    sollen.
	 * @param k           Endindex des Bereichs, dessen Kanten umgekehrt werden
	 *                    sollen.
	 * @return Modifizierte Kantenfolge
	 */
	private static ArrayList<Edge> twoOptSwap(ArrayList<Edge> currentTour, int i, int k) {
		ArrayList<Edge> newRoute = new ArrayList<Edge>();

		Node startNode = currentTour.get(0).getStartNode();
		Edge newEdge;

		// F�ge Knoten [0] bis [i-1] in gleicher Reihenfolger in neue Tour ein
		for (int j = 0; j < i - 1; j++) {
			newRoute.add(currentTour.get(j));
			startNode = currentTour.get(j).getEndNode();
		}

		// F�ge Knoten [i] bis [k] in umgekehrter Reihenfolger in neue Tour ein
		for (int j = k; j >= i - 1; j--) {
			newEdge = new Edge(startNode, currentTour.get(j).getStartNode());
			if (j == i - 1) {
				newEdge = new Edge(startNode, currentTour.get(k).getEndNode());
			}
			startNode = currentTour.get(j).getStartNode();
			newRoute.add(newEdge);
		}

		// F�ge Knoten restliche Knoten in gleicher Reihenfolger in neue Tour ein
		for (int j = k + 1; j < currentTour.size(); j++) {
			newRoute.add(currentTour.get(j));
		}

		return newRoute;
	}

}
