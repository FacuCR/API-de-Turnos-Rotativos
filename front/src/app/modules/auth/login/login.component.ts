import {
  HttpErrorResponse,
  HttpEventType,
  HttpResponse,
} from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
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
    public fb: FormBuilder,
    private authService: AuthService,
    private tokenStorage: TokenStorageService
  ) {}

  ngOnInit(): void {}

  onSubmit(): void {
    const usuario = this.form.get('usuario')?.value;
    const pass = this.form.get('password')?.value;

    this.cargando = true;

    this.authService.login(usuario, pass).subscribe({
      next: (data: any) => {
        if (data.type === HttpEventType.UploadProgress) {
          if (Math.round((100 * data.loaded) / data.total) == 100) {
            this.cargando = false;
          }
        }
        this.tokenStorage.saveToken(data.accessToken);
        this.tokenStorage.saveUser(data);
        this.loginError = '';
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
}
