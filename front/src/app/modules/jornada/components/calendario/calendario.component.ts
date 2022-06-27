import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import {
  localeEs,
  MbscCalendarEvent,
  MbscEventcalendarOptions,
  setOptions,
} from '@mobiscroll/angular';
import { Empleado } from 'src/app/core/models/Empleado';
import { Turnos } from 'src/app/core/models/Turnos';
import { EmpleadoService } from 'src/app/core/services/empleado/empleado.service';
import { Resource } from '../../models/Resource';
import { TurnoNormal } from '../../models/TurnoNormal';
import { TurnoNormalService } from '../../services/turno-normal.service';

setOptions({
  locale: localeEs,
  theme: 'ios',
  themeVariant: 'light',
});

@Component({
  selector: 'app-calendario',
  templateUrl: './calendario.component.html',
  styleUrls: ['./calendario.component.css'],
})
export class CalendarioComponent implements OnInit {
  myResources: Resource[] = [];
  empleadosActuales: Empleado[] = [];
  turnosNormales: TurnoNormal[] = [];
  cargando: boolean = false;

  constructor(
    private empleados: EmpleadoService,
    private turnoNormalService: TurnoNormalService
  ) {}

  ngOnInit(): void {
    this.empleados
      .getAllEmpleados()
      .subscribe({
        next: (event: any) => {
          this.cargando = true;

          if (event.type === HttpEventType.DownloadProgress) {
            if (Math.round((100 * event.loaded) / event.total) == 100) {
              this.cargando = false;
            }
          }
          this.empleadosActuales = event.body;
          this.empleadosActuales
            ? (this.myResources = this.castearEmpleadosAResource(
                this.empleadosActuales
              ))
            : '';
        },
        error: (e: HttpErrorResponse) => {
          console.log(e.error);
          this.cargando = false;
        },
      })
      .add(() => this.cargarTurnos());
  }

  shifts: MbscCalendarEvent[] = [];

  calendarOptions: MbscEventcalendarOptions = {
    view: {
      timeline: {
        type: 'week',
        eventList: true,
        startDay: 1,
      },
    },
    colors: [
      {
        background: '#a5ceff4d',
        slot: 1,
        recurring: {
          repeat: 'weekly',
          weekDays: 'MO,TU,WE,TH,FR,SA,SU',
        },
      },
      {
        background: '#f7f7bb4d',
        slot: 2,
        recurring: {
          repeat: 'weekly',
          weekDays: 'MO,TU,WE,TH,FR,SA,SU',
        },
      },
      {
        background: '#ce448b44',
        slot: 3,
        recurring: {
          repeat: 'weekly',
          weekDays: 'MO,TU,WE,TH,FR,SA,SU',
        },
      },
    ],
    slots: [
      {
        id: 1,
        name: 'Mañana',
      },
      {
        id: 2,
        name: 'Tarde',
      },
      {
        id: 3,
        name: 'Noche',
      },
    ],
    onEventClick: (args: any) => {
      console.log('click en ' + args.resource);
    },
  };

  backgroundDelTurno(id: number): string {
    if (id === 1) {
      return '#a5ceff4d';
    } else if (id === 2) {
      return '#f7f7bb4d';
    } else {
      return '#ce448b4d';
    }
  }

  castearEmpleadosAResource(empleados: Empleado[]): Resource[] {
    let resource: Resource = new Resource();
    let resources: Resource[] = [];
    empleados.forEach((empleado) => {
      resource.id = empleado.id;
      resource.name = empleado.nombre + ' ' + empleado.apellido;
      resource.user = empleado.username;
      resources.push(resource);
      resource = new Resource();
    });

    return resources;
  }

  cargarTurnos(): void {
    if (this.empleadosActuales) {
      this.empleadosActuales.forEach((empleado) => {
        this.turnoNormalService
          .getAllTurnosNormalesById(empleado.id - 2)
          .subscribe({
            next: (event: any) => {
              this.cargando = true;

              if (event.type === HttpEventType.DownloadProgress) {
                if (Math.round((100 * event.loaded) / event.total) == 100) {
                  this.cargando = false;
                }
              }
              event.body && event.body.length != 0
                ? (this.turnosNormales = event.body)
                : '';
            },
            error: (e: HttpErrorResponse) => {
              console.log(e.error);
              this.cargando = false;
            },
          })
          .add(() => {
            let turnosCorregidos: TurnoNormal[] = [];
            if (this.turnosNormales) {
              for (let i = 0; i < this.turnosNormales.length; i++) {
                let turnoCorregido = new TurnoNormal();
                turnoCorregido.fecha = this.turnosNormales[i].fecha;
                turnoCorregido.cantHoras = this.turnosNormales[i].cantHoras;
                turnoCorregido.turno = this.turnosNormales[i].turno;
                turnoCorregido.idTurnoNormal =
                  this.turnosNormales[i].idTurnoNormal;
                turnoCorregido.usuarioId = this.turnosNormales[i].usuarioId + 1;
                turnosCorregidos.push(turnoCorregido);
              }
              this.turnosNormales = turnosCorregidos;
              this.cargarShifts();
            }
          });
      });
    }
  }

  addDias(fecha: Date, dias: number): Date {
    let result = new Date(fecha);
    result.setDate(result.getDate() + dias);
    return result;
  }

  cargarShifts(): void {
    let slotTurno: number = 1;
    for (let i = 0; i < this.turnosNormales.length; i++) {
      switch (this.turnosNormales[i].turno) {
        case Turnos.maniana:
          slotTurno = 1;
          break;
        case Turnos.tarde:
          slotTurno = 2;
          break;
        case Turnos.noche:
          slotTurno = 3;
          break;
        default:
          slotTurno = 1;
          break;
      }
      let newEvent = {
        start: this.turnosNormales[i].fecha
          .toLocaleString("yyyy-MM-dd'T'HH:mm")
          .slice(0, -13),
        title: this.turnosNormales[i].cantHoras + ' horas',
        resource: this.turnosNormales[i].usuarioId,
        slot: slotTurno,
      };
      // Crear un nuevo array conteniendo los eventos anteriores y el nuevo
      this.shifts = [...this.shifts, newEvent];
    }
  }
}
