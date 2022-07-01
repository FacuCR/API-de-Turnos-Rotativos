import {
  HttpErrorResponse,
  HttpEventType,
  HttpResponse,
} from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TokenStorageService } from 'src/app/core/services/token/token-storage.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  form: FormGroup = this.fb.group({
    usuario: [
      '',
      [Validators.required, Validators.minLength(1), Validators.maxLength(20)],
    ],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  isInvalido: boolean = false;
  ocultar: boolean = true;
  loginError: string = '';
  cargando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private tokenStorage: TokenStorageService,
    private router: Router
  ) {
    if (this.tokenStorage.getToken()) {
      this.navegarAJornadaRoute();
    }
  }

  ngOnInit(): void {}

  onSubmit(): void {
    const usuario = this.form.get('usuario')?.value;
    const pass = this.form.get('password')?.value;

    this.cargando = true;

    this.authService.login(usuario, pass).subscribe({
      next: (event: any) => {
        if (event.type === HttpEventType.UploadProgress) {
          if (Math.round((100 * event.loaded) / event.total) == 100) {
            this.cargando = false;
          }
        }
        if (event.body) {
          this.tokenStorage.saveToken(event.body.accessToken);
          this.tokenStorage.saveUser(event.body);
        }

        this.loginError = '';
        this.navegarAJornadaRoute();
      },
      error: (e: HttpErrorResponse) => {
        this.loginError = e.error.message;
        this.form.reset();
        this.isInvalido = true;
        this.cargando = false;
      },
    });
  }

  getMensajeDeError(input: string) {
    return this.form.controls[input].hasError('required')
      ? 'Debes ingresar algo!'
      : '';
  }

  navegarAJornadaRoute(): void {
    this.router.navigate(['jornada']);
  }
}
