import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Role } from 'src/app/core/models/Role';
import { environment } from 'src/environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  observe: 'events' as const,
  reportProgress: true,
};

@Injectable({
  providedIn: 'root'
})
export class SignUpService {

  constructor(private http: HttpClient) { }

  crearUsuario(username: string, password: string, role: Role[]): Observable<any> {
    return this.http.post<any>(
      environment.auth + 'signup',
      {
        username,
        password,
        role
      },
      httpOptions
    );
  }
}
