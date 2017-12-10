import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FoodEntryRoutingModule } from './food-entry-routing.module';
import { FoodEntryComponent } from './food-entry.component';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { FoodService } from '../../shared/services';
import { SharedPipesModule, PageHeaderModule } from '../../shared/index';
import { CalendarModule } from 'primeng/primeng';

@NgModule({
  imports: [
    SharedPipesModule,
    FoodEntryRoutingModule,
    CalendarModule,
    PageHeaderModule
  ],
  declarations: [FoodEntryComponent],
  providers: [FoodService]
})
export class FoodEntryModule { }
