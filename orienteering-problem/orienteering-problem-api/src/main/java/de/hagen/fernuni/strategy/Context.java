package de.hagen.fernuni.strategy;

import de.hagen.fernuni.logic.IHeuristicAlgorithm;
import de.hagen.fernuni.model.Graph;

public class Context {
	private IHeuristicAlgorithm algorithm;

	public Context(IHeuristicAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
	
	public Graph execute() {
		return algorithm.solve();
	}
}
