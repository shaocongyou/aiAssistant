<template>
  <div class="modal-body">
    <span class="badge badge-primary" @click="threadDumpFilter = ''"
      >All&nbsp;<span class="badge badge-pill badge-default">{{ threadDumpData.threadDumpAll }}</span></span
    >&nbsp;
    <span class="badge badge-success" @click="threadDumpFilter = 'RUNNABLE'"
      >Runnable&nbsp;<span class="badge badge-pill badge-default">{{ threadDumpData.threadDumpRunnable }}</span></span
    >&nbsp;
    <span class="badge badge-info" @click="threadDumpFilter = 'WAITING'"
      >Waiting&nbsp;<span class="badge badge-pill badge-default">{{ threadDumpData.threadDumpWaiting }}</span></span
    >&nbsp;
    <span class="badge badge-warning" @click="threadDumpFilter = 'TIMED_WAITING'"
      >Timed Waiting&nbsp;<span class="badge badge-pill badge-default">{{ threadDumpData.threadDumpTimedWaiting }}</span></span
    >&nbsp;
    <span class="badge badge-danger" @click="threadDumpFilter = 'BLOCKED'"
      >Blocked&nbsp;<span class="badge badge-pill badge-default">{{ threadDumpData.threadDumpBlocked }}</span></span
    >&nbsp;
    <div class="mt-2">&nbsp;</div>
    Filter
    <input type="text" v-model="threadDumpFilter" class="form-control" />
    <div class="pad" v-for="(entry, key) of filteredThreadDump" :key="key">
      <h6>
        <span class="badge" :class="getBadgeClass(entry.threadState)">{{ entry.threadState }}</span
        >&nbsp;{{ entry.threadName }} (ID {{ entry.threadId }})
        <a @click="entry.show = !entry.show" href="javascript:void(0);">
          <span :hidden="entry.show">显示</span>
          <span :hidden="!entry.show">隐藏</span>
        </a>
      </h6>
      <div class="card" :hidden="!entry.show">
        <div class="card-body">
          <div v-for="(st, key) of entry.stackTrace" :key="key" class="break">
            <samp
              >{{ st.className }}.{{ st.methodName }}(<code>{{ st.fileName }}:{{ st.lineNumber }}</code
              >)</samp
            >
            <span class="mt-1"></span>
          </div>
        </div>
      </div>
      <table class="table table-sm table-responsive" aria-describedby="Metrics">
        <thead>
          <tr>
            <th scope="col">阻塞时间</th>
            <th scope="col">阻塞次数</th>
            <th scope="col">等待时间</th>
            <th scope="col">等待次数</th>
            <th scope="col">锁名称</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{{ entry.blockedTime }}</td>
            <td>{{ entry.blockedCount }}</td>
            <td>{{ entry.waitedTime }}</td>
            <td>{{ entry.waitedCount }}</td>
            <td class="thread-dump-modal-lock" :title="entry.lockName">
              <code>{{ entry.lockName }}</code>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts" src="./metrics-modal.component.ts"></script>
