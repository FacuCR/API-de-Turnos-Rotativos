import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

const EMPLEADOS_API = 'http://localhost:8080/api/empleados/';

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
      EMPLEADOS_API + `save/${id}`,
      {
        nombre,
        apellido,
      },
      httpOptions
    );
  }

  getAllEmpleados(): Observable<any> {
    return this.http.get<any>(EMPLEADOS_API + `get`, httpOptions);
  }

  getEmpleadoById(id: number): Observable<any> {
    return this.http.get<any>(EMPLEADOS_API + `get/${id}`, httpOptions);
  }
}
