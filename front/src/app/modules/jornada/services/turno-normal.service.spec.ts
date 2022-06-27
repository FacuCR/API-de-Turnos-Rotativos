import { TestBed } from '@angular/core/testing';

import { TurnoNormalService } from './turno-normal.service';

describe('TurnoNormalService', () => {
  let service: TurnoNormalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TurnoNormalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
