package edu.gatech.ihi.nhaa.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.gatech.ihi.nhaa.common.NutrientDetails;
import edu.gatech.ihi.nhaa.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.gatech.ihi.nhaa.repository.GoalRepository;
import edu.gatech.ihi.nhaa.web.dto.GoalStateDto;
import edu.gatech.ihi.nhaa.web.dto.HealthEntryDto;

import javax.transaction.Transactional;

/**
 * Goals Stuff:
 * Goal Entity - The object relational mapping to the goal repository
 * Goal Service - Provides interaction methods for the goal controller; also performs all
 * calculations needed to update goal statuses.
 * Goal Controller - Provides UI integration methods via HTTP get/post requests
 *
 * @author Blaine Spear
 */
@Component
public class GoalService implements IGoalService {
    private final GoalRepository goalRepository;
    private final FoodEntryService foodEntryService;
    private final FHIRService fhirService;

    @Autowired
    public GoalService(GoalRepository goalRepository, FoodEntryService foodEntryService, FHIRService fhirService) {
        this.goalRepository = goalRepository;
        this.foodEntryService = foodEntryService;
        this.fhirService = fhirService;
    }

    @Override
    public void addNewGoal(User user, Goal goal) throws Exception {
        goal.setUser(user);
        goalRepository.save(goal);
    }

    @Override
    public List<Goal> getAllGoalsByUser(User user) throws Exception {
        return goalRepository.findByUser(user);
    }

    @Override
    public Goal getGoalById(User user, long goalId) throws Exception {
        try {
            return goalRepository.findByUserAndGoalId(user, goalId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while retrieving goal: " + e.getMessage());
        }
    }

    @Override
    public List<Goal> getGoalsByDate(User user, Date date) throws Exception {
        try {
            return goalRepository.findByUserAndDate(user, date);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while retrieving goal: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteGoalById(User user, long goalId) throws Exception {
        try {
            Goal goal = goalRepository.findByUserAndGoalId(user, goalId);
            goalRepository.delete(goal);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while deleting goal: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateGoal(User user, long goalId, Goal goal) throws Exception {
        try {
            final Goal foundGoal = goalRepository.getOneByUserAndGoalId(user, goalId);
            foundGoal.setEntryType(goal.getEntryType());
            foundGoal.setGoalType(goal.getGoalType());
            foundGoal.setName(goal.getName());
            foundGoal.setValue(goal.getValue());
            foundGoal.setDate(goal.getDate());
            foundGoal.setUnits(goal.getUnits());
            foundGoal.setTimePeriod(goal.getTimePeriod());

            goalRepository.save(foundGoal);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error updating goal: " + e.getMessage());
        }
    }

    @Override
    public List<GoalStateDto> getGoalStatesByUser(User user) throws Exception {
        List<Goal> goals = goalRepository.findByUser(user);
        List<GoalStateDto> goalStates = new ArrayList<>();

        final Map<HealthEntryType, List<HealthEntryDto>> healthEntries = fhirService.getPatientHealthData(user)
                .stream()
                .sorted(Comparator.comparing(HealthEntryDto::getDate))
                .collect(Collectors.groupingBy(HealthEntryDto::getHealthEntryType, Collectors.toList()));

        final Map<String, List<NutrientDetails>> nutrientDetails = foodEntryService.getFoodEntriesByUser(user)
                .stream()
                .map((FoodEntry f) -> f.getNutrients().stream().map((Nutrient m) -> {
                    final NutrientDetails nwd = new NutrientDetails(f.getConsumptionDate(), m);
                    nwd.setConsumptionDate(f.getConsumptionDate());
                    nwd.setServings(f.getServings());
                    return nwd;
                }))
                .flatMap(Function.identity())
                .sorted(Comparator.comparing(NutrientDetails::getConsumptionDate))
                .collect(Collectors.groupingBy(NutrientDetails::getName));


        for (Goal a : goals) {
            GoalStateDto dto = new GoalStateDto();
            dto.setGoal(a);

            final GoalEntryType entryType = a.getEntryType();
            final GoalTimePeriod timePeriod = a.getTimePeriod();
            final String units = a.getUnits();

            Double compareValue = null;

            if (entryType == GoalEntryType.GLUCOSE) {
                final List<HealthEntryDto> glucoseDtos = healthEntries.getOrDefault(HealthEntryType.GLUCOSE, new ArrayList<>());
                if (timePeriod == GoalTimePeriod.INSTANT) {
                    final HealthEntryDto last = getLast(glucoseDtos);
                    if (last != null) {
                        compareValue = convertToGoalUnits(units, last, entryType);
                    }
                } else if (timePeriod == GoalTimePeriod.DAY) {
                    final OptionalDouble optionalAverage = glucoseDtos.stream()
                            .filter(f -> isWithin24Hours(f.getDate()))
                            .map(f -> convertToGoalUnits(units, f, entryType))
                            .mapToDouble(Double::doubleValue).average();
                    if (optionalAverage.isPresent()) {
                        compareValue = optionalAverage.getAsDouble();
                    }
                } else if (timePeriod == GoalTimePeriod.WEEK) {
                    final OptionalDouble optionalAverage = glucoseDtos.stream()
                            .filter(f -> isWithin7Days(f.getDate()))
                            .collect(Collectors.groupingBy(f -> toDaily(f.getDate()), Collectors.averagingDouble(m -> convertToGoalUnits(units, m, entryType))))
                            .values().stream().mapToDouble(Double::doubleValue).average();
                    if (optionalAverage.isPresent()) {
                        compareValue = optionalAverage.getAsDouble();
                    }
                }
            } else if (entryType == GoalEntryType.WEIGHT) {
                final List<HealthEntryDto> glucoseDtos = healthEntries.getOrDefault(HealthEntryType.WEIGHT, new ArrayList<>());
                if (timePeriod == GoalTimePeriod.INSTANT) {
                    final HealthEntryDto last = getLast(glucoseDtos);
                    if (last != null) {
                        compareValue = convertToGoalUnits(units, last, entryType);
                    }
                } else if (timePeriod == GoalTimePeriod.DAY) {
                    final OptionalDouble optionalAverage = glucoseDtos.stream()
                            .filter(f -> isWithin24Hours(f.getDate()))
                            .map(f -> convertToGoalUnits(units, f, entryType))
                            .mapToDouble(Double::doubleValue).average();
                    if (optionalAverage.isPresent()) {
                        compareValue = optionalAverage.getAsDouble();
                    }
                } else if (timePeriod == GoalTimePeriod.WEEK) {
                    final OptionalDouble optionalAverage = glucoseDtos.stream()
                            .filter(f -> isWithin7Days(f.getDate()))
                            .collect(Collectors.groupingBy(f -> toDaily(f.getDate()), Collectors.averagingDouble(m -> convertToGoalUnits(units, m, entryType))))
                            .values().stream().mapToDouble(Double::doubleValue).average();
                    if (optionalAverage.isPresent()) {
                        compareValue = optionalAverage.getAsDouble();
                    }
                }
            } else {
                final List<NutrientDetails> nutrientDtos = nutrientDetails.getOrDefault(entryType.toString(), new ArrayList<>());
                if (timePeriod == GoalTimePeriod.INSTANT) {
                    final NutrientDetails last = getLast(nutrientDtos);
                    if (last != null) {
                        compareValue = convertToGoalUnits(units, last, entryType);
                    }
                } else if (timePeriod == GoalTimePeriod.DAY) {
                    final OptionalDouble optionalAverage = nutrientDtos.stream()
                            .filter(f -> isWithin24Hours(f.getConsumptionDate()))
                            .map(f -> convertToGoalUnits(units, f, entryType))
                            .mapToDouble(Double::doubleValue).average();
                    if (optionalAverage.isPresent()) {
                        compareValue = optionalAverage.getAsDouble();
                    }
                } else if (timePeriod == GoalTimePeriod.WEEK) {
                    final OptionalDouble optionalAverage = nutrientDtos.stream()
                            .filter(f -> isWithin7Days(f.getConsumptionDate()))
                            .collect(Collectors.groupingBy(f -> toDaily(f.getConsumptionDate()), Collectors.averagingDouble(m -> convertToGoalUnits(units, m, entryType))))
                            .values().stream().mapToDouble(Double::doubleValue).average();
                    if (optionalAverage.isPresent()) {
                        compareValue = optionalAverage.getAsDouble();
                    }
                }
            }


            if (compareValue != null) {
                switch (a.getGoalType()) {
                    case LESS_THAN:
                        if (compareValue < a.getValue()) {
                            dto.setState(GoalState.MET);
                        } else {
                            dto.setState(GoalState.UNMET);
                        }
                        break;
                    case GREATER_THAN:
                        if (compareValue > a.getValue()) {
                            dto.setState(GoalState.MET);
                        } else {
                            dto.setState(GoalState.UNMET);
                        }
                        break;
                }
            } else {
                dto.setState(GoalState.UNKNOWN);
            }

            dto.setCurrentValue(compareValue);
            goalStates.add(dto);
        }

        return goalStates;
    }

    private <T> T getLast(List<T> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            return arrayList.get(arrayList.size()-1);
        }
        return null;
    }

    private boolean isWithin24Hours(Date testDate) {
        final ZonedDateTime now = ZonedDateTime.now();
        return isWithinDateRange(testDate.toInstant(), now.minusHours(24).toInstant(), now.toInstant());
    }

    private boolean isWithin7Days(Date testDate) {
        final ZonedDateTime now = ZonedDateTime.now();
        return isWithinDateRange(testDate.toInstant(), now.minusDays(7).toInstant(), now.toInstant());
    }

    private boolean isWithinDateRange(Instant testDate, Instant startDate, Instant endDate) {
        return testDate.isAfter(startDate) && testDate.isBefore(endDate);
    }

    private long toDaily(Date date) {
        final ZonedDateTime now = ZonedDateTime.now();
        final long v = ChronoUnit.HOURS.between(date.toInstant(), now) / 24;
        return v;
    }

    private double convertToGoalUnits(String toUnits, HealthEntryDto dto, GoalEntryType goalEntryType) throws IllegalArgumentException {
        final String dtoUnits = dto.getUnits();

        if (dtoUnits.equalsIgnoreCase(toUnits)) {
            return dto.getValue().doubleValue();
        }

        if (goalEntryType == GoalEntryType.GLUCOSE) {
           if (dtoUnits.equalsIgnoreCase("mg/dL")) {
               if (toUnits.equalsIgnoreCase("mmol/L")) {
                    return dto.getValue().divide(BigDecimal.valueOf(18), BigDecimal.ROUND_HALF_UP).doubleValue();
               }
               throw new IllegalArgumentException("Invalid toUnits");
           } else if (dtoUnits.equalsIgnoreCase("mmol/L")) {
               if (toUnits.equalsIgnoreCase("mg/dL")) {
                   return dto.getValue().multiply(BigDecimal.valueOf(18)).doubleValue();
               }
               throw new IllegalArgumentException("Invalid toUnits");
           } else {
               throw new IllegalArgumentException("Invalid toUnits");
           }
        } else if (goalEntryType == GoalEntryType.WEIGHT) {
            if (dtoUnits.equalsIgnoreCase("kg")) {
                if (toUnits.equalsIgnoreCase("lbs")) {
                    return dto.getValue().divide(BigDecimal.valueOf(0.45359237), BigDecimal.ROUND_HALF_UP).doubleValue();
                }
                throw new IllegalArgumentException("Invalid toUnits");
            } else if (dtoUnits.equalsIgnoreCase("lbs")) {
                if (toUnits.equalsIgnoreCase("kg")) {
                    return dto.getValue().multiply(BigDecimal.valueOf(0.45359237)).doubleValue();
                }
                throw new IllegalArgumentException("Invalid toUnits");
            } else {
                throw new IllegalArgumentException("Invalid toUnits");
            }
        } else if (goalEntryType == GoalEntryType.CARBOHYDRATE || goalEntryType == GoalEntryType.FAT
                || goalEntryType == GoalEntryType.FIBER || goalEntryType == GoalEntryType.IRON
                || goalEntryType == GoalEntryType.MAGNESIUM || goalEntryType == GoalEntryType.POTASSIUM
                || goalEntryType == GoalEntryType.SUGAR) {
            if (dtoUnits.equalsIgnoreCase("g")) {
                if (toUnits.equalsIgnoreCase("mg")) {
                    return dto.getValue().multiply(BigDecimal.valueOf(1000)).doubleValue();
                }
                throw new IllegalArgumentException("Invalid toUnits");
            } else if (dtoUnits.equalsIgnoreCase("mg")) {
                if (toUnits.equalsIgnoreCase("g")) {
                    return dto.getValue().divide(BigDecimal.valueOf(1000), BigDecimal.ROUND_HALF_UP).doubleValue();
                }
                throw new IllegalArgumentException("Invalid toUnits");
            } else {
                throw new IllegalArgumentException("Invalid toUnits");
            }
        }

        throw new IllegalArgumentException("Invalid goal entry type");
    }

    private double convertToGoalUnits(String toUnits, NutrientDetails dto, GoalEntryType goalEntryType) throws IllegalArgumentException {
        final String dtoUnits = dto.getUnits();

        if (dtoUnits.equalsIgnoreCase(toUnits)) {
            return dto.getValue() * dto.getServings();
        }

        if (goalEntryType == GoalEntryType.CARBOHYDRATE || goalEntryType == GoalEntryType.FAT
                || goalEntryType == GoalEntryType.FIBER || goalEntryType == GoalEntryType.IRON
                || goalEntryType == GoalEntryType.MAGNESIUM || goalEntryType == GoalEntryType.POTASSIUM
                || goalEntryType == GoalEntryType.SUGAR) {
            if (dtoUnits.equalsIgnoreCase("g")) {
                if (toUnits.equalsIgnoreCase("mg")) {
                    return (dto.getValue() * 1000) * dto.getServings();
                }
                throw new IllegalArgumentException("Invalid toUnits");
            } else if (dtoUnits.equalsIgnoreCase("mg")) {
                if (toUnits.equalsIgnoreCase("g")) {
                    return (dto.getValue() / 1000.0) * dto.getServings();
                }
                throw new IllegalArgumentException("Invalid toUnits");
            } else {
                throw new IllegalArgumentException("Invalid toUnits");
            }
        }

        throw new IllegalArgumentException("Invalid goal entry type");
    }


}