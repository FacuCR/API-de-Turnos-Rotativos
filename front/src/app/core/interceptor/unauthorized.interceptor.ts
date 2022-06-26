import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HTTP_INTERCEPTORS, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { TokenStorageService } from '../services/token/token-storage.service';
import { Router } from '@angular/router';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    constructor(private tokeStorage: TokenStorageService, private route: Router) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(catchError((err: HttpErrorResponse) => {
            if ([401, 403].indexOf(err.status) !== -1) {
                // auto deslogueo si se obtiene  una respuesta
                // 401 Unauthorized o 403 Forbidden de la api
                this.tokeStorage.signOut();
                this.route.navigate(['/']);
            }

            return throwError(() => err);
        }))
    }
}

export const UnauthorizedInterceptorProviders = [
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
  ];