import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DateAddDiasService {

  constructor() { }

  addDias(dias: number, fechaDeHoy: Date): Date {
    let fecha = new Date(fechaDeHoy);
    fecha.setDate(fecha.getDate() + dias);
    return fecha;
  }
}
