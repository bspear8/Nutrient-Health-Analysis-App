export class Nutrient {
  nutrientId?: number;
  name: String;
  value?: number;
  units?: NutrientUnit;

  constructor(name: NutrientType, value?: number, units?: NutrientUnit) {
    this.name = name;
    this.value = value;
    this.units = units;
  }

  static getAvailableUnits(): NutrientUnit[] {
    return [ 'mg', 'g' ];
  }
}

export type NutrientUnit = 'mg' | 'g';

export type NutrientType =
  'Potassium'
  | 'Sugar'
  | 'Fat'
  | 'Fiber'
  | 'Magnesium'
  | 'Carbohydrate'
  | 'Iron';
