import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormulariosJornadaComponent } from './formularios-jornada.component';

describe('FormulariosJornadaComponent', () => {
  let component: FormulariosJornadaComponent;
  let fixture: ComponentFixture<FormulariosJornadaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FormulariosJornadaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FormulariosJornadaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
