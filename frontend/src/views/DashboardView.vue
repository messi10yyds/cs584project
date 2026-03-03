<template>
  <div class="wrap">
    <h1>Dashboard (placeholder)</h1>
    <p>UserId: {{ userId }}</p>
    <button @click="logout">Logout</button>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();
const userId = localStorage.getItem("userId");

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userId");
  localStorage.removeItem("tokenType");
  router.push("/login");
}
</script>

<style scoped>
.wrap {
  padding: 24px;
}
button {
  margin-top: 12px;
  padding: 8px 12px;
}
</style>