import { NgModule } from '@angular/core';
import { HealthEntryRoutingModule } from './health-entry-routing.module';
import { HealthEntryComponent } from './health-entry.component';
import { SharedPipesModule, PageHeaderModule } from '../../shared';
import { CalendarModule } from 'primeng/primeng';
import { HealthService } from '../../shared/services';

@NgModule({
  imports: [
    SharedPipesModule,
    HealthEntryRoutingModule,
    CalendarModule,
    PageHeaderModule
  ],
  declarations: [HealthEntryComponent],
  providers: [HealthService]
})
export class HealthEntryModule { }
