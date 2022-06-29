import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IndexJornadaComponent } from './index-jornada.component';

describe('IndexJornadaComponent', () => {
  let component: IndexJornadaComponent;
  let fixture: ComponentFixture<IndexJornadaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IndexJornadaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IndexJornadaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
