import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HealthGoalsComponent } from './health-goals.component';

describe('HealthGoalsComponent', () => {
  let component: HealthGoalsComponent;
  let fixture: ComponentFixture<HealthGoalsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HealthGoalsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HealthGoalsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
