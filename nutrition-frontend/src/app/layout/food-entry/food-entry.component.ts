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
import { FoodEntry, Nutrient, NutrientType, FoodSearchResult } from '../../shared/models';
import { FormControl } from '@angular/forms';
import { FoodService, AlertService } from '../../shared/services';
import { HttpResponseBase } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http/src/response';

@Component({
  selector: 'app-food-entry',
  templateUrl: './food-entry.component.html',
  styleUrls: ['./food-entry.component.scss']
})
export class FoodEntryComponent implements OnInit {

  private food: FoodEntry;
  step = 1;
  private searchingForAFood = false;
  private loading = false;
  private searchField: FormControl;
  private results: Observable<FoodSearchResult[]>;
  private availableNutrients: NutrientType[];

  constructor(private foodService: FoodService, private alertService: AlertService) {}

  ngOnInit() {
    this.searchField = new FormControl();
    this.results = this.searchField.valueChanges
      .debounceTime(400)
      .distinctUntilChanged()
      .do(() => this.loading = true)
      .switchMap(term => this.searchForFood(term))
      .do(() => this.loading = false);

    this.availableNutrients = ['Carbohydrate', 'Fat', 'Fiber', 'Iron', 'Magnesium', 'Potassium', 'Sugar'];
  }

  private searchForFood(term: string): Observable<FoodSearchResult[]> {
    return this.foodService.findFoods(term)
      .catch((err: HttpErrorResponse) => {
        this.alertService.error(err.error.error);
        return Observable.of<FoodSearchResult[]>();
      });
  }

  addFoodManually() {
    this.searchingForAFood = false;
    this.food = new FoodEntry();
    this.food.nutrients = new Array<Nutrient>();
    for (const nut of this.availableNutrients) {
      const newNut = new Nutrient(nut);
      this.food.nutrients.push(newNut);
    }
    this.step = 3;
  }

  searchForAFood() {
    this.searchingForAFood = true;
    this.step = 2;
  }

  selectFood(selectedFood: FoodSearchResult) {
    this.step = 3;
    this.loading = true;

    this.foodService.getFoodDetails(selectedFood.nbdno)
    .subscribe(foodDetails => {
      this.food = new FoodEntry();
      this.food.foodId = foodDetails.foodId;
      this.food.name = foodDetails.name;
      this.food.nutrients = new Array<Nutrient>();
      for (const nut of this.availableNutrients) {
        const foundNut = foodDetails.nutrients.find(f => f.name === nut);
        if (foundNut) {
          const newNut = new Nutrient(nut, foundNut.value, foundNut.units);
          this.food.nutrients.push(newNut);
        } else {
          const newNut = new Nutrient(nut);
          this.food.nutrients.push(newNut);
        }
      }
      this.loading = false;
     });
  }

  addFoodEntry() {
    this.foodService.addFoodEntry(this.food)
    .subscribe(data => {
      this.alertService.success('Added food');
      this.resetState();
    });
  }

  back() {
    if (this.step === 2) {
      this.resetState();
    } else if (this.step === 3) {
      if (this.searchingForAFood) {
        this.step = 2;
      } else {
        this.step = 1;
      }
    }
  }

  private resetState() {
    this.step = 1;
    this.searchingForAFood = false;
    this.food = null;
    this.searchField.setValue('');
  }
}
