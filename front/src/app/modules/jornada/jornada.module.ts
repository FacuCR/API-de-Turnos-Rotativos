import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CalendarioComponent } from './components/calendario/calendario.component';
import { RouterModule, Routes } from '@angular/router';
import { MbscModule } from '@mobiscroll/angular';

const routes: Routes = [
  {
    path: '',
    component: CalendarioComponent,
  },
];

@NgModule({
  declarations: [CalendarioComponent],
  imports: [CommonModule, RouterModule.forChild(routes), MbscModule],
})
export class JornadaModule {}
