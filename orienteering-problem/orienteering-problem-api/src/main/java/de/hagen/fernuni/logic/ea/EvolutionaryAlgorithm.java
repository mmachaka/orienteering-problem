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
 * Hauptklasse zur Anwendung des Evolution�ren Algorithmus. - "An efficient
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
	 * F�hrt die EA-Berechnung durch.
	 * 
	 * @return Gibt eine mit dem evolution�ren Algorithmus erzeugte L�sung des
	 *         Orientierungsproblems zur�ck.
	 */
	public Graph solve() {
		long timeStart = System.currentTimeMillis();
		long timeTotal = 0;

		@SuppressWarnings("unchecked")
		ArrayList<Node> nodeList = (ArrayList<Node>) g.getV().clone();

		// Wenn Tmax zu klein gew�hlt wurde
		if (Edge.getDistance(g.getV().get(0), g.getV().get(1)) > Tmax)
			return new Graph(g);

		// Erzeuge initiale L�sungen (Population)
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

				// Ersetze schlechteste L�sung durch neue L�sung, falls diese besser ist
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

		// Gebe profitabelste Tour zur�ck
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
	 * Ersetzt die schlechteste L�sung der Population mit der aus der Mutation
	 * erzeugten L�sung, falls diese besser ist.
	 * 
	 * @param mutation   Mit dem Mutations-Operator erzeugte L�sung
	 * @param population Population bzw. Liste der L�sungen.
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
	 * Pr�ft, ob das Stopp-Kriterium erf�llt wurde.
	 * 
	 * @param population Aktuelle Population
	 * @return Gibt wahr zur�ck, wenn ein Viertel der Population einen �hnlichen
	 *         (>98,5%) Fitness-Score hat wie die beste L�sung der Population oder
	 *         wenn alle L�sungen einen Profit von 0 haben.
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
	 * Erzeugt die initiale Population, indem mehrere L�sungen zuf�llig erzeugt
	 * werden, welche die Kostenobergrenze �berschreiten d�rfen.
	 * 
	 * @return Initiale Population als Liste von Graphen
	 */
	private ArrayList<Graph> buildInitialPopulation() {
		ArrayList<Graph> population = new ArrayList<Graph>(npop);
		@SuppressWarnings("unchecked")
		ArrayList<Node> nodeList = (ArrayList<Node>) g.getV().clone();

		for (int i = 0; i < npop; i++) {
			ArrayList<Edge> intialSolution = new ArrayList<Edge>();
			// F�ge Kante zwischen Start- und Endknoten zur Tour hinzu
			intialSolution.add(new Edge(nodeList.get(0), nodeList.get(1)));

			// Mische die �brigen Knoten in der Knotenmenge V'
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
	 * F�gt iterativ Knoten mit einer Wahrscheinlichkeit von p an eine zuf�llige
	 * Stelle in der Tour ein.
	 * 
	 * @param intialSolution        Tour einer initialen L�sung
	 * @param nodeListWithoutDepots Liste aller Knoten des Graphen ohne Start- und
	 *                              Endknoten
	 * @param p                     Wahrscheinlichkeit, mit welcher ein Knoten in
	 *                              eine initiale L�sung aufgenommen wird.
	 * @return Zuf�llig erzeugte Tour einer initialen L�sung
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
