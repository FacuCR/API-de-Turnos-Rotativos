import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-form-actualizar',
  templateUrl: './form-actualizar.component.html',
  styleUrls: ['./form-actualizar.component.css'],
})
export class FormActualizarComponent implements OnInit {
  datosDelEvento = {
    id: 0,
    tipo: '',
    fechaInicio: '',
  };

  isFormTurnoActivo: boolean = false;
  isFormExtraActivo: boolean = false;
  isFormLibreActivo: boolean = false;
  isFormVacacionesActivo: boolean = false;
  isAlgunFormActivo: boolean = false;

  sendId: number = 0;

  isDatosDisponibles: string | null = '';

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.isDatosDisponibles = this.route.snapshot.paramMap.get('datos_evento');
    if (this.isDatosDisponibles) {
      this.datosDelEvento = JSON.parse(this.isDatosDisponibles);
      this.mostrarFormCorrespondiente(this.datosDelEvento.tipo);
      this.datosDelEvento.fechaInicio = this.datosDelEvento.fechaInicio.slice(0, -6);
      this.sendId = this.datosDelEvento.id;
    }
  }

  mostrarFormCorrespondiente(form: string) {
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

      default:
        break;
    }
    this.isAlgunFormActivo =
      this.isFormTurnoActivo ||
      this.isFormExtraActivo ||
      this.isFormLibreActivo ||
      this.isFormVacacionesActivo;
  }
}
