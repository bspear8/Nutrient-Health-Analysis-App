import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HealthAlertsRoutingModule } from './health-alerts-routing.module';
import { HealthAlertsComponent } from './health-alerts.component';
import { GoalService } from '../../shared/services';
import { SharedPipesModule, PageHeaderModule } from '../../shared';

@NgModule({
  imports: [
    SharedPipesModule,
    HealthAlertsRoutingModule,
    PageHeaderModule
  ],
  declarations: [HealthAlertsComponent],
  providers: [GoalService]
})
export class HealthAlertsModule { }
