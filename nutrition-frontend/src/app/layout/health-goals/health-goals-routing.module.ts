import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HealthGoalsComponent } from './health-goals.component';

const routes: Routes = [
    { path: '', component: HealthGoalsComponent }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class HealthGoalsRoutingModule { }
