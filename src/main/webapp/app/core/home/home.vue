<template>
  <div class="home row">
    <div class="col-md-3">
      <span class="hipster img-fluid rounded"></span>
    </div>
    <div class="col-md-9">
      <h1 class="display-4">欢迎, Java Hipster!</h1>
      <p class="lead">这里是首页</p>

      <div>
        <div class="alert alert-success" v-if="authenticated">
          <span v-if="username">您目前是以 "{{ username }}" 账号登录.</span>
        </div>

        <div class="alert alert-warning" v-if="!authenticated">
          <span>如果您要</span>
          <a class="alert-link" @click="openLogin()">登录</a
          ><span>, 您可以使用默认账号:<br />- 管理员 (账号="admin"和密码="admin") <br />- 普通用户 (账号="user"和密码="user").</span>
        </div>
        <div class="alert alert-warning" v-if="!authenticated">
          <span>您还没有账号</span>&nbsp;
          <router-link class="alert-link" to="/register">注册一个新账号</router-link>
        </div>
      </div>

      <p>如果您有任何有关 JHipster 的问题, 可以查阅下列资源:</p>

      <ul>
        <li><a href="https://www.jhipster.tech/" target="_blank" rel="noopener noreferrer">JHipster 首頁</a></li>
        <li>
          <a href="https://stackoverflow.com/tags/jhipster/info" target="_blank" rel="noopener noreferrer"
            >Stack Overflow 上关于 JHipster 的讨论</a
          >
        </li>
        <li>
          <a href="https://github.com/jhipster/generator-jhipster/issues?state=open" target="_blank" rel="noopener noreferrer"
            >JHipster 的缺陷追踪</a
          >
        </li>
        <li>
          <a href="https://gitter.im/jhipster/generator-jhipster" target="_blank" rel="noopener noreferrer">JHipster public chat room</a>
        </li>
        <li>
          <a href="https://twitter.com/jhipster" target="_blank" rel="noopener noreferrer">在 Twitter 上联络 @jhipster</a>
        </li>
      </ul>

      <p>
        <span>如果您喜欢 JHipster, 请记得给我们加星在</span>
        <a href="https://github.com/jhipster/generator-jhipster" target="_blank" rel="noopener noreferrer">GitHub</a>!
      </p>
    </div>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>
