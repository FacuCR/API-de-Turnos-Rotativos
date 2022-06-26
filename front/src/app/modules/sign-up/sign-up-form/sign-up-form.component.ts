import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormGroupDirective,
  Validators,
} from '@angular/forms';
import { Role } from 'src/app/core/models/Role';
import { SignUpService } from '../services/sign-up.service';

@Component({
  selector: 'app-sign-up-form',
  templateUrl: './sign-up-form.component.html',
  styleUrls: ['./sign-up-form.component.css'],
})
export class SignUpFormComponent implements OnInit {
  form: FormGroup = this.fb.group({
    usuario: [
      '',
      [Validators.required, Validators.minLength(1), Validators.maxLength(20)],
    ],
    password: ['', [Validators.required, Validators.minLength(6)]],
    admin: [''],
  });

  @ViewChild(FormGroupDirective)
  formGroupDirective!: FormGroupDirective;

  isInvalido: boolean = false;
  ocultar: boolean = true;
  signupError: string = '';
  signupExitoso: string = '';
  cargando: boolean = false;

  constructor(public fb: FormBuilder, private signUpService: SignUpService) {}

  ngOnInit(): void {}

  onSubmit(): void {
    this.cargando = true;
    const usuario = this.form.get('usuario')?.value;
    const pass = this.form.get('password')?.value;
    const checkboxValue: boolean = this.form.get('admin')?.value;
    let roles: Role[] = [];

    roles.push(Role.User);
    checkboxValue ? roles.push(Role.Admin) : '';

    this.signUpService.crearUsuario(usuario, pass, roles).subscribe({
      next: (event: any) => {
        this.signupError = '';
        if (event.type === HttpEventType.UploadProgress) {
          if (Math.round((100 * event.loaded) / event.total) == 100) {
            this.cargando = false;
            this.signupExitoso = "Usuario creado correctamente!";
          }
        }

        // Si el formulario se marca como enviado, independientemente de si está enviado o no,
        // los errores se resaltarán.
        // llamo a resetForm en lugar de reset, que está en el FormGroupDirective
        // En segundo lugar, lo envuelvo en un setTimeout con un tiempo de espera de 0, para que el formulario
        // se envíe antes de que se reinicie.
        setTimeout(() => this.formGroupDirective.resetForm(), 0);
        this.isInvalido = false;
      },
      error: (e: HttpErrorResponse) => {
        this.signupError = e.error.message;
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
