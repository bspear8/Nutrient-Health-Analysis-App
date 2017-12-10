import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FoodEntryComponent } from './food-entry.component';

describe('FoodEntryComponent', () => {
  let component: FoodEntryComponent;
  let fixture: ComponentFixture<FoodEntryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FoodEntryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FoodEntryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
