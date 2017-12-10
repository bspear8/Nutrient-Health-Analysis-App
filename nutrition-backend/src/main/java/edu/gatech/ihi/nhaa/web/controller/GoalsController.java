package edu.gatech.ihi.nhaa.web.controller;

import java.util.Date;
import java.util.List;

import edu.gatech.ihi.nhaa.entity.User;
import edu.gatech.ihi.nhaa.security.AuthenticatedUser;
import edu.gatech.ihi.nhaa.service.IGoalService;
import edu.gatech.ihi.nhaa.web.dto.GoalStateDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.gatech.ihi.nhaa.entity.Goal;
import edu.gatech.ihi.nhaa.service.GoalService;

import static edu.gatech.ihi.nhaa.common.CommonUtil.ISODateFormat;

@RestController
public class GoalsController {

	private final IGoalService goalService;

	@Autowired
    public GoalsController(IGoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping(value = "/goals", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Goal>> getAllGoals(@AuthenticatedUser User user) throws Exception {
        List<Goal> resp = goalService.getAllGoalsByUser(user);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
    
    @GetMapping(value = "/goals/states", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<GoalStateDto>> getAllGoalsState(@AuthenticatedUser User user) throws Exception {
        List<GoalStateDto> resp = goalService.getGoalStatesByUser(user);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(value = "/goals/{goalId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Goal> getGoalByID(@AuthenticatedUser User user, @PathVariable("goalId") long id) throws Exception {
        Goal resp = goalService.getGoalById(user, id);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping(value = "/goals", params = "date", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Goal>> getGoalByDate(@AuthenticatedUser User user,
                                                    @PathVariable("date") @DateTimeFormat(pattern=ISODateFormat) Date date)
            throws Exception {
        List<Goal> resp = goalService.getGoalsByDate(user, date);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PostMapping(value = "/goals", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Goal> addGoal(@AuthenticatedUser User user, @RequestBody Goal goal) throws Exception {
    	System.out.println("Goal ID on edit was: "+goal.getGoalId());
        goalService.addNewGoal(user, goal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/goals/{goalId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Goal> updateGoal(@AuthenticatedUser User user,
                                           @PathVariable long goalId, @RequestBody Goal goal) throws Exception {
	    goalService.updateGoal(user, goalId, goal);
	    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/goals/{goalId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Goal> deleteByGoalId(@AuthenticatedUser User user,
                                               @PathVariable("goalId") long goalId) throws Exception {
	    goalService.deleteGoalById(user, goalId);
	    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
