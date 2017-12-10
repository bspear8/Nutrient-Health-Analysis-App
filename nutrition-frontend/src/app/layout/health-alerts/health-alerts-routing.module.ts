import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HealthAlertsComponent } from './health-alerts.component';

const routes: Routes = [
    { path: '', component: HealthAlertsComponent }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class HealthAlertsRoutingModule { }
