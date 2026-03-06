<template>
  <div class="auth-wrap">
    <div class="auth-card">

      <div class="auth-brand">
        <div class="auth-dot"></div>
        <div>
          <h1>Diacare Egypt</h1>
          <p class="auth-sub">Diabetes self-care support</p>
        </div>
      </div>

      <div v-if="error" class="alert alert-error">
        {{ error }}
      </div>

      <form @submit.prevent="onSubmit">
        <label>Username</label>
        <input v-model.trim="username" placeholder="Enter your username" autocomplete="username" />

        <label>Password</label>
        <input v-model="password" type="password" placeholder="Enter your password" autocomplete="current-password" />

        <button class="auth-primary" :disabled="loading || !username || !password">
          {{ loading ? "Logging in..." : "Login" }}
        </button>
      </form>

      <div class="auth-footer">
        <span>Don’t have an account?</span>
        <a href="#" @click.prevent="router.push('/register')">Create one</a>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import http from "../api/http";

const router = useRouter();

const username = ref("");
const password = ref("");
const loading = ref(false);
const error = ref("");

const canSubmit = computed(() => {
  return username.value.length > 0 && password.value.length > 0 && !loading.value;
});

async function onSubmit() {
  error.value = "";

  if (!username.value || !password.value) {
    error.value = "Please enter username and password.";
    return;
  }

  loading.value = true;
  try {
    const res = await http.post("/auth/login", {
      username: username.value,
      password: password.value,
    });

    if (res.code !== 0) {
      error.value = res.message || "Login failed";
      return;
    }

    const data = res.data;
    localStorage.setItem("token", data.token);
    localStorage.setItem("userId", String(data.userId));
    localStorage.setItem("tokenType", data.tokenType || "Bearer");

    router.push("/dashboard");
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
  background:
      radial-gradient(900px 500px at 20% 10%, rgba(20, 184, 166, 0.10), transparent),
      radial-gradient(900px 500px at 80% 20%, rgba(59, 130, 246, 0.10), transparent);
}

.card {
  width: 380px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 20px 20px 16px;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.08);
}

.brand {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 14px;
}

.logoDot {
  width: 12px;
  height: 12px;
  border-radius: 999px;
  background: #14b8a6; /* teal */
  box-shadow: 0 0 0 4px rgba(20, 184, 166, 0.15);
}

h1 {
  margin: 0;
  font-size: 20px;
  letter-spacing: -0.2px;
}

.sub {
  margin: 2px 0 0;
  color: #64748b;
  font-size: 13px;
}

label {
  display: block;
  margin-top: 12px;
  margin-bottom: 6px;
  font-size: 13px;
  color: #334155;
}

input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 12px;
  outline: none;
  transition: border-color 0.15s, box-shadow 0.15s;
}

input:focus {
  border-color: #14b8a6;
  box-shadow: 0 0 0 4px rgba(20, 184, 166, 0.18);
}

.primary {
  width: 100%;
  margin-top: 16px;
  padding: 10px 12px;
  border: 0;
  border-radius: 12px;
  cursor: pointer;
  background: linear-gradient(90deg, #14b8a6, #2563eb);
  color: #fff;
  font-weight: 700;
}

.primary:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.alert {
  margin: 10px 0 6px;
  padding: 10px 12px;
  border-radius: 12px;
  font-size: 13px;
}

.alert-error {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #b91c1c;
}

.footerLink {
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #475569;
}

.footerLink a {
  color: #0f766e; /* teal-700 */
  font-weight: 700;
  cursor: pointer;
  text-decoration: none;
}
.footerLink a:hover {
  text-decoration: underline;
}
</style>