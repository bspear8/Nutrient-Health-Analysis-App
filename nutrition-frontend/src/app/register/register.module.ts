import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RegisterRoutingModule } from './register-routing.module';
import { RegisterComponent } from './register.component';
import { FormsModule } from '@angular/forms';
import { AlertModule } from '../shared';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    RegisterRoutingModule,
    AlertModule
  ],
  declarations: [RegisterComponent]
})
export class RegisterModule { }
