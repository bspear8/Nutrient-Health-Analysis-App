import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services';

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private router: Router,
              private authService: AuthService) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (!this.authService.isTokenExpired()) {
      return true;
    }

    if (state.url !== '/') {
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
    }

    this.router.navigate(['/login']);
    return false;
  }
}
