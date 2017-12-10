import { Component, OnInit } from '@angular/core';
import { HealthService, FoodService, GoalService, AlertService } from '../../shared/services';
import { HealthEntry, FoodEntry, Goal, HealthAlert, GoalState, HealthAlertState } from '../../shared/models';
declare let d3: any;
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { parse } from 'querystring';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']

})
export class DashboardComponent implements OnInit {

  data: any;
  monthdata: any;
  hourdata: any;
  piedata: any;
  options: any;
  houroptions: any;
  pieoptions: any;
  healthhistory: HealthEntry[] = [];
  healthhistoryPreviousWeek: HealthEntry[] = [];
  foodhistory: FoodEntry[] = [];
  private errorMessage: any = '';
  glucoseData: any[] = [[0], [0], [0], [0], [0], [0], [0]];
  glucoseDataPreviousSunday: any[] = [];
  glucoseDataPreviousMonday: any[] = [];
  glucoseDataPreviousTuesday: any[] = [];
  glucoseDataPreviousWednesday: any[] = [];
  glucoseDataPreviousThursday: any[] = [];
  glucoseDataPreviousFriday: any[] = [];
  glucoseDataPreviousSaturday: any[] = [];
  glucoseDataAvg: any[] = [];
  weightData: any[] = [];
  foodData: any[] = [0.0, 0.0, 0.0];
  private loading = false;
  startDate: Date;
  endDate: Date;
  avgWeight = 0;
  avgGlucose = 0;
  alerts: HealthAlertState[];
  goals: GoalState[];
  mostrecent: FoodEntry;
  fetchedGoals = false;
  fetchedAlerts = false;

  constructor(private healthService: HealthService, private foodService: FoodService,
    private goalService: GoalService, private alertService: AlertService) {

    this.options = {
      title: {
        display: true,
        text: 'My Glucose Entries Averages Daily',
        fontSize: 16
      },
      legend: {
        position: 'bottom'
      }
    };
    this.pieoptions = {
      title: {
        display: true,
        text: 'My Food Intake Over Time',
        fontSize: 16
      },
      legend: {
        position: 'bottom'
      }
    };

    const dateFormat = d3.time.format('%Y-%m-%d %-I:%M %p');
    const xTickFormat = d3.time.format('%Y-%m-%d');

    const now = new Date();
    now.setDate(now.getDate() + 1);
    now.setHours(0, 0, 0, 0);

    const lastWeek = new Date(now);
    lastWeek.setDate(lastWeek.getDate() - 8);
    lastWeek.setHours(0, 0, 0, 0);

    const padding = 100;

    const xScale = d3.time.scale()
      .domain([lastWeek, now])
      .range([padding, 400]);

    this.houroptions = {
      chart: {
        type: 'scatterChart',
        height: 450,
        color: d3.scale.category10().range(),
        scatter: {
          onlyCircles: false
        },
        margin: { left: 100, bottom: 100 },
        size: 20,
        sizeRange: [50, 50],
        showDistX: true,
        showDistY: true,
        tooltip: {
          headerFormatter: function (d) {
            return dateFormat(d);
          }
        },
        reduceXTicks: true,
        duration: 350,
        xAxis: {
          axisLabel: 'Date',
          tickFormat: function (x) { return xTickFormat(new Date(x)); },
          rotateLabels: -45,
          ticks: 9,
          showMaxMin: false,
          tickValues: d3.time.day.range(lastWeek, now, 1)
        },
        xDomain: [lastWeek, now],
        yAxis: {
          axisLabel: 'Glucose (mg/dL)',
          tickFormat: function (d) {
            return d3.format('.02f')(d);
          },
          axisLabelDistance: 5,
          ticks: 10
        },
        yDomain: [0, 240]
      }
    };
  }

  ngOnInit() {
    this.getHealthHistory();
    this.getFoodHistory();
    this.goalService.getHealthAlertsStates()
      .subscribe(alerts => {
        this.alerts = alerts;
        this.fetchedAlerts = true;
      });
    this.goalService.getAllGoalsStates()
      .subscribe(goals => {
        this.goals = goals;
        this.fetchedGoals = true;
      });
  }

  getHealthHistory() {
    this.healthService.getHealthEntries()
      .subscribe(
      health => {
        this.healthhistory = health;
        this.healthhistoryPreviousWeek = this.getHealthHistoryPreviousWeek(health);
        this.processHealthData(this.healthhistory);
        this.processPreviousHealthData(this.healthhistoryPreviousWeek);
      },
      error => {
        this.errorMessage = <any>error;
        this.alertService.error(error.error.message);
      }
      );

  }

  getHealthHistoryPreviousWeek(health: HealthEntry[]): HealthEntry[] {
    const previousWeek = new Date();
    previousWeek.setDate(previousWeek.getDate() - 7);

    this.startDate = previousWeek;
    this.endDate = new Date();
    return health.filter((h: HealthEntry) => h.date >= this.startDate && h.date <= this.endDate);
  }

  private processHealthData(data: HealthEntry[]) {
    const today = new Date();
    for (const entry of data) {
      if (entry.value != null) {
        if (entry.healthEntryType === 'Glucose') {
          const d = new Date(entry.date);
          const n = d.getDay();
          this.glucoseData[n].push(entry.value);
        } else {
          this.weightData.push(entry.value);
        }
      }
    }
    let sum = 0;
    let fullsum = 0;
    let avg = 0;
    for (const entry of this.glucoseData) {
      for (const i in entry) {
        if (entry.hasOwnProperty(i)) {
          sum += entry[i];
          fullsum += entry[i];
        }
      }
      if (sum !== 0) {
        avg = sum / entry.length;
      } else {
        avg = 0;
      }
      this.glucoseDataAvg.push(avg);
      sum = 0;
      avg = 0;
    }
    if (fullsum !== 0) {
      this.avgGlucose = fullsum / this.glucoseData.length;
      this.avgGlucose = Math.floor(this.avgGlucose);
    } else {
      this.avgGlucose = 0;
    }

    for (const entry of this.weightData) {
      sum += entry;
    }
    if (sum !== 0) {
      avg = sum / this.weightData.length;
      this.avgWeight = Math.floor(avg);
    } else {
      this.avgWeight = 0;
    }


    this.data = {
      labels: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'],
      datasets: [
        {
          label: 'Glucose',
          data: this.glucoseDataAvg,
          fill: false,
          borderColor: '#4bc0c0'
        }
      ]
    };
  }

  private processPreviousHealthData(data: HealthEntry[]) {
    const glucose = [];

    for (const entry of data) {
      if (entry.value != null) {
        if (entry.healthEntryType === 'Glucose') {
          glucose.push({ x: new Date(entry.date), y: entry.value, size: 20 });
        }
      }
    }

    this.hourdata = [
      { 'key': 'Glucose', 'values': glucose }
    ];
  }

  getFoodHistory() {
    const previousWeek = new Date();
    previousWeek.setDate(previousWeek.getDate() - 7);

    this.startDate = previousWeek;
    this.endDate = new Date();
    this.foodService.getFoodEntriesForDateRange(this.startDate, this.endDate, true)
      .subscribe(
      history => {
        this.foodhistory = history;
        this.mostrecent = this.foodhistory[this.foodhistory.length - 1];
        this.processFoodData(this.foodhistory);
      },
      error => this.errorMessage = <any>error
      );
  }

  private processFoodData(data: FoodEntry[]) {
    for (const entry of data) {
      for (const i of entry.nutrients) {
        let foodDataIndex: number;
        switch (i.name) {
          case 'Fat':
            foodDataIndex = 0;
            break;
          case 'Carbohydrate':
            foodDataIndex = 1;
            break;
          case 'Sugar':
            foodDataIndex = 2;
            break;
        }

        this.foodData[foodDataIndex] = this.foodData[foodDataIndex] + (i.value * entry.servings);
      }
    }

    let allOver1000 = true;
    for (const idx in this.foodData) {
      if (this.foodData.hasOwnProperty(idx)) {
        if (this.foodData[idx] < 1000) {
          allOver1000 = false;
          break;
        }
      }
    }

    if (allOver1000) {
      for (const idx in this.foodData) {
        if (this.foodData.hasOwnProperty(idx)) {
          this.foodData[idx] = this.foodData[idx] / 1000;
        }
      }
    }

    this.piedata = {
      labels: ['Fat', 'Carbs', 'Sugar'],
      datasets: [
        {
          data: this.foodData,
          backgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56'
          ],
          hoverBackgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56'
          ]
        }]
    };

  }

}
