<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="habit">
        <h2 class="jh-entity-heading" data-cy="habitDetailsHeading"><span>Habit</span> {{ habit.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ habit.name }}</span>
          </dd>
          <dt>
            <span>Habit Type</span>
          </dt>
          <dd>
            <span>{{ habit.habitType }}</span>
          </dd>
          <dt>
            <span>Frequency</span>
          </dt>
          <dd>
            <span>{{ habit.frequency }}</span>
          </dd>
          <dt>
            <span>Start Date</span>
          </dt>
          <dd>
            <span>{{ habit.startDate }}</span>
          </dd>
          <dt>
            <span>Active</span>
          </dt>
          <dd>
            <span>{{ habit.active }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ habit.user ? habit.user.id : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>返回</span>
        </button>
        <router-link v-if="habit.id" :to="{ name: 'HabitEdit', params: { habitId: habit.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>编辑</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./habit-details.component.ts"></script>
