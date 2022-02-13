package de.hagen.fernuni.logic.alns;

import java.util.ArrayList;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * GreedyRepair stellt eine Reparaturmethode nach dem Greedy Algorithmus bereit,
 * welche die Knoten in die Tour einf�gt, die ein besonders gutes Verh�ltnis
 * zwischen erzieltem Profit und Mehrkosten aufweisen.
 * 
 * @author Mahmoud Machaka
 *
 */
public class GreedyRepair implements IRepairMethods {

	/**
	 * F�gt die Knoten des Graphen in die Tour ein, welche ein besonders gutes
	 * Verh�ltnis zwischen erzieltem Profit und Mehrkosten aufweisen.
	 * 
	 * @param g    Graph, in dessen Tour Knoten eingef�gt werden sollen.
	 * @param Tmax Kostenobergrenze
	 */
	@Override
	public Graph repair(Graph g, double Tmax) {
		Graph x = new Graph(g);
		@SuppressWarnings("unchecked")
		ArrayList<Edge> currentTour = (ArrayList<Edge>) x.getE().clone();
		ArrayList<Node> unvisitedNodes = x.getUnvisitedNodes();

		// Lookup-Liste aus der keine Knoten entfernt werden
		@SuppressWarnings("unchecked")
		ArrayList<Node> unvisitedNodesCopy = (ArrayList<Node>) unvisitedNodes.clone();

		if (unvisitedNodes.isEmpty() || currentTour.isEmpty())
			return x;

		// Adjazentliste: Ratios aller unbesuchter Knoten zu allen Kanten der aktuellen
		// Tour
		ArrayList<ArrayList<Double>> ratios = calculateRatios(unvisitedNodes, currentTour);

		Node bestNode;
		int indexOfBestNode = -1;
		int indexOfBestEdge = -1;
		double bestRatio;
		double tmpRatio;
		boolean insertionIsViable = false;

		while (!unvisitedNodes.isEmpty()) {
			// Initialisiere Iteration
			indexOfBestNode = -1;
			indexOfBestEdge = -1;
			bestNode = null;
			bestRatio = Double.POSITIVE_INFINITY;

			// Finde Kombination (Knoten i, Kante j) mit niedrigster Ratio
			for (int i = 0; i < ratios.size(); i++) {
				try {
					ratios.get(i).size();
				} catch (NullPointerException e) {
					continue;
				}
				for (int j = 0; j < ratios.get(i).size(); j++) {
					tmpRatio = ratios.get(i).get(j);

					insertionIsViable = Graph.insertionIsViable(unvisitedNodesCopy.get(i), currentTour.get(j),
							currentTour, Tmax);
					if ((tmpRatio <= bestRatio) && insertionIsViable) {
						indexOfBestNode = i;
						indexOfBestEdge = j;
						bestRatio = tmpRatio;
					}
				}
			}

			// Wenn kein Knoten mehr eingef�gt werden kann, ohne Tmax zu �berschreiten
			if (indexOfBestNode == -1)
				break;

			bestNode = unvisitedNodesCopy.get(indexOfBestNode);
			Edge oldEdge = currentTour.get(indexOfBestEdge);

			// F�ge gefundenden Knoten in Tour ein
			Edge newEdge1 = new Edge(oldEdge.getStartNode(), bestNode);
			Edge newEdge2 = new Edge(bestNode, oldEdge.getEndNode());
			currentTour.set(indexOfBestEdge, newEdge1);
			currentTour.add(indexOfBestEdge + 1, newEdge2);

			// Entferne den Knoten aus der Liste unbesuchter Knoten und setze seine
			// ratioList auf null
			unvisitedNodes.remove(bestNode);
			ratios.set(indexOfBestNode, null); // L�sche Liste, ohne sie aus ratios zu entfernen

			// Aktualisiere Ratios der �brigen Knoten
			ratios = updateRatios(ratios, indexOfBestEdge, unvisitedNodesCopy, newEdge1, newEdge2);
		}
		return new Graph(x.getV(), currentTour);
	}

	/**
	 * Aktualisiert die Verh�ltnis-Liste. Nach dem Einf�gen eines Knotens m�ssen
	 * lediglich die adjazenten Felder aktualisiert werden.
	 * 
	 * @param ratios                Verh�ltnis-Liste
	 * @param indexOfAddedNode    Index des hinzugef�gten Knotens in der
	 *                              Verh�ltnis-Liste
	 * @param unvisitedNodesCopy Liste unbesuchter Knoten
	 * @param newEdge1              Erste neue Kante, die durch das Einf�gen des
	 *                              Knotens erzeugt wurde.
	 * @param newEdge2              Zweite neue Kante, die durch das Einf�gen des
	 *                              Knotens erzeugt wurde.
	 * @return Aktualisierte Verh�ltnis-Liste
	 */
	private ArrayList<ArrayList<Double>> updateRatios(ArrayList<ArrayList<Double>> ratios, int indexOfAddedNode,
			ArrayList<Node> unvisitedNodesCopy, Edge newEdge1, Edge newEdge2) {
		int size = ratios.size();
		ArrayList<Double> ratio;
		for (int i = 0; i < size; i++) {
			ratio = ratios.get(i);
			if (ratio == null)
				continue;
			Node updateNode = unvisitedNodesCopy.get(i);
			ratio.set(indexOfAddedNode, calculateRatio(newEdge1, updateNode));
			ratio.add(indexOfAddedNode + 1, calculateRatio(newEdge2, updateNode));
		}
		return ratios;
	}

	/**
	 * Berechnet f�r alle Knoten der �bergebenen Knoten Liste, die jeweiligen
	 * Verh�ltnisse (Mehraufwand/Profit) zu den Kanten der Tour.
	 * 
	 * @param unvisitedNodes Knotenliste (unbesuchte Knoten)
	 * @param currentTour       Aktuelle Tour
	 * @return Verh�ltnis-Liste: Eine Liste, welche f�r alle Knoten ihre jeweiligen
	 *         Verh�ltnisse zu allen Kanten beinhaltet.
	 */
	private ArrayList<ArrayList<Double>> calculateRatios(ArrayList<Node> unvisitedNodes,
			ArrayList<Edge> currentTour) {
		ArrayList<ArrayList<Double>> ratios = new ArrayList<ArrayList<Double>>();
		for (Node v : unvisitedNodes) {
			ArrayList<Double> ratioList = new ArrayList<Double>();

			// Berechne Ratio des Knotens v zu jeder Kante
			for (Edge e : currentTour) {
				ratioList.add(calculateRatio(e, v));
			}
			ratios.add(ratioList);
		}
		return ratios;
	}

	/**
	 * Berechnet das Verh�ltnis (Mehrkosten/Profit) eines Knotens zu einer Kante.
	 * 
	 * @param e Kante, in welcher der Knoten ggf. eingef�gt werden soll.
	 * @param v Knoten, f�r welchen das Verh�ltnis (Mehrkosten/Profit) berechnet
	 *          werden soll
	 * @return Verh�ltnis (Mehrkosten/Profit)
	 */
	private double calculateRatio(Edge e, Node v) {
		double distStartToNode = Edge.getDistance(e.getStartNode(), v);
		double distNodeToEnd = Edge.getDistance(v, e.getEndNode());
		double distStartToEnd = Edge.getDistance(e.getStartNode(), e.getEndNode());
		return (((distStartToNode + distNodeToEnd) - distStartToEnd) / v.getProfit());
	}

}
