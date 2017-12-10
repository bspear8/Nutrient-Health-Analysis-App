import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { HttpParams } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { AuthService } from '../auth-service/auth.service';
import { FoodEntry, FoodSearchResult } from '../../models';

@Injectable()
export class FoodService {

  constructor(private http: HttpClient, private authService: AuthService) { }

  findFoods(search: string): Observable<FoodSearchResult[]> {
    const params = new HttpParams().set('name', search);
    return this.http.get<FoodSearchResult[]>('api/foods', { params: params, headers: this.authService.headers() });
  }

  getFoodDetails(nbdno: string): Observable<FoodEntry> {
    return this.http.get<FoodEntry>(`api/foods/${nbdno}`, { headers: this.authService.headers() });
  }

  addFoodEntry(food: FoodEntry): Observable<any> {
    return this.http.post('api/food-entries', food, { headers: this.authService.headers() });
  }

  getFoodEntries(): Observable<FoodEntry[]> {
    return this.http.get<FoodEntry[]>('api/food-entries', { headers: this.authService.headers() })
      .map((entries: FoodEntry[]) => {
        return entries.map((f: FoodEntry) => {
          f.consumptionDate = new Date(f.consumptionDate);
          return f;
        });
      });
  }

  getFoodEntriesForDateRange(startDate: Date, endDate: Date, unifyUnits: Boolean): Observable<FoodEntry[]> {
    let params = new HttpParams();
    params = params.append('startDate', startDate.toISOString());
    params = params.append('endDate', endDate.toISOString());
    params = params.append('unifyUnits', unifyUnits.toString());

    return this.http.get<FoodEntry[]>('api/food-entries', { params: params, headers: this.authService.headers() })
      .map((entries: FoodEntry[]) => {
        return entries.map((f: FoodEntry) => {
          f.consumptionDate = new Date(f.consumptionDate);
          return f;
        });
      });
  }
}
