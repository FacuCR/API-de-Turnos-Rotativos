import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CalendarioComponent } from './components/calendario/calendario.component';
import { FormulariosJornadaComponent } from './pages/formularios-jornada/formularios-jornada.component';
import { IndexJornadaComponent } from './pages/index-jornada/index-jornada.component';
import { CrearTurnoComponent } from './components/crear-turno/crear-turno.component';

import { RouterModule, Routes } from '@angular/router';

import { ReactiveFormsModule } from '@angular/forms';

import { MbscModule } from '@mobiscroll/angular';

import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';

const routes: Routes = [
  {
    path: '',
    component: IndexJornadaComponent,
  },
  {
    path: 'formularios',
    component: FormulariosJornadaComponent,
  },
];

@NgModule({
  declarations: [
    CalendarioComponent,
    IndexJornadaComponent,
    FormulariosJornadaComponent,
    CrearTurnoComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MbscModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatInputModule,
    MatIconModule,
    MatProgressBarModule,
    MatButtonToggleModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSelectModule,
  ],
})
export class JornadaModule {}
