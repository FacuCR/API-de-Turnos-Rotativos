import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TurnoEliminado } from '../../models/TurnoEliminado';
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
  turnoEliminado: TurnoEliminado = new TurnoEliminado(this.data.evento.id);

  eliminado: boolean = false;
  constructor(
    public dialogRef: MatDialogRef<DialogEventComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private turnoExtraService: TurnoExtraService,
    private turnoNormalService: TurnoNormalService,
    private diaLibreService: DiaLibreService,
    private vacacionesService: VacacionesService
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
              this.turnoEliminado.isEliminado = true;
            },
            error: () => {
              this.turnoEliminado.isEliminado = false;
            }
          })
          .add(() => {
            this.dialogRef.close(this.turnoEliminado);
          });
        break;
      case 'extra':
        this.turnoExtraService
          .deleteTurnoExtra(this.data.evento.id)
          .subscribe({
            next: () => {
              this.turnoEliminado.isEliminado = true;
            },
            error: () => {
              this.turnoEliminado.isEliminado = false;
            }
          })
          .add(() => {
            this.dialogRef.close(this.turnoEliminado);
          });
        break;
      case 'libre':
        this.diaLibreService
          .deleteDiaLibre(this.data.evento.id)
          .subscribe({
            next: () => {
              this.turnoEliminado.isEliminado = true;
            },
            error: () => {
              this.turnoEliminado.isEliminado = false;
            }
          })
          .add(() => {
            this.dialogRef.close(this.turnoEliminado);
          });
        break;
      case 'vacaciones':
        this.vacacionesService
          .deleteVacaciones(this.data.evento.id)
          .subscribe({
            next: () => {
              this.turnoEliminado.isEliminado = true;
            },
            error: () => {
              this.turnoEliminado.isEliminado = false;
            }
          })
          .add(() => {
            this.dialogRef.close(this.turnoEliminado);
          });
        break;

      default:
        break;
    }
  }
}
