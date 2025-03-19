<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="aiAssistantApp.moodTracker.home.createOrEditLabel" data-cy="MoodTrackerCreateUpdateHeading">创建或编辑 Mood Tracker</h2>
        <div>
          <div class="form-group" v-if="moodTracker.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="moodTracker.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="mood-tracker-mood">Mood</label>
            <input
              type="text"
              class="form-control"
              name="mood"
              id="mood-tracker-mood"
              data-cy="mood"
              :class="{ valid: !v$.mood.$invalid, invalid: v$.mood.$invalid }"
              v-model="v$.mood.$model"
              required
            />
            <div v-if="v$.mood.$anyDirty && v$.mood.$invalid">
              <small class="form-text text-danger" v-for="error of v$.mood.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="mood-tracker-intensity">Intensity</label>
            <input
              type="number"
              class="form-control"
              name="intensity"
              id="mood-tracker-intensity"
              data-cy="intensity"
              :class="{ valid: !v$.intensity.$invalid, invalid: v$.intensity.$invalid }"
              v-model.number="v$.intensity.$model"
              required
            />
            <div v-if="v$.intensity.$anyDirty && v$.intensity.$invalid">
              <small class="form-text text-danger" v-for="error of v$.intensity.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="mood-tracker-date">Date</label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="mood-tracker-date"
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
                id="mood-tracker-date"
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
            <label class="form-control-label" for="mood-tracker-createdAt">Created At</label>
            <div class="d-flex">
              <input
                id="mood-tracker-createdAt"
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
            <label class="form-control-label" for="mood-tracker-user">User</label>
            <select class="form-control" id="mood-tracker-user" data-cy="user" name="user" v-model="moodTracker.user">
              <option :value="null"></option>
              <option
                :value="moodTracker.user && userOption.id === moodTracker.user.id ? moodTracker.user : userOption"
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
<script lang="ts" src="./mood-tracker-update.component.ts"></script>
