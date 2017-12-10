import { Nutrient, NutrientUnit } from './nutrient';
import { HealthEntryType, GlucoseUnit, WeightUnit, GlucoseHealthEntry, WeightHealthEntry } from './health';
import { NutrientType } from './nutrient';

export type GoalRangeType = 'Greater Than' | 'Less Than';
export type GoalEntryType = HealthEntryType | NutrientType;
export type GoalTimePeriod = 'Instant' | 'Last 24 Hours' | 'Daily Average for Last Week';

export class Goal {
    goalId: number;
    name: string;
    value: number;
    date: Date;
    entryType: GoalEntryType;
    units: string;
    goalType: GoalRangeType;
    timePeriod: GoalTimePeriod;

    static getGoalTimePeriods(): GoalTimePeriod[] {
        return ['Instant', 'Last 24 Hours', 'Daily Average for Last Week'];
    }

    static getGoalRangeTypes(): GoalRangeType[] {
        return ['Greater Than', 'Less Than'];
    }

    static getGoalEntryTypes(): GoalEntryType[] {
        return ['Glucose', 'Weight', 'Carbohydrate', 'Fat', 'Fiber', 'Iron', 'Magnesium', 'Potassium', 'Sugar'];
    }

    static getUnitsForType(type: GoalEntryType): GlucoseUnit[] | WeightUnit[] | NutrientUnit[] {
        switch (type) {
            case 'Glucose':
                return GlucoseHealthEntry.getAvailableUnits();
            case 'Weight':
                return WeightHealthEntry.getAvailableUnits();
            case 'Carbohydrate':
            case 'Fat':
            case 'Fiber':
            case 'Glucose':
            case 'Iron':
            case 'Magnesium':
            case 'Potassium':
            case 'Sugar':
            case 'Weight':
                return Nutrient.getAvailableUnits();
            default:
                throw new Error('Invalid health entry type');
        }
    }
}

export class HealthAlert {
    alertId: number;
    entryType: GoalEntryType;
    value: number;
    units: string;
    alertType: GoalRangeType;
    timePeriod: GoalTimePeriod;
}

export class GoalState {
    goal: Goal;
    state: String;
    currentValue: number;
}

export class HealthAlertState {
    alert: HealthAlert;
    state: String;
    currentValue: number;
}
