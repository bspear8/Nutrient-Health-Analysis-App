package edu.gatech.ihi.nhaa.service;

import edu.gatech.ihi.nhaa.common.NutrientDetails;
import edu.gatech.ihi.nhaa.entity.*;
import edu.gatech.ihi.nhaa.repository.AlertRepository;
import edu.gatech.ihi.nhaa.web.dto.AlertDto;
import edu.gatech.ihi.nhaa.web.dto.AlertStateDto;
import edu.gatech.ihi.nhaa.web.dto.GoalStateDto;
import edu.gatech.ihi.nhaa.web.dto.HealthEntryDto;

import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AlertService implements IAlertService {

    private final AlertRepository alertRepository;
    private final FoodEntryService foodEntryService;
    private final FHIRService fhirService;

    public AlertService(AlertRepository alertRepository, FoodEntryService foodEntryService, FHIRService fhirService) {
        this.alertRepository = alertRepository;
        this.foodEntryService = foodEntryService;
        this.fhirService = fhirService;
    }

    @Override
    public List<Alert> getAllAlertsByUser(User user) throws Exception {
        List<Alert> alert;
        try {
            alert = this.alertRepository.findAlertsByUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while retrieving alerts: " + e.getMessage());
        }
        return alert;
    }

    @Override
    public Alert getAlertById(User user, long alertId) throws Exception {
        Alert alert;
        try {
            alert = this.alertRepository.findAlertByUserAndAlertId(user, alertId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while retrieving alert: " + e.getMessage());
        }
        return alert;
    }

    @Override
    public Alert addAlert(User user, AlertDto req) throws Exception {
        try {
            Alert alert = new Alert();

            GoalEntryType goalEntryType = GoalEntryType.fromString(req.getEntryType());
            if (goalEntryType == null) {
                throw new IllegalArgumentException("Invalid health entry type");
            }
            alert.setEntryType(goalEntryType);

            GoalRangeType healthAlertType = GoalRangeType.fromString(req.getAlertType());
            if (healthAlertType == null) {
                throw new IllegalArgumentException("Invalid health alert type");
            }
            alert.setAlertType(healthAlertType);

            alert.setValue(req.getValue());
            alert.setUnits(req.getUnits());

            GoalTimePeriod timePeriod = GoalTimePeriod.fromString(req.getTimePeriod());
            if (timePeriod == null) {
                throw new IllegalArgumentException("Invalid time period");
            }
            alert.setTimePeriod(timePeriod);

            alert.setUser(user);
            this.alertRepository.save(alert);

            return alert;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while adding alert: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteAlertById(User user, long alertId) throws Exception {
        try {
            Alert alert = this.alertRepository.findAlertByUserAndAlertId(user, alertId);
            this.alertRepository.delete(alert);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while deleting alert: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateAlert(User user, long alertId, AlertDto req) throws Exception {
        try {
            Alert alert = this.alertRepository.findAlertByUserAndAlertId(user, alertId);

            GoalEntryType goalEntryType = GoalEntryType.fromString(req.getEntryType());
            if (goalEntryType == null) {
                throw new IllegalArgumentException("Invalid health entry type");
            }
            alert.setEntryType(goalEntryType);

            GoalRangeType healthAlertType = GoalRangeType.fromString(req.getAlertType());
            if (healthAlertType == null) {
                throw new IllegalArgumentException("Invalid health alert type");
            }
            alert.setAlertType(healthAlertType);

            alert.setValue(req.getValue());
            alert.setUnits(req.getUnits());

            GoalTimePeriod timePeriod = GoalTimePeriod.fromString(req.getTimePeriod());
            if (timePeriod == null) {
                throw new IllegalArgumentException("Invalid time period");
            }
            alert.setTimePeriod(timePeriod);

            alertRepository.save(alert);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while updating alert: " + e.getMessage());
        }
    }

    @Override
    public List<AlertStateDto> getAlertStatesByUser(User user) throws Exception {
        List<Alert> alerts = alertRepository.findAlertsByUser(user);
        List<AlertStateDto> alertStates = new ArrayList<>();

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


        for (Alert a : alerts) {
            AlertStateDto dto = new AlertStateDto();
            dto.setAlert(a);

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
                switch (a.getAlertType()) {
                    case LESS_THAN:
                        if (compareValue < a.getValue()) {
                            dto.setState(AlertState.OUT_OF_RANGE);
                        } else {
                            dto.setState(AlertState.ACCEPTABLE_RANGE);
                        }
                        break;
                    case GREATER_THAN:
                        if (compareValue > a.getValue()) {
                            dto.setState(AlertState.OUT_OF_RANGE);
                        } else {
                            dto.setState(AlertState.ACCEPTABLE_RANGE);
                        }
                        break;
                }
            } else {
                dto.setState(AlertState.UNKNOWN);
            }
            dto.setCurrentValue(compareValue);
            alertStates.add(dto);
        }

        return alertStates;
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
        return ChronoUnit.HOURS.between(date.toInstant(), now) / 24;
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
