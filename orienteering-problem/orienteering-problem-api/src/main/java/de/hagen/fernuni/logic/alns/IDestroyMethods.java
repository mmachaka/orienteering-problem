package de.hagen.fernuni.logic.alns;

import de.hagen.fernuni.model.Graph;

/**
 * Interface, welches alle Zerst�rmethoden implementieren.
 * 
 * @author Mahmoud Machaka
 *
 */
public interface IDestroyMethods {
	/**
	 * Entfernt Knoten aus der Tour eines Graphen.
	 * 
	 * @param g     Graph, aus dessen Tour Knoten entfernt werden sollen.
	 * @param alpha Aggressivit�tsfaktor, der die Anzahl zu entfernender Knoten
	 *              beeinflusst.
	 * @return Graph mit einer g�ltigen Tour, aus welcher ggf. Knoten entfernt
	 *         wurden.
	 */
	Graph destroy(Graph g, double alpha);
}
