import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

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
  isAlgunFormActivo: boolean = false;

  constructor() {}

  ngOnInit(): void {}

  cambiarForm(form: string) {
    this.isFormTurnoActivo = false;
    this.isFormExtraActivo = false;
    switch (form) {
      case 'turno':
        this.isFormTurnoActivo = true;
        break;
      case 'extra':
        this.isFormExtraActivo = true;
        break;

      default:
        break;
    }
    this.isAlgunFormActivo = this.isFormTurnoActivo || this.isFormExtraActivo;
  }
}
