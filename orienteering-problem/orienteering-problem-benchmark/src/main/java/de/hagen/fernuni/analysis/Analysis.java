package de.hagen.fernuni.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.LongStream;

import javax.swing.SwingWorker;

import de.hagen.fernuni.factory.HeuristicAlgorithmFactory;
import de.hagen.fernuni.gui.BenchmarkMainGUI;
import de.hagen.fernuni.gui.FileIO;
import de.hagen.fernuni.logic.IHeuristicAlgorithm;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.strategy.Context;

public class Analysis extends SwingWorker<Void, String> {
	private boolean alnsIsSelected, eaIsSelected;
	private int repetitions;
	private int maxTime;
	private ArrayList<String> benchmarkSets;
	private BenchmarkMainGUI benchmarkMainGUI;
	private int publishPosition = 0;
	private String benchmarkSetName;
	private int listSize;
	private IHeuristicAlgorithm alns;
	private IHeuristicAlgorithm ea;

	@SuppressWarnings("unchecked")
	public Analysis(HashMap<String, Object> params, BenchmarkMainGUI benchmarkMainGUI) {
		ArrayList<String> algorithms = (ArrayList<String>) params.get("algorithms");
		if (algorithms.contains("ALNS"))
			this.alnsIsSelected = true;
		if (algorithms.contains("EA"))
			this.eaIsSelected = true;
		this.repetitions = (int) params.get("repetitions");
		this.maxTime = (int) params.get("maxTime");
		this.benchmarkSets = (ArrayList<String>) params.get("benchmarkSets");
		this.benchmarkMainGUI = benchmarkMainGUI;
		this.listSize = algorithms.size() * benchmarkSets.size();
	}

	@Override
	protected Void doInBackground() throws Exception {

		if (repetitions == 0)
			return null;

		// ALNS Parameter
		Integer[] iterations = { 1000, 2000, 3000, 4000, 5000 };
		Double[] a = { 0.1, 0.25, 0.5, 0.75, 0.9 };
		Double[] h = { 0.2, 0.4, 0.6, 0.8 };
		int iterationsSantini = 5000;
		double aSantini = 0.2062;
		double hSantini = 0.4314;
		int alnsCombinations = 101;

		// EA Parameter
		Integer[] d2d = { 5, 10, 20, 50 };
		Integer[] npop = { 10, 20, 50, 100 };
		Integer[] ncand = { 5, 7, 10, 20 };
		Double[] p = { 0.01, 0.05, 0.1, 0.25, 0.5, 0.75 };
		int d2dKobega = 50;
		int npopKobega = 100;
		int ncandKobega = 10;
		double pKobega = 0.01;
		int eaCombinations = 217;

		// Progressbar
		int progress = 1;
		int totalProgress = 0;
		if (alnsIsSelected)
			totalProgress += alnsCombinations * benchmarkSets.size();
		if (eaIsSelected)
			totalProgress += eaCombinations * benchmarkSets.size();

		// Benchmark-Results
		String pathBenchmarkFolder = FileIO.generateFolderName(
				benchmarkMainGUI.getOutputPath() + File.separator + "BenchmarkResults" + File.separator, "Benchmark");

		String benchmarkFolderName = "..." + File.separator + "BenchmarkResults"
				+ pathBenchmarkFolder.split("BenchmarkResults")[1];

		File folder = new File(pathBenchmarkFolder);
		folder.mkdirs();

		String results;
		double profitAvg, costAvg;
		long timeAvg;
		ArrayList<Double> profitList = new ArrayList<Double>();
		ArrayList<Double> costList = new ArrayList<Double>();
		ArrayList<Long> timeList = new ArrayList<Long>();

		while (!isCancelled()) {
			Graph graph, solution;
			long start, end, total;
			List<String> chunks = new ArrayList<String>();
			for (String benchmarkSet : benchmarkSets) {
				benchmarkSetName = benchmarkSet.substring(5, benchmarkSet.length() - 4);

				if (alnsIsSelected) {
					chunks.clear();
					chunks.add("<html><span style=\"color: #ff6600;\">Benchmark Graph " + benchmarkSetName
							+ " (ALNS): In Bearbeitung...</span></html>");
					process(chunks);

					for (int iVal : iterations) {
						for (double aVal : a) {
							for (double hVal : h) {
								results = "Iteration\ta\th\tmaxTime=" + maxTime + "\n";
								results += iVal + "\t" + aVal + "\t" + hVal + "\n";

								// "Problelauf", um Auﬂreiﬂer zu vermeiden
								try {
									Context context = new Context(HeuristicAlgorithmFactory.getInstance().createALNS(
											FileIO.parseAndLoadGraph("/set_100.txt"), getTmax(benchmarkSet), 200, 0.2,
											0.4, 1));
									context.execute();
								} catch (Exception e) {
									// Probelauf fehlgeschlagen: Keine Aktion notwendig.
								}
								results += "Profit\tCost\tTime\n";

								profitList.clear();
								costList.clear();
								timeList.clear();

								for (int j = 0; j < repetitions; j++) {
									if (isCancelled()) {
										abortAlgorithms();
										return null;
									}

									try {
										graph = FileIO.parseAndLoadGraph(benchmarkSet);
										double tmax = getTmax(benchmarkSet);
										alns = HeuristicAlgorithmFactory.getInstance().createALNS(graph, tmax, iVal,
												aVal, hVal, maxTime);
										Context context = new Context(alns);
										start = System.currentTimeMillis();
										solution = context.execute();
										end = System.currentTimeMillis();
										total = end - start;

										benchmarkMainGUI.setProgressbar(
												progress + "/" + (totalProgress * repetitions) + " ("
														+ (int) (100 * progress / (totalProgress * repetitions)) + "%)",
												(int) (100 * progress / (totalProgress * repetitions)));
										progress++;

										profitList.add(solution.getProfitOfTour());
										costList.add(solution.getCostOfTour());
										timeList.add(total);
									} catch (Exception e) {
										// Berechnung fehlgeschlagen
										results += "ERROR: " + e.getClass() + "\n";
										break;
									}
								}

								profitAvg = profitList.stream().flatMapToDouble(num -> DoubleStream.of(num)).average()
										.orElse(0.0);
								costAvg = costList.stream().flatMapToDouble(num -> DoubleStream.of(num)).average()
										.orElse(0.0);
								timeAvg = (long) timeList.stream().flatMapToLong(num -> LongStream.of(num)).average()
										.orElse(0.0);

								results += profitAvg + "\t" + costAvg + "\t" + timeAvg + "\n\n";
								FileIO.saveDataToFile(pathBenchmarkFolder, benchmarkSetName + "_ALNS_" + iVal + ".txt",
										results);
							}
						}
					}

					// Benchmark mit Santinis Parametern
					results = "Iteration\ta\th\tmaxTime=" + maxTime + "\n";
					results += iterationsSantini + "\t" + aSantini + "\t" + hSantini + "\n";
					// "Problelauf", um Auﬂreiﬂer zu vermeiden
					try {
						Context context = new Context(HeuristicAlgorithmFactory.getInstance().createALNS(
								FileIO.parseAndLoadGraph("/set_100.txt"), getTmax(benchmarkSet), 200, 0.2, 0.4, 1));
						context.execute();
					} catch (Exception e) {
						// Probelauf fehlgeschlagen: Keine Aktion notwendig.
					}
					results += "Profit\tCost\tTime\n";

					profitList.clear();
					costList.clear();
					timeList.clear();

					for (int j = 0; j < repetitions; j++) {
						if (isCancelled()) {
							abortAlgorithms();
							return null;
						}

						try {
							graph = FileIO.parseAndLoadGraph(benchmarkSet);
							double tmax = getTmax(benchmarkSet);
							alns = HeuristicAlgorithmFactory.getInstance().createALNS(graph, tmax, iterationsSantini,
									aSantini, hSantini, maxTime);
							Context context = new Context(alns);
							start = System.currentTimeMillis();
							solution = context.execute();
							end = System.currentTimeMillis();
							total = end - start;

							benchmarkMainGUI.setProgressbar(
									progress + "/" + (totalProgress * repetitions) + " ("
											+ (int) (100 * progress / (totalProgress * repetitions)) + "%)",
									(int) (100 * progress / (totalProgress * repetitions)));
							progress++;

							profitList.add(solution.getProfitOfTour());
							costList.add(solution.getCostOfTour());
							timeList.add(total);
						} catch (Exception e) {
							// Berechnung fehlgeschlagen
							results += "ERROR: " + e.getClass() + "\n";
							break;
						}
					}

					profitAvg = profitList.stream().flatMapToDouble(num -> DoubleStream.of(num)).average().orElse(0.0);
					costAvg = costList.stream().flatMapToDouble(num -> DoubleStream.of(num)).average().orElse(0.0);
					timeAvg = (long) timeList.stream().flatMapToLong(num -> LongStream.of(num)).average().orElse(0.0);

					results += profitAvg + "\t" + costAvg + "\t" + timeAvg + "\n\n";
					FileIO.saveDataToFile(pathBenchmarkFolder, benchmarkSetName + "_ALNS_Santini.txt", results);

					// Abschluss des ALNS-Benchmarks
					chunks.clear();
					chunks.add("<html><span style=\"color: #008000;\">Benchmark Graph " + benchmarkSetName
							+ " (ALNS): Abgeschlossen! Ergebnisse gespeichert unter: " + benchmarkFolderName
							+ "</span></html>");
					process(chunks);
					publishPosition++;
				}
				if (eaIsSelected) {
					chunks.clear();
					chunks.add("<html><span style=\"color: #ff6600;\">Benchmark Graph " + benchmarkSetName
							+ " (EA): In Bearbeitung...</span></html>");
					process(chunks);
					for (int npopVal : npop) {
						for (double pVal : p) {
							for (int d2dVal : d2d) {
								for (int ncandVal : ncand) {
									if (d2dVal < npopVal && ncandVal < npopVal) {
										results = "d2d\tnpop\tncand\tp\tmaxTime=" + maxTime + "\n";
										results += d2dVal + "\t" + npopVal + "\t" + ncandVal + "\t" + pVal + "\n";

										// "Problelauf", um Auﬂreiﬂer zu vermeiden
										try {
											Context context = new Context(
													HeuristicAlgorithmFactory.getInstance().createEvolutionaryAlgorithm(
															FileIO.parseAndLoadGraph("/set_100.txt"),
															getTmax(benchmarkSet), 10, 20, 7, 0.01, 1));
											context.execute();
										} catch (Exception e) {
											// Probelauf fehlgeschlagen: Keine Aktion notwendig.
										}

										results += "Profit\tCost\tTime\n";

										profitList.clear();
										costList.clear();
										timeList.clear();

										for (int j = 0; j < repetitions; j++) {
											if (isCancelled()) {
												abortAlgorithms();
												return null;
											}

											try {
												graph = FileIO.parseAndLoadGraph(benchmarkSet);
												double tmax = getTmax(benchmarkSet);
												ea = HeuristicAlgorithmFactory.getInstance()
														.createEvolutionaryAlgorithm(graph, tmax, d2dVal, npopVal,
																ncandVal, pVal, maxTime);
												Context context = new Context(ea);
												start = System.currentTimeMillis();
												solution = context.execute();
												end = System.currentTimeMillis();
												total = end - start;

												benchmarkMainGUI
														.setProgressbar(
																progress + "/" + (totalProgress * repetitions) + " ("
																		+ (int) (100 * progress
																				/ (totalProgress * repetitions))
																		+ "%)",
																(int) (100 * progress / (totalProgress * repetitions)));

												progress++;

												profitList.add(solution.getProfitOfTour());
												costList.add(solution.getCostOfTour());
												timeList.add(total);
											} catch (Exception e) {
												// Berechnung fehlgeschlagen
												results += "ERROR: " + e.getClass() + "\n";
												break;
											}
										}
										profitAvg = profitList.stream().flatMapToDouble(num -> DoubleStream.of(num))
												.average().orElse(0.0);
										costAvg = costList.stream().flatMapToDouble(num -> DoubleStream.of(num))
												.average().orElse(0.0);
										timeAvg = (long) timeList.stream().flatMapToLong(num -> LongStream.of(num))
												.average().orElse(0.0);

										results += profitAvg + "\t" + costAvg + "\t" + timeAvg + "\n\n";
										FileIO.saveDataToFile(pathBenchmarkFolder,
												benchmarkSetName + "_EA_" + npopVal + ".txt", results);
									}
								}
							}
						}
					}

					// Benchmark mit Kobegas Parametern
					results = "d2d\tnpop\tncand\tp\tmaxTime=" + maxTime + "\n";
					results += d2dKobega + "\t" + npopKobega + "\t" + ncandKobega + "\t" + pKobega + "\n";

					// "Problelauf", um Auﬂreiﬂer zu vermeiden
					try {
						Context context = new Context(HeuristicAlgorithmFactory.getInstance()
								.createEvolutionaryAlgorithm(FileIO.parseAndLoadGraph("/set_100.txt"),
										getTmax(benchmarkSet), 10, 20, 7, 0.01, 1));
						context.execute();
					} catch (Exception e) {
						// Probelauf fehlgeschlagen: Keine Aktion notwendig.
					}

					results += "Profit\tCost\tTime\n";

					profitList.clear();
					costList.clear();
					timeList.clear();

					for (int j = 0; j < repetitions; j++) {
						if (isCancelled()) {
							abortAlgorithms();
							return null;
						}

						try {
							graph = FileIO.parseAndLoadGraph(benchmarkSet);
							double tmax = getTmax(benchmarkSet);
							ea = HeuristicAlgorithmFactory.getInstance().createEvolutionaryAlgorithm(graph, tmax,
									d2dKobega, npopKobega, ncandKobega, pKobega, maxTime);
							Context context = new Context(ea);
							start = System.currentTimeMillis();
							solution = context.execute();
							end = System.currentTimeMillis();
							total = end - start;

							benchmarkMainGUI.setProgressbar(
									progress + "/" + (totalProgress * repetitions) + " ("
											+ (int) (100 * progress / (totalProgress * repetitions)) + "%)",
									(int) (100 * progress / (totalProgress * repetitions)));

							progress++;

							profitList.add(solution.getProfitOfTour());
							costList.add(solution.getCostOfTour());
							timeList.add(total);
						} catch (Exception e) {
							// Berechnung fehlgeschlagen
							results += "ERROR: " + e.getClass() + "\n";
							break;
						}
					}
					profitAvg = profitList.stream().flatMapToDouble(num -> DoubleStream.of(num)).average().orElse(0.0);
					costAvg = costList.stream().flatMapToDouble(num -> DoubleStream.of(num)).average().orElse(0.0);
					timeAvg = (long) timeList.stream().flatMapToLong(num -> LongStream.of(num)).average().orElse(0.0);

					results += profitAvg + "\t" + costAvg + "\t" + timeAvg + "\n\n";
					FileIO.saveDataToFile(pathBenchmarkFolder, benchmarkSetName + "_EA_Kobega.txt", results);

					// Abschluss des EA-Benchmarks
					chunks.clear();
					chunks.add("<html><span style=\"color: #008000;\">Benchmark Graph " + benchmarkSetName
							+ " (EA): Abgeschlossen! Ergebnisse gespeichert unter: " + benchmarkFolderName
							+ "</span></html>");
					process(chunks);
					publishPosition++;
				}
			}
			break;
		}
		return null;
	}

	private double getTmax(String benchmarkSet) {
		double tmax = 0;
		switch (benchmarkSet) {
		case "/set_100.txt":
			tmax = 400.0;
			break;
		case "/set_200.txt":
			tmax = 900;
			break;
		case "/set_300.txt":
			tmax = 1400;
			break;
		case "/set_400.txt":
			tmax = 1900;
			break;
		case "/set_500.txt":
			tmax = 2200;
			break;
		case "/set_600.txt":
			tmax = 2800;
			break;
		case "/set_700.txt":
			tmax = 3600;
			break;
		case "/set_800.txt":
			tmax = 4400;
			break;
		case "/set_900.txt":
			tmax = 5000;
			break;
		case "/set_1000.txt":
			tmax = 5400;
			break;
		}
		return tmax;
	}

	@Override
	protected void process(List<String> chunks) {
		benchmarkMainGUI.setListProgess(publishPosition, chunks);
	}

	@Override
	protected void done() {
		benchmarkMainGUI.lockGUIForCalculation(false);

		if (isCancelled()) {
			List<String> chunks = new ArrayList<String>();
			chunks.add(
					"<html><span style=\"color: #800000;\">Ausstehende Berechnungen wurden abgebrochen.</span></html>");
			process(chunks);
			publishPosition++;
			while (publishPosition < listSize) {
				chunks.clear();
				chunks.add("");
				process(chunks);
				publishPosition++;
			}
		} else {
			List<String> chunks = new ArrayList<String>();
			chunks.clear();
			chunks.add(
					"<html><strong><span style=\"color: #008000;\">*** Benchmarking abgeschlossen ***</span></strong></html>");
			publishPosition++;
			process(chunks);
		}
	}

	private void abortAlgorithms() {
		if (alns != null) {
			alns.cancel();
		}
		if (ea != null) {
			ea.cancel();
		}
	}

}
