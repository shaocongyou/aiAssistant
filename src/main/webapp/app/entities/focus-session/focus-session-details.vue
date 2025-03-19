<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="focusSession">
        <h2 class="jh-entity-heading" data-cy="focusSessionDetailsHeading"><span>Focus Session</span> {{ focusSession.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Duration</span>
          </dt>
          <dd>
            <span>{{ focusSession.duration }}</span>
          </dd>
          <dt>
            <span>Task</span>
          </dt>
          <dd>
            <span>{{ focusSession.task }}</span>
          </dd>
          <dt>
            <span>Date</span>
          </dt>
          <dd>
            <span>{{ focusSession.date }}</span>
          </dd>
          <dt>
            <span>Created At</span>
          </dt>
          <dd>
            <span v-if="focusSession.createdAt">{{ formatDateLong(focusSession.createdAt) }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ focusSession.user ? focusSession.user.id : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>返回</span>
        </button>
        <router-link
          v-if="focusSession.id"
          :to="{ name: 'FocusSessionEdit', params: { focusSessionId: focusSession.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>编辑</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./focus-session-details.component.ts"></script>
