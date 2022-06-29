import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormGroupDirective,
  Validators,
} from '@angular/forms';
import { Turnos } from 'src/app/core/models/Turnos';
import { TokenStorageService } from 'src/app/core/services/token/token-storage.service';
import { TurnoNormalService } from '../../services/turno-normal.service';

@Component({
  selector: 'app-crear-turno',
  templateUrl: './crear-turno.component.html',
  styleUrls: ['./crear-turno.component.css'],
})
export class CrearTurnoComponent implements OnInit {
  form: FormGroup = this.fb.group({
    fecha: ['', Validators.required],
    turno: ['', Validators.required],
    cantHoras: [
      '',
      [Validators.required, Validators.min(6), Validators.max(8)],
    ],
  });

  @ViewChild(FormGroupDirective)
  formGroupDirective!: FormGroupDirective;

  isInvalido: boolean = false;
  formError: string = '';
  formExitoso: string = '';
  cargando: boolean = false;
  fechaMinima: Date = new Date();

  constructor(
    public fb: FormBuilder,
    private turnoNormalService: TurnoNormalService,
    private tokenStorage: TokenStorageService
  ) {}

  ngOnInit(): void {}

  onSubmit(): void {
    this.cargando = true;
    const fecha: string = this.formatDate(this.form.get('fecha')?.value);
    const turno: Turnos = this.form.get('turno')?.value;
    const cantHoras: number = this.form.get('cantHoras')?.value;
    const jornadaId: number = this.tokenStorage.getUser().id;

    this.turnoNormalService
      .saveTurnoNormal(fecha, turno, cantHoras, jornadaId)
      .subscribe({
        next: (event: any) => {
          this.formError = '';
          if (event.type === HttpEventType.UploadProgress) {
            if (Math.round((100 * event.loaded) / event.total) == 100) {
              this.cargando = false;
              this.formExitoso = 'Turno guardado correctamente!';
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
          this.formError = e.error.message;
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

  formatDate(date: Date): string {
    return [
      this.padTo2Digits(date.getDate()),
      this.padTo2Digits(date.getMonth() + 1),
      date.getFullYear(),
    ].join('/');
  }

  padTo2Digits(num: number) {
    return num.toString().padStart(2, '0');
  }
}
