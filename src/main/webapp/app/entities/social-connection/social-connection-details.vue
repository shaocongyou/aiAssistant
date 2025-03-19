<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="socialConnection">
        <h2 class="jh-entity-heading" data-cy="socialConnectionDetailsHeading"><span>Social Connection</span> {{ socialConnection.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Friend Username</span>
          </dt>
          <dd>
            <span>{{ socialConnection.friendUsername }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <span>{{ socialConnection.status }}</span>
          </dd>
          <dt>
            <span>Created At</span>
          </dt>
          <dd>
            <span v-if="socialConnection.createdAt">{{ formatDateLong(socialConnection.createdAt) }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            <span v-for="(user, i) in socialConnection.users" :key="user.id"
              >{{ i > 0 ? ', ' : '' }}
              {{ user.id }}
            </span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>返回</span>
        </button>
        <router-link
          v-if="socialConnection.id"
          :to="{ name: 'SocialConnectionEdit', params: { socialConnectionId: socialConnection.id } }"
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

<script lang="ts" src="./social-connection-details.component.ts"></script>
