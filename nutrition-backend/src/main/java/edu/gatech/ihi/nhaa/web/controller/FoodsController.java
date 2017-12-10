package edu.gatech.ihi.nhaa.web.controller;

import java.util.List;

import edu.gatech.ihi.nhaa.service.IUSDAService;
import edu.gatech.ihi.nhaa.web.dto.FoodEntryDto;
import edu.gatech.ihi.nhaa.web.dto.FoodSearchResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FoodsController {

    private final IUSDAService usdaService;

    @Autowired
    public FoodsController(IUSDAService usdaService) {
        this.usdaService = usdaService;
    }

    @GetMapping(value = "/foods/{ndbno}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<FoodEntryDto> getNutrientReportsByNdbno(@PathVariable("ndbno") String ndbno) throws Exception {
        FoodEntryDto resp = usdaService.getNutrientReportsByNdbno(ndbno);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(value = "/foods", params = { "name" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<FoodSearchResultDto>> searchByFoodName(@RequestParam("name") String foodName) throws Exception {
        List<FoodSearchResultDto> resp = usdaService.searchByFoodName(foodName);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}