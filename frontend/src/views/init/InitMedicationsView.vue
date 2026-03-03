<template>
  <div class="wrap">
    <div class="card">
      <h1>Init (3/3) - Medications</h1>
      <p class="hint">Optional. You can leave this empty.</p>

      <form @submit.prevent="onFinish">
        <label>Name (optional)</label>
        <input v-model="med.name" placeholder="e.g., Metformin" />

        <label>Dosage (optional)</label>
        <input v-model="med.dosage" placeholder="e.g., 500 mg" />

        <label>Expected doses per day (optional)</label>
        <input v-model.number="med.expectedDosesPerDay" type="number" min="0" />

        <div class="row">
          <button type="button" class="secondary" @click="onBack">Back</button>
          <button :disabled="loading">
            {{ loading ? "Submitting..." : "Finish" }}
          </button>
        </div>
      </form>

      <p class="error" v-if="error">{{ error }}</p>
      <p class="ok" v-if="ok">{{ ok }}</p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import http from "../../api/http";
import { loadInitDraft, clearInitDraft, saveInitDraft } from "../../utils/initDraft";

const router = useRouter();
const loading = ref(false);
const error = ref("");
const ok = ref("");

const draft = loadInitDraft();

// 回填（如果你之前保存过）
const existingMed = Array.isArray(draft.medications) ? draft.medications[0] : null;

const med = reactive({
  name: existingMed?.name ?? "",
  dosage: existingMed?.dosage ?? "",
  expectedDosesPerDay: existingMed?.expectedDosesPerDay ?? null,
});

function onBack() {
  // 顺便把当前输入暂存一下（可选）
  const medsToSave = med.name && med.name.trim()
      ? [{ name: med.name.trim(), dosage: med.dosage || null, expectedDosesPerDay: med.expectedDosesPerDay ?? null }]
      : [];
  saveInitDraft({ medications: medsToSave });

  router.push("/init/screenings");
}

async function onFinish() {
  error.value = "";
  ok.value = "";
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

    const medications =
        med.name && med.name.trim()
            ? [
              {
                name: med.name.trim(),
                dosage: med.dosage || null,
                expectedDosesPerDay: med.expectedDosesPerDay ?? null,
              },
            ]
            : [];

    const res = await http.post("/api/registration/init", {
      profile,
      screenings,
      medications,
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
.hint {
  margin: 6px 0 12px;
  font-size: 13px;
}
label {
  display: block;
  margin-top: 10px;
  margin-bottom: 6px;
  font-size: 14px;
}
input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
}
.row {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}
button {
  flex: 1;
  padding: 10px 12px;
  border: 0;
  border-radius: 10px;
  cursor: pointer;
}
.secondary {
  border: 1px solid #d1d5db;
  background: transparent;
}
.error {
  margin-top: 12px;
  color: #b91c1c;
}
.ok {
  margin-top: 12px;
  color: #166534;
}
</style>