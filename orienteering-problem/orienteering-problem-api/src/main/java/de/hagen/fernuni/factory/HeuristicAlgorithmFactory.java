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
	 * und gibt diese zur�ck. Falls bereits eine HeuristicAlgorithmFactory-Instanz
	 * existiert, wird diese zur�ckgegeben.
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
	 * �bergebenen Parametern.
	 * 
	 * @param g           Graph, f�r den eine L�sung gefunden werden soll
	 * @param Tmax        Kostenobergrenze
	 * @param interations Anzahl durchzuf�hrender Iterationen
	 * @param alpha       Aggressivit�tsfaktor
	 * @param h           Decay-Faktor
	 * @param maxTime     Gibt eine maximale Laufzeit in Minuten an, falls der Wert
	 *                    �ber 0 gew�hlt wird.
	 * @return
	 */
	public IHeuristicAlgorithm createALNS(Graph g, double Tmax, int interations, double alpha, double h, int maxTime) {
		return new ALNS(g, Tmax, interations, alpha, h, maxTime);
	}

	/**
	 * Erzeugt eine EA-Instanz des IHeuristicAlgorithm-Interfaces mit den
	 * �bergebenen Parametern.
	 * 
	 * @param g       Graph, f�r den eine L�sung gefunden werden soll
	 * @param Tmax    Kostenobergrenze
	 * @param d2d     H�ufigkeit der Optimierungsoperatoren (Tour Improvement, Drop
	 *                und Add-Operator). Je kleiner die Zahl, desto h�ufiger werden
	 *                die Optimierungsoperatoren ausgef�hrt.
	 * @param npop    Anzahl der Graphen in der Population
	 * @param ncand   Anzahl der Kandidaten, aus denen zwei L�sungen f�r den
	 *                Crossover-Operator entnommen werden.
	 * @param p       Wahrscheinlichkeit, mit welcher ein Knoten in eine initiale
	 *                L�sung aufgenommen wird.
	 * @param maxTime Gibt eine maximale Laufzeit in Minuten an, falls der Wert �ber
	 *                0 gew�hlt wird.
	 * @return
	 */
	public IHeuristicAlgorithm createEvolutionaryAlgorithm(Graph g, double Tmax, int d2d, int npop, int ncand, double p,
			int maxTime) {
		return new EvolutionaryAlgorithm(g, Tmax, d2d, npop, ncand, p, maxTime);
	}

}
