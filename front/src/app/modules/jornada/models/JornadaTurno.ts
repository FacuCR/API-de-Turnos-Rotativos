import { Turnos } from "src/app/core/models/Turnos";

export abstract class JornadaTurno {
    idTurnoNormal: number = 0;
    fecha: Date = new Date();
    turno: Turnos = Turnos.maniana;
    cantHoras: string = '';
    usuarioId: number = 0;
}