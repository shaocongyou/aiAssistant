<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="financeRecord">
        <h2 class="jh-entity-heading" data-cy="financeRecordDetailsHeading"><span>Finance Record</span> {{ financeRecord.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Description</span>
          </dt>
          <dd>
            <span>{{ financeRecord.description }}</span>
          </dd>
          <dt>
            <span>Amount</span>
          </dt>
          <dd>
            <span>{{ financeRecord.amount }}</span>
          </dd>
          <dt>
            <span>Category</span>
          </dt>
          <dd>
            <span>{{ financeRecord.category }}</span>
          </dd>
          <dt>
            <span>Date</span>
          </dt>
          <dd>
            <span>{{ financeRecord.date }}</span>
          </dd>
          <dt>
            <span>Created At</span>
          </dt>
          <dd>
            <span v-if="financeRecord.createdAt">{{ formatDateLong(financeRecord.createdAt) }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ financeRecord.user ? financeRecord.user.id : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>返回</span>
        </button>
        <router-link
          v-if="financeRecord.id"
          :to="{ name: 'FinanceRecordEdit', params: { financeRecordId: financeRecord.id } }"
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

<script lang="ts" src="./finance-record-details.component.ts"></script>
