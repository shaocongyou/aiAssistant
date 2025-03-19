<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="aiAssistantApp.habit.home.createOrEditLabel" data-cy="HabitCreateUpdateHeading">创建或编辑 Habit</h2>
        <div>
          <div class="form-group" v-if="habit.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="habit.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="habit-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="habit-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="habit-habitType">Habit Type</label>
            <select
              class="form-control"
              name="habitType"
              :class="{ valid: !v$.habitType.$invalid, invalid: v$.habitType.$invalid }"
              v-model="v$.habitType.$model"
              id="habit-habitType"
              data-cy="habitType"
              required
            >
              <option v-for="habitType in habitTypeValues" :key="habitType" :value="habitType">{{ habitType }}</option>
            </select>
            <div v-if="v$.habitType.$anyDirty && v$.habitType.$invalid">
              <small class="form-text text-danger" v-for="error of v$.habitType.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="habit-frequency">Frequency</label>
            <input
              type="text"
              class="form-control"
              name="frequency"
              id="habit-frequency"
              data-cy="frequency"
              :class="{ valid: !v$.frequency.$invalid, invalid: v$.frequency.$invalid }"
              v-model="v$.frequency.$model"
              required
            />
            <div v-if="v$.frequency.$anyDirty && v$.frequency.$invalid">
              <small class="form-text text-danger" v-for="error of v$.frequency.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="habit-startDate">Start Date</label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="habit-startDate"
                  v-model="v$.startDate.$model"
                  name="startDate"
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
                id="habit-startDate"
                data-cy="startDate"
                type="text"
                class="form-control"
                name="startDate"
                :class="{ valid: !v$.startDate.$invalid, invalid: v$.startDate.$invalid }"
                v-model="v$.startDate.$model"
              />
            </b-input-group>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="habit-active">Active</label>
            <input
              type="checkbox"
              class="form-check"
              name="active"
              id="habit-active"
              data-cy="active"
              :class="{ valid: !v$.active.$invalid, invalid: v$.active.$invalid }"
              v-model="v$.active.$model"
              required
            />
            <div v-if="v$.active.$anyDirty && v$.active.$invalid">
              <small class="form-text text-danger" v-for="error of v$.active.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="habit-user">User</label>
            <select class="form-control" id="habit-user" data-cy="user" name="user" v-model="habit.user">
              <option :value="null"></option>
              <option
                :value="habit.user && userOption.id === habit.user.id ? habit.user : userOption"
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
<script lang="ts" src="./habit-update.component.ts"></script>
