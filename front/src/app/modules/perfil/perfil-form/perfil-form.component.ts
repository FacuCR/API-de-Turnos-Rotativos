import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  FormGroupDirective,
  Validators,
} from '@angular/forms';
import { EmpleadoService } from 'src/app/core/services/empleado/empleado.service';
import { TokenStorageService } from 'src/app/core/services/token/token-storage.service';

@Component({
  selector: 'app-perfil-form',
  templateUrl: './perfil-form.component.html',
  styleUrls: ['./perfil-form.component.css'],
})
export class PerfilFormComponent implements OnInit {
  validPattern = '^[a-zA-Z0-9]{10}$';

  form: FormGroup = this.fb.group({
    nombre: [
      '',
      [Validators.required, Validators.minLength(2), Validators.maxLength(20)],
    ],
    apellido: [
      '',
      [Validators.required, Validators.minLength(2), Validators.maxLength(20)],
    ],
    antiguedad: [''],
  });

  @ViewChild(FormGroupDirective)
  formGroupDirective!: FormGroupDirective;

  isInvalido: boolean = false;
  ocultar: boolean = true;
  perfilError: string = '';
  perfilExitoso: string = '';
  cargando: boolean = false;

  constructor(
    public fb: FormBuilder,
    private empleadoService: EmpleadoService,
    private tokenStorage: TokenStorageService,
    private _location: Location
  ) {}

  ngOnInit(): void {}

  onSubmit(): void {
    this.cargando = true;
    const nombre = this.form.get('nombre')?.value;
    const apellido = this.form.get('apellido')?.value;
    const antiguedad: number = this.form.get('antiguedad')?.value;

    this.empleadoService
      .saveEmpleado(nombre, apellido, this.tokenStorage.getUser().id)
      .subscribe({
        next: (event: any) => {
          this.perfilError = '';
          if (event.type === HttpEventType.UploadProgress) {
            if (Math.round((100 * event.loaded) / event.total) == 100) {
              this.cargando = false;
              this.perfilExitoso = 'Datos guardados correctamente!';
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
          this.perfilError = e.error.message;
          this.form.reset();
          this.isInvalido = true;
          this.cargando = false;
        },
      })
      .add(() => {
        if (antiguedad) {
          this.empleadoService
            .saveAntiguedad(antiguedad, this.tokenStorage.getUser().id)
            .subscribe({
              next: (event: any) => {
                this.perfilError = '';
                if (event.type === HttpEventType.UploadProgress) {
                  if (Math.round((100 * event.loaded) / event.total) == 100) {
                    this.cargando = false;
                    this.perfilExitoso = event.body;
                  }
                }
                this.isInvalido = false;
              },
              error: (e: HttpErrorResponse) => {
                this.perfilError = e.error.message;
                this.form.reset();
                this.isInvalido = true;
                this.cargando = false;
              },
            });
        }
      });
  }

  getMensajeDeError(input: string) {
    return this.form.controls[input].hasError('required')
      ? 'Debes ingresar algo!'
      : 'Datos invalidos';
  }

  backClicked() {
    this._location.back();
  }
}
