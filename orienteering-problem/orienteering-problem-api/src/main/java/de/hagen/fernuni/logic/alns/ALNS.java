package de.hagen.fernuni.logic.alns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import de.hagen.fernuni.logic.IHeuristicAlgorithm;
import de.hagen.fernuni.logic.LocalSearch2opt;
import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * Hauptklasse zur Anwendung des Adaptive Large Neighbourhood Search
 * Algorithmus. - "An Adaptive Large Neighbourhood Search Algorithm for the
 * Orienteering Problem", Santini 2018.
 * 
 * @author Mahmoud Machaka
 *
 */
public class ALNS implements IHeuristicAlgorithm {

	private double Tmax, alpha, h;
	private int iterations;
	private Graph g;
	private boolean cancelled;
	private long maxTime;

	/**
	 * Konstruktor: Erzeugt eine ALNS-Instanz.
	 * 
	 * @param g           Graph, für den eine Lösung gefunden werden soll
	 * @param Tmax        Kostenobergrenze
	 * @param interations Anzahl durchzuführender Iterationen
	 * @param alpha       Aggressivitätsfaktor
	 * @param h           Decay-Faktor
	 * @param maxTime     Gibt eine maximale Laufzeit in Minuten an, falls der Wert
	 *                    über 0 gewählt wird.
	 */
	public ALNS(Graph g, double Tmax, int interations, double alpha, double h, int maxTime) {
		this.Tmax = Tmax;
		this.alpha = alpha;
		this.h = h;
		this.iterations = interations;
		this.g = new Graph(g);
		this.maxTime = maxTime * 60000;
		this.cancelled = false;
	}

	/**
	 * Führt die ALNS-Berechnung durch.
	 * 
	 * @return Gibt eine mit dem ALNS-Algorithmus erzeugte Lösung des
	 *         Orientierungsproblems zurück.
	 */
	public Graph solve() {
		long timeStart = System.currentTimeMillis();
		long timeTotal = 0;

		if (Edge.getDistance(g.getV().get(0), g.getV().get(1)) > Tmax)
			return new Graph(g);

		// Erzeuge initiale Lösung
		Graph x = createInitialSolution();
		Graph x_asterisk = new Graph(x);

		// Füge zu verwendende Destroy- und Repair-Methoden hinzu
		List<IDestroyMethods> destroyMethods = new ArrayList<IDestroyMethods>();
		destroyMethods.add(new RandomRemove());
		destroyMethods.add(new RandomSequenceRemove());
		destroyMethods.add(new ClusterRemove());

		List<IRepairMethods> repairMethods = new ArrayList<IRepairMethods>();
		repairMethods.add(new GreedyRepair());
		repairMethods.add(new RandomRepair());
		repairMethods.add(new PrizeRepair());
		repairMethods.add(new ClusterRepair());

		Double[] destroyScores = new Double[destroyMethods.size()];
		Double[] repairScores = new Double[repairMethods.size()];

		// Initialisiere Scores der Destroy- und Repair-Methoden
		for (int j = 0; j < destroyScores.length; j++) {
			destroyScores[j] = 1.0;
		}

		for (int j = 0; j < repairScores.length; j++) {
			repairScores[j] = 1.0;
		}

		int i = 1;
		double T = 1.0;
		boolean isAcceptedSolution = false;

		while (i <= iterations && ((timeTotal <= maxTime) || (maxTime == 0))) {
			if (cancelled) {
				return null;
			}
			IDestroyMethods destroyMethod = chooseDestroyMethod(destroyMethods, destroyScores);
			IRepairMethods repairMethod = chooseRepairMethod(repairMethods, repairScores);

			Graph x_Dash = repairMethod.repair(destroyMethod.destroy(x, alpha), Tmax);

			// Wende Local Search ("2OptFill") an, falls eine neue globale Lösung gefunden
			// wurde
			if (x_Dash.getFitnessScore() > x_asterisk.getFitnessScore()) {
				x_Dash = LocalSearch2opt.localSearch(x_Dash);
				x_Dash = new GreedyRepair().repair(x_Dash, Tmax);
			}

			double x_FitnessScore = x.getFitnessScore();
			double x_DashFitnessScore = x_Dash.getFitnessScore();
			double x_asteriskFitnessScore = x_asterisk.getFitnessScore();

			isAcceptedSolution = false;

			double phi = 0.0;
			// Accept new solution
			if ((x_DashFitnessScore > x_FitnessScore)) {
				x = new Graph(x_Dash);
				isAcceptedSolution = true;
				phi = 5.0;
			} else if ((((x_asteriskFitnessScore - x_DashFitnessScore) / x_asteriskFitnessScore) < T)) {
				x = new Graph(x_Dash);
				isAcceptedSolution = true;
				phi = 3.0;
			}

			// Falls gefundene Lösung besser ist, als aktuell global beste Lösung
			if (x.getFitnessScore() > x_asteriskFitnessScore) {
				x_asterisk = new Graph(x);
				isAcceptedSolution = true;
				phi = 15.0;
			}

			// Update Scores
			if (isAcceptedSolution) {
				int indexOfDestroyMethod = destroyMethods.indexOf(destroyMethod);
				int indexOfRepairMethod = repairMethods.indexOf(repairMethod);

				destroyScores[indexOfDestroyMethod] = destroyScores[indexOfDestroyMethod] + h * phi;
				repairScores[indexOfRepairMethod] = repairScores[indexOfRepairMethod] + h * phi;

			}
			i++;
			T -= T / iterations;
			timeTotal = System.currentTimeMillis() - timeStart;
		}
		return x_asterisk;
	}

	/**
	 * Wählt eine Reparaturmethode mit gewichtetem Zufall aus.
	 * 
	 * @param repairMethods Liste vorhandener Reparaturmethoden
	 * @param repairScores  Bewertungen vorhandener Reparaturmethoden
	 * @return Eine Reparaturmethode
	 */
	private IRepairMethods chooseRepairMethod(List<IRepairMethods> repairMethods, Double[] repairScores) {
		ArrayList<Pair<IRepairMethods, Double>> methodWeights = new ArrayList<Pair<IRepairMethods, Double>>();

		for (int i = 0; i < repairScores.length; i++) {
			methodWeights.add(new Pair<IRepairMethods, Double>(repairMethods.get(i), repairScores[i]));
		}

		EnumeratedDistribution<IRepairMethods> enumDist = new EnumeratedDistribution<IRepairMethods>(methodWeights);

		return enumDist.sample();
	}

	/**
	 * Wählt eine Zerstörmethode mit gewichtetem Zufall aus.
	 * 
	 * @param destroyMethods Liste vorhandener Zerstörmethoden
	 * @param destroyScores  Bewertungen vorhandener Zerstörmethoden
	 * @return Eine Zerstörmethode
	 */
	private IDestroyMethods chooseDestroyMethod(List<IDestroyMethods> destroyMethods, Double[] destroyScores) {
		ArrayList<Pair<IDestroyMethods, Double>> methodWeights = new ArrayList<Pair<IDestroyMethods, Double>>();

		for (int i = 0; i < destroyScores.length; i++) {
			methodWeights.add(new Pair<IDestroyMethods, Double>(destroyMethods.get(i), destroyScores[i]));
		}

		EnumeratedDistribution<IDestroyMethods> enumDist = new EnumeratedDistribution<IDestroyMethods>(methodWeights);

		return enumDist.sample();
	}

	/**
	 * Erzeugt eine initiale Lösung.
	 * 
	 * @return Initiale Lösung als Graph
	 */
	private Graph createInitialSolution() {
		ArrayList<Edge> intialSolution = new ArrayList<Edge>();

		// Füge Kante zwischen Start- und Endknoten zur Tour hinzu
		intialSolution.add(new Edge(g.getV().get(0), g.getV().get(1)));

		// Mische die übrigen Knoten in der Knotenmenge V'
		@SuppressWarnings("unchecked")
		ArrayList<Node> vWithoutDepots = (ArrayList<Node>) g.getV().clone();
		vWithoutDepots.remove(0); // Entferne Startknoten
		vWithoutDepots.remove(0); // Entferne Endknoten
		Collections.shuffle(vWithoutDepots);

		for (Node node : vWithoutDepots) {
			intialSolution = addToBestPosition(intialSolution, node, Tmax);
		}

		return new Graph(g.getV(), intialSolution);
	}

	/**
	 * Fügt einen Knoten v and die günstigste Position ein, wenn dadurch die
	 * Kostenobergrenze nicht überschritten wird.
	 * 
	 * @param currentTour Aktuelle Tour
	 * @param v           Einzufügender Knoten
	 * @param Tmax        Kostenobergrenze
	 * @return Potentiell veränderte Tour als Kantenliste
	 */
	private ArrayList<Edge> addToBestPosition(ArrayList<Edge> currentTour, Node v, double Tmax) {
		double currentCost = Graph.getCostOfTour(currentTour);
		double tmpDistance = 0;
		double currentDistanceStartToNode = 0;
		double currentDistanceNodeToEnd = 0;
		int indexOfBestEdge = -1;
		int size = currentTour.size();

		// Falls noch kein Vergleich stattgefunden hat, berechne tmpDistance für erste
		// Kante
		if (tmpDistance == 0) {
			currentDistanceStartToNode = Edge.getDistance(currentTour.get(0).getStartNode(), v);
			currentDistanceNodeToEnd = Edge.getDistance(v, currentTour.get(0).getEndNode());
			tmpDistance = currentDistanceStartToNode + currentDistanceNodeToEnd;
			indexOfBestEdge = 0;
		}

		// Finde günstigste Kante, in welcher der Knoten eingefügt werden soll
		for (int i = 1; i < size; i++) {
			currentDistanceStartToNode = Edge.getDistance(currentTour.get(i).getStartNode(), v);
			currentDistanceNodeToEnd = Edge.getDistance(v, currentTour.get(i).getEndNode());
			if (tmpDistance > (currentDistanceStartToNode + currentDistanceNodeToEnd)) {
				tmpDistance = currentDistanceStartToNode + currentDistanceNodeToEnd;
				indexOfBestEdge = i;
			}
		}

		// Füge Knoten ein, falls Kostenobergrenze Tmax nicht überschritten wird
		// Knoten wird eingefügt indem die alte Kante durch zwei neue ersetzt wird
		if (((currentCost - currentTour.get(indexOfBestEdge).getDistance()) + tmpDistance) <= Tmax) {
			currentTour.add(indexOfBestEdge, new Edge(currentTour.get(indexOfBestEdge).getStartNode(), v));
			currentTour.add(indexOfBestEdge + 1, new Edge(v, currentTour.get(indexOfBestEdge + 1).getEndNode()));
			currentTour.remove(indexOfBestEdge + 2);
		}

		return currentTour;
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
