<template>
  <div class="wrap">
    <div class="card">
      <h1>Init (2/3) - Screenings</h1>

      <form @submit.prevent="onNext">
        <h2 class="section">A1C</h2>
        <label>Type</label>
        <select v-model.number="a1cTypeId">
          <option :value="1">A1C_6M</option>
          <option :value="2">A1C_3M</option>
        </select>

        <label>Last Completed (optional)</label>
        <input v-model="a1cLast" type="date" />

        <h2 class="section">Eye Exam</h2>
        <label>Type</label>
        <select v-model.number="eyeTypeId">
          <option :value="3">EYE_12M</option>
          <option :value="4">EYE_6M</option>
        </select>

        <label>Last Completed (optional)</label>
        <input v-model="eyeLast" type="date" />

        <h2 class="section">Kidney</h2>
        <label>Type</label>
        <select v-model.number="kidneyTypeId" disabled>
          <option :value="5">KIDNEY_12M</option>
        </select>

        <label>Last Completed (optional)</label>
        <input v-model="kidneyLast" type="date" />

        <div class="row">
          <button type="button" class="secondary" @click="onBack">Back</button>
          <button :disabled="loading">{{ loading ? "Saving..." : "Next" }}</button>
        </div>
      </form>

      <p class="error" v-if="error">{{ error }}</p>
      <p class="ok" v-if="ok">{{ ok }}</p>
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

function onBack() {
  router.push("/init/profile");
}

function onNext() {
  error.value = "";
  ok.value = "";
  loading.value = true;

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

  const screenings = [
    { screeningTypeId: a1cTypeId.value, lastCompletedDate: a1cLast.value || null },
    { screeningTypeId: eyeTypeId.value, lastCompletedDate: eyeLast.value || null },
    { screeningTypeId: kidneyTypeId.value, lastCompletedDate: kidneyLast.value || null },
  ];

  saveInitDraft({ screenings });

  ok.value = "Saved. Next: medications.";
  loading.value = false;
  router.push("/init/medications");
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
.section {
  margin-top: 16px;
  margin-bottom: 6px;
  font-size: 16px;
}
label {
  display: block;
  margin-top: 10px;
  margin-bottom: 6px;
  font-size: 14px;
}
input,
select {
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