package de.hagen.fernuni.logic.alns;

import java.util.ArrayList;
import java.util.Random;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * ClusterRepair stellt eine Reparaturmethode bereit, welche Knoten eines
 * Clusters in die Tour einf�gt.
 * 
 * @author Mahmoud Machaka
 *
 */
public class ClusterRepair implements IRepairMethods {

	/**
	 * F�gt Knoten eines zuf�llig ausgew�hlten Clusters in die Tour des Graphen ein.
	 * 
	 * @param g    Graph, in dessen Tour Knoten eingef�gt werden sollen.
	 * @param Tmax Kostenobergrenze
	 */
	@Override
	public Graph repair(Graph g, double Tmax) {
		Graph x = new Graph(g);
		ClusterBuilder cb = new ClusterBuilder();
		ArrayList<ArrayList<Node>> clusters = cb.getClusters(x);

		// W�hle einen Cluster zuf�llig aus
		Random rng = new Random();
		ArrayList<Node> cluster = clusters.get(rng.nextInt(clusters.size()));

		// Ignoriere Knoten des Clusters, die bereits in der Tour enthalten sind
		ArrayList<Node> visitedNodes = x.getVisitedNodes();
		cluster.removeAll(visitedNodes);

		// Cluster enth�lt keine Knoten, die nicht bereits in der Tour enthalten sind
		int numberOfNodesToBeAdded = cluster.size();
		if (numberOfNodesToBeAdded == 0) {
			return x;
		}

		Node randomNode;
		double lowestCost, tmpCost;
		int indexOfBestEdge;
		@SuppressWarnings("unchecked")
		ArrayList<Edge> currentTour = (ArrayList<Edge>) x.getE().clone();

		// F�gt alle Knoten des Clusters in die g�nstigste Stelle der Tour ein
		while (numberOfNodesToBeAdded > 0) {
			randomNode = cluster.get(rng.nextInt(numberOfNodesToBeAdded));
			lowestCost = Double.POSITIVE_INFINITY;
			indexOfBestEdge = -1;
			int currentTourSize = currentTour.size();

			// Finde g�nstigste Position in der Tour, um den Knoten einzuf�gen
			for (int i = 0; i < currentTourSize; i++) {
				Edge currentEdge = currentTour.get(i);
				double startToNode = Edge.getDistance(currentEdge.getStartNode(), randomNode);
				double nodeToEnd = Edge.getDistance(randomNode, currentEdge.getEndNode());
				tmpCost = (startToNode + nodeToEnd) - currentEdge.getDistance();

				if (tmpCost < lowestCost) {
					indexOfBestEdge = i;
					lowestCost = tmpCost;
				}
			}

			// F�ge Knoten in die Tour ein
			Edge oldEdge = currentTour.get(indexOfBestEdge);

			currentTour.set(indexOfBestEdge, new Edge(oldEdge.getStartNode(), randomNode));
			currentTour.add(indexOfBestEdge + 1, new Edge(randomNode, oldEdge.getEndNode()));

			// Entferne Knoten aus der Liste unbesuchter Knoten
			cluster.remove(randomNode);
			numberOfNodesToBeAdded--;
		}

		// F�r den Fall, dass Tmax �berschritten wurde
		currentTour = Graph.restorefeasibility(currentTour, Tmax);

		return new Graph(x.getV(), currentTour);
	}
}
