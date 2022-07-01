import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({
  providedIn: 'root',
})
export class DiaLibreService {
  constructor(private http: HttpClient) {}

  saveDiaLibre(fecha: string, jornadaId: number): Observable<any> {
    return this.http.post<any>(
      environment.jornada + `save/libre/${jornadaId}`,
      {
        fecha,
      },
      httpOptions
    );
  }

  getDiaLibreById(diaLibreId: number): Observable<any> {
    return this.http.get<any>(
      environment.jornada + `get/libre/${diaLibreId}`,
      httpOptions
    );
  }

  getAllDiasLibresById(jornadaId: number): Observable<any> {
    return this.http.get<any>(
      environment.jornada + `get/libre/all/${jornadaId}`,
      httpOptions
    );
  }

  updateDiaLibre(
    fecha: string,
    jornadaId: number,
    diaLibreId: number
  ): Observable<any> {
    return this.http.put<any>(
      environment.jornada + `save/libre/${jornadaId}/${diaLibreId}`,
      {
        fecha,
      },
      httpOptions
    );
  }

  deleteDiaLibre(diaLibreId: number): Observable<any> {
    return this.http.delete<any>(
      environment.jornada + `delete/libre/${diaLibreId}`
    );
  }
}
