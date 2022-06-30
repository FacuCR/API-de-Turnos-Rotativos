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
  providedIn: 'root'
})
export class CombinacionService {

  constructor(private http: HttpClient) { }

  saveCombinacion(combinacion: Combinacion, jornadaId: number): Observable<any> {
    return this.http.post<any>(
      environment.jornada + `save/combinacion/${jornadaId}`,
      {
        combinacion,
      },
      httpOptions
    );
  }
}
