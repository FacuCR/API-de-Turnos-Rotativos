import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { TokenStorageService } from '../services/token/token-storage.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    constructor(private tokeStorage: TokenStorageService) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(catchError(err => {
            if ([401, 403].indexOf(err.status) !== -1) {
                // auto deslogueo si se obtiene  una respuesta 
                // 401 Unauthorized o 403 Forbidden de la api
                this.tokeStorage.signOut();
            }

            const error = err.error.message || err.statusText;
            return throwError(() => new Error(error));
        }))
    }
}