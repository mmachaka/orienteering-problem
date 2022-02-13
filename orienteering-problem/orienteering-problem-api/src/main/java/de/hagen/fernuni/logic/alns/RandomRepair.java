package de.hagen.fernuni.logic.alns;

import java.util.ArrayList;
import java.util.Random;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * RandomRepair stellt eine Reparaturmethode bereit, welche zuf�llig gew�hlte
 * Knoten des Graphen in die Tour aufnimmt.
 * 
 * @author Mahmoud Machaka
 *
 */
public class RandomRepair implements IRepairMethods {

	/**
	 * F�gt zuf�llig ausgew�hlte Knoten des Graphen in die Tour ein.
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

		if (unvisitedNodes.isEmpty() || currentTour.isEmpty())
			return x;

		Random rng = new Random();
		int numberOfNodesToBeAdded = (int) (unvisitedNodes.size() * rng.nextDouble());
		Node randomNode;
		double lowestCost, tmpCost;
		int indexOfBestEdge;

		// F�gt die bestimme Anzahl unbesuchter Knoten in die Tour ein, ohne Tmax zu
		// ber�cksichtigen
		while (numberOfNodesToBeAdded > 0) {
			randomNode = unvisitedNodes.get(rng.nextInt(numberOfNodesToBeAdded));
			lowestCost = Double.POSITIVE_INFINITY;
			indexOfBestEdge = -1;
			int currentTourSize = currentTour.size();

			// Finde g�nstigste Position in der Tour, um den Knoten einzuf�gen
			for (int i = 0; i < currentTourSize; i++) {
				Edge currentEdge = currentTour.get(i);
				double startToV = Edge.getDistance(currentEdge.getStartNode(), randomNode);
				double vToEnd = Edge.getDistance(randomNode, currentEdge.getEndNode());
				tmpCost = (startToV + vToEnd) - currentEdge.getDistance();

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
			unvisitedNodes.remove(randomNode);
			numberOfNodesToBeAdded--;
		}

		// F�r den Fall, dass Tmax �berschritten wurde
		currentTour = Graph.restorefeasibility(currentTour, Tmax);

		return new Graph(x.getV(), currentTour);
	}
}
