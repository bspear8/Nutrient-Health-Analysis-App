import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { FoodEntryComponent } from './food-entry.component';

const routes: Routes = [
    { path: '', component: FoodEntryComponent }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class FoodEntryRoutingModule { }
