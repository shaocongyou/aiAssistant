<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="aiAssistantApp.task.home.createOrEditLabel" data-cy="TaskCreateUpdateHeading">创建或编辑 Task</h2>
        <div>
          <div class="form-group" v-if="task.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="task.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="task-title">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="task-title"
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
            <label class="form-control-label" for="task-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="task-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            />
            <div v-if="v$.description.$anyDirty && v$.description.$invalid">
              <small class="form-text text-danger" v-for="error of v$.description.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="task-status">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="task-status"
              data-cy="status"
              required
            >
              <option v-for="taskStatus in taskStatusValues" :key="taskStatus" :value="taskStatus">{{ taskStatus }}</option>
            </select>
            <div v-if="v$.status.$anyDirty && v$.status.$invalid">
              <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="task-dueDate">Due Date</label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="task-dueDate"
                  v-model="v$.dueDate.$model"
                  name="dueDate"
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
                id="task-dueDate"
                data-cy="dueDate"
                type="text"
                class="form-control"
                name="dueDate"
                :class="{ valid: !v$.dueDate.$invalid, invalid: v$.dueDate.$invalid }"
                v-model="v$.dueDate.$model"
              />
            </b-input-group>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="task-priority">Priority</label>
            <input
              type="number"
              class="form-control"
              name="priority"
              id="task-priority"
              data-cy="priority"
              :class="{ valid: !v$.priority.$invalid, invalid: v$.priority.$invalid }"
              v-model.number="v$.priority.$model"
              required
            />
            <div v-if="v$.priority.$anyDirty && v$.priority.$invalid">
              <small class="form-text text-danger" v-for="error of v$.priority.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="task-createdAt">Created At</label>
            <div class="d-flex">
              <input
                id="task-createdAt"
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
            <label class="form-control-label" for="task-updatedAt">Updated At</label>
            <div class="d-flex">
              <input
                id="task-updatedAt"
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
            <label class="form-control-label" for="task-user">User</label>
            <select class="form-control" id="task-user" data-cy="user" name="user" v-model="task.user">
              <option :value="null"></option>
              <option
                :value="task.user && userOption.id === task.user.id ? task.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="task-goal">Goal</label>
            <select class="form-control" id="task-goal" data-cy="goal" name="goal" v-model="task.goal">
              <option :value="null"></option>
              <option
                :value="task.goal && goalOption.id === task.goal.id ? task.goal : goalOption"
                v-for="goalOption in goals"
                :key="goalOption.id"
              >
                {{ goalOption.id }}
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
<script lang="ts" src="./task-update.component.ts"></script>
