package de.hagen.fernuni.model;

import java.util.Objects;

/**
 * Modelliert eine Kante, die über einen Start- und Endknoten definiert wird.
 * 
 * @author Mahmoud Machaka
 */
public class Edge {
	private Node startNode;
	private Node endNode;
	private double distance;

	/**
	 * Konstruktor: Erzeugt aus einem Start- und Endknoten eine Kante.
	 * 
	 * @param startNode Startknoten der Kante
	 * @param endNode   Endknoten der Kante
	 */
	public Edge(Node startNode, Node endNode) {
		this.startNode = startNode;
		this.endNode = endNode;
		this.distance = getDistance(startNode, endNode);
	}

	/**
	 * Gibt den Startknoten der Kante zurück.
	 * 
	 * @return Startknoten der Kante
	 */
	public Node getStartNode() {
		return startNode;
	}

	/**
	 * Gibt den Endknoten der Kante zurück.
	 * 
	 * @return Endknoten der Kante
	 */
	public Node getEndNode() {
		return endNode;
	}

	/**
	 * Gibt die Distanz der Kante zurück.
	 * 
	 * @return Distanz der Kante
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Berechnet die euklidische Distanz zwischen zwei Knoten.
	 * 
	 * @param startNode
	 * @param endNode
	 * @return Euklidische Distanz zwischen zwei Knoten
	 */
	public static double getDistance(Node startNode, Node endNode) {
		// Distanz = Wurzel ( (x2-x1)^2 + (y2-y1)^2 )
		return Math.sqrt(Math.pow(Math.abs(endNode.getX() - startNode.getX()), 2)
				+ Math.pow(Math.abs(endNode.getY() - startNode.getY()), 2));
	}

	@Override
	public String toString() {
		return "(" + startNode.getX() + ", " + startNode.getY() + ") , (" + endNode.getX() + ", "
				+ endNode.getY() + ") ,  (" + distance + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof Edge) {
			Edge e = (Edge) obj;
			if (this.getStartNode().equals(e.getStartNode()) && this.getEndNode().equals(e.getEndNode()))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		String code = "[" + this.getStartNode() + ", " + this.getEndNode() + "]";
		return Objects.hashCode(code);
	}
}
