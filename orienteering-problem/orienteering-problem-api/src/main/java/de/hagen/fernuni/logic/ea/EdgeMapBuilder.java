package de.hagen.fernuni.logic.ea;

import java.util.ArrayList;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * EdgeMapBuilder erm�glicht die Erzeugung einer Edge Map, welche f�r den
 * Crossover-Operator des evolution�ren Algorithmus ben�tigt wird.
 * 
 * @author Mahmoud Machaka
 *
 */
public class EdgeMapBuilder {

	/**
	 * Erzeugt f�r die beiden �bergebenen Graphen (Eltern) eine Edge Map
	 * 
	 * @param parents     Graphen (Eltern), f�r welche die Edge Map erzeugt werden
	 *                    soll.
	 * @param commonNodes Gemeinsame Knoten der Graphen (Eltern)
	 * @return Edge Map
	 */
	public static ArrayList<EdgeMapEntry> getEdgeMap(Graph[] parents, ArrayList<Node> commonNodes) {
		ArrayList<EdgeMapEntry> edgeMap = new ArrayList<EdgeMapEntry>();

		// Ermittle zwischenliegende Pfade
		ArrayList<ArrayList<Edge>> AllIntermediatePaths = new ArrayList<ArrayList<Edge>>();

		for (int i = 0; i < 2; i++) {
			ArrayList<Edge> intermediatePath = new ArrayList<Edge>();
			for (Edge e : parents[i].getE()) {
				if (!commonNodes.contains(e.getEndNode())) {
					intermediatePath.add(e);
				} else {
					intermediatePath.add(e);
					if (!AllIntermediatePaths.contains(intermediatePath))
						AllIntermediatePaths.add(intermediatePath);
					intermediatePath = new ArrayList<Edge>();
				}
			}
		}

		// Pfade spiegeln, um Traversierung in beide Richtungen zu erm�glichen
		@SuppressWarnings("unchecked")
		ArrayList<ArrayList<Edge>> AllIntermediatePathsTmp = (ArrayList<ArrayList<Edge>>) AllIntermediatePaths.clone();
		for (ArrayList<Edge> intermediatePath : AllIntermediatePathsTmp) {
			ArrayList<Edge> pathMirrored = new ArrayList<Edge>();
			for (int i = intermediatePath.size() - 1; i >= 0; i--) {
				Edge currentEdge = intermediatePath.get(i);
				pathMirrored.add(new Edge(currentEdge.getEndNode(), currentEdge.getStartNode()));
			}
			if (!AllIntermediatePaths.contains(pathMirrored))
				AllIntermediatePaths.add(pathMirrored);
		}

		// Erzeuge Edge Map Entry
		for (Node commonNode : commonNodes) {
			ArrayList<Node> connectedNodes = new ArrayList<Node>();
			ArrayList<ArrayList<Edge>> intermediatePaths = new ArrayList<ArrayList<Edge>>();

			for (ArrayList<Edge> intermediatePath : AllIntermediatePaths) {
				if (intermediatePath.get(0).getStartNode().equals(commonNode)) {
					intermediatePaths.add(intermediatePath);
					Node connectedNode = intermediatePath.get(intermediatePath.size() - 1).getEndNode();
					if (!connectedNodes.contains(connectedNode)) {
						connectedNodes.add(connectedNode);
					}
				}
			}

			edgeMap.add(new EdgeMapEntry(commonNode, connectedNodes, connectedNodes.size(), intermediatePaths));
		}

		return edgeMap;
	}

	/**
	 * Entfernt in der Edge Map einen Knoten aus s�mtlichen Eintr�gen in der Spalte
	 * verbundender Knoten.
	 * 
	 * @param edgeMap Edge Map, aus der ein Knoten in allen Eintr�gen der Spalte
	 *                verbundener Knoten entfernt werden soll.
	 * @param v       Zu entfernender Knoten.
	 * @return Edge Map, aus welcher der Knoten v in der Spalte verbundener Knoten
	 *         vollst�ndig entfernt wurde.
	 */
	public static ArrayList<EdgeMapEntry> removeNodeFromConnectedNodes(ArrayList<EdgeMapEntry> edgeMap, Node v) {
		ArrayList<EdgeMapEntry> newEdgeMap = new ArrayList<EdgeMapEntry>();
		for (EdgeMapEntry e : edgeMap) {
			ArrayList<Node> newConnectedNodes = e.getConnectedNodes();
			while (newConnectedNodes.remove(v))
				;
			newEdgeMap.add(new EdgeMapEntry(e.getCommonNode(), newConnectedNodes, newConnectedNodes.size(),
					e.getIntermediatePaths()));
		}
		return newEdgeMap;
	}

}
