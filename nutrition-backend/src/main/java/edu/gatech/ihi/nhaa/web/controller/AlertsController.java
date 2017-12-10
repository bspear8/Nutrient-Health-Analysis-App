package edu.gatech.ihi.nhaa.web.controller;

import edu.gatech.ihi.nhaa.entity.Alert;
import edu.gatech.ihi.nhaa.entity.User;
import edu.gatech.ihi.nhaa.security.AuthenticatedUser;
import edu.gatech.ihi.nhaa.service.IAlertService;
import edu.gatech.ihi.nhaa.web.dto.AlertDto;
import edu.gatech.ihi.nhaa.web.dto.AlertStateDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AlertsController {

    private final IAlertService alertService;

    @Autowired
    public AlertsController(IAlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping(value = "/alerts", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Alert>> getAllAlerts(@AuthenticatedUser User user) throws Exception {
        List<Alert> resp = alertService.getAllAlertsByUser(user);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
    
    @GetMapping(value = "/alerts/states", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AlertStateDto>> getAlertStates(@AuthenticatedUser User user) throws Exception {
        List<AlertStateDto> resp = alertService.getAlertStatesByUser(user);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(value = "/alerts/{alertId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Alert> getAlertById(@AuthenticatedUser User user,
                                              @PathVariable("alertId") long alertId) throws Exception {
        Alert resp = alertService.getAlertById(user, alertId);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PostMapping(value = "/alerts", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AlertDto> addFoodEntry(@AuthenticatedUser User user, @RequestBody AlertDto req) throws Exception {
        alertService.addAlert(user, req);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/alerts/{alertId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AlertDto> updateFoodEntry(@AuthenticatedUser User user,
                                                 @PathVariable("alertId") long alertId, @RequestBody AlertDto req) throws Exception {
        alertService.updateAlert(user, alertId, req);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/alerts/{alertId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteFoodEntry(@AuthenticatedUser User user, @PathVariable("alertId") long alertId) throws Exception {
        alertService.deleteAlertById(user, alertId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
