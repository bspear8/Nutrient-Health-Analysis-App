import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { HttpParams } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { AuthService } from '../auth-service/auth.service';
import { User } from '../../models';
import 'rxjs/add/observable/of';

@Injectable()
export class UserService {

  constructor(private http: HttpClient, private authService: AuthService) { }

  updateProfile(user: User): Observable<any> {
    return this.http.patch('api/user', user, { headers: this.authService.headers() });
  }

  getProfile(): Observable<User> {
    return this.http.get<User>('api/user', { headers: this.authService.headers() });
  }

}
