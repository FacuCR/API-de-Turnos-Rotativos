import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CalendarioComponent } from './components/calendario/calendario.component';
import { RouterModule, Routes } from '@angular/router';
import { MbscModule } from '@mobiscroll/angular';
import { PageComponent } from './page/page.component';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { CrearTurnoComponent } from './components/crear-turno/crear-turno.component';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';

const routes: Routes = [
  {
    path: '',
    component: PageComponent,
  },
  {
    path: 'crear',
    component: CrearTurnoComponent,
  },
];

@NgModule({
  declarations: [CalendarioComponent, PageComponent, CrearTurnoComponent],
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
