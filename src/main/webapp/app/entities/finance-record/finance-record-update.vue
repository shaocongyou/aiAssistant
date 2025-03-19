<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="aiAssistantApp.financeRecord.home.createOrEditLabel" data-cy="FinanceRecordCreateUpdateHeading">
          创建或编辑 Finance Record
        </h2>
        <div>
          <div class="form-group" v-if="financeRecord.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="financeRecord.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="finance-record-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="finance-record-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
              required
            />
            <div v-if="v$.description.$anyDirty && v$.description.$invalid">
              <small class="form-text text-danger" v-for="error of v$.description.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="finance-record-amount">Amount</label>
            <input
              type="number"
              class="form-control"
              name="amount"
              id="finance-record-amount"
              data-cy="amount"
              :class="{ valid: !v$.amount.$invalid, invalid: v$.amount.$invalid }"
              v-model.number="v$.amount.$model"
              required
            />
            <div v-if="v$.amount.$anyDirty && v$.amount.$invalid">
              <small class="form-text text-danger" v-for="error of v$.amount.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="finance-record-category">Category</label>
            <input
              type="text"
              class="form-control"
              name="category"
              id="finance-record-category"
              data-cy="category"
              :class="{ valid: !v$.category.$invalid, invalid: v$.category.$invalid }"
              v-model="v$.category.$model"
              required
            />
            <div v-if="v$.category.$anyDirty && v$.category.$invalid">
              <small class="form-text text-danger" v-for="error of v$.category.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="finance-record-date">Date</label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="finance-record-date"
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
                id="finance-record-date"
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
            <label class="form-control-label" for="finance-record-createdAt">Created At</label>
            <div class="d-flex">
              <input
                id="finance-record-createdAt"
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
            <label class="form-control-label" for="finance-record-user">User</label>
            <select class="form-control" id="finance-record-user" data-cy="user" name="user" v-model="financeRecord.user">
              <option :value="null"></option>
              <option
                :value="financeRecord.user && userOption.id === financeRecord.user.id ? financeRecord.user : userOption"
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
<script lang="ts" src="./finance-record-update.component.ts"></script>
