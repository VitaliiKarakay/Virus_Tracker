package com.vkarakay.virustracker.controller;

import com.vkarakay.virustracker.models.Location;
import com.vkarakay.virustracker.services.VirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

@Controller
public class MainController {

    @Autowired
    private VirusDataService virusDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<Location> allStats = virusDataService.getAllStats();
        int totalReportedCases = allStats.stream()
                .mapToInt(Location::getLatestConfirmedCases).sum();
        model.addAttribute("allStats", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        return "home";
    }
}
