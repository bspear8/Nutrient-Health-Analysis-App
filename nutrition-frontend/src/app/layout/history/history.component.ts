import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FoodService, HealthService } from '../../shared/services';
import { FoodEntry, HealthEntry } from '../../shared/models';


@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class HistoryComponent implements OnInit {
  public searchString: string;
  title: string;
  allHistory: [{
    datetime: string,
    name: string,
    value: string,
    note: string
  }];

  startDate: Date;
  endDate: Date;

  yearFilter: number;

  yearTimeout: any;

  foodHistory: FoodEntry[] = [];
  healthhistory: HealthEntry[] = [];
  private errorMessage: any = '';

  constructor(private healthService: HealthService, private foodService: FoodService) {
    this.getFoodHistory();
    this.getHealthHistory();

    this.title = 'My History';
  }

  ngOnInit() {
  }

  onYearChange(event, dt, col) {
    if (this.yearTimeout) {
      clearTimeout(this.yearTimeout);
    }

    this.yearTimeout = setTimeout(() => {
      dt.filter(event.value, col.field, col.filterMatchMode);
    }, 250);
  }


  getFoodHistory() {
    const previousWeek = new Date();
    previousWeek.setDate(previousWeek.getDate() - 7);

    this.startDate = previousWeek;
    this.endDate = new Date();
    this.foodService.getFoodEntriesForDateRange(this.startDate, this.endDate, false)
      .subscribe(
        food => this.foodHistory = food,
        error => this.errorMessage = <any>error
      );
  }

  getHealthHistory() {
    this.healthService.getHealthEntries()
      .subscribe(
        health => {
          this.healthhistory = health;
        },
        error => this.errorMessage = <any>error
      );

  }

}
