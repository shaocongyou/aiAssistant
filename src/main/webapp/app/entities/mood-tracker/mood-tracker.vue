<template>
  <div>
    <h2 id="page-heading" data-cy="MoodTrackerHeading">
      <span id="mood-tracker-heading">Mood Trackers</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'MoodTrackerCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-mood-tracker"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>创建新 Mood Tracker</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && moodTrackers && moodTrackers.length === 0">
      <span>No Mood Trackers found</span>
    </div>
    <div class="table-responsive" v-if="moodTrackers && moodTrackers.length > 0">
      <table class="table table-striped" aria-describedby="moodTrackers">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span>ID</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('mood')">
              <span>Mood</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'mood'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('intensity')">
              <span>Intensity</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'intensity'"></jhi-sort-indicator>
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
          <tr v-for="moodTracker in moodTrackers" :key="moodTracker.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'MoodTrackerView', params: { moodTrackerId: moodTracker.id } }">{{ moodTracker.id }}</router-link>
            </td>
            <td>{{ moodTracker.mood }}</td>
            <td>{{ moodTracker.intensity }}</td>
            <td>{{ moodTracker.date }}</td>
            <td>{{ formatDateShort(moodTracker.createdAt) || '' }}</td>
            <td>
              {{ moodTracker.user ? moodTracker.user.id : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'MoodTrackerView', params: { moodTrackerId: moodTracker.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">查看</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'MoodTrackerEdit', params: { moodTrackerId: moodTracker.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">编辑</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(moodTracker)"
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
        <span id="aiAssistantApp.moodTracker.delete.question" data-cy="moodTrackerDeleteDialogHeading">确认删除</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-moodTracker-heading">你确定要删除 Mood Tracker {{ removeId }} 吗？</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">取消</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-moodTracker"
            data-cy="entityConfirmDeleteButton"
            @click="removeMoodTracker()"
          >
            删除
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="moodTrackers && moodTrackers.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./mood-tracker.component.ts"></script>
