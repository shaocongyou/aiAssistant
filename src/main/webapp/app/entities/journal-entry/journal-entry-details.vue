<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="journalEntry">
        <h2 class="jh-entity-heading" data-cy="journalEntryDetailsHeading"><span>Journal Entry</span> {{ journalEntry.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Title</span>
          </dt>
          <dd>
            <span>{{ journalEntry.title }}</span>
          </dd>
          <dt>
            <span>Content</span>
          </dt>
          <dd>
            <span>{{ journalEntry.content }}</span>
          </dd>
          <dt>
            <span>Mood</span>
          </dt>
          <dd>
            <span>{{ journalEntry.mood }}</span>
          </dd>
          <dt>
            <span>Created At</span>
          </dt>
          <dd>
            <span v-if="journalEntry.createdAt">{{ formatDateLong(journalEntry.createdAt) }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ journalEntry.user ? journalEntry.user.id : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>返回</span>
        </button>
        <router-link
          v-if="journalEntry.id"
          :to="{ name: 'JournalEntryEdit', params: { journalEntryId: journalEntry.id } }"
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

<script lang="ts" src="./journal-entry-details.component.ts"></script>
