package com.csc375.genetic_algorithm_project1_backend.models;

import lombok.Data;

@Data
public class StartRequest {
    private int numberOfStations;
    private int numberOfThreads;
    private int countOfGAOperations;
}

