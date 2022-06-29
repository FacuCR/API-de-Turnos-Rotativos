import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TurnoEliminado } from '../../models/TurnoEliminado';
import { TurnoExtraService } from '../../services/turno-extra/turno-extra.service';
import { TurnoNormalService } from '../../services/turno-normal.service';

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
    private turnoNormalService: TurnoNormalService
  ) {}

  ngOnInit(): void {}

  onNadaClick(): void {
    this.dialogRef.close();
  }

  onEliminarClick(): void {
    if (this.data.evento.tipo === 'turno') {
      this.turnoNormalService
        .deleteTurnoNormal(this.data.evento.id)
        .subscribe({
          next: () => {
            this.turnoEliminado.isEliminado = true;
          },
        })
        .add(() => {
          this.dialogRef.close(this.turnoEliminado);
        });
    } else {
      this.turnoExtraService
        .deleteTurnoExtra(this.data.evento.id)
        .subscribe({
          next: () => {
            this.turnoEliminado.isEliminado = true;
          },
        })
        .add(() => {
          this.dialogRef.close(this.turnoEliminado);
        });
    }
  }
}
