import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActualizarExtraComponent } from './actualizar-extra.component';

describe('ActualizarExtraComponent', () => {
  let component: ActualizarExtraComponent;
  let fixture: ComponentFixture<ActualizarExtraComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ActualizarExtraComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ActualizarExtraComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
