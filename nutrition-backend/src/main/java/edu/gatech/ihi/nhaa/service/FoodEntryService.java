package edu.gatech.ihi.nhaa.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.gatech.ihi.nhaa.entity.User;
import edu.gatech.ihi.nhaa.entity.FoodEntry;
import edu.gatech.ihi.nhaa.entity.Nutrient;
import edu.gatech.ihi.nhaa.web.dto.FoodEntryDto;
import edu.gatech.ihi.nhaa.web.dto.NutrientDto;
import edu.gatech.ihi.nhaa.web.dto.HistorySearchDto;

import edu.gatech.ihi.nhaa.repository.FoodEntryRepository;

import org.springframework.stereotype.Component;

@Component
public class FoodEntryService implements IFoodEntryService {

    private final FoodEntryRepository foodEntryRepository;

    public FoodEntryService(FoodEntryRepository foodEntryRepository) throws Exception {

        this.foodEntryRepository = foodEntryRepository;
    }

    @Override
    public void addFoodEntry(User user, FoodEntryDto req) throws Exception {
        try {
            FoodEntry foodEntry = new FoodEntry();
            foodEntry.setUser(user);
            foodEntry.setName(req.getName());
            foodEntry.setConsumptionDate(req.getConsumptionDate());
            foodEntry.setNotes(req.getNotes());
            foodEntry.setServings(req.getServings());

            for (NutrientDto nutrientDto : req.getNutrients()) {
                if (nutrientDto.getName() != null && nutrientDto.getUnits() != null && nutrientDto.getValue() != null) {
                    Nutrient nutrient = new Nutrient();
                    nutrient.setName(nutrientDto.getName());
                    nutrient.setValue(nutrientDto.getValue());
                    nutrient.setUnits(nutrientDto.getUnits());
                    foodEntry.addNutrient(nutrient);
                }
            }

            foodEntryRepository.save(foodEntry);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while adding food entry: " + e.getMessage());
        }
    }

    @Override
    public List<FoodEntry> getFoodEntriesByUser(User user) throws Exception {
        try {
            return foodEntryRepository.findByUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while retrieving food entry: " + e.getMessage());
        }
    }

    @Override
    public List<FoodEntry> getFoodEntryByFoodName(User user, String foodName) throws Exception {
        try {
            return foodEntryRepository.findByUserAndName(user, foodName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while retrieving food entry: " + e.getMessage());
        }
    }

    @Override
    public FoodEntry getFoodEntryById(User user, long foodEntryId) throws Exception {
        FoodEntry log;
        try {
            log = foodEntryRepository.findOneByUserAndFoodEntryId(user, foodEntryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while retrieving food entry: " + e.getMessage());
        }
        return log;
    }

    @Override
    public void deleteFoodEntryById(User user, long foodEntryId) throws Exception {
        try {
            foodEntryRepository.deleteFoodEntryByUserAndFoodEntryId(user, foodEntryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while deleting food entry: " + e.getMessage());
        }
    }

    @Override
    public List<FoodEntry> getFoodEntryByDateRange(User user, HistorySearchDto req, Boolean unifyUnits) throws Exception {
        try {
            final List<FoodEntry> foodEntries = foodEntryRepository.findByUserAndConsumptionDateBetween(user, req.getStartDate(), req.getEndDate());
            if (unifyUnits != null && unifyUnits) {
                return convertFoodEntryUnits(foodEntries);
            }
            return foodEntries;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while getting food entry by param: " + e.getMessage());
        }
    }

    private List<FoodEntry> convertFoodEntryUnits(List<FoodEntry> foodEntries) {
        return foodEntries.stream()
                .peek(f -> {
                    final Set<Nutrient> nutrients = f.getNutrients().stream()
                            .peek(n -> {
                                final String units = n.getUnits();
                                final Double value = n.getValue();
                                if (units.equalsIgnoreCase("g")) {
                                    n.setValue(value * 1000);
                                    n.setUnits("mg");
                                }
                            })
                            .collect(Collectors.toCollection(HashSet::new));
                    f.setNutrients(nutrients);
                })
                .collect(Collectors.toList());
    }
}