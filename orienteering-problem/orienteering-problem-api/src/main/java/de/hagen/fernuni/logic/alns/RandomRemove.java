package de.hagen.fernuni.logic.alns;

import java.util.Random;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * RandomRemove stellt eine Zerstörmethode bereit, welche aus der Tour des
 * Graphen zufällig ausgewählte Knoten entfernt.
 * 
 * @author Mahmoud Machaka
 *
 */
public class RandomRemove implements IDestroyMethods {

	/**
	 * Entfernt zufällig ausgewählte Knoten aus der Tour des Graphen und fügt die
	 * übrigen Kanten zu einer zusammenhängenden Tour wieder zusammen.
	 * 
	 * @param g     Graph, aus dessen Tour Knoten entfernt werden sollen.
	 * @param alpha Aggressivitätsfaktor, der die Anzahl zu entfernender Knoten
	 *              beeinflusst.
	 */
	@Override
	public Graph destroy(Graph g, double alpha) {
		Graph x = new Graph(g);
		int numberOfNodesToBeRemoved = (int) (alpha * (x.getE().size() - 1));
		if (numberOfNodesToBeRemoved == 0 || x.getE().size() <= 1) {
			return x;
		}

		Random rng = new Random();
		int index = -1;
		Node nodeToBeRemoved;
		Node start = x.getV().get(0);
		Node end = x.getV().get(1);

		while (numberOfNodesToBeRemoved > 0) {
			// Wähle den Startknoten einer zufälligen Kante, der weder Start- noch Endknoten
			// der Tour ist
			do {
				index = rng.nextInt(x.getE().size());
				nodeToBeRemoved = x.getE().get(index).getStartNode();
			} while (nodeToBeRemoved.equals(start) || nodeToBeRemoved.equals(end));

			// Verbinde Startknoten der Vorgängerkante mit Endknoten der aktuellen Kante
			// (index) und entferne aktuelle Kante
			x.getE().set(index, new Edge(x.getE().get(index - 1).getStartNode(), x.getE().get(index).getEndNode()));
			x.getE().remove(index - 1);

			numberOfNodesToBeRemoved--;
		}

		return new Graph(x);
	}

}
