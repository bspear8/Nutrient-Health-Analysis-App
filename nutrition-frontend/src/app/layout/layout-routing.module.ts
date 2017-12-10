import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LayoutComponent } from './layout.component';

const routes: Routes = [
    {
        path: '', component: LayoutComponent,
        children: [
            { path: 'dashboard', loadChildren: './dashboard/dashboard.module#DashboardModule' },
            { path: 'food-entry', loadChildren: './food-entry/food-entry.module#FoodEntryModule' },
            { path: 'health-alerts', loadChildren: './health-alerts/health-alerts.module#HealthAlertsModule' },
            { path: 'health-entry', loadChildren: './health-entry/health-entry.module#HealthEntryModule' },
            { path: 'health-goals', loadChildren: './health-goals/health-goals.module#HealthGoalsModule' },
            { path: 'history', loadChildren: './history/history.module#HistoryModule' },
            { path: 'profile', loadChildren: './profile/profile.module#ProfileModule' }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class LayoutRoutingModule { }
