import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/switchMap';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/of';
import { Observable } from 'rxjs/Observable';
import { FormControl } from '@angular/forms';
import { GoalService, AlertService } from '../../shared/services';
import { HttpResponseBase } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http/src/response';
import { HealthEntryType, HealthEntry, GoalRangeType, Goal, NutrientType,
         GoalEntryType, GoalState, GoalTimePeriod } from '../../shared/models';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Component({
  selector: 'app-health-goals',
  templateUrl: './health-goals.component.html',
  styleUrls: ['./health-goals.component.scss']
})
export class HealthGoalsComponent implements OnInit {

  goal: Goal;
  // private goals:Observable<Goal[]>;
  goalTitle: string;
  goals: GoalState[] = [];
  activeGoals: Goal[] = [];
  inActiveGoals: Goal[] = [];
  errorMessage: any = '';
  availableEntryTypes: GoalEntryType[] = Goal.getGoalEntryTypes();
  availableGoalRangeTypes: GoalRangeType[] = Goal.getGoalRangeTypes();
  availableGoalTimePeriods: GoalTimePeriod[] = Goal.getGoalTimePeriods();
  availableUnits$: BehaviorSubject<string[]>;
  selectedEntryType: GoalEntryType = null;
  selectedUnits: string;
  loading = true;

  constructor(private goalService: GoalService, private alertService: AlertService) {
  }

  ngOnInit() {
    this.initGoal();
    this.getGoals();
  }

  initGoal() {
    this.goal = new Goal();
    this.goal.entryType = null;
    this.goal.units = null;
    this.goal.goalType = null;
    this.goal.name = '';
    this.goal.date = null;
    this.goal.timePeriod = null;
    this.availableUnits$ = new BehaviorSubject(null);
    this.selectedEntryType = null;
    this.selectedUnits = null;
  }

  onChangeGoalEntryType(goalEntryType: GoalEntryType) {
    this.availableUnits$.next(Goal.getUnitsForType(goalEntryType));
  }

  saveGoal() {
    this.goal.entryType = this.selectedEntryType;

    let processGoal: Observable<any>;
    if (this.goal.goalId) {
      processGoal = this.goalService.updateGoal(this.goal);
    } else {
      processGoal = this.goalService.addGoal(this.goal);
    }

    processGoal.subscribe(
      res => {
        this.alertService.success('Saved health goal');
        this.getGoals();
        this.initGoal();
      },
      err => {
        this.alertService.error(err);
      });
  }

  getGoals() {
    this.loading = true;
    this.goalService.getAllGoalsStates()
    .subscribe(goals => {
      this.goals = goals;
      this.loading = false;
    });
  }

  cancel() {
    this.goal.name = '';
    this.goal.value = 0;
    this.goal.date = null;
  }

  private processGoals(goals) {
    const today = new Date();
    for (const entry of goals) {
      if (entry.date <= today) {
        this.inActiveGoals.push(entry);
      } else {
        this.activeGoals.push(entry);
      }
    }

  }

  compareByStringValue(itemOne, itemTwo) {
    return itemOne && itemTwo && itemOne === itemTwo;
  }

  onChangeHealthGoalType($event) {
  }

  editGoal(goal: Goal) {
    this.onChangeGoalEntryType(goal.entryType);
    this.selectedEntryType = goal.entryType;
    this.selectedUnits = goal.units;
    this.goal = Object.assign({}, goal);
  }

  deleteGoal(goal: Goal) {
    this.goalService.deleteGoal(goal)
      .subscribe(res => {
        this.alertService.success('Deleted goal');
        this.getGoals();
        if (this.goal.goalId === goal.goalId) {
          this.goal.goalId = null;
        }
        this.initGoal();
      }, err => {
        this.alertService.error('Error deleting goal');
        console.log(err);
      });
  }

}
