package com.vkarakay.virustracker.services;

import com.vkarakay.virustracker.models.Location;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class VirusDataService

{
    public final String URL =
            "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<Location> allStats = new ArrayList<>();

    public List<Location> getAllStats() {
        return allStats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        List <Location> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        StringReader csvReader = new StringReader(response.body());
        Iterable<CSVRecord> records = CSVFormat
                .Builder.create().setHeader().setSkipHeaderRecord(true).build()
                .parse(csvReader);
        for (CSVRecord csvRecord: records) {
            Location location = new Location();
            location.setState(csvRecord.get("Province/State"));
            location.setCountry(csvRecord.get("Country/Region"));
            location.setLatestConfirmedCases(Integer.parseInt(csvRecord.get(csvRecord.size()-1)));
            int prevDayConfirmedCases = Integer.parseInt(csvRecord.get(csvRecord.size() - 2));
            location.setDelta(location.getLatestConfirmedCases() - prevDayConfirmedCases);
            newStats.add(location);
            if (location.getCountry().equals("Ukraine")) {
                Collections.swap(newStats, newStats.size()-1, 0);
            }
        }
        this.allStats = newStats;
    }
}
