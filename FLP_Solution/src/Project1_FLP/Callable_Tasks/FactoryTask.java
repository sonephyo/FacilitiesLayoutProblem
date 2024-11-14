package Project1_FLP.Callable_Tasks;

import Project1_FLP.Factory;

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
