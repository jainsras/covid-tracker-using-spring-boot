package com.ayushi.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ayushi.demo.models.LocationStats;
import com.ayushi.demo.services.CovidData;

@Controller
public class HomeController {

	@Autowired
	CovidData covidData;
	
	@GetMapping("/")
	public String home(Model model)
	{
		List<LocationStats>stats = covidData.getAllStats();
		int totalCases = stats.stream().mapToInt(stat->stat.getLatestTotalCases()).sum();
		int totalNewCases = stats.stream().mapToInt(stat->stat.getDiffFromPrevDay()).sum();
		
		model.addAttribute("stats", stats);
		model.addAttribute("totalCases", totalCases);
		model.addAttribute("totalNewCases", totalNewCases);
		
		return "home";
	}
	
}
