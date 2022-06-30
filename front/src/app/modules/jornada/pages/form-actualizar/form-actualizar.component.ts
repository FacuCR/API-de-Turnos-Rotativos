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

  isDatosDisponibles: string | null = '';

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.isDatosDisponibles = this.route.snapshot.paramMap.get('datos_evento');
    if (this.isDatosDisponibles) {
      this.datosDelEvento = JSON.parse(this.isDatosDisponibles);
    }
  }
}
