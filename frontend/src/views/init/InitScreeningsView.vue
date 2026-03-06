<template>
  <div class="auth-wrap">
    <div class="auth-card">
      <div class="init-header">
        <div class="brand-block">
          <div class="auth-dot"></div>

          <div class="brand-text">
            <div class="auth-title">Diabetes Care Assistant</div>
            <div class="auth-sub">Initialization · Step 2 of 3</div>
          </div>
        </div>

        <button type="button" class="logout-btn" @click="logout">
          Log out
        </button>
      </div>

      <div class="init-step">Screening Information</div>

      <label class="checkbox-row">
        <input v-model="allowAutoFetch" type="checkbox" />
        <span>
          I allow the automatic retrieval of my last screening dates and results
          from my medical record.
        </span>
      </label>

      <form @submit.prevent="onNext" class="init-form">
        <div class="screening-block">
          <h2 class="section-title">A1C Test</h2>

          <label class="auth-label">
            Do you remember when you last completed this test?
          </label>
          <select v-model="a1cRecall" class="auth-input">
            <option value="">Select</option>
            <option value="remember">Yes, I remember</option>
            <option value="never">I have never done this test</option>
            <option value="cannot_recall">
              I have done it, but I cannot recall the year
            </option>
          </select>

          <template v-if="a1cRecall === 'remember'">
            <label class="auth-label">Last completed date</label>
            <input v-model="a1cLast" type="date" class="auth-input" />

            <label class="auth-label">Which situation best fits you?</label>
            <select v-model.number="a1cTypeId" class="auth-input">
              <option :value="1">
                Meeting treatment and blood sugar goals (about every 6 months)
              </option>
              <option :value="2">
                Treatment changed or goals not met (about every 3 months)
              </option>
            </select>
          </template>
        </div>

        <div class="screening-block">
          <h2 class="section-title">Eye Exam</h2>

          <label class="auth-label">
            Do you remember when you last completed this test?
          </label>
          <select v-model="eyeRecall" class="auth-input">
            <option value="">Select</option>
            <option value="remember">Yes, I remember</option>
            <option value="never">I have never done this test</option>
            <option value="cannot_recall">
              I have done it, but I cannot recall the year
            </option>
          </select>

          <template v-if="eyeRecall === 'remember'">
            <label class="auth-label">Last completed date</label>
            <input v-model="eyeLast" type="date" class="auth-input" />

            <label class="auth-label">Which situation best fits you?</label>
            <select v-model.number="eyeTypeId" class="auth-input">
              <option :value="3">
                No diabetes-related eye problems (about every year)
              </option>
              <option :value="4">
                Diabetes-related eye problems present (about every 6 months)
              </option>
            </select>
          </template>
        </div>

        <div class="screening-block">
          <h2 class="section-title">Kidney Screening</h2>

          <label class="auth-label">
            Do you remember when you last completed this test?
          </label>
          <select v-model="kidneyRecall" class="auth-input">
            <option value="">Select</option>
            <option value="remember">Yes, I remember</option>
            <option value="never">I have never done this test</option>
            <option value="cannot_recall">
              I have done it, but I cannot recall the year
            </option>
          </select>

          <template v-if="kidneyRecall === 'remember'">
            <label class="auth-label">Last completed date</label>
            <input v-model="kidneyLast" type="date" class="auth-input" />
          </template>
        </div>

        <div v-if="error" class="auth-alert auth-alert-error">
          {{ error }}
        </div>

        <div v-if="ok" class="auth-alert auth-alert-success">
          {{ ok }}
        </div>

        <div class="init-actions">
          <button type="button" class="auth-secondary" @click="onBack">
            Back
          </button>
          <button
              type="submit"
              class="auth-primary"
              :disabled="loading || !allowAutoFetch"
          >
            {{ loading ? "Saving..." : "Next" }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { loadInitDraft, saveInitDraft } from "../../utils/initDraft";

const router = useRouter();
const loading = ref(false);
const error = ref("");
const ok = ref("");

const draft = loadInitDraft();

// 默认值（如果 draft 里有就回填）
const existing = Array.isArray(draft.screenings) ? draft.screenings : [];

function findExisting(typeIds) {
  return existing.find((x) => x && typeIds.includes(x.screeningTypeId));
}

const a1cExisting = findExisting([1, 2]);
const eyeExisting = findExisting([3, 4]);
const kidneyExisting = findExisting([5]);

const a1cTypeId = ref(a1cExisting?.screeningTypeId ?? 1);
const a1cLast = ref(a1cExisting?.lastCompletedDate ?? "");

const eyeTypeId = ref(eyeExisting?.screeningTypeId ?? 3);
const eyeLast = ref(eyeExisting?.lastCompletedDate ?? "");

const kidneyTypeId = ref(5);
const kidneyLast = ref(kidneyExisting?.lastCompletedDate ?? "");

const allowAutoFetch = ref(false);

const a1cRecall = ref("");
const eyeRecall = ref("");
const kidneyRecall = ref("");

function logout() {
  localStorage.clear();
  router.replace("/login");
}

function onBack() {
  router.push("/init/profile");
}

function onNext() {
  error.value = "";
  ok.value = "";
  if (!allowAutoFetch.value) {
    error.value = "Please confirm the medical record access option.";
    return;
  }
  loading.value = true;

  if (!a1cRecall.value) {
    loading.value = false;
    return (error.value = "Please answer the A1C question.");
  }
  if (!eyeRecall.value) {
    loading.value = false;
    return (error.value = "Please answer the Eye Exam question.");
  }
  if (!kidneyRecall.value) {
    loading.value = false;
    return (error.value = "Please answer the Kidney screening question.");
  }

  // 轻量校验：确保三类都存在（typeId 合法）
  if (![1, 2].includes(a1cTypeId.value)) {
    loading.value = false;
    return (error.value = "Invalid A1C type");
  }
  if (![3, 4].includes(eyeTypeId.value)) {
    loading.value = false;
    return (error.value = "Invalid Eye type");
  }
  if (kidneyTypeId.value !== 5) {
    loading.value = false;
    return (error.value = "Invalid Kidney type");
  }

  if (a1cRecall.value === "remember" && !a1cLast.value) {
    loading.value = false;
    return (error.value = "Please enter the last completed date for A1C.");
  }
  if (eyeRecall.value === "remember" && !eyeLast.value) {
    loading.value = false;
    return (error.value = "Please enter the last completed date for Eye Exam.");
  }
  if (kidneyRecall.value === "remember" && !kidneyLast.value) {
    loading.value = false;
    return (error.value = "Please enter the last completed date for Kidney screening.");
  }

  const FALLBACK_OLD_DATE = "2000-01-01";

  function resolveLastDate(recallValue, manualDate) {
    if (recallValue === "remember") {
      return manualDate || null;
    }
    return FALLBACK_OLD_DATE;
  }

  const screenings = [
    {
      screeningTypeId: a1cTypeId.value,
      lastCompletedDate: resolveLastDate(a1cRecall.value, a1cLast.value),
    },
    {
      screeningTypeId: eyeTypeId.value,
      lastCompletedDate: resolveLastDate(eyeRecall.value, eyeLast.value),
    },
    {
      screeningTypeId: kidneyTypeId.value,
      lastCompletedDate: resolveLastDate(kidneyRecall.value, kidneyLast.value),
    },
  ];

  saveInitDraft({ screenings });

  ok.value = "Saved. Next: medications.";
  loading.value = false;
  router.push("/init/medications");
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

/* logout */

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

/* section title */

.init-step {
  margin-bottom: 14px;
  font-size: 14px;
  font-weight: 700;
  color: #0f766e;
}

/* form layout */

.init-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.screening-block {
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 14px;
  background: #fcfcfd;
}

.section-title {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
}

/* consent row */

.checkbox-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 14px;
  font-size: 14px;
  color: #334155;
  line-height: 1.5;
}

.checkbox-row input {
  margin-top: 3px;
}

/* buttons */

.init-actions {
  display: flex;
  gap: 10px;
  margin-top: 4px;
}

.auth-secondary {
  width: 120px;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 12px;
  background: #fff;
  cursor: pointer;
  font-weight: 600;
  color: #334155;
}

.auth-secondary:hover {
  background: #f8fafc;
}

.auth-primary {
  margin-top: 0;
  flex: 1;
}

/* success alert */

.auth-alert-success {
  background: #ecfdf5;
  border: 1px solid #bbf7d0;
  color: #166534;
}
</style>