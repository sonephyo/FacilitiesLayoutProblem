package com.csc375.genetic_algorithm_project1_backend.facilityLayoutProblemSolution;

import com.csc375.genetic_algorithm_project1_backend.facilityLayoutProblemSolution.Enum.Station_Type;

import java.util.ArrayList;
import java.util.List;

public class ClusterStation {

    private Station_Type station_type;
    private final List<int[]> coordinates;

    public ClusterStation(int stationValue) {
        this.station_type = Station_Type.getStation_Type(stationValue);
        this.coordinates = new ArrayList<>();
    }

    public void add_coordinate(int[] coordinate) {
        if (coordinate.length == 2) {
            this.coordinates.add(coordinate);
        } else {
            throw new IllegalArgumentException("Only two coordinates are allowed");
        }
    }

    public int get_value_of_Station_type() {
        return this.station_type.getValue();
    }

    public Station_Type getStation_type() {
        return station_type;
    }

    public List<int[]> getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return "ClusterStation{" +
                "station_type=" + station_type +
                ", coordinates=" + coordinates +
                '}';
    }
}
