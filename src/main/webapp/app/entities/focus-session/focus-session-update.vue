<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="aiAssistantApp.focusSession.home.createOrEditLabel" data-cy="FocusSessionCreateUpdateHeading">创建或编辑 Focus Session</h2>
        <div>
          <div class="form-group" v-if="focusSession.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="focusSession.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="focus-session-duration">Duration</label>
            <input
              type="number"
              class="form-control"
              name="duration"
              id="focus-session-duration"
              data-cy="duration"
              :class="{ valid: !v$.duration.$invalid, invalid: v$.duration.$invalid }"
              v-model.number="v$.duration.$model"
              required
            />
            <div v-if="v$.duration.$anyDirty && v$.duration.$invalid">
              <small class="form-text text-danger" v-for="error of v$.duration.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="focus-session-task">Task</label>
            <input
              type="text"
              class="form-control"
              name="task"
              id="focus-session-task"
              data-cy="task"
              :class="{ valid: !v$.task.$invalid, invalid: v$.task.$invalid }"
              v-model="v$.task.$model"
            />
            <div v-if="v$.task.$anyDirty && v$.task.$invalid">
              <small class="form-text text-danger" v-for="error of v$.task.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="focus-session-date">Date</label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="focus-session-date"
                  v-model="v$.date.$model"
                  name="date"
                  class="form-control"
                  :locale="currentLanguage"
                  button-only
                  today-button
                  reset-button
                  close-button
                >
                </b-form-datepicker>
              </b-input-group-prepend>
              <b-form-input
                id="focus-session-date"
                data-cy="date"
                type="text"
                class="form-control"
                name="date"
                :class="{ valid: !v$.date.$invalid, invalid: v$.date.$invalid }"
                v-model="v$.date.$model"
                required
              />
            </b-input-group>
            <div v-if="v$.date.$anyDirty && v$.date.$invalid">
              <small class="form-text text-danger" v-for="error of v$.date.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="focus-session-createdAt">Created At</label>
            <div class="d-flex">
              <input
                id="focus-session-createdAt"
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
            <label class="form-control-label" for="focus-session-user">User</label>
            <select class="form-control" id="focus-session-user" data-cy="user" name="user" v-model="focusSession.user">
              <option :value="null"></option>
              <option
                :value="focusSession.user && userOption.id === focusSession.user.id ? focusSession.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.id }}
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
<script lang="ts" src="./focus-session-update.component.ts"></script>
