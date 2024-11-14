package Project1_FLP;

import Project1_FLP.Enum.Station_Type;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Factory implements  Comparable<Factory>{
    private final String id;
    private int[][] spots;
    private final int num_of_stations;
    private double affinity_value;
    private HashMap<Station_Type, List<ClusterStation>> clusterStation_HashMap = new HashMap<>();


    public Factory(int numOfStations) {
        this.id = UUID.randomUUID().toString();
        int randomRowSize = (int) Math.ceil(numOfStations / 2.0);
        int randomColSize = (int) Math.ceil(numOfStations / 2.0);
        this.spots = new int[randomRowSize][randomColSize];
        this.num_of_stations = numOfStations;
    }

    public Factory (Factory factory_to_clone) {
        this.id = UUID.randomUUID().toString();
        if (factory_to_clone.spots != null) {
            this.spots = new int[factory_to_clone.spots.length][];
            for (int i = 0; i < factory_to_clone.spots.length; i++) {
                this.spots[i] = factory_to_clone.spots[i].clone();
            }
        } else {
            this.spots = null;
        }
        this.num_of_stations = factory_to_clone.num_of_stations;
        this.affinity_value = factory_to_clone.affinity_value;
        this.clusterStation_HashMap = factory_to_clone.clusterStation_HashMap;
    }


    public void populate_factory() {

        for (int i = 0 ; i < num_of_stations ; i++) {
            Station station = new Station();
            assign_station(station, 0);
        }
        outputSpots();
    }

    private void assign_station(Station station, int count_of_recursion) {
        int row = ThreadLocalRandom.current().nextInt(spots.length);
        int col = ThreadLocalRandom.current().nextInt(spots[0].length);

        // To prevent recursion overflow
        // Note: Increasing the count_of_recursion more than 10 can lead to stack over flow
        if (count_of_recursion == 10) {
            return;
        }

            try {

                int value = station.getStation_type_val();
                if (value == 1) {
                    placeStation1(station, row, col, count_of_recursion);
                } else if (value == 2) {
                    placeStation2(station, row, col, count_of_recursion);
                } else if (value == 3) {
                    placeStation3(station, row, col, count_of_recursion);
                } else if (value == 4) {
                    placeStation4(station, row, col, count_of_recursion);
                }
            } catch(IndexOutOfBoundsException e) {
                assign_station(station, + 1);
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
            }

    }

    public void placeStation1(Station station, int row, int col, int count_of_recursion) {
        if (spots[row][col] == 0 && spots[row - 1][col] == 0 && spots[row][col + 1] == 0 && spots[row - 1][col + 1] == 0) {
            spots[row][col] = station.getStation_type_val();
        } else {
            this.assign_station(station, count_of_recursion + 1);
        }
    }
    public void placeStation2(Station station, int row, int col, int count_of_recursion) {
        if (spots[row][col] == 0 && spots[row-1][col] == 0 && spots[row-1][col+1] == 0) {
            spots[row][col] = station.getStation_type_val();
            spots[row-1][col] = station.getStation_type_val();
        } else {
            this.assign_station(station, count_of_recursion + 1);
        }
    }

    public void placeStation3(Station station, int row, int col, int count_of_recursion) {
        if (spots[row][col] == 0 && spots[row+1][col] == 0 && spots[row-1][col] == 0) {
            spots[row][col] = station.getStation_type_val();
            spots[row+1][col] = station.getStation_type_val();
            spots[row-1][col] = station.getStation_type_val();
        } else {
            this.assign_station(station, count_of_recursion + 1);
        }
    }

    public void placeStation4(Station station, int row, int col, int count_of_recursion) {
        if (spots[row][col] == 0 && spots[row-1][col] == 0 && spots[row][col-1] == 0 && spots[row][col+1] == 0) {
            spots[row][col] = station.getStation_type_val();
            spots[row-1][col] = station.getStation_type_val();
            spots[row][col-1] = station.getStation_type_val();
            spots[row][col+1] = station.getStation_type_val();
        } else {
            this.assign_station(station, count_of_recursion + 1);
        }
    }

    public double evaluate_affinity() {

        this.create_cluster(this.spots);
        double result = 0;

        for (Station_Type station_type : clusterStation_HashMap.keySet()) {
            List<ClusterStation> cluster_stations = clusterStation_HashMap.get(station_type);

            // get the station of next char (e.g. StationA -> StationB)
            Station_Type connected_station= Station_Type.getStation_Type(station_type.getValue()+1);
            if (connected_station == null) {
                connected_station = Station_Type.TypeA;
                } // If the type doesn't exist, it should connect back to the first station
            List<ClusterStation> connected_cluster_stations = clusterStation_HashMap.get(connected_station);
            if (connected_cluster_stations != null) {

                for (ClusterStation cluster_station : cluster_stations) {
                    for (ClusterStation connected_cluster_station : connected_cluster_stations) {
                        result += this.getConnectionValue(cluster_station,connected_cluster_station);
                    }
                }
            }

        }

        for (Station_Type station_type : clusterStation_HashMap.keySet()) {
            List<ClusterStation> cluster_stations_copy = new ArrayList<>(clusterStation_HashMap.get(station_type));
            for (int i = 0; i<cluster_stations_copy.size(); i++) {
                ClusterStation cluster_station = cluster_stations_copy.get(i);
                for (int j = i+1; j < cluster_stations_copy.size(); j++) {
                    ClusterStation connected_cluster_station = cluster_stations_copy.get(j);
                    result -= this.getConnectionValue(cluster_station,connected_cluster_station);
                }
            }
        }
        if (result == Double.POSITIVE_INFINITY) {
            return 0;
        }
        this.affinity_value = result;
        return result;
    }

    /**
     * Generate the connection value between the two station
     * Two Factor influences the connection value
     * 1) The distances between the mid-points of the two stations
     * 2) The spots that each cluster occupies
     * @param first_cluster_station - first cluster station
     * @param second_cluster_station - second cluster station
     * @return
     */
    private double getConnectionValue(ClusterStation first_cluster_station, ClusterStation second_cluster_station) {
        double[] first_station_cluster_mid_point = get_mid_point_cluster(first_cluster_station);
        double[] connected_cluster_station_mid_point = get_mid_point_cluster(second_cluster_station);

        double deltaX = first_station_cluster_mid_point[0] - connected_cluster_station_mid_point[0];
        double deltaY = first_station_cluster_mid_point[1] - connected_cluster_station_mid_point[1];

        int first_cluster_station_len = first_cluster_station.getCoordinates().size();
        int second_cluster_station_len = second_cluster_station.getCoordinates().size();

        double deltaXSquared = deltaX * deltaX * first_cluster_station_len * first_cluster_station_len;
        double deltaYSquared = deltaY * deltaY * second_cluster_station_len * first_cluster_station_len;

        return 1/Math.sqrt(deltaXSquared + deltaYSquared);
    }

    private double[] get_mid_point_cluster(ClusterStation cluster_station) {
        double sumX = 0;
        double sumY = 0;
        int sizeOfCluster = cluster_station.getCoordinates().size();
        for (int[] point: cluster_station.getCoordinates()){
            sumX += point[0];
            sumY += point[1];
        }

        return new double[]{sumX/sizeOfCluster, sumY/sizeOfCluster};

    }

    private void create_cluster(int[][] matrix) {
        int[][] copiedMatrix = Arrays.stream(matrix)
                .map(int[]::clone) // Clone each row
                .toArray(int[][]::new);

        clusterStation_HashMap = new HashMap<>();

        for (int i = 0; i < copiedMatrix.length; i++) {
            for (int j = 0; j < copiedMatrix[i].length; j++) {
                if (copiedMatrix[i][j] != 0) {
                    ClusterStation clusterStation = new ClusterStation(copiedMatrix[i][j]);
                    visit_connected_stations(copiedMatrix, i, j, clusterStation);
                    if (clusterStation_HashMap.containsKey(clusterStation.getStation_type())) {
                        clusterStation_HashMap.get(clusterStation.getStation_type()).add(clusterStation);
                    } else {
                        clusterStation_HashMap.put(clusterStation.getStation_type(), new ArrayList<>());
                        clusterStation_HashMap.get(clusterStation.getStation_type()).add(clusterStation);
                    }
                }
            }
        }

    }

    private void visit_connected_stations(int[][] matrix, int row, int col,ClusterStation clusterStation) {

        try {

        if (row < 0 || row >= matrix.length || col < 0 || col >= matrix[0].length) {
            return ;
        }
        if (matrix[row][col] != clusterStation.get_value_of_Station_type()) {
            return ;
        }

        clusterStation.add_coordinate(new int[]{row, col});
        matrix[row][col]= 0;

        visit_connected_stations(matrix, row+1, col, clusterStation);
        visit_connected_stations(matrix, row, col+1, clusterStation);
        visit_connected_stations(matrix, row, col-1, clusterStation);
        visit_connected_stations(matrix, row-1, col, clusterStation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Factory doMutation() throws Exception {
        Set<Station_Type> allStations = this.clusterStation_HashMap.keySet();
        if (allStations.isEmpty()) {
            throw new NullPointerException();
        }
        destroyOldCreateNewClusters();

        this.evaluate_affinity();

        return this;
    }

    public void destroyOldCreateNewClusters() throws Exception {
        Set<Station_Type> allStations = this.clusterStation_HashMap.keySet();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < random.nextInt(1,10); i++) {
            try {

                int randomIndex = random.nextInt(this.clusterStation_HashMap.size());

                List<Station_Type> stationList = new ArrayList<>(allStations);

                Station_Type randomStation;
                if (randomIndex < stationList.size()) {
                    randomStation = stationList.get(randomIndex);
                } else{
                    randomStation = stationList.getFirst();
                }


//            System.out.println(randomStation);
            List<ClusterStation> selectedClusterStations = clusterStation_HashMap.get(randomStation);

            if (selectedClusterStations == null || selectedClusterStations.isEmpty()) {
                return;
            }

            ClusterStation clusterStation = selectedClusterStations.get(random.nextInt(selectedClusterStations.size()));

            destroyCoordinatesOfClusterStation(clusterStation);
            randomPlacementOfDestroyedCoordinates(clusterStation);
            this.create_cluster(this.spots);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }



    }

    public void destroyCoordinatesOfClusterStation(ClusterStation clusterStation) {
        for (int[] coordinate: clusterStation.getCoordinates()){
            spots[coordinate[0]][coordinate[1]] = 0;
        }
    }

    private void randomPlacementOfDestroyedCoordinates(ClusterStation clusterStation) {

        Station_Type station_type = Station_Type.getStation_Type(clusterStation.get_value_of_Station_type());
//        System.out.println("random placement done for " + station_type);
        if (station_type == Station_Type.TypeA) {
            int countOfLoop = clusterStation.getCoordinates().size() + 1;
            for (int i = 0; i < countOfLoop; i++) {
                assign_station(new Station(station_type), -1000);
            }
        } else if (station_type == Station_Type.TypeB) {
            int countOfLoop = clusterStation.getCoordinates().size() / 2 + 1;
            for (int i = 0; i < countOfLoop; i++) {
                assign_station(new Station(station_type), -1000);
            }
        } else if (station_type == Station_Type.TypeC) {
            int countOfLoop = clusterStation.getCoordinates().size() / 3 + 1;
            for (int i = 0; i < countOfLoop; i++) {
                assign_station(new Station(station_type), -1000);
            }
        } else if (station_type == Station_Type.TypeD) {
            int countOfLoop = clusterStation.getCoordinates().size() / 4 + 1;
            for (int i = 0; i < countOfLoop; i++) {
                assign_station(new Station(station_type), -1000);
            }
        }

    }



    public Factory doCrossover(Factory other_factory) throws Exception {
        Factory copy_this_factory = new Factory(this);
        Factory copy_other_factory = new Factory(other_factory);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int randomSegmentation = random.nextInt(0,4);
        int rowStart = 0;
        int rowEnd = 0;
        int colStart = 0;
        int colEnd = 0;
        if (randomSegmentation == 0) {
            rowEnd = spots.length/2;
            colEnd = spots[0].length/2;
        } else if (randomSegmentation == 1) {
            rowEnd = spots.length/2;
            colStart = spots[0].length/2;
            colEnd = spots[0].length;
        } else if (randomSegmentation == 2) {
            rowStart = spots.length/2;
            rowEnd = spots.length;
            colEnd = spots[0].length/2;
        } else if (randomSegmentation == 3) {
            rowStart = spots.length/2;
            colStart = spots[0].length/2;
            rowEnd = spots.length;
            colEnd = spots[0].length;
        }

        int[][] copy_this_factory_spots = copy_this_factory.spots;
        int[][] copy_other_factory_spots = copy_other_factory.spots;

        for (int i = rowStart; i < rowEnd; i++) {
            for (int j = colStart; j < colEnd; j++) {
                // Use the deep copies for swapping
                int temp = copy_this_factory_spots[i][j];
                copy_this_factory_spots[i][j] = copy_other_factory_spots[i][j];
                copy_other_factory_spots[i][j] = temp;
            }
        }

        modifyingRuleBreakingStation(copy_this_factory_spots, copy_this_factory);
        modifyingRuleBreakingStation(copy_other_factory_spots, copy_other_factory);

        copy_other_factory.evaluate_affinity();
        copy_this_factory.evaluate_affinity();

        Factory maxAffinityValueFactory = copy_this_factory;

        if (maxAffinityValueFactory.getAffinity_value() < copy_other_factory.getAffinity_value()) {
            maxAffinityValueFactory = copy_other_factory;
        }

        System.out.println("original: " + maxAffinityValueFactory.getAffinity_value());
        System.out.println("recalculated: " + maxAffinityValueFactory.evaluate_affinity());
        System.out.println("---");


        return maxAffinityValueFactory;
    }

    private void modifyingRuleBreakingStation(int[][] matrix, Factory factory) throws Exception {
        int[][] copiedMatrix = Arrays.stream(matrix)
                .map(int[]::clone) // Clone each row
                .toArray(int[][]::new);

        try {

        for (int i = 0; i < copiedMatrix.length; i++) {
            for (int j = 0; j < copiedMatrix[i].length; j++) {
                if (copiedMatrix[i][j] != 0) {
                    ClusterStation clusterStation = new ClusterStation(copiedMatrix[i][j]);
                    factory.visit_connected_stations(copiedMatrix, i, j, clusterStation);
                    factory.evaluateValidityOfClusterStation(clusterStation);
                }
            }
        }
        } catch (Exception e) {
            throw new Exception(e);
        }


    }

    public void evaluateValidityOfClusterStation(ClusterStation clusterStation) {
        int stationValue = clusterStation.get_value_of_Station_type();

        if (stationValue == 2) {
            if (clusterStation.getCoordinates().size() % 2 != 0) {
                destroyCoordinatesOfClusterStation(clusterStation);
                randomPlacementOfDestroyedCoordinates(clusterStation);
            }
        } else if (stationValue == 3) {

            if (clusterStation.getCoordinates().size() % 3 != 0) {
                destroyCoordinatesOfClusterStation(clusterStation);
                randomPlacementOfDestroyedCoordinates(clusterStation);
            }
        } else if (stationValue == 4) {
            if (clusterStation.getCoordinates().size() % 4 != 0) {
                destroyCoordinatesOfClusterStation(clusterStation);
                randomPlacementOfDestroyedCoordinates(clusterStation);
            }
        }
    }

    public void setSpots(int[][] spots) {
        this.spots = spots;
    }

    public int[][] getSpots() {
        return spots;
    }

    public double getAffinity_value() {
        return affinity_value;
    }

    @Override
    public int compareTo(Factory other) {
        return Double.compare(this.getAffinity_value(), other.getAffinity_value());
    }

    public static void main(String[] args) throws Exception {

//        Factory factory1 = new Factory(7);
//        factory1.spots = new int[][]{
//                {1, 1, 0, 4, 0, 0, 0},
//                {1, 1, 4, 4, 4, 0, 0},
//                {0, 0, 0, 2, 0, 0, 3},
//                {0, 0, 0, 2, 0, 0, 3},
//                {0, 2, 0, 0, 1, 1, 3},
//                {0, 2, 0, 0, 1, 1, 0},
//                {0, 0, 0, 0, 0, 0, 0}
//        };
//
//        Factory factory2 = new Factory(7);
//        factory2.spots = new int[][]{
//                {1, 1, 2, 0, 0, 1, 1},
//                {1, 1, 2, 0, 0, 1, 1},
//                {0, 0, 0, 3, 0, 4, 0},
//                {2, 0, 0, 3, 4, 4, 4},
//                {2, 3, 0, 3, 0, 0, 0},
//                {0, 3, 0, 2, 0, 0, 0},
//                {0, 3, 0, 2, 0, 0, 0}
//        };
//
//
//        factory1.create_cluster(factory1.spots);
//        factory2.create_cluster(factory2.spots);
//
//        Factory newFactory = factory1.doCrossover(factory2);
//
//        System.out.println("result");
//        for (int[] i: factory1.spots) {
//            System.out.println(Arrays.toString(i));
//        }
//
//        System.out.println("----");
//        for (int[] i: factory2.spots) {
//            System.out.println(Arrays.toString(i));
//        }
//
//        System.out.println("----");
//        for (int[] i: newFactory.spots) {
//            System.out.println(Arrays.toString(i));
//        }

    }

    public void outputSpots() {
        for (int[] spot : this.spots) {
            System.out.println(Arrays.toString(spot));
        }
    }

}
