import { Nutrient } from './nutrient';

export class FoodEntry {
  foodId: number;
  name: String;
  notes: String;
  servings: number;
  consumptionDate: Date;
  nutrients: Nutrient[];
}

export class FoodSearchResult {
  nbdno: string;
  name: string;
}
