import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrearDiaLibreComponent } from './crear-dia-libre.component';

describe('CrearDiaLibreComponent', () => {
  let component: CrearDiaLibreComponent;
  let fixture: ComponentFixture<CrearDiaLibreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CrearDiaLibreComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CrearDiaLibreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
