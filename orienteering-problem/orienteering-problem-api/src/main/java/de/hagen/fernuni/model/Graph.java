package de.hagen.fernuni.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

/**
 * Modelliert einen Graphen, der je eine Knoten- und Kantenliste besitzt.
 * 
 * @author Mahmoud Machaka
 */
public class Graph {

	private ArrayList<Node> nodeList;
	private ArrayList<Edge> edgeList;

	/**
	 * Konstruktor: Erzeugt aus einem �bergebenen Graphen einen neuen Graphen.
	 * 
	 * @param g Graph, der gecloned werden soll.
	 */
	
	@SuppressWarnings("unchecked")
	public Graph(Graph g) {
		this.nodeList = (ArrayList<Node>) g.getV().clone();
		this.edgeList = (ArrayList<Edge>) g.getE().clone();
	}

	/**
	 * Konstruktor: Erzeugt aus einer Knotenliste und einer Kantenliste einen
	 * Graphen.
	 * 
	 * @param nodeList Knotenliste des zu erzeugenden Graphen
	 * @param edgeList   Kantenliste des zu erzeugenden Graphen
	 */
	public Graph(ArrayList<Node> nodeList, ArrayList<Edge> edgeList) {
		this.nodeList = nodeList;
		this.edgeList = edgeList;
	}

	/**
	 * Gibt die Knotenliste des Graphen zur�ck.
	 * 
	 * @return Knotenliste als ArrayList
	 */
	public ArrayList<Node> getV() {
		return nodeList;
	}

	/**
	 * Gibt die Kantenliste bzw. aktuelle Tour des Graphen zur�ck.
	 * 
	 * @return Kantenliste als ArrayList
	 */
	public ArrayList<Edge> getE() {
		return edgeList;
	}

	@Override
	public String toString() {
		String output = "";
		for (Node v : nodeList) {
			output = output + v.toString() + "\n";
		}
		output = output + "\n";
		for (Edge e : edgeList) {
			output = output + e.toString() + "\n";
		}
		return output;
	}

	/**
	 * Gibt die Gr��e des Graphen bzw. seiner Knotenmenge zur�ck.
	 * 
	 * @return Gr��e der Knotenmenge
	 */
	public int getSize() {
		return nodeList.size();
	}

	/**
	 * Gibt die Summe der Profite besuchter Knoten zur�ck.
	 * 
	 * @return Summe der Profite besuchter Knoten
	 */
	public double getProfitOfTour() {
		double profit = 0.0;
		for (Edge k : this.getE()) {
			profit += k.getEndNode().getProfit();
		}
		return profit;
	}

	/**
	 * Gibt die Summe der Profite aller Knoten zur�ck.
	 * 
	 * @return Summe der Profite aller Knoten
	 */
	public double getTotalProfit() {
		double totalProfit = 0.0;
		for (Node v : nodeList) {
			totalProfit += v.getProfit();
		}
		return totalProfit;
	}

	/**
	 * Gibt Kosten der aktuellen Tour zur�ck.
	 * 
	 * @return Kosten der aktuellen Tour
	 */
	public double getCostOfTour() {
		double distance = 0;
		for (Edge e : this.getE()) {
			distance += e.getDistance();
		}
		return distance;
	}

	/**
	 * Gibt Kosten der �bergebenen Tour zur�ck.
	 * 
	 * @param tour Tour, f�r welche die Kosten berechnet werden soll
	 * @return Kosten der �bergebenen Tour
	 */
	public static double getCostOfTour(ArrayList<Edge> tour) {
		double distance = 0;
		for (Edge e : tour) {
			distance += e.getDistance();
		}
		return distance;
	}

	/**
	 * Gibt die in der Tour besuchten Knoten zur�ck.
	 * 
	 * @return In der Tour besuchte Knoten als Knotenliste
	 */
	public ArrayList<Node> getVisitedNodes() {
		ArrayList<Node> visitedNodes = new ArrayList<Node>();
		if (getE().isEmpty()) {
			return visitedNodes;
		}

		for (Edge e : getE()) {
			visitedNodes.add(e.getStartNode());
		}

		visitedNodes.add(getE().get(getE().size() - 1).getEndNode());

		return visitedNodes;
	}

	/**
	 * Gibt die in der �bergebenen Tour besuchten Knoten zur�ck.
	 * 
	 * @param edges Tour, in welcher die besuchten Knoten identifiziert werden
	 *              sollen.
	 * @return In der �bergebenen Tour besuchte Knoten als Knotenliste
	 */
	public static ArrayList<Node> getVisitedNodes(ArrayList<Edge> edges) {
		ArrayList<Node> visitedNodes = new ArrayList<Node>();
		if (edges.isEmpty()) {
			return visitedNodes;
		}
		for (Edge e : edges) {
			visitedNodes.add(e.getStartNode());
		}
		visitedNodes.add(edges.get(edges.size() - 1).getEndNode());
		return visitedNodes;
	}

	/**
	 * Gibt die unbesuchten Knoten zur�ck.
	 * 
	 * @return Unbesuchte Knoten als Knotenliste.
	 */
	public ArrayList<Node> getUnvisitedNodes() {
		@SuppressWarnings("unchecked")
		ArrayList<Node> unvisitedNodes = (ArrayList<Node>) getV().clone();
		unvisitedNodes.removeAll(getVisitedNodes());
		return unvisitedNodes;
	}

	/**
	 * Liefert Fitness-Score der Tour zur�ck: Profit der Tour / Gesamtprofit
	 * 
	 * @return Fitness-Score der Tour
	 */
	public double getFitnessScore() {
		if (getTotalProfit() == 0)
			return 0;
		return getProfitOfTour() / getTotalProfit();
	}

	/**
	 * Pr�ft, ob ein Knoten v in einer Kante e der Tour eingesetzt werden kann, ohne
	 * die Kostenobergrenze zu �berschreiten.
	 * 
	 * @param v           Der einzuf�gende Knoten
	 * @param e           Kante, in welcher der Knoten eingef�gt werden soll
	 * @param currentTour Aktuelle Tour
	 * @param Tmax        Kostenobergrenze
	 * @return Gibt wahr zur�ck, wenn der Knoten eingesetzt werden kann, ohne die
	 *         Kostenobergrenze zu �berschreiten.
	 */
	public static boolean insertionIsViable(Node v, Edge e, ArrayList<Edge> currentTour, double Tmax) {
		double currentCost = Graph.getCostOfTour(currentTour);
		double vToStartCost = Edge.getDistance(e.getStartNode(), v);
		double vToEndCost = Edge.getDistance(v, e.getEndNode());

		if (((currentCost - e.getDistance()) + vToStartCost + vToEndCost) <= Tmax)
			return true;
		return false;
	}

	/**
	 * Entfernt aus der �bergebenen Tour solange die ung�nstigsten Knoten, bis die
	 * Kostenobergrenze nicht mehr �berschritten wird, um eine g�ltige Tour zu
	 * erzeugen.
	 * 
	 * @param currentTour Aktuelle Tour
	 * @param Tmax        Kostenobergrenze
	 * @return Eine Tour, welche die Kostenobergrenze nicht �berschreitet.
	 */
	public static ArrayList<Edge> restorefeasibility(ArrayList<Edge> currentTour, double Tmax) {
		double currentCost = Graph.getCostOfTour(currentTour);
		if (currentCost <= Tmax)
			return currentTour;

		double highestScore, tmpScore;
		Node newStartV = null;
		Node newEndV = null;
		int indexEdge;

		while (Graph.getCostOfTour(currentTour) > Tmax && currentTour.size() > 1) {
			highestScore = Double.NEGATIVE_INFINITY;
			indexEdge = -1;

			int currentTourSize = currentTour.size();
			Edge currentEdge, nextEdge;

			// Berechne f�r die Endknoten aller Kanten den Score - Ausgenommen Endknoten der
			// Tour
			for (int i = 0; i < currentTourSize - 1; i++) {
				currentEdge = currentTour.get(i);
				nextEdge = currentTour.get(i + 1);
				tmpScore = ((currentEdge.getDistance() + nextEdge.getDistance())
						- Edge.getDistance(currentEdge.getStartNode(), nextEdge.getEndNode()))
						/ currentEdge.getEndNode().getProfit();

				if (tmpScore > highestScore) {
					highestScore = tmpScore;
					indexEdge = i;
					newStartV = currentEdge.getStartNode();
					newEndV = nextEdge.getEndNode();
				}
			}

			// Ersetze die beiden mit dem Knoten verbundenen Kanten mit einer neuen Kante
			currentTour.remove(indexEdge);
			currentTour.set(indexEdge, new Edge(newStartV, newEndV));
		}

		return currentTour;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof Graph) {
			Graph g = (Graph) obj;
			if (getV().equals(g.getV()) && getE().equals(g.getE()))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		String code = nodeList.toString() + edgeList.toString();
		return Objects.hashCode(code);
	}

	/**
	 * Pr�ft, ob die Tour unterbrechungsfrei ist. Wird f�r die JUnit-Tests ben�tigt.
	 * 
	 * @return Gibt true zur�ck, wenn die Tour unterbrechungsfrei ist.
	 */
	public boolean tourIsUninterrupted() {
		for (int i = 0; i < this.getE().size() - 1; i++) {
			if (!this.getE().get(i).getEndNode().equals(this.getE().get(i + 1).getStartNode()))
				return false;
		}
		return true;
	}

	/**
	 * Pr�ft, ob die Tour im Startknoten beginnt und im Endknoten endet. Wird f�r
	 * die JUnit-Tests ben�tigt.
	 * 
	 * @return Gibt true zur�ck, wenn die Tour im Startknoten beginnt und im
	 *         Endknoten endet
	 */
	public boolean tourHasValidDepots() {
		if (this.getE().size() == 0)
			return true;
		return ((this.getE().get(0).getStartNode() == this.getV().get(0))
				&& (this.getE().get(this.getE().size() - 1).getEndNode() == this.getV().get(1)));
	}

	/**
	 * Pr�ft, ob Knoten mehrmals besucht wurden. Wird f�r die JUnit-Tests ben�tigt.
	 * 
	 * @return Gibt true zur�ck, wenn kein Knoten mehrmals besucht wurde
	 *         (ausgenommen Start- und Endknoten).
	 */
	public boolean tourHasNoDuplicates() {
		ArrayList<Node> visitedNodes = getVisitedNodes();
		visitedNodes.remove(getV().get(0)); // Start und Endknoten d�rfen gleich sein

		HashSet<Node> visitedNodesHashSet = new HashSet<Node>();
		for (Node v : visitedNodes) {
			if (!visitedNodesHashSet.add(v)) {
				return false;
			}
		}
		return true;
	}

}
