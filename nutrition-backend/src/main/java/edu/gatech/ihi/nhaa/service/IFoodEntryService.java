package edu.gatech.ihi.nhaa.service;

import edu.gatech.ihi.nhaa.entity.FoodEntry;
import edu.gatech.ihi.nhaa.entity.User;
import edu.gatech.ihi.nhaa.web.dto.FoodEntryDto;
import edu.gatech.ihi.nhaa.web.dto.HistorySearchDto;

import java.util.List;

public interface IFoodEntryService {
    void addFoodEntry(User user, FoodEntryDto req) throws Exception;

    List<FoodEntry> getFoodEntriesByUser(User user) throws Exception;

    List<FoodEntry> getFoodEntryByFoodName(User user, String foodName) throws Exception;

    FoodEntry getFoodEntryById(User user, long foodEntryId) throws Exception;

    void deleteFoodEntryById(User user, long id) throws Exception;

    List<FoodEntry> getFoodEntryByDateRange(User user, HistorySearchDto req, Boolean unifyUnits) throws Exception;
}
