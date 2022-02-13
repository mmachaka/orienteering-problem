package de.hagen.fernuni.logic.alns;

import java.util.ArrayList;
import java.util.Collections;


import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * Klasse zur Bildung von Clustern
 * <p>
 * Basierend auf dem DBSCAN nach Ester, Kriegel, Sander und Xu ermöglicht die
 * Klasse ein potentiell unvollständiges Clustering auf einen Graphen
 * anzuwenden. Zwar benötigt der DBSCAN-Algorithmus als Eingabeparameter einen
 * Radius r und eine Anzahl N zur Beschreibung der Nachbarschaftsgröße. Die
 * Klasse ClusterBuilder basiert auf den von Santini angepassen Algorithmus, der
 * diese Parameter automatisch bestimmt.
 * 
 * @author Mahmoud Machaka
 *
 */
public class ClusterBuilder {
	
	/**
	 * Führt auf einen Graphen ein Clustering durch und gibt die Cluster-Liste
	 * zurück.
	 * 
	 * @param g Graph, für welches ein Clustering durchgeführt werden soll.
	 * @return Clustering als Liste von Knotenlisten
	 */

	public ArrayList<ArrayList<Node>> getClusters(Graph g) {
		Graph x = new Graph(g);
		ArrayList<ArrayList<Node>> clusters = new ArrayList<ArrayList<Node>>();
		@SuppressWarnings("unchecked")
		ArrayList<Node> allNodes = (ArrayList<Node>) x.getV().clone();

		if (allNodes.isEmpty())
			return clusters;

		double r = calculateRadius(x);
		int N = calculateNumberOfNeighoursForCore(x, r);

		if (N == 0)
			return clusters;

		@SuppressWarnings("unchecked")
		ArrayList<Node> nodesWithoutClusters = (ArrayList<Node>) x.getV().clone();
		ArrayList<Node> cluster = new ArrayList<Node>();
		ArrayList<Node> reachableNodes;

		// Clonen, um ConcurrentModificationException beim Entfernen von Knoten zu
		// umgehen
		@SuppressWarnings("unchecked")
		ArrayList<Node> nodesWithoutClustersTmp = (ArrayList<Node>) nodesWithoutClusters.clone();

		for (Node node : nodesWithoutClusters) {
			// Falls der Knoten ein Kernknoten ist und noch keinem Cluster angehört
			if (isCoreNode(node, allNodes, r, N) && !cluster.contains(node)) {
				cluster = new ArrayList<Node>();
				reachableNodes = getNeighbours(node, cluster, nodesWithoutClustersTmp, r);
				for (Node reachableNode : reachableNodes) {
					nodesWithoutClustersTmp.remove(reachableNode);
				}
				if (!cluster.isEmpty())
					clusters.add(cluster);
			}
		}
		return clusters;
	}

	/**
	 * Fügt rekursiv alle Knoten in einen Cluster ein, die von einem Knoten
	 * erreichbar sind und in keinem anderen Cluster enthalten sind
	 * 
	 * @param coreNode              Kernknoten
	 * @param cluster                 Aktueller Cluster
	 * @param nodesWithoutClusters Knoten, die noch keinem Cluster zugeordnet
	 *                                wurden
	 * @param r                       Radius
	 * @return Nachbaren des Kernknotens
	 */
	private ArrayList<Node> getNeighbours(Node coreNode, ArrayList<Node> cluster,
			ArrayList<Node> nodesWithoutClusters, double r) {

		ArrayList<Node> neighboursRecursive = new ArrayList<Node>();
		ArrayList<Node> neighboursRecursiveTmp = new ArrayList<Node>();
		ArrayList<Node> allNeighbours = new ArrayList<Node>();

		if (nodesWithoutClusters.isEmpty())
			return neighboursRecursive;

		// Füge direkte Nachbaren des Kernknoten hinzu, falls sie nicht bereits
		// vorhanden sind
		for (Node v : nodesWithoutClusters) {
			if ((Edge.getDistance(coreNode, v) <= r) && !cluster.contains(v)) {
				cluster.add(v);
				neighboursRecursive.add(v);
				allNeighbours.add(v);
			}
		}

		// Entferne in Cluster aufgenommene Knoten aus der Liste
		// nodesWithoutClusters.
		// Clonen zur Umgehung der ConcurrentModificationExeption beim Entfernen von
		// Knoten.
		@SuppressWarnings("unchecked")
		ArrayList<Node> nodesWithoutClustersTmp = (ArrayList<Node>) nodesWithoutClusters.clone();
		nodesWithoutClustersTmp.removeAll(neighboursRecursive);
		nodesWithoutClusters = nodesWithoutClustersTmp;

		// Füge rekursiv die Nachbaren der Nachbaren hinzu
		for (Node v : neighboursRecursive) {
			neighboursRecursiveTmp = getNeighbours(v, cluster, nodesWithoutClusters, r);
			for (Node vTmp : neighboursRecursiveTmp) {
				allNeighbours.add(vTmp);
			}
			neighboursRecursive = neighboursRecursiveTmp;
		}
		return allNeighbours;
	}

	/**
	 * Prüft, ob ein Knoten ein Kernknoten ist.
	 * 
	 * @param v        Der zu prüfende Knoten
	 * @param nodes Knotenliste des Graphen
	 * @param r        Radius
	 * @param N        Mindestanzahl an Nachbaren, um als Kernknoten zu gelten.
	 * @return Gibt true zurück, wenn der betrachtete Knoten mindestens N Knoten
	 *         innerhalb des Radius r um sich hat.
	 */
	private boolean isCoreNode(Node v, ArrayList<Node> nodes, double r, int N) {
		int amountNeighbours = 0;
		for (Node node : nodes) {
			if (Edge.getDistance(v, node) <= r)
				amountNeighbours++;
			if (amountNeighbours >= N)
				return true;
		}
		return false;
	}

	/**
	 * Bestimmt automatisch die Mindestanzahl an Nachbaren N, die ein Knoten haben
	 * muss, um als Kernknoten zu gelten.
	 * 
	 * @param g Graph, für welches ein Clustering durchgeführt werden soll.
	 * @param r Radius
	 * @return Anzahl N
	 */
	private int calculateNumberOfNeighoursForCore(Graph g, double r) {
		ArrayList<Node> nodeList = g.getV();
		int nodeListSize = nodeList.size();

		// Berechne für jeden Knoten die Anzahl seiner Nachbaren
		ArrayList<Integer> numberOfNeighboursList = new ArrayList<Integer>();
		int amountOfNeighbours;
		for (int i = 0; i < nodeListSize; i++) {
			amountOfNeighbours = 0;
			for (int j = 0; j < nodeListSize; j++) {
				// Ein Knoten ist nicht sein eigener Nachbar
				if (i != j) {
					if (Edge.getDistance(nodeList.get(i), nodeList.get(j)) <= r)
						amountOfNeighbours++;
				}
			}
			numberOfNeighboursList.add(amountOfNeighbours);
		}

		Collections.sort(numberOfNeighboursList);
		ArrayList<ArrayList<Integer>> buckets = sortIntoBuckets(numberOfNeighboursList);

		int indexOfBiggestBucket = 0;
		int bucketSize = 0;
		for (int i = 0; i < buckets.size(); i++) {
			if (buckets.get(i).size() > bucketSize) {
				indexOfBiggestBucket = i;
				bucketSize = buckets.get(i).size();
			}
		}

		// Gib die größtmögliche Zahl des ausgewählten Buckets zurück
		return (int) ((double) (indexOfBiggestBucket + 1)
				* (double) numberOfNeighboursList.get(numberOfNeighboursList.size() - 1) / 19.0);
	}

	// Sortiert die Liste in 20 Bereiche ein
	/**
	 * Sortiert eine Liste in 20 Bereiche (Buckets) ein, welche die Anzahl der
	 * Nachbaren aller Knoten enthält.
	 * 
	 * @param numberOfNeighboursList Eine Liste, welche jeweils die Anzahl der
	 *                               Nachbaren aller Knoten enthält.
	 * @return Sortierte Liste, die in 20 Bereiche aufgeteilt ist.
	 */
	private ArrayList<ArrayList<Integer>> sortIntoBuckets(ArrayList<Integer> numberOfNeighboursList) {
		ArrayList<ArrayList<Integer>> buckets = new ArrayList<ArrayList<Integer>>(20);
		while (buckets.size() < 20) {
			buckets.add(new ArrayList<Integer>());
		}
		int indexOfBucket;
		int max = numberOfNeighboursList.get(numberOfNeighboursList.size() - 1);
		for (Integer numberOfNeighbours : numberOfNeighboursList) {
			indexOfBucket = (int) (((double) numberOfNeighbours / (double) max) * 19);
			buckets.get(indexOfBucket).add(numberOfNeighbours);
		}
		return buckets;
	}

	/**
	 * Berechnet automatisch den Radius r. Knoten mit einem Abstand kleiner oder
	 * gleich dem Radius werden als Nachbaren bezeichnet.
	 * 
	 * @param g Graph, für welches ein Clustering durchgeführt werden soll.
	 * @return Radius r
	 */
	private double calculateRadius(Graph g) {
		ArrayList<Double> distances_di = new ArrayList<Double>();
		ArrayList<Node> nodeList = g.getV();

		double minDistance, tmpDistance;
		int nodeListSize = nodeList.size();

		// Finde für alle Knoten die Distanz zum nächstgelegenen Knoten
		for (int i = 0; i < nodeListSize; i++) {
			minDistance = Double.POSITIVE_INFINITY;
			for (int j = 0; j < nodeListSize; j++) {
				// Betrachte nicht die Distanz zu sich selbst
				if (i != j) {
					tmpDistance = Edge.getDistance(nodeList.get(i), nodeList.get(j));
					if (tmpDistance < minDistance) {
						minDistance = tmpDistance;
					}
				}
			}
			distances_di.add(minDistance);
		}

		return Collections.max(distances_di);
	}

}
