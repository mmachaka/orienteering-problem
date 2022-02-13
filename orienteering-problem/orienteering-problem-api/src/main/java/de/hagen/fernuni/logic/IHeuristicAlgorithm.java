package de.hagen.fernuni.logic;

import de.hagen.fernuni.model.Graph;

/**
 * Interface, welches von den Klassen heuristischer Algorithmen zur Lösung des
 * Orientierungsproblems implementiert wird.
 * 
 * @author Mahmoud Machaka
 */
public interface IHeuristicAlgorithm {

	/**
	 * Führt einen heuristischen Algorithmus zur Lösung des Orientierungsproblems
	 * auf einen Graphen aus.
	 * 
	 * @return Gibt für einen Graphen eine heuristische Lösung des Orientierungsproblems zurück. 
	 */
	Graph solve();
	
	/**
	 * Setzt einen Flag, der signalisiert, dass die Berechnung vorzeitig abgebrochen werden soll.
	 */
	void cancel();
}
