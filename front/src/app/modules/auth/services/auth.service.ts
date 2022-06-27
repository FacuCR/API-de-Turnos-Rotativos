import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

// TypeScript infiere en el tipo de las opciones {observe: string}. El tipo es 
// demasiado amplio para pasarlo a HttpClient.post, que espera que el tipo de responseType sea una de las cadenas 
// específicas. HttpClient está tipado explícitamente de esta manera para que el compilador pueda reportar el tipo 
// de retorno correcto basado en las opciones que usted proporcionó.
// Uso as const para que TypeScript sepa que realmente quiero usar un tipo de cadena constante:
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  observe: 'events' as const,
  reportProgress: true,
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(
      environment.auth + 'signin',
      {
        username,
        password,
      },
      httpOptions
    );
  }
}
