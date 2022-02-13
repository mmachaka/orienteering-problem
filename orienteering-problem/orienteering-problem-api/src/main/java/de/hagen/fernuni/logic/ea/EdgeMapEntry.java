package de.hagen.fernuni.logic.ea;

import java.util.ArrayList;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Node;

/**
 * EdgeMapEntry modelliert einen Eintrag in der Edge Map, welche für den
 * Crossover-Operator des evolutionären Algorithmus benötigt wird.
 * 
 * @author Mahmoud Machaka
 *
 */
public class EdgeMapEntry {
	private Node commonNode;
	private ArrayList<Node> connectedNodes;
	private int degree;
	private ArrayList<ArrayList<Edge>> intermediatePaths;

	public EdgeMapEntry(Node commonNode, ArrayList<Node> connectedNodes, int degree,
			ArrayList<ArrayList<Edge>> intermediatePaths) {
		this.commonNode = commonNode;
		this.connectedNodes = connectedNodes;
		this.degree = degree;
		this.intermediatePaths = intermediatePaths;
	}

	public Node getCommonNode() {
		return commonNode;
	}

	public ArrayList<Node> getConnectedNodes() {
		return connectedNodes;
	}

	public int getDegree() {
		return degree;
	}

	public ArrayList<ArrayList<Edge>> getIntermediatePaths() {
		return intermediatePaths;
	}

	public void decreaseDegree() {
		degree--;
	}

}
