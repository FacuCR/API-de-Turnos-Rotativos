import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  observe: 'events' as const,
  reportProgress: true,
};

@Injectable({
  providedIn: 'root',
})
export class EmpleadoService {
  constructor(private http: HttpClient) {}

  saveEmpleado(nombre: string, apellido: string, id: number): Observable<any> {
    return this.http.post<any>(
      environment.empleado + `save/${id}`,
      {
        nombre,
        apellido,
      },
      httpOptions
    );
  }

  getAllEmpleados(): Observable<any> {
    return this.http.get<any>(environment.empleado + `get`, httpOptions);
  }

  getEmpleadoById(id: number): Observable<any> {
    return this.http.get<any>(environment.empleado + `get/${id}`, httpOptions);
  }
  
  saveAntiguedad(antiguedad: number, usuarioId: number): Observable<any> {
    return this.http.post<any>(
      environment.jornada + `save/antiguedad/${usuarioId}/${antiguedad}`,
      httpOptions
    );
  }
}
