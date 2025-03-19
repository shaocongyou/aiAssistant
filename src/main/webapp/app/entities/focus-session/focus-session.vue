<template>
  <div>
    <h2 id="page-heading" data-cy="FocusSessionHeading">
      <span id="focus-session-heading">Focus Sessions</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'FocusSessionCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-focus-session"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>创建新 Focus Session</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && focusSessions && focusSessions.length === 0">
      <span>No Focus Sessions found</span>
    </div>
    <div class="table-responsive" v-if="focusSessions && focusSessions.length > 0">
      <table class="table table-striped" aria-describedby="focusSessions">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span>ID</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('duration')">
              <span>Duration</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'duration'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('task')">
              <span>Task</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'task'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('date')">
              <span>Date</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'date'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createdAt')">
              <span>Created At</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('user.id')">
              <span>User</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'user.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="focusSession in focusSessions" :key="focusSession.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'FocusSessionView', params: { focusSessionId: focusSession.id } }">{{
                focusSession.id
              }}</router-link>
            </td>
            <td>{{ focusSession.duration }}</td>
            <td>{{ focusSession.task }}</td>
            <td>{{ focusSession.date }}</td>
            <td>{{ formatDateShort(focusSession.createdAt) || '' }}</td>
            <td>
              {{ focusSession.user ? focusSession.user.id : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'FocusSessionView', params: { focusSessionId: focusSession.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">查看</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'FocusSessionEdit', params: { focusSessionId: focusSession.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">编辑</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(focusSession)"
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
        <span id="aiAssistantApp.focusSession.delete.question" data-cy="focusSessionDeleteDialogHeading">确认删除</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-focusSession-heading">你确定要删除 Focus Session {{ removeId }} 吗？</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">取消</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-focusSession"
            data-cy="entityConfirmDeleteButton"
            @click="removeFocusSession()"
          >
            删除
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="focusSessions && focusSessions.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./focus-session.component.ts"></script>
