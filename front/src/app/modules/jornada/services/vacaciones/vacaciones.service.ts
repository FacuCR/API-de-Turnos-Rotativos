import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({
  providedIn: 'root'
})
export class VacacionesService {

  constructor(private http: HttpClient) { }

  saveVacaciones(fecha: string, jornadaId: number): Observable<any> {
    return this.http.post<any>(
      environment.jornada + `save/vacaciones/${jornadaId}`,
      {
        fecha,
      },
      httpOptions
    );
  }

  getVacacionesById(vacacionesId: number): Observable<any> {
    return this.http.get<any>(
      environment.jornada + `get/vacaciones/${vacacionesId}`,
      httpOptions
    );
  }

  getAllVacacionesById(jornadaId: number): Observable<any> {
    return this.http.get<any>(
      environment.jornada + `get/vacaciones/all/${jornadaId}`,
      httpOptions
    );
  }

  updateVacaciones(
    fecha: string,
    jornadaId: number,
    vacacionesId: number
  ): Observable<any> {
    return this.http.put<any>(
      environment.jornada + `save/vacaciones/${jornadaId}/"${vacacionesId}`,
      {
        fecha,
      },
      httpOptions
    );
  }

  deleteVacaciones(vacacionesId: number): Observable<any> {
    return this.http.delete<any>(
      environment.jornada + `delete/vacaciones/${vacacionesId}`
    );
  }
}
