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
export class TurnoNormalService {
  constructor(private http: HttpClient) {}

  saveTurnoNormal(
    fecha: string,
    turno: Turnos,
    cantHoras: number,
    jornadaId: number
  ): Observable<any> {
    return this.http.post<any>(
      environment.jornada + `save/normal/${jornadaId}`,
      {
        fecha,
        turno,
        cantHoras,
      },
      httpOptions
    );
  }

  getTurnoNormalById(turnoId: number): Observable<any> {
    turnoId++;
    return this.http.get<any>(
      environment.jornada + `get/normal/${turnoId}`,
      httpOptions
    );
  }

  getAllTurnosNormalesById(jornadaId: number): Observable<any> {
    return this.http.get<any>(
      environment.jornada + `get/normal/all/${jornadaId}`,
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
    return this.http.put<any>(
      environment.jornada + `save/normal/${jornadaId}/"${turnoNormalId}`,
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
      environment.jornada + `delete/normal/${turnoNormalId}`
    );
  }
}
