export class JornadaEliminada {
    private _idJornadaEliminada: number = 0;
    private _isEliminado: boolean = false;
    private _tipo: string = '';
  
    public constructor(id: number, tipo: string) {
      this._idJornadaEliminada = id;
      this._tipo = tipo;
    }
  
    public get isEliminado(): boolean {
      return this._isEliminado;
    }
    public set isEliminado(value: boolean) {
      this._isEliminado = value;
    }

    public get tipo(): string {
      return this._tipo;
    }

    public get idJornadaEliminada(): number {
      return this._idJornadaEliminada;
    }
  }
  