<template>
  <div class="auth-wrap">
    <div class="auth-card">

      <div class="init-header">
        <div class="brand-block">
          <div class="auth-dot"></div>

          <div class="brand-text">
            <div class="auth-title">Diabetes Care Assistant</div>
            <div class="auth-sub">Initialization · Step 1 of 3</div>
          </div>
        </div>

        <button type="button" class="logout-btn" @click="logout">
          Log out
        </button>
      </div>

      <div class="init-step">
        Profile Information
      </div>

      <form @submit.prevent="handleNext" class="init-form">

        <label class="auth-label">Name</label>
        <input v-model="name" type="text" class="auth-input" />

        <label class="auth-label">MRN Number</label>
        <input v-model="mrnNumber" type="text" class="auth-input" />

        <label class="auth-label">Age</label>
        <input v-model.number="age" type="number" min="1" class="auth-input" />

        <label class="auth-label">Gender</label>
        <select v-model="gender" class="auth-input">
          <option value="">Select</option>
          <option>Male</option>
          <option>Female</option>
        </select>

        <label class="auth-label">Diabetes Type</label>
        <select v-model="diabetesType" class="auth-input">
          <option value="">Select</option>
          <option>T1D</option>
          <option>T2D</option>
        </select>

        <label class="auth-label">Diagnosis Timeframe</label>
        <select v-model="diagnosisTimeframe" class="auth-input">
          <option value="">Select</option>
          <option>&lt; 1 year</option>
          <option>1-3 years</option>
          <option>4-7 years</option>
          <option>8-10 years</option>
          <option>> 10 years</option>
        </select>

        <div v-if="error" class="auth-alert auth-alert-error">
          {{ error }}
        </div>

        <div class="init-actions">
          <button type="submit" class="auth-primary">
            Next
          </button>
        </div>

      </form>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { loadInitDraft, saveInitDraft } from "../../utils/initDraft";

const router = useRouter();

const name = ref("");
const mrnNumber = ref("");
const age = ref("");
const gender = ref("");
const diabetesType = ref("");
const diagnosisTimeframe = ref("");
const error = ref("");

onMounted(() => {
  const draft = loadInitDraft();
  const profile = draft.profile || {};

  name.value = profile.name || "";
  mrnNumber.value = profile.mrnNumber || "";
  age.value = profile.age ?? "";
  gender.value = profile.gender || "";
  diabetesType.value = profile.diabetesType || "";
  diagnosisTimeframe.value = profile.diagnosisTimeframe || "";
});

function goBack() {
  localStorage.removeItem("initDraft");
  router.push("/login");
}
function logout() {
  localStorage.clear();
  router.push("/login");
}
function handleNext() {
  error.value = "";

  if (
      !name.value.trim() ||
      age.value === "" ||
      !gender.value ||
      !diabetesType.value ||
      !diagnosisTimeframe.value
  ) {
    error.value = "Please complete the required fields.";
    return;
  }

  saveInitDraft({
    profile: {
      name: name.value.trim(),
      mrnNumber: mrnNumber.value.trim() || null,
      age: Number(age.value),
      gender: gender.value,
      diabetesType: diabetesType.value,
      diagnosisTimeframe: diagnosisTimeframe.value,
    },
  });

  router.push("/init/screenings");
}
</script>

<style scoped>
@import "../../styles/auth.css";

/* 顶部标题 + logout */

.init-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 18px;
}

.brand-block {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

/* logout 按钮 */

.logout-btn {
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  color: #475569;

  border-radius: 10px;
  padding: 6px 12px;

  font-size: 13px;
  font-weight: 600;

  cursor: pointer;

  transition: all 0.15s ease;
}

.logout-btn:hover {
  background: #f1f5f9;
  color: #0f172a;
}

/* section 标题 */

.init-step {
  margin-bottom: 14px;

  font-size: 14px;
  font-weight: 700;

  color: #0f766e;
}

/* 表单结构 */

.init-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 按钮区域 */

.init-actions {
  margin-top: 12px;
  display: flex;
}

.auth-primary {
  flex: 1;
}
</style>