import { NgModule } from '@angular/core';

import { DataTableModule, ButtonModule } from 'primeng/primeng';

import { HistoryRoutingModule } from './history-routing.module';
import { HistoryComponent } from './history.component';
import { FilterPipe } from '../../shared/pipes/filter.pipe';
import { SharedPipesModule, PageHeaderModule } from '../../shared';
import { HealthService, FoodService } from '../../shared/services';

@NgModule({
  imports: [
    SharedPipesModule,
    HistoryRoutingModule,
    DataTableModule,
    ButtonModule,
    PageHeaderModule
  ],
  declarations: [HistoryComponent, FilterPipe],
  providers: [HealthService, FoodService]
})
export class HistoryModule { }
