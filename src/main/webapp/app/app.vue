<template>
  <div id="app">
    <ribbon></ribbon>
    <div id="app-header">
      <jhi-navbar></jhi-navbar>
    </div>
    <div class="container-fluid">
      <div class="card jh-card">
        <!--这个是看网址，根据网址进行显示-->
        <router-view></router-view>
      </div>

      <!--
    ### **`<b-modal>` 的默认行为**
    - **默认隐藏**：`<b-modal>` 默认是隐藏的，除非你显式地调用方法或触发事件来显示它。
    - **`id` 属性**：`<b-modal>` 需要一个唯一的 `id`，用于通过事件或方法控制它的显示和隐藏。
    - **`lazy` 属性**：`lazy` 表示模态框的内容在第一次显示时才会加载，这可以提高页面初始加载的性能。
    -->
      <b-modal id="login-page" hide-footer lazy>
        <template #modal-title>
          <span data-cy="loginTitle" id="login-title">登录</span>
        </template>
        <login-form></login-form>
      </b-modal>

      <jhi-footer></jhi-footer>
    </div>
  </div>
</template>

<script lang="ts" src="./app.component.ts"></script>
