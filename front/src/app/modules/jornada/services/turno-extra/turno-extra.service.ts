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
    fecha: Date,
    turno: Turnos,
    cantDeHoras: number,
    jornadaId: number
  ): Observable<any> {
    jornadaId++;
    return this.http.post<any>(
      environment.jornada + `save/extra/${jornadaId}`,
      {
        fecha,
        turno,
        cantDeHoras,
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
    jornadaId++;
    return this.http.get<any>(
      environment.jornada + `get/extra/all/${jornadaId}`,
      httpOptions
    );
  }

  updateTurnoExtra(
    fecha: Date,
    turno: Turnos,
    cantDeHoras: number,
    jornadaId: number,
    turnoExtraId: number
  ): Observable<any> {
    jornadaId++;
    return this.http.put<any>(
      environment.jornada + `save/extra/${jornadaId}/"${turnoExtraId}`,
      {
        fecha,
        turno,
        cantDeHoras,
      },
      httpOptions
    );
  }

  deleteTurnoExtra(turnoExtraId: number): Observable<any> {
    return this.http.delete<any>(
      environment.jornada + `delete/extra/${turnoExtraId}`,
      httpOptions
    );
  }
}
