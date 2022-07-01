import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormActualizarComponent } from './form-actualizar.component';

describe('FormActualizarComponent', () => {
  let component: FormActualizarComponent;
  let fixture: ComponentFixture<FormActualizarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FormActualizarComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FormActualizarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
