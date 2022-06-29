import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CalendarioComponent } from './components/calendario/calendario.component';
import { RouterModule, Routes } from '@angular/router';
import { MbscModule } from '@mobiscroll/angular';
import { PageComponent } from './page/page.component';
import { MatProgressBarModule } from '@angular/material/progress-bar';

const routes: Routes = [
  {
    path: '',
    component: PageComponent,
  },
];

@NgModule({
  declarations: [CalendarioComponent, PageComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MbscModule,
    MatProgressBarModule,
  ],
})
export class JornadaModule {}
