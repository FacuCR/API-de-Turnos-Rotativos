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

  formatDate(date: Date): string {
    return [
      this.padTo2Digits(date.getDate()),
      this.padTo2Digits(date.getMonth() + 1),
      date.getFullYear(),
    ].join('/');
  }

  padTo2Digits(num: number) {
    return num.toString().padStart(2, '0');
  }
}
