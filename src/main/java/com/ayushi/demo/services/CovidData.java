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
	private static String Recovered_url = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
	private static String Deaths_url = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
	
	private List<LocationStats>allStats = new ArrayList<>();
	private List<LocationStats>recoveredStats = new ArrayList<>();
	private List<LocationStats>deathStats = new ArrayList<>();
	
	
	public List<LocationStats> getRecoveredStats() {
		return recoveredStats;
	}


	public void setRecoveredStats(List<LocationStats> recoveredStats) {
		this.recoveredStats = recoveredStats;
	}


	public List<LocationStats> getDeathStats() {
		return deathStats;
	}


	public void setDeathStats(List<LocationStats> deathStats) {
		this.deathStats = deathStats;
	}


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
		StringReader csvBodyReader = new StringReader(httpResponse.body());
		
		Integer lc =0, pdc=0;
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			LocationStats ls = new LocationStats();
		    String state = record.get("Province/State");
		    
		    String country = record.get("Country/Region");
		    if(country.equals("Canada"))
		    {
		    	lc+=Integer.parseInt(record.get(record.size()-1));
		    	pdc+=Integer.parseInt(record.get(record.size()-2));
//		    	System.out.println(lc);
		    	continue;
		    }
		    else if(country.equals("Central African Republic"))
		    {
		    	LocationStats canada = new LocationStats();
		    	canada.setState("-");
		    	canada.setCountry("Canada");
		    	canada.setLatestTotalCases(lc);
		    	canada.setDiffFromPrevDay(lc - pdc);
		    	
		    	newStats.add(canada);
		    }
		    if(state.equals(""))
		    	ls.setState("-");
		    else
		    	ls.setState(state);
		    ls.setCountry(country);
		    ls.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
		    int latestcases = Integer.parseInt(record.get(record.size()-1));
		    int prevDaycases = Integer.parseInt(record.get(record.size()-2));
			ls.setDiffFromPrevDay(latestcases - prevDaycases);
//		    System.out.println(ls);
		    newStats.add(ls);
		    
		}
		this.allStats= newStats;
		
		
List<LocationStats>newStats2 = new ArrayList<>();
		
		HttpClient client2 = HttpClient.newHttpClient();
		HttpRequest request2 = HttpRequest.newBuilder().uri(URI.create(Recovered_url)).build();
		HttpResponse<String>httpResponse2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());
		StringReader csvBodyReader2 = new StringReader(httpResponse2.body());
		
		Integer rc =0, rpdc=0, i=0;
		
		Iterable<CSVRecord> records2 = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader2);
		for (CSVRecord record : records2) {
			LocationStats ls = new LocationStats();
		    String country = record.get("Country/Region");
		    if(country.equals("Canada"))
		    {
		    	rc+=Integer.parseInt(record.get(record.size()-1));
		    	rpdc+=Integer.parseInt(record.get(record.size()-2));
		    	System.out.println(rc);
		    	continue;
		    }
		    else if(country.equals("Central African Republic"))
		    {
		    	allStats.get(i).setRecovered(rc);
				   
			    i++;
			   
		    	
		    }
		    
		    int recases = Integer.parseInt(record.get(record.size()-1));
//		    int prevDaycases = Integer.parseInt(record.get(record.size()-2));
			allStats.get(i).setRecovered(recases);
		   
		    i++;
		    
		}
List<LocationStats>newStats3 = new ArrayList<>();
		
		HttpClient client3 = HttpClient.newHttpClient();
		HttpRequest request3 = HttpRequest.newBuilder().uri(URI.create(Deaths_url)).build();
		HttpResponse<String>httpResponse3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());
		StringReader csvBodyReader3 = new StringReader(httpResponse3.body());
		
		Integer cd =0;
	    i=0;
		
		Iterable<CSVRecord> records3 = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader3);
		for (CSVRecord record : records3) {
			LocationStats ls = new LocationStats();
		    String country = record.get("Country/Region");
		    if(country.equals("Canada"))
		    {
		    	cd+=Integer.parseInt(record.get(record.size()-1));
		    	System.out.println(cd);
		    	continue;
		    }
		    else if(country.equals("Central African Republic"))
		    {
		    	allStats.get(i).setDeaths(cd);
				allStats.get(i).setActive(lc-rc-cd);   
			    i++;
			   
		    	
		    }
		    
		    int dcases = Integer.parseInt(record.get(record.size()-1));
//		    int prevDaycases = Integer.parseInt(record.get(record.size()-2));
		    int activecases= allStats.get(i).getLatestTotalCases()- allStats.get(i).getRecovered() - dcases;
			allStats.get(i).setDeaths(dcases);
			allStats.get(i).setActive(activecases);
		   
		    i++;
		    
		}

		
	}
}
