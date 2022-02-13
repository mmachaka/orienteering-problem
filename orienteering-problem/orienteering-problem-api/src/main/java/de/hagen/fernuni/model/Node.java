package de.hagen.fernuni.model;

import java.util.Objects;

/**
 * Modelliert einen Knoten, der aus einer X- und Y-Koordinate und einem
 * Profitwert besteht.
 * 
 * @author Mahmoud Machaka
 */
public class Node implements Comparable<Node> {
	private double x;
	private double y;
	private double profit; //

	/**
	 * Konstruktor: Erzeugt aus einer X- und Y-Koordinate und einem Profitwert einen Knoten.
	 * @param x X-Koordinate des Knotens
	 * @param y Y-Koordinate des Knotens
	 * @param profit Profit des Knotens
	 */
	public Node(double x, double y, double profit) {
		this.x = x;
		this.y = y;
		this.profit = profit;
	}

	/**
	 * Gibt die X-Koordinate des Knotens zurück.
	 * @return X-Koordinate des Knotens
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gibt die Y-Koordinate des Knotens zurück.
	 * @return Y-Koordinate des Knotens
	 */
	public double getY() {
		return y;
	}

	/**
	 * Gibt den Profit des Knotens zurück.
	 * @return Profit des Knotens
	 */
	public double getProfit() {
		return profit;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof Node) {
			Node v = (Node) obj;
			if (this.getX() == v.getX() && this.getY() == v.getY())
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		String code = this.getX() + " " + this.getY();
		return Objects.hashCode(code);
	}

	@Override
	public String toString() {
		return x + "\t" + y + "\t" + profit;
	}

	@Override
	public int compareTo(Node v) {
		if (this.profit < v.profit)
			return -1;
		else if (this.profit == v.profit)
			return 0;
		return 1;
	}

}
