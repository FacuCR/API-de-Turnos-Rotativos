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
import { MatDialog } from '@angular/material/dialog';
import { DialogEventComponent } from '../dialog-event/dialog-event.component';
import { TokenStorageService } from 'src/app/core/services/token/token-storage.service';
import { TurnoEliminado } from '../../models/TurnoEliminado';
import { DiaLibreService } from '../../services/dia-libre/dia-libre.service';
import { DiaLibre } from '../../models/DiaLibre';
import { Vacaciones } from '../../models/Vacaciones';
import { VacacionesService } from '../../services/vacaciones/vacaciones.service';

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
  diasLibres: DiaLibre[] = [];
  vacaciones: Vacaciones[] = [];
  cargando: boolean = false;
  @Output() sendCargando = new EventEmitter<boolean>();

  constructor(
    private empleados: EmpleadoService,
    private turnoNormalService: TurnoNormalService,
    private turnoExtraService: TurnoExtraService,
    private diaLibreService: DiaLibreService,
    private vacacionesService: VacacionesService,
    public dialog: MatDialog,
    private tokenStorage: TokenStorageService
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
          this.cargando = false;
          this.sendCargandoEvent(this.cargando);
        },
      })
      .add(() => {
        this.cargarTurnosNormales();
      });
  }

  // Los eventos del calendario
  shifts: MbscCalendarEvent[] = [];

  // Las opciones del calendario
  calendarOptions: MbscEventcalendarOptions = {
    // Tipo de vista del calendario , en este caso se visualiza por semanas
    view: {
      timeline: {
        type: 'week',
        eventList: true,
        startDay: 1,
      },
    },
    // colores de las tablas del calendario
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
    // Los slots los use para crear turnos
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
    // Metodo que se llama cuando se hace click en algun evento del calendario
    onEventClick: (args: any) => {
      // Solo muestra el dialogRef si es un evento del usuario
      if (args.event.resource === this.tokenStorage.getUser().id) {
        const dialogRef = this.dialog.open(DialogEventComponent, {
          data: {
            evento: args.event,
            jornadaId: args.resource,
          },
        });

        // Luego del dialogRef si se borro alguna jornada de la BD llama el metodo deleteEvent para borrarlo del calendario
        dialogRef.afterClosed().subscribe((turnoEliminado: TurnoEliminado) => {
          if (turnoEliminado.isEliminado) {
            this.deleteEvent(turnoEliminado.idTurnoEliminado);
          }
        });
      }
    },
  };

  // Color de fondo de los turnos(slots)
  backgroundDelTurno(id: number): string {
    if (id === 1) {
      return '#a5ceff4d';
    } else if (id === 2) {
      return '#f7f7bb4d';
    } else {
      return '#ce448b4d';
    }
  }

  // Casteo los empleados para guardarlos en el resource del calendario
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

  // Solicatos los turnos normales de todos los usuarios de la BD
  cargarTurnosNormales(): void {
    if (this.empleadosActuales) {
      this.empleadosActuales.forEach((empleado) => {
        this.turnoNormalService
          .getAllTurnosNormalesById(empleado.id)
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
              this.cargando = false;
              this.sendCargandoEvent(this.cargando);
            },
          })
          .add(() => {
            this.cargarTurnosExtras();
            if (this.turnosNormales) {
              this.cargarShiftsDeTurnos(this.turnosNormales, '#673ab7');
            }
          });
      });
    }
  }

  // Cargar los turnos a los eventos del calendario
  cargarShiftsDeTurnos(turnos: JornadaTurno[], colorTurno: string): void {
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
      let tipoDeJornadaTurno: string = '';
      if (turnos[i].cantHoras[0] == 'E' || turnos[i].cantHoras[0] == 'e') {
        tipoDeJornadaTurno = 'extra';
      } else {
        tipoDeJornadaTurno = 'turno';
      }
      let newEvent = {
        id: turnos[i].idTurnoNormal,
        start: turnos[i].fecha
          .toLocaleString("yyyy-MM-dd'T'HH:mm")
          .slice(0, -13),
        title: turnos[i].cantHoras + ' horas',
        resource: turnos[i].usuarioId,
        slot: slotTurno,
        color: colorTurno,
        tipo: tipoDeJornadaTurno,
      };
      // Si hay algun evento igual no lo agrega ya que solo puede haber un turno en un dia por el mismo usuario
      if (
        !this.shifts.some(
          (shift) =>
            shift.start === newEvent.start &&
            shift.resource === newEvent.resource &&
            shift.slot === newEvent.slot
        )
      ) {
        // Crear un nuevo array conteniendo los eventos anteriores y el nuevo
        this.shifts = [...this.shifts, newEvent];
      }
    }
  }

  // Cargar los dias libres a los eventos del calendario
  cargarShiftDiaLibre(diasLibre: DiaLibre[], color: string): void {
    for (let i = 0; i < diasLibre.length; i++) {
      let newEvent = {
        id: diasLibre[i].idDiaLibre,
        start: diasLibre[i].fecha
          .toLocaleString("yyyy-MM-dd'T'HH:mm")
          .slice(0, -13),
        title: 'Dia libre',
        resource: diasLibre[i].usuarioId,
        slot: 1,
        color: color,
        tipo: 'libre',
      };
      let newEvent2 = {
        id: diasLibre[i].idDiaLibre,
        start: diasLibre[i].fecha
          .toLocaleString("yyyy-MM-dd'T'HH:mm")
          .slice(0, -13),
        title: 'Dia libre',
        resource: diasLibre[i].usuarioId,
        slot: 2,
        color: color,
        tipo: 'libre',
      };
      let newEvent3 = {
        id: diasLibre[i].idDiaLibre,
        start: diasLibre[i].fecha
          .toLocaleString("yyyy-MM-dd'T'HH:mm")
          .slice(0, -13),
        title: 'Dia libre',
        resource: diasLibre[i].usuarioId,
        slot: 3,
        color: color,
        tipo: 'libre',
      };

      // Si hay algun evento igual no lo agrega ya que solo puede haber un evento en un dia en el mismo turno por el mismo usuario
      if (
        !this.shifts.some(
          (shift) =>
            shift.start === newEvent.start &&
            shift.resource === newEvent.resource &&
            shift.slot === newEvent.slot
        )
      ) {
        // Crear un nuevo array conteniendo los eventos anteriores y el nuevo
        this.shifts = [...this.shifts, newEvent];
        this.shifts = [...this.shifts, newEvent2];
        this.shifts = [...this.shifts, newEvent3];
      }
    }
  }

  // Cargar las vacaciones a los eventos del calendario
  cargarShiftVacaciones(vacaciones: Vacaciones[], color: string): void {
    for (let i = 0; i < vacaciones.length; i++) {
      let newEvent = {
        id: vacaciones[i].idVacaciones,
        start: vacaciones[i].fecha
          .toLocaleString("yyyy-MM-dd'T'HH:mm")
          .slice(0, -13),
        end: vacaciones[i].fechaFinal,
        title: 'Vacaciones',
        resource: vacaciones[i].usuarioId,
        slot: 1,
        color: color,
        tipo: 'vacaciones',
      };
      let newEvent2 = {
        id: vacaciones[i].idVacaciones,
        start: vacaciones[i].fecha
          .toLocaleString("yyyy-MM-dd'T'HH:mm")
          .slice(0, -13),
        end: vacaciones[i].fechaFinal,
        title: 'Vacaciones',
        resource: vacaciones[i].usuarioId,
        slot: 2,
        color: color,
        tipo: 'vacaciones',
      };
      let newEvent3 = {
        id: vacaciones[i].idVacaciones,
        start: vacaciones[i].fecha
          .toLocaleString("yyyy-MM-dd'T'HH:mm")
          .slice(0, -13),
        end: vacaciones[i].fechaFinal,
        title: 'Vacaciones',
        resource: vacaciones[i].usuarioId,
        slot: 3,
        color: color,
        tipo: 'vacaciones',
      };
      // Si hay algun evento igual no lo agrega ya que solo puede haber un evento en un dia en el mismo turno por el mismo usuario
      if (
        !this.shifts.some(
          (shift) =>
            shift.start === newEvent.start &&
            shift.resource === newEvent.resource &&
            shift.slot === newEvent.slot
        )
      ) {
        // Crear un nuevo array conteniendo los eventos anteriores y el nuevo
        this.shifts = [...this.shifts, newEvent];
        this.shifts = [...this.shifts, newEvent2];
        this.shifts = [...this.shifts, newEvent3];
      }
    }
  }

  // Solicatos los turnos extras de todos los usuarios de la BD
  cargarTurnosExtras(): void {
    if (this.empleadosActuales) {
      this.empleadosActuales.forEach((empleado) => {
        this.turnoExtraService
          .getAllTurnosExtrasById(empleado.id)
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
              this.cargando = false;
              this.sendCargandoEvent(this.cargando);
            },
          })
          .add(() => {
            this.cargarDiasLibre();
            this.cargarVacaciones();
            if (this.turnosExtras) {
              for (let i = 0; i < this.turnosExtras.length; i++) {
                this.turnosExtras[i].cantHoras =
                  'Extra: ' + this.turnosExtras[i].cantHoras;
              }
              this.cargarShiftsDeTurnos(this.turnosExtras, '#ffd740');
            }
          });
      });
    }
  }

  // Solicatos los dias libres de todos los usuarios de la BD
  cargarDiasLibre(): void {
    if (this.empleadosActuales) {
      this.empleadosActuales.forEach((empleado) => {
        this.diaLibreService
          .getAllDiasLibresById(empleado.id)
          .subscribe({
            next: (event: any) => {
              event && event.length != 0 ? (this.diasLibres = event) : '';
            },
          })
          .add(() => {
            this.cargarShiftDiaLibre(this.diasLibres, '#f44336');
          });
      });
    }
  }

  // Solicatos las vacaciones de todos los usuarios de la BD
  cargarVacaciones(): void {
    if (this.empleadosActuales) {
      this.empleadosActuales.forEach((empleado) => {
        this.vacacionesService
          .getAllVacacionesById(empleado.id)
          .subscribe({
            next: (event: any) => {
              event && event.length != 0 ? (this.vacaciones = event) : '';
            },
          })
          .add(() => {
            this.cargarShiftVacaciones(this.vacaciones, 'green');
          });
      });
    }
  }

  // Enviar al padre el estado de cargando
  sendCargandoEvent(cargando: boolean): void {
    this.sendCargando.emit(cargando);
  }

  // Borrar un evento del calendario para no tener que recargar la pag
  deleteEvent(eventoId: number) {
    // creo una copia de los eventos
    const eventos: MbscCalendarEvent[] = [...this.shifts];
    // Encuentro el index del evento que va a ser borrado
    while (eventos.some((evento) => evento.id === eventoId)) {
      let index: number = eventos.indexOf(
        eventos.filter((evento) => evento.id === eventoId)[0]
      );
      // Borro el evento del array
      eventos.splice(index, 1);
    }
    // Paso el nuevo array al calendario
    this.shifts = eventos;
  }
}
