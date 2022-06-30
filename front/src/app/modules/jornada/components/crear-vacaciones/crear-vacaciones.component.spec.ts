import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrearVacacionesComponent } from './crear-vacaciones.component';

describe('CrearVacacionesComponent', () => {
  let component: CrearVacacionesComponent;
  let fixture: ComponentFixture<CrearVacacionesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CrearVacacionesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CrearVacacionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
