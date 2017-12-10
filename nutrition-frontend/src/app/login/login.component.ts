import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService, AlertService } from '../shared/services';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  model: any = {};
  loading = false;
  returnUrl: string;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private authService: AuthService,
              private alertService: AlertService) { }

  ngOnInit() {
    this.authService.logout();
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  login() {
    this.loading = true;
    this.authService.login(this.model.email, this.model.password)
      .subscribe(
        data => {
          this.router.navigate(['/dashboard']);
        },
        (err: HttpErrorResponse) => {
          if (err.error !== null && err.error.apierror !== null) {
            const apierror = err.error.apierror;
            if (apierror.subErrors != null && apierror.subErrors.length > 0) {
              apierror.subErrors.forEach(subError => {
                this.alertService.error(apierror.message + ': ' + subError.message);
              });
            } else {
              this.alertService.error(err.error.apierror.message);
            }
          } else {
            this.alertService.error(err.statusText);
          }
          this.loading = false;
        }
      );
  }
}
