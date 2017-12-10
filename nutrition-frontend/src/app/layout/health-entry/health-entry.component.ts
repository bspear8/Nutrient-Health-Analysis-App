import { Component, OnInit } from '@angular/core';
import { HealthEntryType, HealthEntry } from '../../shared/models';
import { HealthService, AlertService } from '../../shared/services';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Component({
  selector: 'app-health-entry',
  templateUrl: './health-entry.component.html',
  styleUrls: ['./health-entry.component.scss']
})
export class HealthEntryComponent implements OnInit {

  availableTypes: HealthEntryType[] = HealthEntry.getHealthEntryTypes();
  availableUnits$: BehaviorSubject<string[]>;
  selectedType: HealthEntryType = null;
  entry: HealthEntry;

  constructor(private healthService: HealthService, private alertService: AlertService) { }

  ngOnInit() {
    this.entry = HealthEntry.getHealthEntryForType(this.availableTypes[0]);
    this.entry.units = null;
    this.availableUnits$ = new BehaviorSubject(null);
  }

  onChangeEntryType(entryType) {
    this.entry = HealthEntry.getHealthEntryForType(entryType);
    this.entry.units = null;
    this.availableUnits$.next(this.entry.getAvailableUnits());
  }

  addEntry() {
    this.healthService.addHealthEntry(this.entry)
    .subscribe(data => {
      this.alertService.success('Added entry');
      this.selectedType = null;
      this.entry = HealthEntry.getHealthEntryForType(this.availableTypes[0]);
      this.availableUnits$.next(null);
    }, error => {
      this.alertService.error(error);
      console.log(error);
    });
  }
}
