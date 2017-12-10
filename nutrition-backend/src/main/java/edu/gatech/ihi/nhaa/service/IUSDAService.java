package edu.gatech.ihi.nhaa.service;

import edu.gatech.ihi.nhaa.web.dto.FoodEntryDto;
import edu.gatech.ihi.nhaa.web.dto.FoodSearchResultDto;

import java.util.List;

public interface IUSDAService {
    FoodEntryDto getNutrientReportsByNdbno(String ndbno) throws Exception;

    List<FoodSearchResultDto> searchByFoodName(String foodName) throws Exception;
}
