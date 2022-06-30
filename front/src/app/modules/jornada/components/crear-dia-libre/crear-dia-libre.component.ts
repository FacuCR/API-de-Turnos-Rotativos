import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormGroupDirective,
  Validators,
} from '@angular/forms';
import { TokenStorageService } from 'src/app/core/services/token/token-storage.service';
import { DateAddDiasService } from '../../services/date-add-dias/date-add-dias.service';
import { DiaLibreService } from '../../services/dia-libre/dia-libre.service';

@Component({
  selector: 'app-crear-dia-libre',
  templateUrl: './crear-dia-libre.component.html',
  styleUrls: ['./crear-dia-libre.component.css'],
})
export class CrearDiaLibreComponent implements OnInit {
  form: FormGroup = this.fb.group({
    fecha: ['', Validators.required],
  });

  @ViewChild(FormGroupDirective)
  formGroupDirective!: FormGroupDirective;

  isInvalido: boolean = false;
  formError: string = '';
  formExitoso: string = '';
  fechaMinima: Date = this.dateAddService.addDias(1, new Date());

  constructor(
    public fb: FormBuilder,
    private diaLibreService: DiaLibreService,
    private tokenStorage: TokenStorageService,
    private dateAddService: DateAddDiasService
  ) {}

  ngOnInit(): void {}

  onSubmit(): void {
    const fecha: string = this.formatDate(this.form.get('fecha')?.value);
    const jornadaId: number = this.tokenStorage.getUser().id;

    this.diaLibreService.saveDiaLibre(fecha, jornadaId).subscribe({
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
