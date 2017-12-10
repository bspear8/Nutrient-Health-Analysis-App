import { NgModule, ErrorHandler } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AuthErrorHandler, ErrorInterceptor } from '../../app.errorhandler';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

@NgModule({
    exports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule
    ],
    declarations: [],
    providers: [
        {
            provide: ErrorHandler,
            useClass: AuthErrorHandler
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorInterceptor,
            multi: true
        },
    ]
})
export class SharedPipesModule { }
