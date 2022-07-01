import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-index-jornada',
  templateUrl: './index-jornada.component.html',
  styleUrls: ['./index-jornada.component.css'],
})
export class IndexJornadaComponent implements OnInit {
  cargando: boolean = false;

  constructor() {}

  ngOnInit(): void {}

  recibirCargando(cargando: boolean) {
    if (cargando) {
      this.cargando = cargando;
    }
  }
}
