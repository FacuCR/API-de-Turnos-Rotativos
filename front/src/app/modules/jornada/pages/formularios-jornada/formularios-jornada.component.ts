import {
  Component,
  ElementRef,
  HostListener,
  OnInit,
  ViewChild,
} from '@angular/core';

@Component({
  selector: 'app-formularios-jornada',
  templateUrl: './formularios-jornada.component.html',
  styleUrls: ['./formularios-jornada.component.css'],
})
export class FormulariosJornadaComponent implements OnInit {
  @ViewChild('group')
  buttonGroup!: ElementRef;

  isFormTurnoActivo: boolean = false;
  isFormExtraActivo: boolean = false;
  isFormLibreActivo: boolean = false;
  isFormVacacionesActivo: boolean = false;
  isFormCombinacionActivo: boolean = false;
  isAlgunFormActivo: boolean = false;
  screenXl: boolean = false;

  constructor() {}

  ngOnInit(): void {
    this.ifScreenXl(window.innerWidth);
  }

  cambiarForm(form: string) {
    this.isFormTurnoActivo = false;
    this.isFormExtraActivo = false;
    this.isFormLibreActivo = false;
    this.isFormVacacionesActivo = false;
    this.isFormCombinacionActivo = false;
    switch (form) {
      case 'turno':
        this.isFormTurnoActivo = true;
        break;
      case 'extra':
        this.isFormExtraActivo = true;
        break;
      case 'libre':
        this.isFormLibreActivo = true;
        break;
      case 'vacaciones':
        this.isFormVacacionesActivo = true;
        break;
      case 'combinacion':
        this.isFormCombinacionActivo = true;
        break;

      default:
        break;
    }
    this.isAlgunFormActivo =
      this.isFormTurnoActivo ||
      this.isFormExtraActivo ||
      this.isFormLibreActivo ||
      this.isFormVacacionesActivo ||
      this.isFormCombinacionActivo;
  }

  // Para poner el boutton group en modo vertical si la pantalla no es amplia
  @HostListener('window:resize', ['$event'])
  onResize() {
    this.ifScreenXl(window.innerWidth);
  }

  ifScreenXl(windowInnerWidth: number) {
    this.screenXl = windowInnerWidth >= 1200;
  }
}
