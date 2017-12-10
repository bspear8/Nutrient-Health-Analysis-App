package edu.gatech.ihi.nhaa.service;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.ihi.nhaa.usda.NutrientHelper;
import edu.gatech.ihi.nhaa.web.dto.FoodSearchResultDto;
import edu.gatech.ihi.nhaa.web.dto.NutrientDto;
import org.springframework.stereotype.Component;

import edu.gatech.ihi.nhaa.usda.USDARestClient;
import edu.gatech.ihi.nhaa.web.dto.FoodEntryDto;
import edu.gatech.ihi.nhaa.model.Item;
import edu.gatech.ihi.nhaa.model.NutrientsResponse;
import edu.gatech.ihi.nhaa.model.SearchResponse;

@Component
public class USDAService implements IUSDAService {

    private final USDARestClient usdaClient;
    private final NutrientHelper nutrientHelper;

    public USDAService(USDARestClient usdaClient, NutrientHelper nutrientHelper) {
        this.usdaClient = usdaClient;
        this.nutrientHelper = nutrientHelper;
    }

    @Override
    public FoodEntryDto getNutrientReportsByNdbno(String ndbno) throws Exception {
        FoodEntryDto resp = new FoodEntryDto();
        try {
            NutrientsResponse nutrientsResponse = usdaClient.getNutrientReportsByNdbno(ndbno);
            if(nutrientsResponse == null)
                throw new Exception("Nutrients response from USDA is empty");
            edu.gatech.ihi.nhaa.model.Food foodObj = nutrientsResponse.getReport().getFood();
            resp.setName(foodObj.getName());
            List<NutrientDto> nutrients = new ArrayList<>();
            for(edu.gatech.ihi.nhaa.model.Nutrient nutrient : foodObj.getNutrients()) {
                NutrientDto nt = new NutrientDto();
                nt.setName(nutrientHelper.translateNutritionName(nutrient.getName()));
                nt.setValue(Double.parseDouble(nutrient.getValue()));
                nt.setUnits(nutrient.getUnit());
                nutrients.add(nt);
            }
            resp.setNutrients(nutrients);

        } catch(Exception e) {
            e.printStackTrace();
            throw new Exception("Error while calling USDA Nutrient Report API: " + e.getMessage());
        }
        return resp;
    }

    @Override
    public List<FoodSearchResultDto> searchByFoodName(String foodName) throws Exception {
        try {
            SearchResponse searchResponse = usdaClient.searchByFoodName(foodName);
            if(searchResponse == null)
                throw new Exception("USDA search result is empty");

            List<FoodSearchResultDto> foundFoods = new ArrayList<>();
            for(Item item : searchResponse.getList().getItem()) {
                FoodSearchResultDto food = new FoodSearchResultDto();
                food.setNbdno(item.getNdbno());
                food.setName(item.getName());
                foundFoods.add(food);
            }

            return foundFoods;
        } catch(Exception e) {
            e.printStackTrace();
            throw new Exception("Error while calling USDA Search API: " + e.getMessage());
        }
    }
}