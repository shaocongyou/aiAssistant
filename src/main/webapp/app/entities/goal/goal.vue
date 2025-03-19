<template>
  <div>
    <h2 id="page-heading" data-cy="GoalHeading">
      <span id="goal-heading">Goals</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'GoalCreate' }" custom v-slot="{ navigate }">
          <button @click="navigate" id="jh-create-entity" data-cy="entityCreateButton" class="btn btn-primary jh-create-entity create-goal">
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>创建新 Goal</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && goals && goals.length === 0">
      <span>No Goals found</span>
    </div>
    <div class="table-responsive" v-if="goals && goals.length > 0">
      <table class="table table-striped" aria-describedby="goals">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span>ID</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('title')">
              <span>Title</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'title'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('description')">
              <span>Description</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'description'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('goalType')">
              <span>Goal Type</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'goalType'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('deadline')">
              <span>Deadline</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'deadline'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('completed')">
              <span>Completed</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'completed'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createdAt')">
              <span>Created At</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('updatedAt')">
              <span>Updated At</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'updatedAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('user.id')">
              <span>User</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'user.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="goal in goals" :key="goal.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'GoalView', params: { goalId: goal.id } }">{{ goal.id }}</router-link>
            </td>
            <td>{{ goal.title }}</td>
            <td>{{ goal.description }}</td>
            <td>{{ goal.goalType }}</td>
            <td>{{ goal.deadline }}</td>
            <td>{{ goal.completed }}</td>
            <td>{{ formatDateShort(goal.createdAt) || '' }}</td>
            <td>{{ formatDateShort(goal.updatedAt) || '' }}</td>
            <td>
              {{ goal.user ? goal.user.id : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'GoalView', params: { goalId: goal.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">查看</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'GoalEdit', params: { goalId: goal.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">编辑</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(goal)"
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
        <span id="aiAssistantApp.goal.delete.question" data-cy="goalDeleteDialogHeading">确认删除</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-goal-heading">你确定要删除 Goal {{ removeId }} 吗？</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">取消</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-goal"
            data-cy="entityConfirmDeleteButton"
            @click="removeGoal()"
          >
            删除
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="goals && goals.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./goal.component.ts"></script>
