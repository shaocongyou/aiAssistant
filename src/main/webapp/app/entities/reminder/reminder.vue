<template>
  <div>
    <h2 id="page-heading" data-cy="ReminderHeading">
      <span id="reminder-heading">Reminders</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'ReminderCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-reminder"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>创建新 Reminder</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && reminders && reminders.length === 0">
      <span>No Reminders found</span>
    </div>
    <div class="table-responsive" v-if="reminders && reminders.length > 0">
      <table class="table table-striped" aria-describedby="reminders">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span>ID</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('message')">
              <span>Message</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'message'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('reminderTime')">
              <span>Reminder Time</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'reminderTime'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('repeatInterval')">
              <span>Repeat Interval</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'repeatInterval'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createdAt')">
              <span>Created At</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('user.id')">
              <span>User</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'user.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('task.id')">
              <span>Task</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'task.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="reminder in reminders" :key="reminder.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ReminderView', params: { reminderId: reminder.id } }">{{ reminder.id }}</router-link>
            </td>
            <td>{{ reminder.message }}</td>
            <td>{{ formatDateShort(reminder.reminderTime) || '' }}</td>
            <td>{{ reminder.repeatInterval }}</td>
            <td>{{ formatDateShort(reminder.createdAt) || '' }}</td>
            <td>
              {{ reminder.user ? reminder.user.id : '' }}
            </td>
            <td>
              <div v-if="reminder.task">
                <router-link :to="{ name: 'TaskView', params: { taskId: reminder.task.id } }">{{ reminder.task.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ReminderView', params: { reminderId: reminder.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">查看</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ReminderEdit', params: { reminderId: reminder.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">编辑</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(reminder)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">删除</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="aiAssistantApp.reminder.delete.question" data-cy="reminderDeleteDialogHeading">确认删除</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-reminder-heading">你确定要删除 Reminder {{ removeId }} 吗？</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">取消</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-reminder"
            data-cy="entityConfirmDeleteButton"
            @click="removeReminder()"
          >
            删除
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="reminders && reminders.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./reminder.component.ts"></script>
