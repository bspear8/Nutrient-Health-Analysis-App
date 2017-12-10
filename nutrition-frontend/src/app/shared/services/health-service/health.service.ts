import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { HttpParams } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { AuthService } from '../auth-service/auth.service';
import { HealthEntry } from '../../models';
import 'rxjs/add/observable/of';

@Injectable()
export class HealthService {

  constructor(private http: HttpClient, private authService: AuthService) { }

  addHealthEntry(health: HealthEntry): Observable<any> {
    return this.http.post('api/health-entries', health, { headers: this.authService.headers() });
  }

  getHealthEntries(): Observable<HealthEntry[]> {
    return this.http.get<HealthEntry[]>('api/health-entries', { headers: this.authService.headers() })
    .map((entries: HealthEntry[]) => {
      return entries.map((f: HealthEntry) => {
        f.date = new Date(f.date);
        return f;
      });
    });
  }

  getHealthEntriesForDateRange(startDate: Date, endDate: Date): Observable<any> {
    let params = new HttpParams();
    params = params.append('startDate', startDate.toISOString());
    params = params.append('endDate', endDate.toISOString());

    return this.http.get<HealthEntry[]>('api/health-entries', { params: params, headers: this.authService.headers() })
    .map((entries: HealthEntry[]) => {
      return entries.map((f: HealthEntry) => {
        f.date = new Date(f.date);
        return f;
      });
    });
  }
}
