import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

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

  constructor(public fb: FormBuilder) {}

  ngOnInit(): void {}

  onSubmit(): void {}

  getMensajeDeError(input: string) {
    return this.form.controls[input].hasError('required')
      ? 'Debes ingresar algo!'
      : '';
  }
}
