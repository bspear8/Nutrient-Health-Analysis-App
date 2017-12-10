import { NgModule } from '@angular/core';

import { SharedPipesModule, PageHeaderModule } from '../../shared';
import { ProfileComponent } from './profile.component';
import { ProfileRoutingModule } from './profile-routing.module';
import { UserService } from '../../shared/services/index';

@NgModule({
  imports: [
    SharedPipesModule,
    ProfileRoutingModule,
    PageHeaderModule
  ],
  declarations: [ProfileComponent],
  providers: [UserService]
})
export class ProfileModule { }
