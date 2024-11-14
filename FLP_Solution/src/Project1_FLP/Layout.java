// Author - Phone Pyae Sone Phyo

package Project1_FLP;

import Project1_FLP.Callable_Tasks.FactoryTask;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Layout{

    private Factory maxValueFactory = null;
    private final int num_of_threads;

    private int count_cFactories;
    private List<Future<Factory>> future_Factories = new ArrayList<>();
    private List<Factory> current_Factories = new ArrayList<>();
    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    public Layout(int num_of_threads) {
        this.num_of_threads = num_of_threads;
    }

    /**
     * Generate factories based on the num_of_threads the class is assigned
     * The maximum affinity will be assigned, to be used in the future.
     * Note: The factories are created in parallel, and need to be put in Future class first
     * @param num_of_stations - station that all factories should have. Number of station influences the factory size.
     */
    public void evaluate(int num_of_stations, int count_of_GAOperations) {

        ExecutorService executorService = Executors.newFixedThreadPool(num_of_threads);

        try {
            List<Callable<Factory>> factoryTasks = new ArrayList<>();

            for (int i = 0; i < num_of_threads; i++) {
                System.out.println("FactoryTask: " + i);
                factoryTasks.add(new FactoryTask(num_of_stations));
            }

            future_Factories = executorService.invokeAll(factoryTasks);

            current_Factories = new ArrayList<>();

            update_current_Factories(future_Factories);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }

        current_Factories.sort(Collections.reverseOrder());

        for (int i = 0; i < count_of_GAOperations; i++) {
            doGAOperations();
        }


        System.out.println("--- end --- ");
        for (Factory f: current_Factories) {
            System.out.println("Before: " + f.getAffinity_value());
            f.evaluate_affinity();
            System.out.println(f.getAffinity_value());
        }

        System.out.println("end ");

        current_Factories.getFirst().evaluate_affinity();
        System.out.println("----  " + current_Factories.getFirst().getAffinity_value());
        for (int[] list: current_Factories.getFirst().getSpots()) {
            System.out.println(Arrays.toString(list));
        }
    }

    /**
     * One thread can only access the method at a time to prevent race conditions
     * @return a random Factory from the available future_Factories
     * @throws ExecutionException -
     * @throws InterruptedException -
     */
    public synchronized Factory pickRandom() throws ExecutionException, InterruptedException {

//        System.out.println("size in pickRandom: " + current_Factories.size());
        if (current_Factories.isEmpty()) return null;

        int randomIndex = new Random().nextInt(current_Factories.size());
        Factory selectedFactory = current_Factories.get(randomIndex);
        current_Factories.remove(randomIndex);

        return selectedFactory;
    }

    /**
     * Randomly assign mutation or crossover to the future_Factories and then, generate new and higher affinity factories
     */
    public void doGAOperations() {
        ExecutorService executorServiceForGA = Executors.newFixedThreadPool(num_of_threads);
        try {

            count_cFactories = current_Factories.size();
            List<Callable<Factory>> tasks = new ArrayList<>();
            int index = 0;

            List<Factory> copied_Factories = new ArrayList<>(current_Factories);


            while (index < count_cFactories) {

                int gaOperationRandom = ThreadLocalRandom.current().nextInt(20);
//                int gaOperationRandom = 0;

                Callable<Factory> task = () -> {
                    Factory factory = null;
                    // Increment your atomic integer if needed
                    // System.out.println("atomicValue: " + atomicInteger.incrementAndGet());

                    try {
                        if (gaOperationRandom != 0) {
                            // Mutation operation
//                            System.out.println("Mutation operation is going");
                            factory = requestMutationOperation(pickRandom());
                        } else {
                            // Crossover operation
                            System.out.println("Crossover operation is going");
                            factory = requestCrossOverOperation(pickRandom(), pickRandom());

                            // You can choose which factory to return. Here I return the first one
                        }
                    } catch (Exception e) {
                        System.out.println("There was an error in the thread: " + e.getMessage());
                        throw new RuntimeException(e);
                    }

                    return factory;
                };


                tasks.add(task);
                if (gaOperationRandom != 0) {
                    index++;
                }
                if (gaOperationRandom == 0) {
                    index += 2;
                }
            }
            future_Factories = new ArrayList<>();
            future_Factories = executorServiceForGA.invokeAll(tasks);

            current_Factories = new ArrayList<>(copied_Factories);

            update_current_Factories(future_Factories);

            current_Factories = current_Factories.stream()
                    .filter(c -> c != null)
                    .collect(Collectors.toList());

            for (Factory f: current_Factories) {
                f.evaluate_affinity();
            }

            current_Factories.sort(Collections.reverseOrder());

            doSelection();

            System.out.println("-----");
            for (int a = 0; a< 5; a++) {
                if (current_Factories.size() > a) {
                    System.out.println(current_Factories.get(a).getAffinity_value());
                }
            }

//            current_Factories.getFirst().outputSpots();
            System.out.println("---");

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorServiceForGA.shutdown();
        }
    }

    private void doSelection() {
        if (current_Factories.size() > 64) {
            current_Factories = current_Factories.subList(0,64);
        }
    }

    private void update_current_Factories(List<Future<Factory>> future_Factories) {
        for (Future<Factory> future : future_Factories) {
            try {
                Factory factory = future.get();
                current_Factories.add(factory);
            } catch (ExecutionException | InterruptedException e) {
                System.err.println("Task encountered an exception: " + e.getCause());
            }
        }
    }

    private Factory requestCrossOverOperation(Factory factory1, Factory factory2) throws Exception {
        if (factory1 != null && factory2 == null) {
            return requestMutationOperation(factory1);
        }
        if (factory1 == null) {
            throw new NullPointerException();
        }
        Factory output = factory1.doCrossover(factory2);
//        factory2.setSpots(factory1.getSpots());
        return output;
    }

    private Factory requestMutationOperation(Factory factory) throws Exception {
        if (factory == null) {
            return null;
        }

        Factory copyFactory = new Factory(factory);
        return copyFactory.doMutation();
    }

}
