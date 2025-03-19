import { Authority } from '@/shared/security/authority';
const Entities = () => import('@/entities/entities.vue');

const FinanceRecord = () => import('@/entities/finance-record/finance-record.vue');
const FinanceRecordUpdate = () => import('@/entities/finance-record/finance-record-update.vue');
const FinanceRecordDetails = () => import('@/entities/finance-record/finance-record-details.vue');

const FocusSession = () => import('@/entities/focus-session/focus-session.vue');
const FocusSessionUpdate = () => import('@/entities/focus-session/focus-session-update.vue');
const FocusSessionDetails = () => import('@/entities/focus-session/focus-session-details.vue');

const Goal = () => import('@/entities/goal/goal.vue');
const GoalUpdate = () => import('@/entities/goal/goal-update.vue');
const GoalDetails = () => import('@/entities/goal/goal-details.vue');

const Habit = () => import('@/entities/habit/habit.vue');
const HabitUpdate = () => import('@/entities/habit/habit-update.vue');
const HabitDetails = () => import('@/entities/habit/habit-details.vue');

const JournalEntry = () => import('@/entities/journal-entry/journal-entry.vue');
const JournalEntryUpdate = () => import('@/entities/journal-entry/journal-entry-update.vue');
const JournalEntryDetails = () => import('@/entities/journal-entry/journal-entry-details.vue');

const MoodTracker = () => import('@/entities/mood-tracker/mood-tracker.vue');
const MoodTrackerUpdate = () => import('@/entities/mood-tracker/mood-tracker-update.vue');
const MoodTrackerDetails = () => import('@/entities/mood-tracker/mood-tracker-details.vue');

const ReadingList = () => import('@/entities/reading-list/reading-list.vue');
const ReadingListUpdate = () => import('@/entities/reading-list/reading-list-update.vue');
const ReadingListDetails = () => import('@/entities/reading-list/reading-list-details.vue');

const Reminder = () => import('@/entities/reminder/reminder.vue');
const ReminderUpdate = () => import('@/entities/reminder/reminder-update.vue');
const ReminderDetails = () => import('@/entities/reminder/reminder-details.vue');

const SocialConnection = () => import('@/entities/social-connection/social-connection.vue');
const SocialConnectionUpdate = () => import('@/entities/social-connection/social-connection-update.vue');
const SocialConnectionDetails = () => import('@/entities/social-connection/social-connection-details.vue');

const Task = () => import('@/entities/task/task.vue');
const TaskUpdate = () => import('@/entities/task/task-update.vue');
const TaskDetails = () => import('@/entities/task/task-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'finance-record',
      name: 'FinanceRecord',
      component: FinanceRecord,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'finance-record/new',
      name: 'FinanceRecordCreate',
      component: FinanceRecordUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'finance-record/:financeRecordId/edit',
      name: 'FinanceRecordEdit',
      component: FinanceRecordUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'finance-record/:financeRecordId/view',
      name: 'FinanceRecordView',
      component: FinanceRecordDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'focus-session',
      name: 'FocusSession',
      component: FocusSession,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'focus-session/new',
      name: 'FocusSessionCreate',
      component: FocusSessionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'focus-session/:focusSessionId/edit',
      name: 'FocusSessionEdit',
      component: FocusSessionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'focus-session/:focusSessionId/view',
      name: 'FocusSessionView',
      component: FocusSessionDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'goal',
      name: 'Goal',
      component: Goal,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'goal/new',
      name: 'GoalCreate',
      component: GoalUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'goal/:goalId/edit',
      name: 'GoalEdit',
      component: GoalUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'goal/:goalId/view',
      name: 'GoalView',
      component: GoalDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'habit',
      name: 'Habit',
      component: Habit,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'habit/new',
      name: 'HabitCreate',
      component: HabitUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'habit/:habitId/edit',
      name: 'HabitEdit',
      component: HabitUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'habit/:habitId/view',
      name: 'HabitView',
      component: HabitDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'journal-entry',
      name: 'JournalEntry',
      component: JournalEntry,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'journal-entry/new',
      name: 'JournalEntryCreate',
      component: JournalEntryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'journal-entry/:journalEntryId/edit',
      name: 'JournalEntryEdit',
      component: JournalEntryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'journal-entry/:journalEntryId/view',
      name: 'JournalEntryView',
      component: JournalEntryDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mood-tracker',
      name: 'MoodTracker',
      component: MoodTracker,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mood-tracker/new',
      name: 'MoodTrackerCreate',
      component: MoodTrackerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mood-tracker/:moodTrackerId/edit',
      name: 'MoodTrackerEdit',
      component: MoodTrackerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'mood-tracker/:moodTrackerId/view',
      name: 'MoodTrackerView',
      component: MoodTrackerDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'reading-list',
      name: 'ReadingList',
      component: ReadingList,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'reading-list/new',
      name: 'ReadingListCreate',
      component: ReadingListUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'reading-list/:readingListId/edit',
      name: 'ReadingListEdit',
      component: ReadingListUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'reading-list/:readingListId/view',
      name: 'ReadingListView',
      component: ReadingListDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'reminder',
      name: 'Reminder',
      component: Reminder,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'reminder/new',
      name: 'ReminderCreate',
      component: ReminderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'reminder/:reminderId/edit',
      name: 'ReminderEdit',
      component: ReminderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'reminder/:reminderId/view',
      name: 'ReminderView',
      component: ReminderDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'social-connection',
      name: 'SocialConnection',
      component: SocialConnection,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'social-connection/new',
      name: 'SocialConnectionCreate',
      component: SocialConnectionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'social-connection/:socialConnectionId/edit',
      name: 'SocialConnectionEdit',
      component: SocialConnectionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'social-connection/:socialConnectionId/view',
      name: 'SocialConnectionView',
      component: SocialConnectionDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'task',
      name: 'Task',
      component: Task,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'task/new',
      name: 'TaskCreate',
      component: TaskUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'task/:taskId/edit',
      name: 'TaskEdit',
      component: TaskUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'task/:taskId/view',
      name: 'TaskView',
      component: TaskDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
