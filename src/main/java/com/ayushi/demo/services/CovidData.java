package com.ayushi.demo.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ayushi.demo.models.LocationStats;

@Service
public class CovidData {

	private static String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<LocationStats>allStats = new ArrayList<>();
	
	
	public List<LocationStats> getAllStats() {
		return allStats;
	}


	public void setAllStats(List<LocationStats> allStats) {
		this.allStats = allStats;
	}


	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchData() throws IOException, InterruptedException {
		List<LocationStats>newStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(DATA_URL)).build();
		HttpResponse<String>httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
//		System.out.println(httpResponse.body());
		StringReader csvBodyReader = new StringReader(httpResponse.body());
		
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			LocationStats ls = new LocationStats();
		    String state = record.get("Province/State");
		    
		    ls.setState(state);
		    ls.setCountry(record.get("Country/Region"));
		    ls.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
		
		    System.out.println(ls);
		    newStats.add(ls);
		    
		}
		this.allStats= newStats;
		
	}
}
