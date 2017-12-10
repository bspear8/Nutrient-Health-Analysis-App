import { Component, OnInit } from '@angular/core';
import { HealthEntryType, HealthEntry, HealthAlert, Goal, GoalRangeType, GoalEntryType,
         HealthAlertState, GoalTimePeriod } from '../../shared/models';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { HealthService, AlertService, GoalService } from '../../shared/services';
import { Observable } from 'rxjs/Observable';

@Component({
  selector: 'app-health-alerts',
  templateUrl: './health-alerts.component.html',
  styleUrls: ['./health-alerts.component.scss']
})
export class HealthAlertsComponent implements OnInit {

  availableEntryTypes: GoalEntryType[] = Goal.getGoalEntryTypes();
  availableAlertRangeTypes: GoalRangeType[] = Goal.getGoalRangeTypes();
  availableAlertTimePeriods: GoalTimePeriod[] = Goal.getGoalTimePeriods();
  availableUnits$: BehaviorSubject<string[]>;
  selectedEntryType: GoalEntryType = null;
  selectedUnits: string;
  alert: HealthAlert;
  alerts: HealthAlertState[];
  loading = true;

  constructor(private goalService: GoalService, private alertService: AlertService) { }

  ngOnInit() {
    this.initAlert();
    this.getAlerts();
  }

  initAlert() {
    this.alert = new HealthAlert();
    this.alert.entryType = null;
    this.alert.units = null;
    this.alert.alertType = null;
    this.alert.timePeriod = null;
    this.availableUnits$ = new BehaviorSubject(null);
    this.selectedEntryType = null;
    this.selectedUnits = null;
  }

  getAlerts() {
    this.loading = true;
    this.goalService.getHealthAlertsStates()
    .subscribe(alerts => {
      this.alerts = alerts;
      this.loading = false;
    });
  }

  onChangeAlertEntryType(goalEntryType: GoalEntryType) {
    this.availableUnits$.next(Goal.getUnitsForType(goalEntryType));
  }

  saveAlert() {
    this.alert.entryType = this.selectedEntryType;

    let processAlert: Observable<any>;
    if (this.alert.alertId) {
      processAlert = this.goalService.updateHealthAlert(this.alert);
    } else {
      processAlert = this.goalService.addHealthAlert(this.alert);
    }
    processAlert.subscribe(
      res => {
        this.alertService.success('Saved health alert');
        this.getAlerts();
        this.initAlert();
      },
      err => {
        this.alertService.error(err);
      });
  }

  editAlert(alert: HealthAlert) {
    this.alert = Object.assign({}, alert);
    this.selectedEntryType = alert.entryType;
    this.onChangeAlertEntryType(alert.entryType);
  }

  deleteAlert(alert: HealthAlert) {
    this.goalService.deleteHealthAlert(alert)
      .subscribe(res => {
        this.alertService.success('Deleted alert successfully');
        if (this.alert.alertId === alert.alertId) {
          this.alert.alertId = null;
        }
        this.getAlerts();
        this.initAlert();
      }, err => {
        this.alertService.error('Error deleting alert');
        console.log(err);
      });
  }

  compareByStringValue(itemOne, itemTwo) {
    return itemOne && itemTwo && itemOne === itemTwo;
  }
}
