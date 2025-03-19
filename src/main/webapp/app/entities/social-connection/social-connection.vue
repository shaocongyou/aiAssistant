<template>
  <div>
    <h2 id="page-heading" data-cy="SocialConnectionHeading">
      <span id="social-connection-heading">Social Connections</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'SocialConnectionCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-social-connection"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>创建新 Social Connection</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && socialConnections && socialConnections.length === 0">
      <span>No Social Connections found</span>
    </div>
    <div class="table-responsive" v-if="socialConnections && socialConnections.length > 0">
      <table class="table table-striped" aria-describedby="socialConnections">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span>ID</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('friendUsername')">
              <span>Friend Username</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'friendUsername'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('status')">
              <span>Status</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createdAt')">
              <span>Created At</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="socialConnection in socialConnections" :key="socialConnection.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'SocialConnectionView', params: { socialConnectionId: socialConnection.id } }">{{
                socialConnection.id
              }}</router-link>
            </td>
            <td>{{ socialConnection.friendUsername }}</td>
            <td>{{ socialConnection.status }}</td>
            <td>{{ formatDateShort(socialConnection.createdAt) || '' }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'SocialConnectionView', params: { socialConnectionId: socialConnection.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">查看</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'SocialConnectionEdit', params: { socialConnectionId: socialConnection.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">编辑</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(socialConnection)"
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
        <span id="aiAssistantApp.socialConnection.delete.question" data-cy="socialConnectionDeleteDialogHeading">确认删除</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-socialConnection-heading">你确定要删除 Social Connection {{ removeId }} 吗？</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">取消</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-socialConnection"
            data-cy="entityConfirmDeleteButton"
            @click="removeSocialConnection()"
          >
            删除
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="socialConnections && socialConnections.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./social-connection.component.ts"></script>
