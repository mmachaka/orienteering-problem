package de.hagen.fernuni.logic;

import de.hagen.fernuni.model.Graph;

/**
 * Interface, welches von den Klassen heuristischer Algorithmen zur L�sung des
 * Orientierungsproblems implementiert wird.
 * 
 * @author Mahmoud Machaka
 */
public interface IHeuristicAlgorithm {

	/**
	 * F�hrt einen heuristischen Algorithmus zur L�sung des Orientierungsproblems
	 * auf einen Graphen aus.
	 * 
	 * @return Gibt f�r einen Graphen eine heuristische L�sung des Orientierungsproblems zur�ck. 
	 */
	Graph solve();
	
	/**
	 * Setzt einen Flag, der signalisiert, dass die Berechnung vorzeitig abgebrochen werden soll.
	 */
	void cancel();
}
