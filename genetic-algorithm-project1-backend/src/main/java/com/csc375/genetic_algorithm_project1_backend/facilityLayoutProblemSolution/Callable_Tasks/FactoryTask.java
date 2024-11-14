package com.csc375.genetic_algorithm_project1_backend.facilityLayoutProblemSolution.Callable_Tasks;

import com.csc375.genetic_algorithm_project1_backend.facilityLayoutProblemSolution.Factory;

import java.util.concurrent.Callable;

public class FactoryTask implements Callable<Factory>{

    private final int numOfStations;

    public FactoryTask(int numOfStations) {
        this.numOfStations = numOfStations;
    }

    @Override
    public Factory call() throws Exception {
        Factory factory = new Factory(numOfStations);
        factory.populate_factory();
        double result = factory.evaluate_affinity();
        factory.getSpots();

        return factory;
    }
}
