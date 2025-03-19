<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="moodTracker">
        <h2 class="jh-entity-heading" data-cy="moodTrackerDetailsHeading"><span>Mood Tracker</span> {{ moodTracker.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Mood</span>
          </dt>
          <dd>
            <span>{{ moodTracker.mood }}</span>
          </dd>
          <dt>
            <span>Intensity</span>
          </dt>
          <dd>
            <span>{{ moodTracker.intensity }}</span>
          </dd>
          <dt>
            <span>Date</span>
          </dt>
          <dd>
            <span>{{ moodTracker.date }}</span>
          </dd>
          <dt>
            <span>Created At</span>
          </dt>
          <dd>
            <span v-if="moodTracker.createdAt">{{ formatDateLong(moodTracker.createdAt) }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ moodTracker.user ? moodTracker.user.id : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>返回</span>
        </button>
        <router-link
          v-if="moodTracker.id"
          :to="{ name: 'MoodTrackerEdit', params: { moodTrackerId: moodTracker.id } }"
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

<script lang="ts" src="./mood-tracker-details.component.ts"></script>
