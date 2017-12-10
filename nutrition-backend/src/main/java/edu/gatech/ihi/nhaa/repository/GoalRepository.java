package edu.gatech.ihi.nhaa.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.gatech.ihi.nhaa.entity.Goal;
import edu.gatech.ihi.nhaa.entity.User;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long>{
	
	List<Goal> findByUser(User user);
	
	Goal findByUserAndGoalId(User user, long goalId);

	List<Goal> findByUserAndDate(User user, Date date);

	void deleteByUserAndGoalId(User user, long goalId);

	Goal getOneByUserAndGoalId(User user, long goalId);
}
