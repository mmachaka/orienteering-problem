package de.hagen.fernuni.logic.ea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * ServiceMethods bietet genetische und optimierende Operatoren für den
 * evolutionären Algorithmus an.
 * 
 * @author Mahmoud Machaka
 *
 */
public class ServiceMethods {

	/**
	 * Wählt zwei Lösungen (Eltern) aus der Population nach gewichtetem Zufall aus.
	 * <p>
	 * Bildet aus der Population zunächst eine Untermenge bzw. Kandidatenliste. Die
	 * Kandidaten werden nach ihrem Fitness-Score bewertet, der als Grundlage zur
	 * gewichteten Auswahl zweier Lösungen (Eltern) verwendet wird.
	 * 
	 * @param population Aktuelle Population
	 * @param ncand      Anzahl der Kandidaten
	 * @return Zwei Graphen (Eltern)
	 */
	public static Graph[] selectParents(ArrayList<Graph> population, int ncand) {
		// Wähle zufällig ncand Kandidaten aus
		ArrayList<Graph> candidates = new ArrayList<Graph>();
		@SuppressWarnings("unchecked")
		ArrayList<Graph> populationTmp = (ArrayList<Graph>) population.clone();
		Random rng = new Random();

		while (candidates.size() < ncand) {
			candidates.add(populationTmp.remove(rng.nextInt(populationTmp.size())));
		}

		// Ermittle kleinsten Fitness-Score
		ArrayList<Double> fitnessScores = new ArrayList<Double>(ncand);
		for (Graph candidate : candidates) {
			fitnessScores.add(candidate.getFitnessScore());
		}
		double minFitnessScore = Collections.min(fitnessScores);

		// Berechne Gewichte für Wahl zweier Eltern-Graphen (gewichteter Zufall)
		double totalSumFitnessCorrected = 0.0;
		for (Graph candidate : candidates) {
			totalSumFitnessCorrected += candidate.getFitnessScore() - minFitnessScore + 1;
		}

		ArrayList<Pair<Graph, Double>> candidateWeights = new ArrayList<Pair<Graph, Double>>();
		for (Graph candidate : candidates) {
			double individualProbability = (candidate.getFitnessScore() - minFitnessScore + 1)
					/ totalSumFitnessCorrected;
			candidateWeights.add(new Pair<Graph, Double>(candidate, individualProbability));
		}

		// Wähle zwei Eltern-Graphen (gewichteter Zufall)
		EnumeratedDistribution<Graph> enumDist = new EnumeratedDistribution<Graph>(candidateWeights);

		Graph[] parents = new Graph[2];
		parents[0] = enumDist.sample();

		int counter = 0;
		while (parents[1] == null && counter < 10) {
			Graph parent2 = enumDist.sample();
			if (!parent2.equals(parents[0]) || counter == 9)
				parents[1] = parent2;
			counter++;
		}
		return parents;
	}

	/**
	 * Kreuzt zwei Graphen (Eltern) miteinander und bildet eine neue Lösung (Kind).
	 * <p>
	 * Bei der Kreuzung der Graphen wird ein neuer Graph gebildet, der mindestens
	 * alle gemeinsamen Knoten der Eltern in einer Tour enthält. Knoten, die nur in
	 * einem Elternteil vorkommen haben auch die Chance in der Kind-Lösungen
	 * enthalten zu sein. Die Kind-Lösung verbindet die Knoten nur mit den Kanten,
	 * die auch in einer oder beiden Eltern-Lösungen enthalten sind.
	 * 
	 * @param parents Zwei Graphen (Eltern)
	 * @return Graph (Kind)
	 */
	public static Graph crossover(Graph[] parents) {
		ArrayList<Node> visitedNodes1 = parents[0].getVisitedNodes();
		ArrayList<Node> visitedNodes2 = parents[1].getVisitedNodes();
		ArrayList<Node> commonNodes = (ArrayList<Node>) visitedNodes1.stream().filter(visitedNodes2::contains)
				.collect(Collectors.toList());

		ArrayList<Edge> tourOfChildSolution = new ArrayList<Edge>();

		// Wenn beide Elternteile identisch sind oder nur ein gemeinsamer Start- und
		// EndKnoten existieren, gib einen Elternteil zurück
		if (parents[0].equals(parents[1]) || commonNodes.size() == 2) {
			return new Graph(parents[0]);
		}

		// Entferne vorrübergehend den Endknoten der Tour
		commonNodes.remove(parents[0].getV().get(1));

		// Erzeuge Edge Map
		ArrayList<EdgeMapEntry> edgeMap = EdgeMapBuilder.getEdgeMap(parents, commonNodes);

		// Initialisiere Algorithmus
		Node currentNode = parents[0].getV().get(0);
		@SuppressWarnings("unchecked")
		ArrayList<Node> unvisitedCommonNodes = (ArrayList<Node>) commonNodes.clone();
		ArrayList<Node> visitedNodes = new ArrayList<Node>();

		unvisitedCommonNodes.remove(currentNode);
		visitedNodes.add(currentNode);

		int minDegree;
		Node nextNode = null;
		Random rng = new Random();

		while (!unvisitedCommonNodes.isEmpty()) {
			// Entferne aktuellen Knoten in der EdgeMap aus allen ConnectedNodes-Listen und
			// reduziere die Grade
			edgeMap = EdgeMapBuilder.removeNodeFromConnectedNodes(edgeMap, currentNode);

			minDegree = Integer.MAX_VALUE;
			// Betrachte EdgeMap-Eintrag des aktuellen Knotens
			for (EdgeMapEntry entry : edgeMap) {
				if (entry.getCommonNode().equals(currentNode)) {
					// Wenn Connected Nodes des aktuellen Eintrags vorhanden sind, wähle nächsten
					// Knoten
					// mit niedrigstem Rang
					if (!entry.getConnectedNodes().isEmpty()) {

						ArrayList<Node> nodesWithLowestDegree = new ArrayList<Node>();

						// Prüfe Grade aller ConnectedNodes des aktuellen Knotens
						for (EdgeMapEntry entryDegree : edgeMap) {

							// Überspringe den Knoten, falls dieser bereits in der Tour enthalten ist
							if (visitedNodes.contains(entryDegree.getCommonNode())) {
								edgeMap = EdgeMapBuilder.removeNodeFromConnectedNodes(edgeMap,
										entryDegree.getCommonNode());
								continue;
							}

							if (entry.getConnectedNodes().contains(entryDegree.getCommonNode())) {
								if (entryDegree.getDegree() < minDegree) {
									minDegree = entryDegree.getDegree();
									nodesWithLowestDegree = new ArrayList<Node>();
									nodesWithLowestDegree.add(entryDegree.getCommonNode());
								}
								if (entryDegree.getDegree() == minDegree) {
									nodesWithLowestDegree.add(entryDegree.getCommonNode());
								}
							}
						}

						// Wenn mehrere Knoten den niedrigsten Grad haben, wähle zufällig einen aus
						nextNode = nodesWithLowestDegree.get(rng.nextInt(nodesWithLowestDegree.size()));

						// Wähle einen Intermediate Path zum nächsten Knoten zufällig aus
						ArrayList<ArrayList<Edge>> intermediatePathSelection = new ArrayList<ArrayList<Edge>>();
						for (ArrayList<Edge> intermediatePath : entry.getIntermediatePaths()) {
							if (intermediatePath.get(intermediatePath.size() - 1).getEndNode().equals(nextNode))
								intermediatePathSelection.add(intermediatePath);
						}

						tourOfChildSolution
								.addAll(intermediatePathSelection.get(rng.nextInt(intermediatePathSelection.size())));
						unvisitedCommonNodes.remove(nextNode);
						visitedNodes.add(nextNode);
						currentNode = nextNode;

						// Eintrag wurde gefunden und abgearbeitet
						break;
					}

					// Andernfalls (Wenn Connected Nodes des aktuellen Eintrags leer ist) wähle
					// einen anderen unbesuchten Common Node und verbinde diesen
					else {
						if (!unvisitedCommonNodes.isEmpty()) {
							nextNode = unvisitedCommonNodes.get(rng.nextInt(unvisitedCommonNodes.size()));
							tourOfChildSolution.add(new Edge(currentNode, nextNode));
							unvisitedCommonNodes.remove(nextNode);
							visitedNodes.add(nextNode);
							currentNode = nextNode;
						}
					}
				}
			}
		}

		// Füge Endknoten wieder in CommonNodes ein und füge ihn in die Tour ein
		commonNodes.add(parents[0].getV().get(1));
		tourOfChildSolution.add(new Edge(currentNode, parents[0].getV().get(1)));

		return new Graph(parents[0].getV(), tourOfChildSolution);
	}

	/**
	 * Mutiert einen Graphen (Kind-Lösung)
	 * <p>
	 * Die Methode wählt aus der Knotenmenge des Graphen zufällig einen Knoten aus
	 * (ausgenommen Start- und Endknoten). Ist dieser Knoten in der Kind-Lösung
	 * enthalten, wird dieser aus der Tour entfernt. Andernfalls wird der Knoten an
	 * die günstigste Position eingesetzt.
	 * 
	 * @param child Der zu mutierende Graph (Kind-Lösung)
	 * @return Mutierter Graph
	 */
	public static Graph mutate(Graph child) {
		// Wähle einen zufälligen Knoten des Graphen, der nicht Start- oder Endknoten
		// ist
		@SuppressWarnings("unchecked")
		ArrayList<Node> nodeList = (ArrayList<Node>) child.getV().clone();
		nodeList.remove(0);
		nodeList.remove(0);
		Random rng = new Random();
		Node randomNode = nodeList.get(rng.nextInt(nodeList.size()));

		ArrayList<Node> visitedNodes = child.getVisitedNodes();
		@SuppressWarnings("unchecked")
		ArrayList<Edge> currentTour = (ArrayList<Edge>) child.getE().clone();
		int currentTourSize = currentTour.size();

		// Wenn der Knoten in der Tour enthalten ist, entferne ihn
		if (visitedNodes.contains(randomNode)) {
			for (int i = 0; i < currentTourSize - 1; i++) {
				if (currentTour.get(i).getEndNode().equals(randomNode)) {
					Edge currentEdge = currentTour.get(i);
					Edge nextEdge = currentTour.get(i + 1);
					currentTour.set(i, new Edge(currentEdge.getStartNode(), nextEdge.getEndNode()));
					currentTour.remove(i + 1);
					break;
				}
			}
		}

		// Andernfalls füge ihn an die beste Position hinzu
		else {
			double tmpCost;
			double lowestCost = Double.POSITIVE_INFINITY;
			int indexOfBestEdge = -1;

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
		}
		return new Graph(child.getV(), currentTour);
	}

	/**
	 * Add-Operator des evolutionären Algorithmus
	 * <p>
	 * Fügt einer Tour solange weitere Knoten hinzu, bis die Kostenobergrenze Tmax
	 * erreicht wurde oder bis keine unbesuchten Knoten mehr existieren. Knoten mit
	 * einem besseren Verhältnis von Profit zu erzeugten Mehrkosten werden bevorzugt
	 * eingefügt.
	 * 
	 * @param g    Graph
	 * @param Tmax Kostenobergrenze
	 * @return Graph mit potentiell weiteren besuchten Knoten
	 */
	public static Graph addOperator(Graph g, double Tmax) {
		// Abbruch, falls es keine Kante in der Tour gibt.
		if (g.getE().isEmpty())
			return g;

		@SuppressWarnings("unchecked")
		ArrayList<Edge> newTour = (ArrayList<Edge>) g.getE().clone();
		ArrayList<Node> unvisitedNodes = g.getUnvisitedNodes();
		ArrayList<Node> threeVisitedNeighbours = new ArrayList<Node>();

		while (!unvisitedNodes.isEmpty()) {
			Edge bestEdge = null;
			double addCost = 0.0;
			double addValue = 0.0;
			ArrayList<Triple<Double, Node, Edge>> valueList = new ArrayList<Triple<Double, Node, Edge>>();

			for (Node v : unvisitedNodes) {
				// Für den Fall, dass aktuell nur zwei Knoten besucht werden
				if (newTour.size() == 1) {
					bestEdge = newTour.get(0);
					addCost = calculateCostOfInsertion(bestEdge, v);
				} else {
					threeVisitedNeighbours = getNeighbours(newTour, v);

					ArrayList<Edge> adjacentEdges = getAdjacentEdges(newTour, threeVisitedNeighbours);
					if (adjacentEdges.size() >= 1) {
						bestEdge = chooseEdgeForAddingNode(adjacentEdges, v);
					} else {
						ArrayList<Edge> contiguousEdges = getContiguousEdges(newTour, threeVisitedNeighbours);
						bestEdge = chooseEdgeForAddingNode(contiguousEdges, v);
					}
					addCost = calculateCostOfInsertion(bestEdge, v);
				}

				if (Graph.getCostOfTour(newTour) + addCost <= Tmax) {
					addValue = v.getProfit() / addCost;
					valueList.add(Triple.of(addValue, v, bestEdge));
				} else {
					addValue = 0.0;
					valueList.add(Triple.of(addValue, v, bestEdge));
				}
			}

			// Sortiere valueList absteigend
			Collections.sort(valueList, new Comparator<Triple<Double, Node, Edge>>() {
				@Override
				public int compare(Triple<Double, Node, Edge> o1, Triple<Double, Node, Edge> o2) {
					if (o1.getLeft() < o2.getLeft())
						return 1;
					if (o1.getLeft() > o2.getLeft())
						return -1;
					return 0;
				}
			});

			// Abbruch, wenn kein Knoten mehr eingefügt werden kann ohne Tmax zu
			// überschreiten
			if (valueList.get(0).getLeft() == 0.0)
				break;

			// Füge gewählten Knoten in entsprechende Kante ein
			Edge oldEdge = valueList.get(0).getRight();
			Node newNode = valueList.get(0).getMiddle();
			int indexOldEdge = newTour.indexOf(oldEdge);
			newTour.set(indexOldEdge, new Edge(oldEdge.getStartNode(), newNode));
			newTour.add(indexOldEdge + 1, new Edge(newNode, oldEdge.getEndNode()));
			unvisitedNodes.remove(newNode);

		}

		return new Graph(g.getV(), newTour);
	}

	/**
	 * Liefert zum Knoten v die drei nächstgelegenen besuchten Knoten zurück. Sollte
	 * der Graph weniger als drei Knoten in der Tour enthalten, wird eine leere
	 * Liste zurückgegeben.
	 * 
	 * @param currentTour Kantenliste, in welchem die in der Tour enthaltenen
	 *                    Nachbaren gesucht werden sollen.
	 * @param v           Knoten, dessen besuchte Nachbaren gefunden werden sollen.
	 * @return ArrayList mit den drei nächstgelegenen besuchten Knoten
	 */
	private static ArrayList<Node> getNeighbours(ArrayList<Edge> currentTour, Node v) {
		ArrayList<Node> neighbours = new ArrayList<Node>();

		ArrayList<Node> visitedNodes = Graph.getVisitedNodes(currentTour);
		if (visitedNodes.size() < 3)
			return neighbours;

		ArrayList<Pair<Node, Double>> distances = new ArrayList<Pair<Node, Double>>();

		for (Node visitedNode : visitedNodes) {
			distances.add(new Pair<Node, Double>(visitedNode, Edge.getDistance(v, visitedNode)));
		}

		Collections.sort(distances, new Comparator<Pair<Node, Double>>() {
			@Override
			public int compare(Pair<Node, Double> o1, Pair<Node, Double> o2) {
				if (o1.getValue() < o2.getValue())
					return -1;
				if (o1.getValue() > o2.getValue())
					return 1;
				return 0;
			}
		});

		for (int i = 0; i < 3; i++) {
			neighbours.add(distances.get(i).getKey());
		}

		return neighbours;
	}

	/**
	 * Prüft, ob die benachbarten Knoten adjazent zueinander sind bzw. in der Tour
	 * direkt mit einer Kante verbunden sind und gibt diese Kanten zurück.
	 * 
	 * @param currentTour Aktuelle Tour als Kantenliste
	 * @param neighbours  Nachbaren, dessen gemeinsame Kanten zurückgegeben werden
	 *                    sollen.
	 * @return ArrayList mit Kanten, welche die Nachbaren verbinden.
	 */
	private static ArrayList<Edge> getAdjacentEdges(ArrayList<Edge> currentTour, ArrayList<Node> neighbours) {
		ArrayList<Edge> adjacentEdges = new ArrayList<Edge>();

		ArrayList<Edge> referenceEdges = new ArrayList<Edge>();
		referenceEdges.add(new Edge(neighbours.get(0), neighbours.get(1)));
		referenceEdges.add(new Edge(neighbours.get(1), neighbours.get(0)));
		referenceEdges.add(new Edge(neighbours.get(0), neighbours.get(2)));
		referenceEdges.add(new Edge(neighbours.get(2), neighbours.get(0)));
		referenceEdges.add(new Edge(neighbours.get(1), neighbours.get(2)));
		referenceEdges.add(new Edge(neighbours.get(2), neighbours.get(1)));

		for (Edge e : referenceEdges) {
			if (currentTour.contains(e))
				adjacentEdges.add(e);
		}
		return adjacentEdges;
	}

	/**
	 * Wenn die benachbarten Knoten nicht adjazent zueinander sind, können mit
	 * dieser Methode die zu den Nachbaren angrenzenden Kanten zurückgegeben werden.
	 * 
	 * @param currentTour Aktuelle Tour als Kantenliste
	 * @param neighbours  Nachbaren, dessen angrenzende Kanten zurückgegeben werden
	 *                    sollen.
	 * @return Gibt die zu den Nachbaren angrenzenden Kanten zurück.
	 */
	private static ArrayList<Edge> getContiguousEdges(ArrayList<Edge> currentTour, ArrayList<Node> neighbours) {
		ArrayList<Edge> contiguousEdges = new ArrayList<Edge>();

		for (Edge e : currentTour) {
			for (Node n : neighbours) {
				if (e.getStartNode() == n)
					contiguousEdges.add(e);
				if (e.getEndNode() == n)
					contiguousEdges.add(e);
			}
		}
		return contiguousEdges;
	}

	/**
	 * Wählt aus der Liste von Kanten die Kante aus, bei welcher durch das
	 * Hinzufügen des Knotens v die geringsten Mehrkosten entstehen.
	 * 
	 * @param edgeSelection Liste von Kanten, die zur Auswahl stehen.
	 * @param v             Der zu prüfende Knoten.
	 * @return Kante, welche die gerinsten Mehrkosten durch das Hinzufügen von v
	 *         erzeugt.
	 */
	private static Edge chooseEdgeForAddingNode(ArrayList<Edge> edgeSelection, Node v) {
		double distanceDelta = Double.POSITIVE_INFINITY;
		double distanceTmp;
		Edge bestEdge = null;
		for (Edge e : edgeSelection) {
			distanceTmp = calculateCostOfInsertion(e, v);
			if (distanceTmp < distanceDelta) {
				distanceDelta = distanceTmp;
				bestEdge = e;
			}
		}
		return bestEdge;
	}

	/**
	 * Berechnet die Mehrkosten, die durch das Einsetzen des Knotens v in die Kante
	 * e entstehen.
	 * 
	 * @param e Kante, in welcher der Knoten v eingesetzt werden soll.
	 * @param v Knoten, der in die Kante e eingesetzt werden soll.
	 * @return Mehrkosten
	 */
	private static double calculateCostOfInsertion(Edge e, Node v) {
		return Edge.getDistance(e.getStartNode(), v) + Edge.getDistance(v, e.getEndNode())
				- Edge.getDistance(e.getStartNode(), e.getEndNode());
	}
}
