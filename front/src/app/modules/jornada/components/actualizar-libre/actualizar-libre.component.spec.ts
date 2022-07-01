import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActualizarLibreComponent } from './actualizar-libre.component';

describe('ActualizarLibreComponent', () => {
  let component: ActualizarLibreComponent;
  let fixture: ComponentFixture<ActualizarLibreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ActualizarLibreComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ActualizarLibreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
