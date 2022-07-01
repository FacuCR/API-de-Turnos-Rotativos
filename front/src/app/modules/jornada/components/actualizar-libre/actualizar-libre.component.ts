import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { TokenStorageService } from 'src/app/core/services/token/token-storage.service';
import { DateAddDiasService } from '../../services/date-add-dias/date-add-dias.service';
import { DiaLibreService } from '../../services/dia-libre/dia-libre.service';

@Component({
  selector: 'app-actualizar-libre',
  templateUrl: './actualizar-libre.component.html',
  styleUrls: ['./actualizar-libre.component.css']
})
export class ActualizarLibreComponent implements OnInit {

  form: FormGroup = this.fb.group({
    fecha: ['', Validators.required],
  });

  @ViewChild(FormGroupDirective)
  formGroupDirective!: FormGroupDirective;

  @Input() id: number = 0;

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
    const fecha: string = this.dateAddService.formatDate(this.form.get('fecha')?.value);
    const jornadaId: number = this.tokenStorage.getUser().id;
    const diaLibreId: number = this.id;

    this.diaLibreService.updateDiaLibre(fecha, jornadaId, diaLibreId).subscribe({
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

}
