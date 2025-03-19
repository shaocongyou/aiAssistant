import { defineComponent, provide } from 'vue';

import FinanceRecordService from './finance-record/finance-record.service';
import FocusSessionService from './focus-session/focus-session.service';
import GoalService from './goal/goal.service';
import HabitService from './habit/habit.service';
import JournalEntryService from './journal-entry/journal-entry.service';
import MoodTrackerService from './mood-tracker/mood-tracker.service';
import ReadingListService from './reading-list/reading-list.service';
import ReminderService from './reminder/reminder.service';
import SocialConnectionService from './social-connection/social-connection.service';
import TaskService from './task/task.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('financeRecordService', () => new FinanceRecordService());
    provide('focusSessionService', () => new FocusSessionService());
    provide('goalService', () => new GoalService());
    provide('habitService', () => new HabitService());
    provide('journalEntryService', () => new JournalEntryService());
    provide('moodTrackerService', () => new MoodTrackerService());
    provide('readingListService', () => new ReadingListService());
    provide('reminderService', () => new ReminderService());
    provide('socialConnectionService', () => new SocialConnectionService());
    provide('taskService', () => new TaskService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
