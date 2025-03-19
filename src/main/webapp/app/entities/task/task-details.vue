<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="task">
        <h2 class="jh-entity-heading" data-cy="taskDetailsHeading"><span>Task</span> {{ task.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Title</span>
          </dt>
          <dd>
            <span>{{ task.title }}</span>
          </dd>
          <dt>
            <span>Description</span>
          </dt>
          <dd>
            <span>{{ task.description }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <span>{{ task.status }}</span>
          </dd>
          <dt>
            <span>Due Date</span>
          </dt>
          <dd>
            <span>{{ task.dueDate }}</span>
          </dd>
          <dt>
            <span>Priority</span>
          </dt>
          <dd>
            <span>{{ task.priority }}</span>
          </dd>
          <dt>
            <span>Created At</span>
          </dt>
          <dd>
            <span v-if="task.createdAt">{{ formatDateLong(task.createdAt) }}</span>
          </dd>
          <dt>
            <span>Updated At</span>
          </dt>
          <dd>
            <span v-if="task.updatedAt">{{ formatDateLong(task.updatedAt) }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ task.user ? task.user.id : '' }}
          </dd>
          <dt>
            <span>Goal</span>
          </dt>
          <dd>
            <div v-if="task.goal">
              <router-link :to="{ name: 'GoalView', params: { goalId: task.goal.id } }">{{ task.goal.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>返回</span>
        </button>
        <router-link v-if="task.id" :to="{ name: 'TaskEdit', params: { taskId: task.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>编辑</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./task-details.component.ts"></script>
