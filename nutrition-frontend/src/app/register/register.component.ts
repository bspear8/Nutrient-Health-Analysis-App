import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AlertService, AuthService } from '../shared/services';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  model: any = {};
  loading = false;

  constructor(private router: Router,
              private authService: AuthService,
              private alertService: AlertService) { }

  ngOnInit() {
  }

  register() {
    this.loading = true;
    this.authService.register(this.model)
      .subscribe(
        data => {
          this.alertService.success('Registration successful', true);
          this.router.navigate(['/login']);
        },
        err => {
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
