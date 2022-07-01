import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActualizarTurnoComponent } from './actualizar-turno.component';

describe('ActualizarTurnoComponent', () => {
  let component: ActualizarTurnoComponent;
  let fixture: ComponentFixture<ActualizarTurnoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ActualizarTurnoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ActualizarTurnoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
