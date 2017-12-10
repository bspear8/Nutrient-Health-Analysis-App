package edu.gatech.ihi.nhaa.service;

import edu.gatech.ihi.nhaa.entity.Alert;
import edu.gatech.ihi.nhaa.entity.User;
import edu.gatech.ihi.nhaa.web.dto.AlertDto;
import edu.gatech.ihi.nhaa.web.dto.AlertStateDto;

import java.util.List;

public interface IAlertService {
    List<Alert> getAllAlertsByUser(User user) throws Exception;

    Alert getAlertById(User user, long alertId) throws Exception;

    Alert addAlert(User user, AlertDto req) throws Exception;

    void deleteAlertById(User user, long alertId) throws Exception;
    
    List<AlertStateDto> getAlertStatesByUser(User user) throws Exception;

    void updateAlert(User user, long alertId, AlertDto req) throws Exception;
}
