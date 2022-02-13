package de.hagen.fernuni.logic.alns;

import de.hagen.fernuni.model.Graph;

/**
 * Interface, welches alle Reparaturmethoden implementieren.
 * 
 * @author Mahmoud Machaka
 */
public interface IRepairMethods {
	/**
	 * Nimmt weitere Knoten in die Tour eines Graphen auf.
	 * 
	 * @param g    Graph, in dessen Tour Knoten eingef�gt werden sollen.
	 * @param Tmax Kostenobergrenze
	 * @return Graph mit einer g�ltigen Tour, in welcher ggf. Knoten eingef�gt
	 *         wurden.
	 */
	Graph repair(Graph g, double Tmax);
}
