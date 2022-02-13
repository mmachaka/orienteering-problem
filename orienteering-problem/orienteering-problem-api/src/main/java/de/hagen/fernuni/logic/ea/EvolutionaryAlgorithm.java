package de.hagen.fernuni.logic.ea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import de.hagen.fernuni.logic.IHeuristicAlgorithm;
import de.hagen.fernuni.logic.LocalSearch2opt;
import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * Hauptklasse zur Anwendung des Evolutionären Algorithmus. - "An efficient
 * evolutionary algorithm for the orienteering problem", Kobeaga et al. 2017.
 * 
 * @author Mahmoud Machaka
 *
 */
public class EvolutionaryAlgorithm implements IHeuristicAlgorithm {
	private double Tmax, p;
	private int d2d, npop, ncand;
	private Graph g;
	private boolean cancelled;
	private long maxTime;

	/**
	 * Konstruktor: Erzeugt eine EvolutionaryAlgorithm-Instanz
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
	 */
	public EvolutionaryAlgorithm(Graph g, double Tmax, int d2d, int npop, int ncand, double p, int maxTime) {
		this.g = new Graph(g);
		this.Tmax = Tmax;
		this.p = p;
		this.d2d = d2d;
		this.npop = npop;
		this.ncand = ncand;
		this.maxTime = maxTime * 60000;
		this.cancelled = false;
	}

	/**
	 * Führt die EA-Berechnung durch.
	 * 
	 * @return Gibt eine mit dem evolutionären Algorithmus erzeugte Lösung des
	 *         Orientierungsproblems zurück.
	 */
	public Graph solve() {
		long timeStart = System.currentTimeMillis();
		long timeTotal = 0;

		@SuppressWarnings("unchecked")
		ArrayList<Node> nodeList = (ArrayList<Node>) g.getV().clone();

		// Wenn Tmax zu klein gewählt wurde
		if (Edge.getDistance(g.getV().get(0), g.getV().get(1)) > Tmax)
			return new Graph(g);

		// Erzeuge initiale Lösungen (Population)
		ArrayList<Graph> population = buildInitialPopulation();

		// Tour Improvement: Local Search (2-Opt)
		for (int c = 0; c < npop; c++) {
			population.set(c, LocalSearch2opt.localSearch(population.get(c)));
		}

		// Drop Operator
		for (int c = 0; c < npop; c++) {
			population.set(c, new Graph(nodeList, Graph.restorefeasibility(population.get(c).getE(), Tmax)));
		}

		// Add Operator
		for (int c = 0; c < npop; c++) {
			population.set(c, ServiceMethods.addOperator(population.get(c), Tmax));
			if (cancelled) {
				return null;
			}
		}

		boolean stopCriteria = false;
		int i = 0;

		while (!(stopCriteria && (i % d2d == 0))) {
			if (cancelled) {
				return null;
			}
			i++;

			// Abschnitt mit genetischen Methoden
			if ((i % d2d != 0) && ((timeTotal <= maxTime) || (maxTime == 0))) {
				// Select two parents
				Graph[] parents = ServiceMethods.selectParents(population, ncand);

				// Crossover
				Graph child = ServiceMethods.crossover(parents);

				// Mutation
				Graph mutation = ServiceMethods.mutate(child);

				// Ersetze schlechteste Lösung durch neue Lösung, falls diese besser ist
				population = replaceWorstIndividual(mutation, population);
				timeTotal = System.currentTimeMillis() - timeStart;
			}
			// Abschnitt mit optimizierenden Methoden
			else {
				// Tour Improvement: Local Search (2-Opt)
				for (int c = 0; c < npop; c++) {
					population.set(c, LocalSearch2opt.localSearch(population.get(c)));
				}

				// Drop Operator
				for (int c = 0; c < npop; c++) {
					population.set(c, new Graph(nodeList, Graph.restorefeasibility(population.get(c).getE(), Tmax)));
				}

				// Add Operator
				for (int c = 0; c < npop; c++) {
					// population.set(c, new GreedyRepair().repair(population.get(c), Tmax));
					population.set(c, ServiceMethods.addOperator(population.get(c), Tmax));
					if (cancelled) {
						return null;
					}
				}

				stopCriteria = checkStopCriteria(population);
				timeTotal = System.currentTimeMillis() - timeStart;
				if ((timeTotal > maxTime) && (maxTime != 0)) {
					break;
				}
			}
		}

		// Gebe profitabelste Tour zurück
		double bestProfit = 0.0;
		double profitTmp;
		Graph bestGraph = g;
		for (int j = 0; j < population.size(); j++) {
			profitTmp = population.get(j).getProfitOfTour();
			if (profitTmp > bestProfit) {
				bestGraph = population.get(j);
				bestProfit = profitTmp;
			}
		}
		return bestGraph;
	}

	/**
	 * Ersetzt die schlechteste Lösung der Population mit der aus der Mutation
	 * erzeugten Lösung, falls diese besser ist.
	 * 
	 * @param mutation   Mit dem Mutations-Operator erzeugte Lösung
	 * @param population Population bzw. Liste der Lösungen.
	 * @return Neue Population
	 */
	private ArrayList<Graph> replaceWorstIndividual(Graph mutation, ArrayList<Graph> population) {
		@SuppressWarnings("unchecked")
		ArrayList<Graph> newPopulation = (ArrayList<Graph>) population.clone();
		double worstFitnessScore = Double.POSITIVE_INFINITY;
		int popSize = newPopulation.size();
		int indexWorstIndividual = -1;
		double fitnessScoreTmp;
		for (int i = 0; i < popSize; i++) {
			fitnessScoreTmp = newPopulation.get(i).getFitnessScore();
			if (fitnessScoreTmp < worstFitnessScore) {
				indexWorstIndividual = i;
				worstFitnessScore = fitnessScoreTmp;
			}
		}
		if (mutation.getFitnessScore() > worstFitnessScore) {
			newPopulation.set(indexWorstIndividual, mutation);
		}

		return newPopulation;
	}

	/**
	 * Prüft, ob das Stopp-Kriterium erfüllt wurde.
	 * 
	 * @param population Aktuelle Population
	 * @return Gibt wahr zurück, wenn ein Viertel der Population einen ähnlichen
	 *         (>98,5%) Fitness-Score hat wie die beste Lösung der Population oder
	 *         wenn alle Lösungen einen Profit von 0 haben.
	 */
	private boolean checkStopCriteria(ArrayList<Graph> population) {
		ArrayList<Double> fitnessScores = new ArrayList<Double>();

		for (Graph g : population) {
			fitnessScores.add(g.getFitnessScore());
		}

		double maxFitness = Collections.max(fitnessScores);

		if (maxFitness == 0.0) {
			return true;
		}

		int counter = 0;
		for (Double score : fitnessScores) {
			if (score / maxFitness >= 0.985)
				counter++;
		}

		return (counter >= (int) (population.size() / 4));
	}

	/**
	 * Erzeugt die initiale Population, indem mehrere Lösungen zufällig erzeugt
	 * werden, welche die Kostenobergrenze überschreiten dürfen.
	 * 
	 * @return Initiale Population als Liste von Graphen
	 */
	private ArrayList<Graph> buildInitialPopulation() {
		ArrayList<Graph> population = new ArrayList<Graph>(npop);
		@SuppressWarnings("unchecked")
		ArrayList<Node> nodeList = (ArrayList<Node>) g.getV().clone();

		for (int i = 0; i < npop; i++) {
			ArrayList<Edge> intialSolution = new ArrayList<Edge>();
			// Füge Kante zwischen Start- und Endknoten zur Tour hinzu
			intialSolution.add(new Edge(nodeList.get(0), nodeList.get(1)));

			// Mische die übrigen Knoten in der Knotenmenge V'
			@SuppressWarnings("unchecked")
			ArrayList<Node> nodeListWithoutDepots = (ArrayList<Node>) g.getV().clone();
			nodeListWithoutDepots.remove(0); // Entferne Startknoten
			nodeListWithoutDepots.remove(0); // Entferne Endknoten
			Collections.shuffle(nodeListWithoutDepots);

			intialSolution = addNodesRandomly(intialSolution, nodeListWithoutDepots, p);

			population.add(new Graph(nodeList, intialSolution));
		}
		return population;
	}

	/**
	 * Fügt iterativ Knoten mit einer Wahrscheinlichkeit von p an eine zufällige
	 * Stelle in der Tour ein.
	 * 
	 * @param intialSolution        Tour einer initialen Lösung
	 * @param nodeListWithoutDepots Liste aller Knoten des Graphen ohne Start- und
	 *                              Endknoten
	 * @param p                     Wahrscheinlichkeit, mit welcher ein Knoten in
	 *                              eine initiale Lösung aufgenommen wird.
	 * @return Zufällig erzeugte Tour einer initialen Lösung
	 */
	private ArrayList<Edge> addNodesRandomly(ArrayList<Edge> intialSolution, ArrayList<Node> nodeListWithoutDepots,
			double p) {
		Random rng = new Random();
		Node lastEdgeStart, lastEdgeEnd;
		int intialSolutionSize;
		for (Node v : nodeListWithoutDepots) {
			intialSolutionSize = intialSolution.size();
			lastEdgeStart = intialSolution.get(intialSolutionSize - 1).getStartNode();
			lastEdgeEnd = intialSolution.get(intialSolutionSize - 1).getEndNode();
			if (rng.nextDouble() <= p) {
				intialSolution.set(intialSolutionSize - 1, new Edge(lastEdgeStart, v));
				intialSolution.add(new Edge(v, lastEdgeEnd));
			}
		}
		return intialSolution;
	}

	/**
	 * Setzt einen Flag, der signalisiert, dass die Berechnung vorzeitig abgebrochen
	 * werden soll.
	 */
	@Override
	public void cancel() {
		cancelled = true;
	}

}
