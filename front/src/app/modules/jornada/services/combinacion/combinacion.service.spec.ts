import { TestBed } from '@angular/core/testing';

import { CombinacionService } from './combinacion.service';

describe('CombinacionService', () => {
  let service: CombinacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CombinacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
