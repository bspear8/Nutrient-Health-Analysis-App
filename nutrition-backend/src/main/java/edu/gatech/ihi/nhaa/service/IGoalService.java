package edu.gatech.ihi.nhaa.service;

import edu.gatech.ihi.nhaa.entity.Goal;
import edu.gatech.ihi.nhaa.entity.User;
import edu.gatech.ihi.nhaa.web.dto.GoalStateDto;

import java.util.Date;
import java.util.List;

public interface IGoalService {
    void addNewGoal(User user, Goal goal) throws Exception;

    List<Goal> getAllGoalsByUser(User user) throws Exception;

    Goal getGoalById(User user, long goalId) throws Exception;

    List<Goal> getGoalsByDate(User user, Date date) throws Exception;

    void deleteGoalById(User user, long goalId) throws Exception;
    
    List<GoalStateDto> getGoalStatesByUser(User user) throws Exception;

    void updateGoal(User user, long goalId, Goal goal) throws Exception;
}
