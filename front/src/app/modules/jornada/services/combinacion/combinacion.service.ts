import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Turnos } from 'src/app/core/models/Turnos';
import { environment } from 'src/environments/environment';
import { Combinacion } from '../../models/Combinacion';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({
  providedIn: 'root',
})
export class CombinacionService {
  constructor(private http: HttpClient) {}

  saveCombinacion(
    combinacion: Combinacion,
    jornadaId: number
  ): Observable<any> {
    const fecha = combinacion.fecha;
    const turnoNormal = combinacion.turnoNormal;
    const turnoExtra = combinacion.turnoExtra;
    const cantHorasNormal = combinacion.cantHorasNormal;
    const cantHorasExtra = combinacion.cantHorasExtra;
    return this.http.post<any>(
      environment.jornada + `save/combinacion/${jornadaId}`,
      {
        fecha,
        turnoNormal,
        turnoExtra,
        cantHorasNormal,
        cantHorasExtra,
      },
      httpOptions
    );
  }
}
