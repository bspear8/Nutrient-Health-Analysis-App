package edu.gatech.ihi.nhaa.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.PreferReturnEnum;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import edu.gatech.ihi.nhaa.AppConfig;
import edu.gatech.ihi.nhaa.entity.HealthEntryType;
import edu.gatech.ihi.nhaa.web.dto.HealthEntryDto;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import edu.gatech.ihi.nhaa.entity.User;
import edu.gatech.ihi.nhaa.fhir.FHIRHelper;

@Component
public class FHIRService implements IFHIRService {

    private static final String NHAASystemIdentifier = "nutrient-health-analysis-app";
    private static final String LoincSystemIdentifier = "http://loinc.org";
    private static final String UnitsOfMeasureSystemIdentifier = "http://unitsofmeasure.org";

    private final FHIRHelper fhirHelper;
    private final IGenericClient client;

    @Autowired
    public FHIRService(AppConfig appConfig, FHIRHelper fhirHelper) {
        this.fhirHelper = fhirHelper;
        this.client = getRestClient(appConfig.getFhirServer());
    }

    @Override
    public void addPatientHealthData(User user, HealthEntryDto req) throws Exception {

        Patient patient = getPatient(user);
        if (patient == null) {
            patient = createPatient(user);
        }

        addObservationForPatient(patient, req);
    }

    @Override
    @Transactional
    public void deletePatientHealthData(User user) throws Exception {

        Patient patient = getPatient(user);
        if (patient == null) {
            return;
        }

        deleteObservationsForPatient(patient);
        deletePatient(patient);
    }

    @Override
    @Transactional
    public void deletePatientHealthDataById(User user, String id) throws Exception {
        Patient patient = getPatient(user);
        if (patient == null) {
            return;
        }

        deleteObservationsForPatientById(patient, id);
    }

    @Override
    public List<HealthEntryDto> getPatientHealthData(User user) throws Exception {

        Patient patient = getPatient(user);
        if (patient == null) {
            return new ArrayList<>();
        }

        final List<Observation> observations = getObservationsForPatient(patient);
        final List<HealthEntryDto> healthEntries = new ArrayList<>(observations.size());
        for (Observation observation : observations) {
            healthEntries.add(buildHealthEntry(observation));
        }
        healthEntries.sort(Comparator.comparing(HealthEntryDto::getDate));

        return healthEntries;
    }

    @Override
    public List<HealthEntryDto> getPatientHealthDataByDateRange(User user, Date startDate, Date endDate) throws Exception {
        final List<HealthEntryDto> healthData = getPatientHealthData(user);
        return healthData.stream()
                .filter(h -> h.getDate().compareTo(startDate) >= 0 && h.getDate().compareTo(endDate) <= 0)
                .collect(Collectors.toList());
    }

    private Patient getPatient(User user) throws Exception {
        Bundle bundle = client.search()
                .forResource(Patient.class)
                .where(Patient.IDENTIFIER.exactly().systemAndIdentifier(NHAASystemIdentifier, user.getPatientId()))
                .returnBundle(Bundle.class)
                .cacheControl(new CacheControlDirective().setNoCache(true))
                .execute();

        int total = bundle.getTotal();
        if (total > 1) {
            throw new Exception("Multiple patients found for user");
        } else if (total == 1) {
            BundleEntryComponent entry = bundle.getEntryFirstRep();
            if (entry.getResource() instanceof Patient) {
                return (Patient) entry.getResource();
            }
        }
        return null;
    }

    private Patient createPatient(User user) throws Exception {
        Patient patient = buildPatient(user);
        MethodOutcome outcome = client.create()
                .resource(patient)
                .prefer(PreferReturnEnum.REPRESENTATION)
                .preferResponseType(Patient.class)
                .execute();

        if (outcome.getCreated()) {
            if (outcome.getResource() instanceof Patient) {
                return (Patient) outcome.getResource();
            }
        }
        throw new Exception("Failed to create patient");
    }

    private void deletePatient(Patient patient) throws Exception {
        final IBaseOperationOutcome outcome = client.delete()
                .resourceById(new IdDt("Patient", patient.getIdElement().getIdPart()))
                .execute();
        System.out.println(outcome);
    }

    private List<Observation> getObservationsForPatient(Patient patient) throws Exception {

        final Bundle bundle = client.search()
                .forResource(Observation.class)
                .where(Observation.PATIENT.hasId(patient.getIdElement().getIdPart()))
                .returnBundle(Bundle.class)
                .cacheControl(new CacheControlDirective().setNoCache(true))
                .execute();

        FHIRHelper.fetchRestOfBundle(client, bundle);

        List<Observation> observations = new ArrayList<>();
        for (BundleEntryComponent entry : bundle.getEntry()) {
            if (entry.getResource() instanceof Observation) {
            	Observation obs = (Observation) entry.getResource();
            	obs = fhirHelper.addConvertedValueToObservation(obs);
            	
                observations.add(obs);
            }
        }

        return observations;
    }

    private void deleteObservationsForPatient(Patient patient) {
        final IBaseOperationOutcome outcome = client.delete()
                .resourceConditionalByType(Observation.class)
                .where(Observation.PATIENT.hasId(patient.getIdElement().getIdPart()))
                .execute();
    }

    private void deleteObservationsForPatientById(Patient patient, String id) {
        final IBaseOperationOutcome outcome = client.delete()
                .resourceConditionalByType(Observation.class)
                .where(Observation.PATIENT.hasId(patient.getIdElement().getIdPart()))
                .and(Observation.RES_ID.exactly().identifier(id))
                .execute();
    }

    private Observation addObservationForPatient(Patient patient, HealthEntryDto healthEntryDto) throws Exception {
        final Observation observation = buildObservation(patient, healthEntryDto);
        final MethodOutcome outcome = client.create()
                .resource(observation)
                .prefer(PreferReturnEnum.REPRESENTATION)
                .preferResponseType(Observation.class)
                .cacheControl(new CacheControlDirective().setNoCache(true))
                .execute();

        if (outcome.getCreated()) {
            if (outcome.getResource() instanceof Observation) {
                return (Observation) outcome.getResource();
            }

        }
        throw new Exception("Error creating observation");
    }

    private HealthEntryDto buildHealthEntry(Observation observation) throws Exception {

        HealthEntryDto dto = new HealthEntryDto();
        dto.setId(observation.getIdElement().getIdPart());
        dto.setDate(observation.getEffectiveDateTimeType().getValue());
        dto.setNotes(observation.getComment());

        final Optional<String> coding = observation.getCode()
                .getCoding().stream().filter(f -> f.getSystem().equalsIgnoreCase(LoincSystemIdentifier))
                .map(Coding::getCode)
                .findFirst();

        coding.ifPresent(s -> dto.setHealthEntryType(HealthEntryType.fromString(fhirHelper.translateObservationCode(s))));

        final Quantity value = observation.getValueQuantity();
        dto.setValue(value.getValue());
        dto.setUnits(value.getUnit());

        return dto;
    }

    private Observation buildObservation(Patient patient, HealthEntryDto healthEntryDto) throws Exception {
        Observation observation = new Observation();
        observation.setStatus(ObservationStatus.FINAL);

        String code = fhirHelper.translateObservationName(healthEntryDto.getHealthEntryType());
        String unit = fhirHelper.translateObservationUnit(code, healthEntryDto.getUnits());
        observation
                .getCode()
                .addCoding()
                .setSystem(LoincSystemIdentifier)
                .setCode(code);

        observation.setValue(
                new Quantity()
                        .setValue(healthEntryDto.getValue())
                        .setUnit(unit)
                        .setSystem(UnitsOfMeasureSystemIdentifier));

        observation
                .setEffective(new DateTimeType(healthEntryDto.getDate()))
                .setComment(healthEntryDto.getNotes())
                .setSubject(new Reference(patient.getId()));

        return observation;
    }

    private Patient buildPatient(User user) {
        Patient patient = new Patient();
        patient.addIdentifier()
                .setSystem(NHAASystemIdentifier)
                .setValue(user.getPatientId());
        patient.addName()
                .setFamily(user.getLastName())
                .addGiven(user.getFirstName());
        patient.setId(IdDt.newRandomUuid());
        return patient;
    }

    private IGenericClient getRestClient(String fhirUrl) {
        FhirContext context = FhirContext.forDstu3();
        context.getRestfulClientFactory().setConnectTimeout(20 * 1000);
        context.getRestfulClientFactory().setSocketTimeout(20 * 1000);
        context.getRestfulClientFactory().setConnectionRequestTimeout(20 * 1000);

        final IGenericClient client = context.newRestfulGenericClient(fhirUrl);

        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
        loggingInterceptor.setLogRequestSummary(true);
        loggingInterceptor.setLogRequestHeaders(true);
        loggingInterceptor.setLogRequestBody(true);

        client.registerInterceptor(loggingInterceptor);

        return client;
    }

}
