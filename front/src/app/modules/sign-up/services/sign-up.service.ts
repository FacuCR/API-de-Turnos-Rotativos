import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Role } from 'src/app/core/models/Role';

const AUTH_API = 'http://localhost:8080/api/auth/';

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
      AUTH_API + 'signup',
      {
        username,
        password,
        role
      },
      httpOptions
    );
  }
}
