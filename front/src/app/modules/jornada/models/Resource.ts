export class Resource {
  _id: number = 0;
  _name: string = '';
  _user: string = '';

  public constructor() {}

  public get id(): number {
    return this._id;
  }
  public set id(value: number) {
    this._id = value;
  }

  public get name(): string {
    return this._name;
  }
  public set name(value: string) {
    this._name = value;
  }

  public get user(): string {
    return this._user;
  }
  public set user(value: string) {
    this._user = value;
  }
}
