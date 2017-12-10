import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';
import { HttpHeaders } from '@angular/common/http';
import { AuthService } from '../auth-service/auth.service';
import { Goal, HealthAlert, GoalState, HealthAlertState } from '../../models';

@Injectable()
export class GoalService {

  constructor(private http: HttpClient,
    private authService: AuthService) {
  }

  addGoal(goal: Goal): Observable<any> {
    return this.http
      .post('api/goals', goal, { headers: this.authService.headers() })
      .catch(this.handleError);
  }

  getAllGoals(): Observable<Goal[]> {
    return this.http.get<Goal[]>('api/goals', { headers: this.authService.headers() })
    .map((entries: Goal[]) => {
      return entries.map((f: Goal) => {
        f.date = new Date(f.date);
        return f;
      });
    });
  }

  getAllGoalsStates(): Observable<GoalState[]> {
    return this.http.get<GoalState[]>('api/goals/states', { headers: this.authService.headers() })
    .map((entries: GoalState[]) => {
      return entries.map((f: GoalState) => {
        f.goal.date = new Date(f.goal.date);
        return f;
      });
    });
  }

  deleteGoal(goal: Goal): Observable<any> {
    return this.http.delete<any>(`api/goals/${goal.goalId}`, { headers: this.authService.headers() });
  }

  updateGoal(goal: Goal): Observable<any> {
    return this.http.patch<any>(`api/goals/${goal.goalId}`, goal, { headers: this.authService.headers() });
  }

  getHealthAlerts(): Observable<HealthAlert[]> {
    return this.http.get<HealthAlert[]>('api/alerts', { headers: this.authService.headers() });
  }

  getHealthAlertsStates(): Observable<HealthAlertState[]> {
    return this.http.get<HealthAlertState[]>('api/alerts/states', { headers: this.authService.headers() });
  }

  addHealthAlert(alert: HealthAlert): Observable<any> {
    return this.http.post('api/alerts', alert, { headers: this.authService.headers() });
  }

  updateHealthAlert(alert: HealthAlert): Observable<any> {
    return this.http.patch<any>(`api/alerts/${alert.alertId}`, alert, { headers: this.authService.headers() });
  }

  deleteHealthAlert(alert: HealthAlert): Observable<any> {
    return this.http.delete<any>(`api/alerts/${alert.alertId}`, { headers: this.authService.headers() });
  }

  private handleError(error: any) {
    // In a real world app, we might use a remote logging infrastructure
    // We'd also dig deeper into the error to get a better message
    const errMsg = (error.message) ? error.message :
      error.status ? `${error.status} - ${error.statusText}` : 'Server error';
    console.error(errMsg); // log to console instead
    return Observable.throw(errMsg);
  }

}

