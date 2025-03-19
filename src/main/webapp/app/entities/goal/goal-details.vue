<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="goal">
        <h2 class="jh-entity-heading" data-cy="goalDetailsHeading"><span>Goal</span> {{ goal.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Title</span>
          </dt>
          <dd>
            <span>{{ goal.title }}</span>
          </dd>
          <dt>
            <span>Description</span>
          </dt>
          <dd>
            <span>{{ goal.description }}</span>
          </dd>
          <dt>
            <span>Goal Type</span>
          </dt>
          <dd>
            <span>{{ goal.goalType }}</span>
          </dd>
          <dt>
            <span>Deadline</span>
          </dt>
          <dd>
            <span>{{ goal.deadline }}</span>
          </dd>
          <dt>
            <span>Completed</span>
          </dt>
          <dd>
            <span>{{ goal.completed }}</span>
          </dd>
          <dt>
            <span>Created At</span>
          </dt>
          <dd>
            <span v-if="goal.createdAt">{{ formatDateLong(goal.createdAt) }}</span>
          </dd>
          <dt>
            <span>Updated At</span>
          </dt>
          <dd>
            <span v-if="goal.updatedAt">{{ formatDateLong(goal.updatedAt) }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ goal.user ? goal.user.id : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>返回</span>
        </button>
        <router-link v-if="goal.id" :to="{ name: 'GoalEdit', params: { goalId: goal.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>编辑</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./goal-details.component.ts"></script>
