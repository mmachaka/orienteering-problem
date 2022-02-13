package de.hagen.fernuni.factory;

import de.hagen.fernuni.logic.IHeuristicAlgorithm;
import de.hagen.fernuni.logic.alns.ALNS;
import de.hagen.fernuni.logic.ea.EvolutionaryAlgorithm;
import de.hagen.fernuni.model.Graph;

/**
 * Factory Klasse zur zentralen Erzeugung einer Algorithmus-Instanz. Es kann nur
 * eine Algorithmus-Instanz zur Laufzeit erzeugt werden (Singleton).
 * 
 * @author Mahmoud
 *
 */
public class HeuristicAlgorithmFactory {
	private static HeuristicAlgorithmFactory instance = null;

	/**
	 * Privater Konstruktor
	 */
	private HeuristicAlgorithmFactory() {
	}

	/**
	 * Erzeugt eine Instanz der HeuristicAlgorithmFactory falls noch keine existiert
	 * und gibt diese zurück. Falls bereits eine HeuristicAlgorithmFactory-Instanz
	 * existiert, wird diese zurückgegeben.
	 * 
	 * @return HeuristicAlgorithmFactory-Instanz
	 */
	public static synchronized HeuristicAlgorithmFactory getInstance() {
		if (instance == null) {
			instance = new HeuristicAlgorithmFactory();
		}
		return instance;
	}

	/**
	 * Erzeugt eine ALNS-Instanz des IHeuristicAlgorithm-Interfaces mit den
	 * übergebenen Parametern.
	 * 
	 * @param g           Graph, für den eine Lösung gefunden werden soll
	 * @param Tmax        Kostenobergrenze
	 * @param interations Anzahl durchzuführender Iterationen
	 * @param alpha       Aggressivitätsfaktor
	 * @param h           Decay-Faktor
	 * @param maxTime     Gibt eine maximale Laufzeit in Minuten an, falls der Wert
	 *                    über 0 gewählt wird.
	 * @return
	 */
	public IHeuristicAlgorithm createALNS(Graph g, double Tmax, int interations, double alpha, double h, int maxTime) {
		return new ALNS(g, Tmax, interations, alpha, h, maxTime);
	}

	/**
	 * Erzeugt eine EA-Instanz des IHeuristicAlgorithm-Interfaces mit den
	 * übergebenen Parametern.
	 * 
	 * @param g       Graph, für den eine Lösung gefunden werden soll
	 * @param Tmax    Kostenobergrenze
	 * @param d2d     Häufigkeit der Optimierungsoperatoren (Tour Improvement, Drop
	 *                und Add-Operator). Je kleiner die Zahl, desto häufiger werden
	 *                die Optimierungsoperatoren ausgeführt.
	 * @param npop    Anzahl der Graphen in der Population
	 * @param ncand   Anzahl der Kandidaten, aus denen zwei Lösungen für den
	 *                Crossover-Operator entnommen werden.
	 * @param p       Wahrscheinlichkeit, mit welcher ein Knoten in eine initiale
	 *                Lösung aufgenommen wird.
	 * @param maxTime Gibt eine maximale Laufzeit in Minuten an, falls der Wert über
	 *                0 gewählt wird.
	 * @return
	 */
	public IHeuristicAlgorithm createEvolutionaryAlgorithm(Graph g, double Tmax, int d2d, int npop, int ncand, double p,
			int maxTime) {
		return new EvolutionaryAlgorithm(g, Tmax, d2d, npop, ncand, p, maxTime);
	}

}
