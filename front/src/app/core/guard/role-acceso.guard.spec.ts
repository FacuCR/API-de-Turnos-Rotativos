import { TestBed } from '@angular/core/testing';

import { RoleAccesoGuard } from './role-acceso.guard';

describe('RoleAccesoGuard', () => {
  let guard: RoleAccesoGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(RoleAccesoGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
