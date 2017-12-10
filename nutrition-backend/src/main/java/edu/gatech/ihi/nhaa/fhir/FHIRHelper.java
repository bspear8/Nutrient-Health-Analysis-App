package edu.gatech.ihi.nhaa.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import edu.gatech.ihi.nhaa.entity.HealthEntryType;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

@Component
public class FHIRHelper {

	public String translateObservationName(HealthEntryType healthEntryType) throws IllegalArgumentException {
	    switch (healthEntryType) {
            case GLUCOSE:
                return "2345-7";
            case WEIGHT:
                return "29463-7";
            default:
                throw new IllegalArgumentException("Unsupported health entry type");
        }
    }
	
	public String translateObservationUnit(String code) {
		String translation = "";
        if(code.toLowerCase().contains("2345-7"))
            translation = "mg/dL";
        if(code.toLowerCase().contains("29463-7"))
            translation = "kg";
        return translation;
	}
	
	public String translateObservationUnit(String code, String unit) throws Exception {
		String translation = "";
        if(code.toLowerCase().contains("2345-7") && unit.toLowerCase().contains("mg"))
            translation = "mg/dL";
        if(code.toLowerCase().contains("2345-7") && unit.toLowerCase().contains("mmol"))
            translation = "mmol/L";
        if(code.toLowerCase().contains("29463-7") && unit.toLowerCase().contains("kg"))
            translation = "kg";
        if(code.toLowerCase().contains("29463-7") && unit.toLowerCase().contains("lb"))
            translation = "lb";
        if(translation.isEmpty())
        	throw new Exception("Measurement unit is an invalid for " + translateObservationCode(code));
        
        return translation;
	}
	
	public String translateObservationCode(String code) {
		String translation = "";
        if(code.toLowerCase().contains("2345-7"))
            translation = "Glucose";
        if(code.toLowerCase().contains("29463-7"))
            translation = "Weight";
        if(code.toLowerCase().contains("883-9"))
            translation = "Blood Type";
        if(code.toLowerCase().contains("39156-5"))
            translation = "BMI";
        return translation;
	}

    public static void fetchRestOfBundle(IGenericClient theClient, Bundle theBundle) {
        // we need to keep track of which resources are already in the bundle so that if other resources (e.g. Practitioner) are _included,
        // we don't end up with multiple copies
        Set<String> resourcesAlreadyAdded = new HashSet<>();
        addInitialUrlsToSet(theBundle, resourcesAlreadyAdded);
        Bundle partialBundle = theBundle;
        for (;;) {
            if (partialBundle.getLink(IBaseBundle.LINK_NEXT) != null) {
                partialBundle = theClient.loadPage().next(partialBundle).execute();
                addAnyResourcesNotAlreadyPresentToBundle(theBundle, partialBundle, resourcesAlreadyAdded);
            } else {
                break;
            }
        }
        // the self and next links for the aggregated bundle aren't really valid anymore, so remove them
        theBundle.getLink().clear();
    }
    
    public Observation addConvertedValueToObservation(Observation observation) throws FHIRException {
    	String unit = observation.getValueQuantity().getUnit();
    	if(!unit.contains("kg") && observation.getCode().getCodingFirstRep().getCode().equals("29463-7")) {
    		BigDecimal convertedValue = observation.getValueQuantity().getValue()
    				.multiply(new BigDecimal(.454)).setScale(2, RoundingMode.HALF_UP);
    		observation.getValueQuantity().setValue(convertedValue).setUnit("kg");
    	}
    	if(!unit.contains("mg/dL") && observation.getCode().getCodingFirstRep().getCode().equals("2345-7")) {
    		BigDecimal convertedValue = observation.getValueQuantity().getValue()
    				.multiply(new BigDecimal(18)).setScale(2, RoundingMode.HALF_UP);
    		observation.getValueQuantity().setValue(convertedValue).setUnit("mg/dL");
    	}
    	return observation;
    }

    private static void addInitialUrlsToSet(Bundle theBundle, Set<String> theResourcesAlreadyAdded) {
        for (Bundle.BundleEntryComponent entry : theBundle.getEntry()) {
            theResourcesAlreadyAdded.add(entry.getFullUrl());
        }
    }

    private static void addAnyResourcesNotAlreadyPresentToBundle(Bundle theAggregatedBundle, Bundle thePartialBundle, Set<String> theResourcesAlreadyAdded) {
        for (Bundle.BundleEntryComponent entry : thePartialBundle.getEntry()) {
            if (!theResourcesAlreadyAdded.contains(entry.getFullUrl())) {
                theResourcesAlreadyAdded.add(entry.getFullUrl());
                theAggregatedBundle.getEntry().add(entry);
            }
        }
    }
    
    
}
