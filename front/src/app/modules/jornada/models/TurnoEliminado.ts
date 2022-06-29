export class TurnoEliminado {
    private _idTurnoEliminado: number = 0;
    private _isEliminado: boolean = false;
  
    public constructor(id: number) {
      this._idTurnoEliminado = id;
    }
  
    public get isEliminado(): boolean {
      return this._isEliminado;
    }
    public set isEliminado(value: boolean) {
      this._isEliminado = value;
    }

    public get idTurnoEliminado(): number {
      return this._idTurnoEliminado;
    }
  }
  