import { Injectable } from '@angular/core';
import { Router, NavigationStart } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class AlertService {
  private subject = new Subject<Alert>();
  private keepAfterNavigationChange = false;

  constructor(private router: Router) {
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        if (this.keepAfterNavigationChange) {
          this.keepAfterNavigationChange = false;
        } else {
          this.clear();
        }
      }
    });
   }

   success(message: string, keepAfterNavigationChange = false) {
     this.alert(AlertType.Success, message, keepAfterNavigationChange);
   }

   error(message: string, keepAfterNavigationChange = false) {
     this.alert(AlertType.Error, message, keepAfterNavigationChange);
   }

   info(message: string, keepAfterNavigationChange = false) {
     this.alert(AlertType.Info, message, keepAfterNavigationChange);
   }

   warn(message: string, keepAfterNavigationChange = false) {
     this.alert(AlertType.Warning, message, keepAfterNavigationChange);
   }

   alert(type: AlertType, message: string, keepAfterNavigationChange = false) {
     this.keepAfterNavigationChange = keepAfterNavigationChange;
     this.subject.next(<Alert>{ type: type, message: message });
   }

   getAlert(): Observable<Alert> {
     return this.subject.asObservable();
   }

   clear() {
     this.subject.next();
   }
}

export class Alert {
  type: AlertType;
  message: string;
}

export enum AlertType {
  Success,
  Error,
  Info,
  Warning
}
