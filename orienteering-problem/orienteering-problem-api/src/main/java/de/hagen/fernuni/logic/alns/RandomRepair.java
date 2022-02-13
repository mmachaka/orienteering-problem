package de.hagen.fernuni.logic.alns;

import java.util.ArrayList;
import java.util.Random;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * RandomRepair stellt eine Reparaturmethode bereit, welche zufällig gewählte
 * Knoten des Graphen in die Tour aufnimmt.
 * 
 * @author Mahmoud Machaka
 *
 */
public class RandomRepair implements IRepairMethods {

	/**
	 * Fügt zufällig ausgewählte Knoten des Graphen in die Tour ein.
	 * 
	 * @param g    Graph, in dessen Tour Knoten eingefügt werden sollen.
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

		// Fügt die bestimme Anzahl unbesuchter Knoten in die Tour ein, ohne Tmax zu
		// berücksichtigen
		while (numberOfNodesToBeAdded > 0) {
			randomNode = unvisitedNodes.get(rng.nextInt(numberOfNodesToBeAdded));
			lowestCost = Double.POSITIVE_INFINITY;
			indexOfBestEdge = -1;
			int currentTourSize = currentTour.size();

			// Finde günstigste Position in der Tour, um den Knoten einzufügen
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

			// Füge Knoten in die Tour ein
			Edge oldEdge = currentTour.get(indexOfBestEdge);
			currentTour.set(indexOfBestEdge, new Edge(oldEdge.getStartNode(), randomNode));
			currentTour.add(indexOfBestEdge + 1, new Edge(randomNode, oldEdge.getEndNode()));

			// Entferne Knoten aus der Liste unbesuchter Knoten
			unvisitedNodes.remove(randomNode);
			numberOfNodesToBeAdded--;
		}

		// Für den Fall, dass Tmax überschritten wurde
		currentTour = Graph.restorefeasibility(currentTour, Tmax);

		return new Graph(x.getV(), currentTour);
	}
}
