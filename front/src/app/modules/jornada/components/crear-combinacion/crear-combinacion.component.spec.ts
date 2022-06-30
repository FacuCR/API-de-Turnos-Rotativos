import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrearCombinacionComponent } from './crear-combinacion.component';

describe('CrearCombinacionComponent', () => {
  let component: CrearCombinacionComponent;
  let fixture: ComponentFixture<CrearCombinacionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CrearCombinacionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CrearCombinacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
