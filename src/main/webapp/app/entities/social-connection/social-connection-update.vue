<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="aiAssistantApp.socialConnection.home.createOrEditLabel" data-cy="SocialConnectionCreateUpdateHeading">
          创建或编辑 Social Connection
        </h2>
        <div>
          <div class="form-group" v-if="socialConnection.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="socialConnection.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="social-connection-friendUsername">Friend Username</label>
            <input
              type="text"
              class="form-control"
              name="friendUsername"
              id="social-connection-friendUsername"
              data-cy="friendUsername"
              :class="{ valid: !v$.friendUsername.$invalid, invalid: v$.friendUsername.$invalid }"
              v-model="v$.friendUsername.$model"
              required
            />
            <div v-if="v$.friendUsername.$anyDirty && v$.friendUsername.$invalid">
              <small class="form-text text-danger" v-for="error of v$.friendUsername.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="social-connection-status">Status</label>
            <input
              type="text"
              class="form-control"
              name="status"
              id="social-connection-status"
              data-cy="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              required
            />
            <div v-if="v$.status.$anyDirty && v$.status.$invalid">
              <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="social-connection-createdAt">Created At</label>
            <div class="d-flex">
              <input
                id="social-connection-createdAt"
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
            <label for="social-connection-user">User</label>
            <select
              class="form-control"
              id="social-connection-users"
              data-cy="user"
              multiple
              name="user"
              v-if="socialConnection.users !== undefined"
              v-model="socialConnection.users"
            >
              <option :value="getSelected(socialConnection.users, userOption, 'id')" v-for="userOption in users" :key="userOption.id">
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
<script lang="ts" src="./social-connection-update.component.ts"></script>
