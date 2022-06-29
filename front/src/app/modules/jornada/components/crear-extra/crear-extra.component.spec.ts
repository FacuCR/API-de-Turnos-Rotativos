import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrearExtraComponent } from './crear-extra.component';

describe('CrearExtraComponent', () => {
  let component: CrearExtraComponent;
  let fixture: ComponentFixture<CrearExtraComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CrearExtraComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CrearExtraComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
