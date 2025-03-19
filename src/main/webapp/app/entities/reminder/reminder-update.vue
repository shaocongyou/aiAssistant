<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="aiAssistantApp.reminder.home.createOrEditLabel" data-cy="ReminderCreateUpdateHeading">创建或编辑 Reminder</h2>
        <div>
          <div class="form-group" v-if="reminder.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="reminder.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="reminder-message">Message</label>
            <input
              type="text"
              class="form-control"
              name="message"
              id="reminder-message"
              data-cy="message"
              :class="{ valid: !v$.message.$invalid, invalid: v$.message.$invalid }"
              v-model="v$.message.$model"
              required
            />
            <div v-if="v$.message.$anyDirty && v$.message.$invalid">
              <small class="form-text text-danger" v-for="error of v$.message.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="reminder-reminderTime">Reminder Time</label>
            <div class="d-flex">
              <input
                id="reminder-reminderTime"
                data-cy="reminderTime"
                type="datetime-local"
                class="form-control"
                name="reminderTime"
                :class="{ valid: !v$.reminderTime.$invalid, invalid: v$.reminderTime.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.reminderTime.$model)"
                @change="updateInstantField('reminderTime', $event)"
              />
            </div>
            <div v-if="v$.reminderTime.$anyDirty && v$.reminderTime.$invalid">
              <small class="form-text text-danger" v-for="error of v$.reminderTime.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="reminder-repeatInterval">Repeat Interval</label>
            <input
              type="text"
              class="form-control"
              name="repeatInterval"
              id="reminder-repeatInterval"
              data-cy="repeatInterval"
              :class="{ valid: !v$.repeatInterval.$invalid, invalid: v$.repeatInterval.$invalid }"
              v-model="v$.repeatInterval.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="reminder-createdAt">Created At</label>
            <div class="d-flex">
              <input
                id="reminder-createdAt"
                data-cy="createdAt"
                type="datetime-local"
                class="form-control"
                name="createdAt"
                :class="{ valid: !v$.createdAt.$invalid, invalid: v$.createdAt.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.createdAt.$model)"
                @change="updateInstantField('createdAt', $event)"
              />
            </div>
            <div v-if="v$.createdAt.$anyDirty && v$.createdAt.$invalid">
              <small class="form-text text-danger" v-for="error of v$.createdAt.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="reminder-user">User</label>
            <select class="form-control" id="reminder-user" data-cy="user" name="user" v-model="reminder.user">
              <option :value="null"></option>
              <option
                :value="reminder.user && userOption.id === reminder.user.id ? reminder.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="reminder-task">Task</label>
            <select class="form-control" id="reminder-task" data-cy="task" name="task" v-model="reminder.task">
              <option :value="null"></option>
              <option
                :value="reminder.task && taskOption.id === reminder.task.id ? reminder.task : taskOption"
                v-for="taskOption in tasks"
                :key="taskOption.id"
              >
                {{ taskOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>取消</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>保存</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./reminder-update.component.ts"></script>
