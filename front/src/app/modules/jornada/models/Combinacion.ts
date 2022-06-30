import { Turnos } from 'src/app/core/models/Turnos';
export class Combinacion {
  private _fecha: string = '';
  private _turnoNormal: Turnos = Turnos.maniana;
  private _turnoExtra: Turnos = Turnos.maniana;
  private _cantHorasNormal: number = 0;
  private _cantHorasExtra: number = 0;

  public constructor() {}

  public get fecha(): string {
    return this._fecha;
  }
  public set fecha(value: string) {
    this._fecha = value;
  }

  public get turnoNormal(): Turnos {
    return this._turnoNormal;
  }
  public set turnoNormal(value: Turnos) {
    this._turnoNormal = value;
  }

  public get turnoExtra(): Turnos {
    return this._turnoExtra;
  }
  public set turnoExtra(value: Turnos) {
    this._turnoExtra = value;
  }

  public get cantHorasNormal(): number {
    return this._cantHorasNormal;
  }
  public set cantHorasNormal(value: number) {
    this._cantHorasNormal = value;
  }

  public get cantHorasExtra(): number {
    return this._cantHorasExtra;
  }
  public set cantHorasExtra(value: number) {
    this._cantHorasExtra = value;
  }
}
