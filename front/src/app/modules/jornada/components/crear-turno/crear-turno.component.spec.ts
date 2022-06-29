import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrearTurnoComponent } from './crear-turno.component';

describe('CrearTurnoComponent', () => {
  let component: CrearTurnoComponent;
  let fixture: ComponentFixture<CrearTurnoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CrearTurnoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CrearTurnoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
