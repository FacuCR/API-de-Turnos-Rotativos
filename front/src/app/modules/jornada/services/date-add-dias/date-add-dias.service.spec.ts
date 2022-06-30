import { TestBed } from '@angular/core/testing';

import { DateAddDiasService } from './date-add-dias.service';

describe('DateAddDiasService', () => {
  let service: DateAddDiasService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DateAddDiasService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
