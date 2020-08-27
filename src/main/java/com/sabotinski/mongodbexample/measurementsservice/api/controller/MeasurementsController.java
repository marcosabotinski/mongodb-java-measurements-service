package com.sabotinski.mongodbexample.measurementsservice.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;

import com.sabotinski.mongodbexample.measurementsservice.api.models.Measurement;
import com.sabotinski.mongodbexample.measurementsservice.api.dao.MeasurementsDao;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/measurements")
public class MeasurementsController {
    
    @Autowired
    private MeasurementsDao dao;
    
    @GetMapping
    public List<Measurement> getMeasurements(@RequestParam String device, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return dao.getMeasuments(device, start, end);
    }

    @PostMapping
    public void createMeasurement(@RequestBody Measurement m) { 
        dao.addMeasurement(m);
    }
}