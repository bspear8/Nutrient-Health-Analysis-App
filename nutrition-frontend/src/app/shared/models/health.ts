import { NutrientType } from './index';

export abstract class HealthEntry {
  id?: string;
  healthEntryType: HealthEntryType;
  value: number;
  units: string;
  date: Date;
  notes: string;

  constructor(healthEntryType: HealthEntryType, value: number, units: string,
              date: Date, notes: string) {
    this.healthEntryType = healthEntryType;
    this.value = value;
    this.units = units;
    this.date = date;
    this.notes = notes;
  }

  static getHealthEntryTypes(): HealthEntryType[] | NutrientType[] {
    return ['Glucose', 'Weight' ];
  }

  static getHealthEntryForType(entryType: HealthEntryType): HealthEntry {
    switch (entryType) {
      case 'Glucose':
        return new GlucoseHealthEntry();
      case 'Weight':
        return new WeightHealthEntry();
    }
  }

  abstract getAvailableUnits(): string[];
}

export type HealthEntryType = 'Glucose' | 'Weight' | NutrientType;

export type GlucoseUnit = 'mg/dL' | 'mmol/L';
export class GlucoseHealthEntry extends HealthEntry {
  constructor(value?: number, units?: GlucoseUnit, date?: Date, notes?: string) {
    super('Glucose', value, units, date, notes);
  }

  static getAvailableUnits(): GlucoseUnit[] {
    return ['mg/dL', 'mmol/L'];
  }

  getAvailableUnits(): GlucoseUnit[] {
    return GlucoseHealthEntry.getAvailableUnits();
  }
}

export type WeightUnit = 'lbs' | 'kg';
export class WeightHealthEntry extends HealthEntry {

  constructor(value?: number, units?: WeightUnit, date?: Date, notes?: string) {
    super('Weight', value, units, date, notes);
  }

  static getAvailableUnits(): WeightUnit[]  {
    return ['lbs', 'kg'];
  }

  getAvailableUnits(): WeightUnit[] {
    return WeightHealthEntry.getAvailableUnits();
  }
}
