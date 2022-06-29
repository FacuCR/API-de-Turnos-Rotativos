import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Turnos } from 'src/app/core/models/Turnos';
import { environment } from 'src/environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  observe: 'events' as const,
  reportProgress: true,
};

@Injectable({
  providedIn: 'root',
})
export class TurnoExtraService {
  constructor(private http: HttpClient) {}

  saveTurnoExtra(
    fecha: string,
    turno: Turnos,
    cantHoras: number,
    jornadaId: number
  ): Observable<any> {
    return this.http.post<any>(
      environment.jornada + `save/extra/${jornadaId}`,
      {
        fecha,
        turno,
        cantHoras,
      },
      httpOptions
    );
  }

  getTurnoExtraById(turnoId: number): Observable<any> {
    turnoId++;
    return this.http.get<any>(
      environment.jornada + `get/extra/${turnoId}`,
      httpOptions
    );
  }

  getAllTurnosExtrasById(jornadaId: number): Observable<any> {
    return this.http.get<any>(
      environment.jornada + `get/extra/all/${jornadaId}`,
      httpOptions
    );
  }

  updateTurnoExtra(
    fecha: string,
    turno: Turnos,
    cantHoras: number,
    jornadaId: number,
    turnoExtraId: number
  ): Observable<any> {
    return this.http.put<any>(
      environment.jornada + `save/extra/${jornadaId}/"${turnoExtraId}`,
      {
        fecha,
        turno,
        cantHoras,
      },
      httpOptions
    );
  }

  deleteTurnoExtra(turnoExtraId: number): Observable<any> {
    return this.http.delete<any>(
      environment.jornada + `delete/extra/${turnoExtraId}`
    );
  }
}
