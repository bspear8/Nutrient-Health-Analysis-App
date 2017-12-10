package edu.gatech.ihi.nhaa.usda;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import edu.gatech.ihi.nhaa.model.NutrientsResponse;
import edu.gatech.ihi.nhaa.model.SearchResponse;

@Component
public class USDARestClient {

    private final String USDA_JSON_FORMAT = "json";

    @Value("${usda.api.host}")
    private String serviceUrl;
    @Value("${usda.api.key}")
    private String apiKey;

    private final NutrientHelper nutrientHelper;

    public USDARestClient(NutrientHelper nutrientHelper) {
        this.nutrientHelper = nutrientHelper;
    }

    public NutrientsResponse getNutrientReportsByNdbno(String ndbno) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(serviceUrl)
                .path("/reports")
                .queryParam("format", USDA_JSON_FORMAT)
                .queryParam("ndbno", ndbno)
                .queryParam("api_key", apiKey)
                .build()
                .toUriString();
        NutrientsResponse resp = restTemplate.getForObject(url, NutrientsResponse.class);
        if(resp == null)
            throw new Exception("Nutrients response is empty");

        resp.getReport()
            .getFood()
            .getNutrients()
            .removeIf(n -> !nutrientHelper.isSupportedNutrient(n.getName()));

        return resp;
    }

    public SearchResponse searchByFoodName(String foodName) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(serviceUrl)
                .path("/search")
                .queryParam("format", USDA_JSON_FORMAT)
                .queryParam("q", foodName)
                .queryParam("api_key", apiKey)
                .build()
                .toUriString();
        SearchResponse resp = restTemplate.getForObject(url, SearchResponse.class);
        if(resp == null)
            throw new Exception("Search response is empty");
        return resp;
    }
}