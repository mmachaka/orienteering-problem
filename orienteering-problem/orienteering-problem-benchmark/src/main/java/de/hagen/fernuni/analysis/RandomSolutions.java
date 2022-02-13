package de.hagen.fernuni.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import de.hagen.fernuni.gui.FileIO;
import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

public class RandomSolutions {

	public static void main(String[] args) {
		try {
			Graph g1 = FileIO.parseAndLoadGraph("/tsiligirides_problem_1.txt");
			double[] g1_tmax = { 5, 10, 15, 20, 25, 30, 35, 40, 46, 50, 55, 60, 65, 70, 73, 75, 80, 85 };

			Graph g2 = FileIO.parseAndLoadGraph("/tsiligirides_problem_2.txt");
			double[] g2_tmax = { 15, 20, 23, 25, 27, 30, 32, 35, 38, 40, 45 };

			Graph g3 = FileIO.parseAndLoadGraph("/tsiligirides_problem_3.txt");

			Graph g4 = FileIO.parseAndLoadGraph("/tsiligirides_problem_4.txt");
			double[] g4_tmax = { 5, 10, 15, 20, 25, 30, 35, 40, 46, 50, 55, 60, 65, 70, 73, 75, 80, 85 };

			Graph g5 = FileIO.parseAndLoadGraph("/set_66.txt");

			Graph g6 = FileIO.parseAndLoadGraph("/set_64.txt");

			// Speicherort
			String savePath = System.getProperty("user.home");

			for (double tmax : g1_tmax) {
				getSolution(g1, tmax, 1, savePath, "g1");
			}

			for (double tmax : g2_tmax) {
				getSolution(g2, tmax, 1, savePath, "g2");
			}

			for (int tmax = 15; tmax <= 110; tmax += 5) {
				getSolution(g3, tmax, 1, savePath, "g3");
			}

			for (double tmax : g4_tmax) {
				getSolution(g4, tmax, 1, savePath, "g4");
			}

			for (int tmax = 5; tmax <= 130; tmax += 5) {
				getSolution(g5, tmax, 1, savePath, "g5");
			}

			for (int tmax = 15; tmax <= 80; tmax += 5) {
				getSolution(g6, tmax, 1, savePath, "g6");
			}

		} catch (Exception e) {
			System.out.println("Graph konnte nicht geladen werden.");
			e.printStackTrace();
		}
	}

	private static Graph getSolution(Graph g, double Tmax, int timeLimitMinutes, String path, String instanceName) {
		long timeStart = System.currentTimeMillis();
		Graph gClone = new Graph(g);
		Graph bestSolution = new Graph(g);

		int counter = 0;
		while ((System.currentTimeMillis() - timeStart) < timeLimitMinutes * 60000) {
			Graph gTmp = createRandomSolution(gClone, Tmax);
			counter++;
			if (gTmp.getProfitOfTour() > bestSolution.getProfitOfTour()) {
				if (gTmp.tourIsUninterrupted() && gTmp.tourHasNoDuplicates() && gTmp.tourHasValidDepots())
					bestSolution = gTmp;
				else {
					System.out.println("Graph ist ungültig!");
				}
			}
		}
		String results = g.getSize() + "\t" + Tmax + "\t" + bestSolution.getProfitOfTour() + "\t" + counter + "\n";
		FileIO.saveDataToFile(path, instanceName + ".txt", results);

		String graphResults = "Instanz: " + instanceName + "\nTmax: " + Tmax + "\nProfit der Tour: "
				+ bestSolution.getProfitOfTour() + "\nKosten der Tour: " + bestSolution.getCostOfTour()
				+ "\nAnzahl erzeugter Lösungen: " + counter + "\n" + bestSolution;
		FileIO.saveDataToFile(path, instanceName + "_" + Tmax + ".txt", graphResults);

		return g;
	}

	private static Graph createRandomSolution(Graph g, double Tmax) {
		ArrayList<Edge> randomSolution = new ArrayList<Edge>();

		// Füge Kante zwischen Start- und Endknoten zur Tour hinzu
		randomSolution.add(new Edge(g.getV().get(0), g.getV().get(1)));

		// Mische die übrigen Knoten in der Knotenmenge V'
		@SuppressWarnings("unchecked")
		ArrayList<Node> vWithoutDepots = (ArrayList<Node>) g.getV().clone();
		vWithoutDepots.remove(0); // Entferne Startknoten
		vWithoutDepots.remove(0); // Entferne Endknoten
		Collections.shuffle(vWithoutDepots);

		for (Node node : vWithoutDepots) {
			randomSolution = addToRandomPosition(randomSolution, node, Tmax);
		}

		return new Graph(g.getV(), randomSolution);
	}

	private static ArrayList<Edge> addToRandomPosition(ArrayList<Edge> currentTour, Node v, double Tmax) {
		double currentCost = Graph.getCostOfTour(currentTour);
		double tmpDistance = 0;

		int size = currentTour.size();
		Random rng = new Random();
		int indexOfRandomEdge = rng.nextInt(size);
		Edge randomEdge = currentTour.get(indexOfRandomEdge);
		tmpDistance = Edge.getDistance(randomEdge.getStartNode(), v) + Edge.getDistance(v, randomEdge.getEndNode());

		// Füge Knoten ein, falls Kostenobergrenze Tmax nicht überschritten wird
		// Knoten wird eingefügt indem die alte Kante durch zwei neue ersetzt wird
		if (((currentCost - currentTour.get(indexOfRandomEdge).getDistance()) + tmpDistance) <= Tmax) {
			currentTour.add(indexOfRandomEdge, new Edge(currentTour.get(indexOfRandomEdge).getStartNode(), v));
			currentTour.add(indexOfRandomEdge + 1, new Edge(v, currentTour.get(indexOfRandomEdge + 1).getEndNode()));
			currentTour.remove(indexOfRandomEdge + 2);
		}

		return currentTour;
	}

}
