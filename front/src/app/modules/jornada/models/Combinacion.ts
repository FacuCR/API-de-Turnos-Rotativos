import { Turnos } from 'src/app/core/models/Turnos';
export class Combinacion {
    fecha: string = "";
    turnoNormal: Turnos = Turnos.maniana;
    turnoExtra: Turnos = Turnos.maniana;
    cantHorasNormal: number = 0;
    cantHorasExtra: number = 0;
}