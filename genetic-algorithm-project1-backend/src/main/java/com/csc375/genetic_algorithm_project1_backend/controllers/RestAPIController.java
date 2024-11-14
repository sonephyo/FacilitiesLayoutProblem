package com.csc375.genetic_algorithm_project1_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@CrossOrigin("http://localhost:3000")
public class RestAPIController {

    @GetMapping("/")
    public ResponseEntity<String> testing() {
        return ResponseEntity.ok("Hello World!");
    }


}
