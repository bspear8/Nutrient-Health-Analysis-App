import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './dashboard.component';

import { PanelModule, ChartModule } from 'primeng/primeng';
import { HealthService, FoodService, GoalService } from '../../shared/services';
import { NvD3Module } from 'angular2-nvd3';
// // d3 and nvd3 should be included somewhere
import 'd3';
import 'nvd3';

@NgModule({
  imports: [
    CommonModule,
    DashboardRoutingModule,
    PanelModule,
    ChartModule,
    NvD3Module
  ],
  declarations: [DashboardComponent],
  providers: [HealthService, FoodService, GoalService]
})
export class DashboardModule { }
