import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HealthGoalsRoutingModule } from './health-goals-routing.module';
import { HealthGoalsComponent } from './health-goals.component';

import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { GoalService } from '../../shared/services';
import { SharedPipesModule, PageHeaderModule } from '../../shared/index';

import { PanelModule, SplitButtonModule, CalendarModule,
        InputTextareaModule, MessagesModule, MessageModule,
        Message } from 'primeng/primeng';

@NgModule({
  imports: [
    CommonModule,
    PanelModule,
    SplitButtonModule,
    CalendarModule,
    InputTextareaModule,
    HealthGoalsRoutingModule,
    SharedPipesModule,
    MessagesModule,
    MessageModule,
    PageHeaderModule
  ],
  declarations: [HealthGoalsComponent],
  providers: [GoalService]
})
export class HealthGoalsModule { }
