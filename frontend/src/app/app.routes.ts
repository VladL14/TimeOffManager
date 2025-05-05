import { Routes } from '@angular/router';
import { MainMenuComponent } from './main-menu/main-menu.component';
import { UserDashboardComponent } from './dashboards/user-dashboard/user-dashboard.component';
import { ManagerDashboardComponent } from './dashboards/manager-dashboard/manager-dashboard.component';
import { AdminDashboardComponent } from './dashboards/admin-dashboard/admin-dashboard.component';

export const routes: Routes = [
    {
       path: '', component: MainMenuComponent
    },
    {
        path: 'dashboard/user', component: UserDashboardComponent
    },
    {
        path: 'dashboard/manager', component: ManagerDashboardComponent
    },
    {
        path: 'dashboard/admin', component: AdminDashboardComponent
    }
];
