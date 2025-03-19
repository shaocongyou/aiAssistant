<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="aiAssistantApp.journalEntry.home.createOrEditLabel" data-cy="JournalEntryCreateUpdateHeading">创建或编辑 Journal Entry</h2>
        <div>
          <div class="form-group" v-if="journalEntry.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="journalEntry.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="journal-entry-title">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="journal-entry-title"
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
            <label class="form-control-label" for="journal-entry-content">Content</label>
            <div>
              <div v-if="journalEntry.content" class="form-text text-danger clearfix">
                <span class="pull-left">{{ journalEntry.contentContentType }}, {{ byteSize(journalEntry.content) }}</span>
                <button
                  type="button"
                  @click="
                    journalEntry.content = null;
                    journalEntry.contentContentType = null;
                  "
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_content" class="btn btn-primary pull-right">增加 BLOB</label>
              <input
                type="file"
                ref="file_content"
                id="file_content"
                style="display: none"
                data-cy="content"
                @change="setFileData($event, journalEntry, 'content', false)"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="content"
              id="journal-entry-content"
              data-cy="content"
              :class="{ valid: !v$.content.$invalid, invalid: v$.content.$invalid }"
              v-model="v$.content.$model"
              required
            />
            <input
              type="hidden"
              class="form-control"
              name="contentContentType"
              id="journal-entry-contentContentType"
              v-model="journalEntry.contentContentType"
            />
            <div v-if="v$.content.$anyDirty && v$.content.$invalid">
              <small class="form-text text-danger" v-for="error of v$.content.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="journal-entry-mood">Mood</label>
            <input
              type="text"
              class="form-control"
              name="mood"
              id="journal-entry-mood"
              data-cy="mood"
              :class="{ valid: !v$.mood.$invalid, invalid: v$.mood.$invalid }"
              v-model="v$.mood.$model"
            />
            <div v-if="v$.mood.$anyDirty && v$.mood.$invalid">
              <small class="form-text text-danger" v-for="error of v$.mood.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="journal-entry-createdAt">Created At</label>
            <div class="d-flex">
              <input
                id="journal-entry-createdAt"
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
            <label class="form-control-label" for="journal-entry-user">User</label>
            <select class="form-control" id="journal-entry-user" data-cy="user" name="user" v-model="journalEntry.user">
              <option :value="null"></option>
              <option
                :value="journalEntry.user && userOption.id === journalEntry.user.id ? journalEntry.user : userOption"
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
<script lang="ts" src="./journal-entry-update.component.ts"></script>
