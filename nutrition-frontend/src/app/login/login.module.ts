import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { LoginRoutingModule } from './login-routing.module';
import { LoginComponent } from './login.component';
import { FormsModule } from '@angular/forms';
import { AlertModule } from '../shared';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        LoginRoutingModule,
        AlertModule
    ],
    declarations: [LoginComponent]
})
export class LoginModule {
}
