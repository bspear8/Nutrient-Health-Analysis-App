import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HealthEntryComponent } from './health-entry.component';

const routes: Routes = [
    { path: '', component: HealthEntryComponent }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class HealthEntryRoutingModule { }
