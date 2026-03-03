<template>
  <div class="wrap">
    <div class="card">
      <h1>Init (1/3) - Profile</h1>

      <form @submit.prevent="onNext">
        <label>Age</label>
        <input v-model.number="profile.age" type="number" min="1" />

        <label>Gender</label>
        <select v-model="profile.gender">
          <option value="">Select</option>
          <option>Male</option>
          <option>Female</option>
        </select>

        <label>Diabetes Type</label>
        <select v-model="profile.diabetesType">
          <option value="">Select</option>
          <option>T1D</option>
          <option>T2D</option>
          <option>GDM</option>
        </select>

        <label>Diagnosis Timeframe</label>
        <select v-model="profile.diagnosisTimeframe">
          <option value="">Select</option>
          <option>&lt; 1 year</option>
          <option>1-3 years</option>
          <option>3+ years</option>
        </select>

        <button :disabled="loading">
          {{ loading ? "Saving..." : "Next" }}
        </button>
      </form>

      <p class="error" v-if="error">{{ error }}</p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { loadInitDraft, saveInitDraft } from "../../utils/initDraft";

const router = useRouter();
const loading = ref(false);
const error = ref("");

const draft = loadInitDraft();
const profile = reactive({
  age: draft.profile?.age ?? null,
  gender: draft.profile?.gender ?? "",
  diabetesType: draft.profile?.diabetesType ?? "",
  diagnosisTimeframe: draft.profile?.diagnosisTimeframe ?? "",
});

function onNext() {
  error.value = "";
  loading.value = true;

  if (!profile.age || profile.age <= 0) {
    loading.value = false;
    return (error.value = "Age is required");
  }
  if (!profile.gender) {
    loading.value = false;
    return (error.value = "Gender is required");
  }
  if (!profile.diabetesType) {
    loading.value = false;
    return (error.value = "Diabetes type is required");
  }
  if (!profile.diagnosisTimeframe) {
    loading.value = false;
    return (error.value = "Diagnosis timeframe is required");
  }

  saveInitDraft({ profile: { ...profile } });
  loading.value = false;
  router.push("/init/screenings");
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
label {
  display: block;
  margin-top: 12px;
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
}
</style>