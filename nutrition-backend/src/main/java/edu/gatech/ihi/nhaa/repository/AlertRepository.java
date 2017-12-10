package edu.gatech.ihi.nhaa.repository;

import edu.gatech.ihi.nhaa.entity.Alert;
import edu.gatech.ihi.nhaa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAlertsByUser(User user);
    Alert findAlertByUserAndAlertId(User user, long alertId);

}
