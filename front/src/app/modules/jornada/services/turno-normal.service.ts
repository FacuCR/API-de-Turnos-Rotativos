import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Turnos } from 'src/app/core/models/Turnos';

const JORNADA_API = 'http://localhost:8080/api/jornada/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  observe: 'events' as const,
  reportProgress: true,
};

@Injectable({
  providedIn: 'root',
})
export class TurnoNormalService {
  constructor(private http: HttpClient) {}

  saveTurnoNormal(
    fecha: Date,
    turno: Turnos,
    cantDeHoras: number,
    jornadaId: number
  ): Observable<any> {
    jornadaId++;
    return this.http.post<any>(
      JORNADA_API + `save/normal/${jornadaId}`,
      {
        fecha,
        turno,
        cantDeHoras,
      },
      httpOptions
    );
  }

  getTurnoNormalById(jornadaId: number): Observable<any> {
    jornadaId++;
    return this.http.get<any>(
      JORNADA_API + `get/normal/${jornadaId}`,
      httpOptions
    );
  }

  getAllTurnosNormalesById(jornadaId: number): Observable<any> {
    jornadaId++;
    return this.http.get<any>(
      JORNADA_API + `get/normal/all/${jornadaId}`,
      httpOptions
    );
  }

  updateTurnoNormal(
    fecha: Date,
    turno: Turnos,
    cantDeHoras: number,
    jornadaId: number,
    turnoNormalId: number
  ): Observable<any> {
    jornadaId++;
    return this.http.put<any>(
      JORNADA_API + `save/normal/${jornadaId}/"${turnoNormalId}`,
      {
        fecha,
        turno,
        cantDeHoras,
      },
      httpOptions
    );
  }

  deleteTurnoNormal(turnoNormalId: number): Observable<any> {
    return this.http.delete<any>(
      JORNADA_API + `delete/normal/${turnoNormalId}`,
      httpOptions
    );
  }
}
