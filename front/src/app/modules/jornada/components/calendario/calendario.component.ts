import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
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
import { TurnoExtraService } from '../../services/turno-extra/turno-extra.service';
import { TurnoNormalService } from '../../services/turno-normal.service';
import { TurnoExtra } from '../../models/TurnoExtra';
import { JornadaTurno } from '../../models/JornadaTurno';

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
  turnosExtras: TurnoExtra[] = [];
  cargando: boolean = false;
  @Output() sendCargando = new EventEmitter<boolean>();

  constructor(
    private empleados: EmpleadoService,
    private turnoNormalService: TurnoNormalService,
    private turnoExtraService: TurnoExtraService
  ) {}

  ngOnInit(): void {
    this.empleados
      .getAllEmpleados()
      .subscribe({
        next: (event: any) => {
          this.cargando = true;
          this.sendCargandoEvent(this.cargando);

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
          this.sendCargandoEvent(this.cargando);
        },
      })
      .add(() => {
        this.cargarTurnosNormales();
      });
  }

  shifts: MbscCalendarEvent[] = [
    {
      onEventClick: (args: any) => {
        console.log('click en ' + args);
      },
    },
  ];

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
      console.log(
        'click en ' +
          JSON.stringify(args.event) +
          ' Jornada: ' +
          (args.resource - 1)
      );
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

  cargarTurnosNormales(): void {
    if (this.empleadosActuales) {
      this.empleadosActuales.forEach((empleado) => {
        this.turnoNormalService
          .getAllTurnosNormalesById(empleado.id - 2)
          .subscribe({
            next: (event: any) => {
              if (event.type === HttpEventType.DownloadProgress) {
                if (Math.round((100 * event.loaded) / event.total) == 100) {
                  this.cargando = false;
                  this.sendCargandoEvent(this.cargando);
                }
              }
              event.body && event.body.length != 0
                ? (this.turnosNormales = event.body)
                : '';
            },
            error: (e: HttpErrorResponse) => {
              console.log(e.error);
              this.cargando = false;
              this.sendCargandoEvent(this.cargando);
            },
          })
          .add(() => {
            this.cargarTurnosExtras();
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
              this.cargarShifts(this.turnosNormales, '#673ab7');
            }
          });
      });
    }
  }

  cargarShifts(turnos: JornadaTurno[], colorTurno: string): void {
    let slotTurno: number = 1;
    for (let i = 0; i < turnos.length; i++) {
      switch (turnos[i].turno) {
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
        start: turnos[i].fecha
          .toLocaleString("yyyy-MM-dd'T'HH:mm")
          .slice(0, -13),
        title: turnos[i].cantHoras + ' horas',
        resource: turnos[i].usuarioId,
        slot: slotTurno,
        color: colorTurno,
      };
      // Crear un nuevo array conteniendo los eventos anteriores y el nuevo
      this.shifts = [...this.shifts, newEvent];
    }
  }

  cargarTurnosExtras(): void {
    if (this.empleadosActuales) {
      this.empleadosActuales.forEach((empleado) => {
        this.turnoExtraService
          .getAllTurnosExtrasById(empleado.id - 2)
          .subscribe({
            next: (event: any) => {
              if (event.type === HttpEventType.DownloadProgress) {
                if (Math.round((100 * event.loaded) / event.total) == 100) {
                  this.cargando = false;
                  this.sendCargandoEvent(this.cargando);
                }
              }
              event.body && event.body.length != 0
                ? (this.turnosExtras = event.body)
                : '';
            },
            error: (e: HttpErrorResponse) => {
              console.log(e.error);
              this.cargando = false;
              this.sendCargandoEvent(this.cargando);
            },
          })
          .add(() => {
            let turnosCorregidos: TurnoExtra[] = [];
            if (this.turnosExtras) {
              for (let i = 0; i < this.turnosExtras.length; i++) {
                let turnoCorregido = new TurnoExtra();
                turnoCorregido.fecha = this.turnosExtras[i].fecha;
                turnoCorregido.cantHoras =
                  'Extra: ' + this.turnosExtras[i].cantHoras;
                turnoCorregido.turno = this.turnosExtras[i].turno;
                turnoCorregido.idTurnoNormal =
                  this.turnosExtras[i].idTurnoNormal;
                turnoCorregido.usuarioId = this.turnosExtras[i].usuarioId + 1;
                turnosCorregidos.push(turnoCorregido);
              }
              this.turnosExtras = turnosCorregidos;
              this.cargarShifts(this.turnosExtras, '#ffd740');
            }
          });
      });
    }
  }

  sendCargandoEvent(cargando: boolean): void {
    this.sendCargando.emit(cargando);
  }
}
