import { Injectable } from '@angular/core';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import * as jwt_decode from 'jwt-decode';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from '../../models';

export const TOKEN_NAME = 'jwt';

@Injectable()
export class AuthService {

  constructor(private http: HttpClient) { }

  getToken(): string {
    return localStorage.getItem(TOKEN_NAME);
  }

  setToken(token: string): void {
    localStorage.setItem(TOKEN_NAME, token);
  }

  getTokenExpirationDate(token: string): Date {
    const decoded = jwt_decode(token);

    if (decoded.exp === undefined) {
       return null;
    }

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }

  isTokenExpired(token?: string): boolean {
    if (!token) {
      token = this.getToken();
    }
    if (!token) {
      return true;
    }

    const date = this.getTokenExpirationDate(token);
    if (date === undefined) {
      return false;
    }
    return !(date.valueOf() > new Date().valueOf());
  }

  login(email: string, password: string) {
    const body = JSON.stringify({ email: email, password: password });
    const headers = new HttpHeaders({ 'Content-Type': 'application/json; charset=utf-8' });

    return this.http.post<any>('api/auth/login', body, { headers: headers })
      .map(data => {
        this.setToken(data.jwtToken);
      });
  }

  register(user: User): Observable<any> {
    return this.http.post('api/auth/register', user, { headers: this.headers(false) });
  }

  updateProfile(user: User): Observable<any> {
    return this.http.post('api/auth/updateProfile', user, { headers: this.headers() });
  }

  logout() {
    localStorage.removeItem(TOKEN_NAME);
  }

  headers(includeJwt: boolean = true): HttpHeaders {
    const jwt = this.getToken();
    let headers = new HttpHeaders({ 'Content-Type': 'application/json; charset=utf-8' });

    if (jwt && includeJwt) {
      headers = headers.append('Authorization', 'Bearer ' + jwt);
    }

    return headers;
  }
}
