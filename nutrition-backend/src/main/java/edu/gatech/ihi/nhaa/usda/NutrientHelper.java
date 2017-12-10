package edu.gatech.ihi.nhaa.usda;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import edu.gatech.ihi.nhaa.model.Nutrient;

@Component
public class NutrientHelper {

    public String translateNutritionName(String name) {
        String translation = name;
        if(name.toLowerCase().contains("potassium"))
            translation = "Potassium";
        if(name.toLowerCase().contains("carbohydrate"))
            translation = "Carbohydrate";
        if(name.toLowerCase().contains("fat"))
            translation = "Fat";
        if(name.toLowerCase().contains("fiber"))
            translation = "Fiber";
        if(name.toLowerCase().contains("glucose"))
            translation = "Glucose";
        if(name.toLowerCase().contains("iron"))
            translation = "Iron";
        if(name.toLowerCase().contains("magnesium"))
            translation = "Magnesium";
        if(name.toLowerCase().contains("sugar"))
            translation = "Sugar";
        return translation;
    }

    public NutrientType[] getListOfNutrients() {
        return NutrientType.values();
    }

    public boolean isSupportedNutrient(String nutrient) {
        boolean isSupported = false;

        for(NutrientType obj : getListOfNutrients()) {
            if(nutrient.toUpperCase().contains(obj.name()))
                isSupported = true;
        }
        return isSupported;
    }
    
    public String getNutrientValueByServings(String value, Long servings) {
    	Double valuePerServing = new Double(value);
    	return BigDecimal.valueOf(valuePerServing * servings).setScale(2, RoundingMode.HALF_UP).toString();
    }
    
    public <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
    
    public List<Nutrient> filterNutrientsByName(List<Nutrient> nutrients) throws Exception {
    	List<Nutrient> filteredNutrients = new ArrayList<>();
    	nutrients.forEach(nutrient -> {
        	nutrient.setName(translateNutritionName(nutrient.getName()));
        });
    	filteredNutrients = nutrients.stream().filter(distinctByKey(Nutrient::getName)).collect(Collectors.toList());
    	if(filteredNutrients.isEmpty())
    		throw new Exception("Error while filtering Nutrients list");
    	return filteredNutrients;
    }
}
