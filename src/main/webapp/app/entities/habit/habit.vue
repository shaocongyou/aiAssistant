<template>
  <div>
    <h2 id="page-heading" data-cy="HabitHeading">
      <span id="habit-heading">Habits</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'HabitCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-habit"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>创建新 Habit</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && habits && habits.length === 0">
      <span>No Habits found</span>
    </div>
    <div class="table-responsive" v-if="habits && habits.length > 0">
      <table class="table table-striped" aria-describedby="habits">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span>ID</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('name')">
              <span>Name</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'name'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('habitType')">
              <span>Habit Type</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'habitType'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('frequency')">
              <span>Frequency</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'frequency'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('startDate')">
              <span>Start Date</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'startDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('active')">
              <span>Active</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'active'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('user.id')">
              <span>User</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'user.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="habit in habits" :key="habit.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'HabitView', params: { habitId: habit.id } }">{{ habit.id }}</router-link>
            </td>
            <td>{{ habit.name }}</td>
            <td>{{ habit.habitType }}</td>
            <td>{{ habit.frequency }}</td>
            <td>{{ habit.startDate }}</td>
            <td>{{ habit.active }}</td>
            <td>
              {{ habit.user ? habit.user.id : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'HabitView', params: { habitId: habit.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">查看</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'HabitEdit', params: { habitId: habit.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">编辑</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(habit)"
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
        <span id="aiAssistantApp.habit.delete.question" data-cy="habitDeleteDialogHeading">确认删除</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-habit-heading">你确定要删除 Habit {{ removeId }} 吗？</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">取消</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-habit"
            data-cy="entityConfirmDeleteButton"
            @click="removeHabit()"
          >
            删除
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="habits && habits.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./habit.component.ts"></script>
