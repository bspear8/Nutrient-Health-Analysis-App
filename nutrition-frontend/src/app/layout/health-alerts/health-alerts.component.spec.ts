import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HealthAlertsComponent } from './health-alerts.component';

describe('HealthAlertsComponent', () => {
  let component: HealthAlertsComponent;
  let fixture: ComponentFixture<HealthAlertsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HealthAlertsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HealthAlertsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
