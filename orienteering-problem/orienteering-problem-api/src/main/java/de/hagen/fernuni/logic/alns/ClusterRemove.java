package de.hagen.fernuni.logic.alns;

import java.util.ArrayList;
import java.util.Random;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * ClusterRemove stellt eine Zerstörmethode bereit, welche aus der Tour des
 * Graphen ein Cluster ganz oder teilweise entfernt.
 * 
 * @author Mahmoud Machaka
 *
 */
public class ClusterRemove implements IDestroyMethods {

	/**
	 * Entfernt aus der Tour des Graphen Knoten eines Clusters und fügt die übrigen
	 * Kanten zu einer zusammenhängenden Tour wieder zusammen.
	 * 
	 * @param g     Graph, aus dessen Tour Knoten entfernt werden sollen.
	 * @param alpha Aggressivitätsfaktor, der die Anzahl zu entfernender Knoten
	 *              beeinflusst.
	 */
	@Override
	public Graph destroy(Graph g, double alpha) {
		Graph x = new Graph(g);
		ClusterBuilder cb = new ClusterBuilder();
		ArrayList<ArrayList<Node>> clusters = cb.getClusters(x);

		// Wähle einen Cluster zufällig aus
		Random rng = new Random();
		ArrayList<Node> cluster = clusters.get(rng.nextInt(clusters.size()));

		// Ignoriere Start- und Endknoten der Tour, falls sie im Cluster sind
		Node start = x.getV().get(0);
		Node end = x.getV().get(1);
		cluster.remove(start);
		cluster.remove(end);

		// Ignoriere Knoten des Clusters, die nicht in der Tour enthalten sind
		ArrayList<Node> unvisitedNodes = x.getUnvisitedNodes();
		cluster.removeAll(unvisitedNodes);

		// Cluster enthält nur Knoten des Clusters, die auch in der Tour enthalten sind
		if (cluster.size() == 0) {
			return x;
		}

		int numberOfNodesToBeRemoved = (int) (alpha * x.getE().size() - 1);
		if (numberOfNodesToBeRemoved == 0 || x.getE().size() <= 1) {
			return x;
		}

		// Bestimme tatsächliche Anzahl zu löschender Knoten
		int clusterNodesInTour = cluster.size();
		if (clusterNodesInTour <= numberOfNodesToBeRemoved) {
			numberOfNodesToBeRemoved = clusterNodesInTour;
		}

		int index = -1;
		Node nodeToBeRemoved;

		// Entferne Cluster-Knoten (RandomRemove) und repariere dabei die Tour
		while (numberOfNodesToBeRemoved > 0) {
			index = -1;

			// Wähle zufälligen Knoten aus Cluster
			nodeToBeRemoved = cluster.get(rng.nextInt(cluster.size()));

			// Finde die Kante der Tour, die nodeToBeRemoved als Startknoten hat
			for (int i = 0; i < x.getE().size(); i++) {
				if (x.getE().get(i).getStartNode().equals(nodeToBeRemoved)) {
					index = i;
					break;
				}
			}

			// Verbinde Startknoten der Vorgängerkante mit Endknoten der aktuellen Kante
			// (index) und entferne aktuelle Kante
			x.getE().set(index, new Edge(x.getE().get(index - 1).getStartNode(), x.getE().get(index).getEndNode()));
			x.getE().remove(index - 1);
			cluster.remove(nodeToBeRemoved);
			numberOfNodesToBeRemoved--;
		}
		return new Graph(x);
	}

}
