<template>
  <div class="wrap">
    <div class="card">
      <h1>Register</h1>

      <form @submit.prevent="onSubmit">
        <label>Username</label>
        <input v-model="username" placeholder="username" />

        <label>Password</label>
        <input v-model="password" type="password" placeholder="password" />

        <button :disabled="loading">
          {{ loading ? "Creating..." : "Create account" }}
        </button>
      </form>

      <p class="error" v-if="error">{{ error }}</p>

      <p class="hint">
        Already have an account?
        <a href="#" @click.prevent="goLogin">Login</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import http from "../api/http";

const router = useRouter();

const username = ref("");
const password = ref("");
const loading = ref(false);
const error = ref("");

function goLogin() {
  router.push("/login");
}

async function onSubmit() {
  error.value = "";
  loading.value = true;
  try {
    const res = await http.post("/auth/register", {
      username: username.value,
      password: password.value,
    });

    if (res.code !== 0) {
      error.value = res.message || "Register failed";
      return;
    }

    const data = res.data;
    localStorage.setItem("token", data.token);
    localStorage.setItem("userId", String(data.userId));
    localStorage.setItem("tokenType", data.tokenType || "Bearer");

    router.push("/init");
  } catch (e) {
    error.value = "Network or server error";
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.wrap {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
}
.card {
  width: 360px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
}
h1 {
  margin: 0 0 12px;
  font-size: 22px;
}
label {
  display: block;
  margin-top: 12px;
  margin-bottom: 6px;
  font-size: 14px;
}
input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
}
button {
  width: 100%;
  margin-top: 16px;
  padding: 10px 12px;
  border: 0;
  border-radius: 10px;
  cursor: pointer;
}
.error {
  margin-top: 12px;
  color: #b91c1c;
  font-size: 14px;
}
.hint {
  margin-top: 12px;
  font-size: 14px;
}
</style>