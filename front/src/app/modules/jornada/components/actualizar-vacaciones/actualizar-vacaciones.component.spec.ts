import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActualizarVacacionesComponent } from './actualizar-vacaciones.component';

describe('ActualizarVacacionesComponent', () => {
  let component: ActualizarVacacionesComponent;
  let fixture: ComponentFixture<ActualizarVacacionesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ActualizarVacacionesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ActualizarVacacionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
