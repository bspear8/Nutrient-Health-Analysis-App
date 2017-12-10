import { Component, OnInit } from '@angular/core';
import { UserService, AlertService } from '../../shared/services';
import { error } from 'selenium-webdriver';
import { User } from '../../shared/models';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  user: User = new User();

  constructor(private userService: UserService, private alertService: AlertService) {
    this.userService.getProfile()
      .subscribe(data => {
        this.user = data;
      }, err => {
        this.alertService.error(err);
      });
  }

  ngOnInit() {
  }

  save() {
    this.userService.updateProfile(this.user)
      .subscribe(data => {
        this.alertService.success('Updated Profile');
      }, err => {
        this.alertService.error(err);
      });
  }

}
