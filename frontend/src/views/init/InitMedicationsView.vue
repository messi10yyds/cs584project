<template>
  <div class="auth-wrap">
    <div class="auth-card">
      <div class="init-header">
        <div class="brand-block">
          <div class="auth-dot"></div>

          <div class="brand-text">
            <div class="auth-title">Diabetes Care Assistant</div>
            <div class="auth-sub">Initialization · Step 3 of 3</div>
          </div>
        </div>

        <button type="button" class="logout-btn" @click="logout">
          Log out
        </button>
      </div>

      <div class="init-step">Medication Information</div>

      <label class="checkbox-row">
        <input v-model="allowMedicationAutoFetch" type="checkbox" />
        <span>I allow the automatic retrieval of my current medications from my medical record.</span>
      </label>

      <form @submit.prevent="onFinish" class="init-form">
        <div
            v-for="(med, index) in medications"
            :key="index"
            class="screening-block"
        >
          <div class="med-header">
            <div class="med-title">Medication {{ index + 1 }}</div>
            <button
                type="button"
                class="remove-btn"
                @click="removeMedication(index)"
            >
              Remove
            </button>
          </div>

          <label class="auth-label">Medication name </label>
          <input
              v-model="med.name"
              class="auth-input"
              type="text"
              placeholder="e.g., Metformin"
          />

          <label class="auth-label">Dosage </label>
          <input
              v-model="med.dosage"
              class="auth-input"
              type="text"
              placeholder="e.g., 500 mg"
          />

          <label class="auth-label">Expected doses per day </label>
          <input
              v-model.number="med.expectedDosesPerDay"
              class="auth-input"
              type="number"
              min="0"
          />
        </div>

        <button
            type="button"
            class="auth-secondary add-btn"
            @click="addMedication"
        >
          + Add medication
        </button>

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
          <button type="submit" class="auth-primary" :disabled="loading">
            {{ loading ? "Submitting..." : "Finish" }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import http from "../../api/http";
import { loadInitDraft, clearInitDraft, saveInitDraft } from "../../utils/initDraft";

const router = useRouter();
const loading = ref(false);
const error = ref("");
const ok = ref("");

const draft = loadInitDraft();

const allowMedicationAutoFetch = ref(false);

const existingMeds = Array.isArray(draft.medications) ? draft.medications : [];

const medications = ref(
    existingMeds.length > 0
        ? existingMeds.map((m) => ({
          name: m?.name ?? "",
          dosage: m?.dosage ?? "",
          expectedDosesPerDay: m?.expectedDosesPerDay ?? null,
        }))
        : [
          {
            name: "",
            dosage: "",
            expectedDosesPerDay: null,
          },
        ]
);

function addMedication() {
  medications.value.push({
    name: "",
    dosage: "",
    expectedDosesPerDay: null,
  });
}

function removeMedication(index) {
  if (medications.value.length === 1) {
    medications.value[0] = {
      name: "",
      dosage: "",
      expectedDosesPerDay: null,
    };
    return;
  }
  medications.value.splice(index, 1);
}

function buildMedicationsPayload() {
  return medications.value
      .filter((m) => m.name && m.name.trim())
      .map((m) => ({
        name: m.name.trim(),
        dosage: m.dosage || null,
        expectedDosesPerDay: m.expectedDosesPerDay ?? null,
      }));
}

function logout() {
  localStorage.clear();
  router.replace("/login");
}

function onBack() {
  saveInitDraft({
    medications: buildMedicationsPayload(),
  });

  router.push("/init/screenings");
}

async function onFinish() {
  error.value = "";
  ok.value = "";
  if (!allowMedicationAutoFetch.value) {
    error.value = "Please confirm the medical record access option.";
    return;
  }

  loading.value = true;

  try {
    const d = loadInitDraft();
    const profile = d.profile;
    const screenings = d.screenings;

    if (!profile) {
      error.value = "Missing profile. Please go back.";
      return;
    }

    if (!Array.isArray(screenings) || screenings.length !== 3) {
      error.value = "Missing screenings. Please go back.";
      return;
    }

    const medicationsPayload = buildMedicationsPayload();

    const res = await http.post("/api/registration/init", {
      profile,
      screenings,
      medications: medicationsPayload,
    });

    if (res.code !== 0) {
      error.value = res.message || "Init failed";
      return;
    }

    clearInitDraft();
    ok.value = "Initialized successfully!";
    router.push("/dashboard");
  } catch (e) {
    error.value = "Network or server error";
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
@import "../../styles/auth.css";

.init-step {
  margin-bottom: 14px;
  font-size: 13px;
  font-weight: 700;
  color: #0f766e;
}

.hint {
  margin: 0 0 14px;
  font-size: 13px;
  color: #64748b;
}

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

.checkbox-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 14px;
  font-size: 14px;
  color: #334155;
}

.checkbox-row input {
  margin-top: 3px;
}

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

.auth-alert-success {
  background: #ecfdf5;
  border: 1px solid #bbf7d0;
  color: #166534;
}

.med-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.med-title {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
}

.link-btn {
  border: 0;
  background: transparent;
  color: #dc2626;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  padding: 0;
}

.add-btn {
  width: 100%;
}

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

.remove-btn {
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.remove-btn:hover {
  background: #f1f5f9;
  color: #334155;
}
</style>