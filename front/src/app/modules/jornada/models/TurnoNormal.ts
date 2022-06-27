import { Turnos } from "src/app/core/models/Turnos";

export class TurnoNormal {
    idTurnoNormal: number = 0;
    fecha: Date = new Date();
    turno: Turnos = Turnos.maniana;
    cantHoras: string = '6 horas';
    usuarioId: number = 0;
}