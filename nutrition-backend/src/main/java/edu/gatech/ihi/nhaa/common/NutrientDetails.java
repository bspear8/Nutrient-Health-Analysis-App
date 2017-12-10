package edu.gatech.ihi.nhaa.common;

import edu.gatech.ihi.nhaa.entity.Nutrient;

import java.util.Date;

public class NutrientDetails extends Nutrient {
    private Date consumptionDate;
    private Double servings;

    public NutrientDetails(Date consumptionDate, Nutrient nutrient) {
        this.consumptionDate = consumptionDate;
        this.setUnits(nutrient.getUnits());
        this.setValue(nutrient.getValue());
        this.setName(nutrient.getName());
        this.setNutrientId(nutrient.getNutrientId());
    }

    public Date getConsumptionDate() {
        return consumptionDate;
    }

    public void setConsumptionDate(Date consumptionDate) {
        this.consumptionDate = consumptionDate;
    }

    public Double getServings() {
        return servings;
    }

    public void setServings(Double servings) {
        this.servings = servings;
    }
}