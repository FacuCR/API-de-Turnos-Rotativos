import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormGroupDirective,
  Validators,
} from '@angular/forms';
import { Turnos } from 'src/app/core/models/Turnos';
import { TokenStorageService } from 'src/app/core/services/token/token-storage.service';
import { Combinacion } from '../../models/Combinacion';
import { CombinacionService } from '../../services/combinacion/combinacion.service';
import { DateAddDiasService } from '../../services/date-add-dias/date-add-dias.service';

@Component({
  selector: 'app-crear-combinacion',
  templateUrl: './crear-combinacion.component.html',
  styleUrls: ['./crear-combinacion.component.css'],
})
export class CrearCombinacionComponent implements OnInit {
  form: FormGroup = this.fb.group({
    fecha: ['', Validators.required],
    turnoDelNormal: ['', Validators.required],
    turnoDelExtra: ['', Validators.required],
    cantHorasDelNormal: [
      '',
      [Validators.required, Validators.min(6), Validators.max(8)],
    ],
    cantHorasDelExtra: [
      '',
      [Validators.required, Validators.min(2), Validators.max(6)],
    ],
  });

  @ViewChild(FormGroupDirective)
  formGroupDirective!: FormGroupDirective;

  isInvalido: boolean = false;
  formError: string = '';
  formExitoso: string = '';
  fechaMinima: Date = this.dateAddService.addDias(1, new Date());

  constructor(
    public fb: FormBuilder,
    private combinacionService: CombinacionService,
    private tokenStorage: TokenStorageService,
    private dateAddService: DateAddDiasService
  ) {}

  ngOnInit(): void {}

  onSubmit(): void {
    const fecha: string = this.dateAddService.formatDate(
      this.form.get('fecha')?.value
    );
    const turnoDelNormal: Turnos = this.form.get('turnoDelNormal')?.value;
    const turnoDelExtra: Turnos = this.form.get('turnoDelExtra')?.value;
    const cantHorasDelNormal: number =
      this.form.get('cantHorasDelNormal')?.value;
    const cantHorasDelExtra: number = this.form.get('cantHorasDelExtra')?.value;
    const jornadaId: number = this.tokenStorage.getUser().id;

    if (turnoDelExtra !== turnoDelNormal) {
      let combinacion: Combinacion = new Combinacion();
      combinacion.fecha = fecha;
      combinacion.turnoNormal = turnoDelNormal;
      combinacion.turnoExtra = turnoDelExtra;
      combinacion.cantHorasNormal = cantHorasDelNormal;
      combinacion.cantHorasExtra = cantHorasDelExtra;

      this.combinacionService
        .saveCombinacion(combinacion, jornadaId)
        .subscribe({
          next: (event: any) => {
            this.formError = '';
            this.formExitoso = event.message;

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
          },
        });
    } else {
      this.isInvalido = true;
      this.form.reset();
      this.formError =
        'Los turnos deben ser en horarios distintos, no eres Naruto, no te vas a clonar :(';
    }
  }

  getMensajeDeError(input: string) {
    return this.form.controls[input].hasError('required')
      ? 'Debes ingresar algo!'
      : '';
  }
}
