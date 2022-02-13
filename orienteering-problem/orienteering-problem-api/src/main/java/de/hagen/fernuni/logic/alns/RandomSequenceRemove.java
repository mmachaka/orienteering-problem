package de.hagen.fernuni.logic.alns;

import java.util.Random;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * RandomSequenceRemove stellt eine Zerst�rmethode bereit, welche aus der Tour
 * des Graphen beginnend von einem zuf�llig ausgew�hlten Knoten eine
 * Kantensequenz entfernt.
 * 
 * @author Mahmoud Machaka
 *
 */
public class RandomSequenceRemove implements IDestroyMethods {

	/**
	 * Entfernt beginnend von einem zuf�llig ausgew�hlten Knoten der Tour eine
	 * Kantensequenz und f�gt die �brigen Kanten zu einer zusammenh�ngenden Tour
	 * wieder zusammen.
	 * 
	 * @param g     Graph, aus dessen Tour Knoten entfernt werden sollen.
	 * @param alpha Aggressivit�tsfaktor, der die Anzahl zu entfernender Knoten
	 *              beeinflusst.
	 */
	@Override
	public Graph destroy(Graph g, double alpha) {
		Graph x = new Graph(g);
		int numberOfNodesToBeRemoved = (int) (alpha * (x.getE().size() - 1));
		if (numberOfNodesToBeRemoved == 0 || x.getE().size() == 0) {
			return x;
		}

		Random rng = new Random();
		int index = -1;
		Node firstNodeToBeRemoved;
		Node start = x.getV().get(0);
		Node end = x.getV().get(1);

		do {
			index = rng.nextInt(x.getE().size());
			firstNodeToBeRemoved = x.getE().get(index).getStartNode();
		} while (firstNodeToBeRemoved.equals(start) || firstNodeToBeRemoved.equals(end));

		// Verschiebe den Beginn der L�schsequenz nach links, falls �ber den Endknoten
		// hinweg Kanten gel�scht werden.
		if (index + numberOfNodesToBeRemoved > x.getE().size()) {
			index = x.getE().size() - numberOfNodesToBeRemoved;
		}

		while (numberOfNodesToBeRemoved > 0) {
			// Verbinde Startknoten der Vorg�ngerkante mit Endknoten der aktuellen Kante
			// (index) und entferne aktuelle Kante
			x.getE().set(index, new Edge(x.getE().get(index - 1).getStartNode(), x.getE().get(index).getEndNode()));
			x.getE().remove(index - 1);

			numberOfNodesToBeRemoved--;
		}

		return new Graph(x);
	}

}
