package edu.gatech.ihi.nhaa.web.controller;

import edu.gatech.ihi.nhaa.entity.FoodEntry;
import edu.gatech.ihi.nhaa.entity.User;
import edu.gatech.ihi.nhaa.security.AuthenticatedUser;
import edu.gatech.ihi.nhaa.service.IFoodEntryService;
import edu.gatech.ihi.nhaa.web.dto.FoodEntryDto;
import edu.gatech.ihi.nhaa.web.dto.HistorySearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static edu.gatech.ihi.nhaa.common.CommonUtil.ISODateFormat;

@RestController
public class FoodEntriesController {

    private final IFoodEntryService foodEntryService;

    @Autowired
    public FoodEntriesController(IFoodEntryService foodEntryService) {
        this.foodEntryService = foodEntryService;
    }

    @GetMapping(value = "/food-entries", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<FoodEntry>> getFoodEntry(@AuthenticatedUser User user) throws Exception {
        List<FoodEntry> resp = foodEntryService.getFoodEntriesByUser(user);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(value = "/food-entries/{foodEntryId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<FoodEntry> getFoodEntryById(@AuthenticatedUser User user,
                                                      @PathVariable("foodEntryId") long foodEntryId) throws Exception {
        FoodEntry resp = foodEntryService.getFoodEntryById(user, foodEntryId);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(value = "/food-entries", params = "name", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<FoodEntry>> getFoodEntryByFoodName(@AuthenticatedUser User user,
                                                                  @PathVariable("name") String foodName) throws Exception {
        List<FoodEntry> resp = foodEntryService.getFoodEntryByFoodName(user, foodName);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(value = "/food-entries", params = { "startDate", "endDate", "unifyUnits" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<FoodEntry>> getFoodEntryByParam(
            @AuthenticatedUser User user,
            @RequestParam("startDate")  @DateTimeFormat(pattern = ISODateFormat) Date startDate,
            @RequestParam("endDate")  @DateTimeFormat(pattern = ISODateFormat) Date endDate,
            @RequestParam("unifyUnits") Boolean unifyUnits) throws Exception {
        HistorySearchDto historySearchDto = new HistorySearchDto(startDate, endDate);

        List<FoodEntry> resp = foodEntryService.getFoodEntryByDateRange(user, historySearchDto, unifyUnits);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PostMapping(value = "/food-entries", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<FoodEntryDto> addFoodEntry(@AuthenticatedUser User user, @RequestBody FoodEntryDto req) throws Exception {
        foodEntryService.addFoodEntry(user, req);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/food-entries/{foodId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteFoodEntry(@AuthenticatedUser User user, @PathVariable("foodId") long foodId) throws Exception {
        foodEntryService.deleteFoodEntryById(user, foodId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
