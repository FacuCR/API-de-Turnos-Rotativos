import { FormsModule } from '@angular/forms';
import { MbscModule } from '@mobiscroll/angular';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { AuthInterceptorProviders } from './core/interceptor/auth.interceptor';
import { UnauthorizedInterceptorProviders } from './core/interceptor/unauthorized.interceptor';

@NgModule({
  declarations: [AppComponent],
  imports: [
    FormsModule,
    MbscModule,
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
  ],
  providers: [
    [AuthInterceptorProviders],
    [UnauthorizedInterceptorProviders],
    [HttpClient],
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
