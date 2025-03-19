<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="reminder">
        <h2 class="jh-entity-heading" data-cy="reminderDetailsHeading"><span>Reminder</span> {{ reminder.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Message</span>
          </dt>
          <dd>
            <span>{{ reminder.message }}</span>
          </dd>
          <dt>
            <span>Reminder Time</span>
          </dt>
          <dd>
            <span v-if="reminder.reminderTime">{{ formatDateLong(reminder.reminderTime) }}</span>
          </dd>
          <dt>
            <span>Repeat Interval</span>
          </dt>
          <dd>
            <span>{{ reminder.repeatInterval }}</span>
          </dd>
          <dt>
            <span>Created At</span>
          </dt>
          <dd>
            <span v-if="reminder.createdAt">{{ formatDateLong(reminder.createdAt) }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ reminder.user ? reminder.user.id : '' }}
          </dd>
          <dt>
            <span>Task</span>
          </dt>
          <dd>
            <div v-if="reminder.task">
              <router-link :to="{ name: 'TaskView', params: { taskId: reminder.task.id } }">{{ reminder.task.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>返回</span>
        </button>
        <router-link v-if="reminder.id" :to="{ name: 'ReminderEdit', params: { reminderId: reminder.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>编辑</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./reminder-details.component.ts"></script>
