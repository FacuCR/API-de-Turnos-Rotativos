import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { JornadaEliminada } from '../../models/JornadaEliminada';
import { DiaLibreService } from '../../services/dia-libre/dia-libre.service';
import { TurnoExtraService } from '../../services/turno-extra/turno-extra.service';
import { TurnoNormalService } from '../../services/turno-normal.service';
import { VacacionesService } from '../../services/vacaciones/vacaciones.service';

@Component({
  selector: 'app-dialog-event',
  templateUrl: './dialog-event.component.html',
  styleUrls: ['./dialog-event.component.css'],
})
export class DialogEventComponent implements OnInit {
  jornadaEliminada: JornadaEliminada = new JornadaEliminada(this.data.evento.id, this.data.evento.tipo);

  eliminado: boolean = false;
  constructor(
    public dialogRef: MatDialogRef<DialogEventComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private turnoExtraService: TurnoExtraService,
    private turnoNormalService: TurnoNormalService,
    private diaLibreService: DiaLibreService,
    private vacacionesService: VacacionesService,
    private router: Router,
  ) {}

  ngOnInit(): void {}

  onNadaClick(): void {
    this.dialogRef.close();
  }

  onEliminarClick(): void {
    switch (this.data.evento.tipo) {
      case 'turno':
        this.turnoNormalService
          .deleteTurnoNormal(this.data.evento.id)
          .subscribe({
            next: () => {
              this.jornadaEliminada.isEliminado = true;
            },
            error: () => {
              this.jornadaEliminada.isEliminado = false;
            }
          })
          .add(() => {
            this.dialogRef.close(this.jornadaEliminada);
          });
        break;
      case 'extra':
        this.turnoExtraService
          .deleteTurnoExtra(this.data.evento.id)
          .subscribe({
            next: () => {
              this.jornadaEliminada.isEliminado = true;
            },
            error: () => {
              this.jornadaEliminada.isEliminado = false;
            }
          })
          .add(() => {
            this.dialogRef.close(this.jornadaEliminada);
          });
        break;
      case 'libre':
        this.diaLibreService
          .deleteDiaLibre(this.data.evento.id)
          .subscribe({
            next: () => {
              this.jornadaEliminada.isEliminado = true;
            },
            error: () => {
              this.jornadaEliminada.isEliminado = false;
            }
          })
          .add(() => {
            this.dialogRef.close(this.jornadaEliminada);
          });
        break;
      case 'vacaciones':
        this.vacacionesService
          .deleteVacaciones(this.data.evento.id)
          .subscribe({
            next: () => {
              this.jornadaEliminada.isEliminado = true;
            },
            error: () => {
              this.jornadaEliminada.isEliminado = false;
            }
          })
          .add(() => {
            this.dialogRef.close(this.jornadaEliminada);
          });
        break;

      default:
        break;
    }
  }

  onActualizarClick(): void {
    let dataEventoAActualizar = {
      id: this.data.evento.id,
      tipo: this.data.evento.tipo,
      fechaInicio: this.data.evento.start
    }
    this.dialogRef.close(this.jornadaEliminada);
    this.router.navigate(['jornada/actualizar', {datos_evento: JSON.stringify(dataEventoAActualizar)}]);
  }
}
