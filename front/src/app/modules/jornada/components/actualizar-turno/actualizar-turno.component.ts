import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { Turnos } from 'src/app/core/models/Turnos';
import { TokenStorageService } from 'src/app/core/services/token/token-storage.service';
import { DateAddDiasService } from '../../services/date-add-dias/date-add-dias.service';
import { TurnoNormalService } from '../../services/turno-normal.service';

@Component({
  selector: 'app-actualizar-turno',
  templateUrl: './actualizar-turno.component.html',
  styleUrls: ['./actualizar-turno.component.css']
})
export class ActualizarTurnoComponent implements OnInit {

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

  @Input() id: number = 0;

  isInvalido: boolean = false;
  formError: string = '';
  formExitoso: string = '';
  cargando: boolean = false;
  fechaMinima: Date = this.dateAddService.addDias(1, new Date());

  constructor(
    public fb: FormBuilder,
    private turnoNormalService: TurnoNormalService,
    private tokenStorage: TokenStorageService,
    private dateAddService: DateAddDiasService
  ) {}

  ngOnInit(): void {}

  onSubmit(): void {
    this.cargando = true;
    const fecha: string = this.dateAddService.formatDate(
      this.form.get('fecha')?.value
    );
    const turno: Turnos = this.form.get('turno')?.value;
    const cantHoras: number = this.form.get('cantHoras')?.value;
    const jornadaId: number = this.tokenStorage.getUser().id;

    this.turnoNormalService
      .updateTurnoNormal(fecha, turno, cantHoras, jornadaId, this.id)
      .subscribe({
        next: (event: any) => {
          this.formError = '';
          if (event.type === HttpEventType.UploadProgress) {
            if (Math.round((100 * event.loaded) / event.total) == 100) {
              this.cargando = false;
            }
          }
          if (event.type === HttpEventType.Response) {
            this.formExitoso = event.body.message;
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

}