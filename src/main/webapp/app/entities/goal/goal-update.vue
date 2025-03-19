<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="aiAssistantApp.goal.home.createOrEditLabel" data-cy="GoalCreateUpdateHeading">创建或编辑 Goal</h2>
        <div>
          <div class="form-group" v-if="goal.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="goal.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="goal-title">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="goal-title"
              data-cy="title"
              :class="{ valid: !v$.title.$invalid, invalid: v$.title.$invalid }"
              v-model="v$.title.$model"
              required
            />
            <div v-if="v$.title.$anyDirty && v$.title.$invalid">
              <small class="form-text text-danger" v-for="error of v$.title.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="goal-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="goal-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            />
            <div v-if="v$.description.$anyDirty && v$.description.$invalid">
              <small class="form-text text-danger" v-for="error of v$.description.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="goal-goalType">Goal Type</label>
            <select
              class="form-control"
              name="goalType"
              :class="{ valid: !v$.goalType.$invalid, invalid: v$.goalType.$invalid }"
              v-model="v$.goalType.$model"
              id="goal-goalType"
              data-cy="goalType"
              required
            >
              <option v-for="goalType in goalTypeValues" :key="goalType" :value="goalType">{{ goalType }}</option>
            </select>
            <div v-if="v$.goalType.$anyDirty && v$.goalType.$invalid">
              <small class="form-text text-danger" v-for="error of v$.goalType.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="goal-deadline">Deadline</label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="goal-deadline"
                  v-model="v$.deadline.$model"
                  name="deadline"
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
                id="goal-deadline"
                data-cy="deadline"
                type="text"
                class="form-control"
                name="deadline"
                :class="{ valid: !v$.deadline.$invalid, invalid: v$.deadline.$invalid }"
                v-model="v$.deadline.$model"
              />
            </b-input-group>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="goal-completed">Completed</label>
            <input
              type="checkbox"
              class="form-check"
              name="completed"
              id="goal-completed"
              data-cy="completed"
              :class="{ valid: !v$.completed.$invalid, invalid: v$.completed.$invalid }"
              v-model="v$.completed.$model"
              required
            />
            <div v-if="v$.completed.$anyDirty && v$.completed.$invalid">
              <small class="form-text text-danger" v-for="error of v$.completed.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="goal-createdAt">Created At</label>
            <div class="d-flex">
              <input
                id="goal-createdAt"
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
            <label class="form-control-label" for="goal-updatedAt">Updated At</label>
            <div class="d-flex">
              <input
                id="goal-updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                class="form-control"
                name="updatedAt"
                :class="{ valid: !v$.updatedAt.$invalid, invalid: v$.updatedAt.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.updatedAt.$model)"
                @change="updateInstantField('updatedAt', $event)"
              />
            </div>
            <div v-if="v$.updatedAt.$anyDirty && v$.updatedAt.$invalid">
              <small class="form-text text-danger" v-for="error of v$.updatedAt.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="goal-user">User</label>
            <select class="form-control" id="goal-user" data-cy="user" name="user" v-model="goal.user">
              <option :value="null"></option>
              <option
                :value="goal.user && userOption.id === goal.user.id ? goal.user : userOption"
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
<script lang="ts" src="./goal-update.component.ts"></script>
