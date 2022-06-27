import { TestBed } from '@angular/core/testing';

import { TurnoExtraService } from './turno-extra.service';

describe('TurnoExtraService', () => {
  let service: TurnoExtraService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TurnoExtraService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
