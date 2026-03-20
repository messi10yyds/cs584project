<template>
  <div class="foot-page">
    <div class="foot-shell">
      <!-- loading -->
      <div v-if="loading" class="state-card">
        Loading foot check...
      </div>

      <!-- error -->
      <div v-else-if="error" class="state-card error-card">
        {{ error }}
      </div>

      <!-- content -->
      <template v-else>
        <!-- header -->
        <section class="foot-hero">
          <div class="foot-hero-top">Daily Foot Check · 🦶</div>
          <h1 class="foot-title">Foot Symptom Check</h1>
          <p class="foot-subtitle">
            Answer each question about how your feet feel today.
          </p>
        </section>

        <!-- education -->
        <section v-if="footData.education" class="foot-card tip-card">
          <div class="tip-title">{{ footData.education.title }}</div>
          <div class="tip-desc">{{ footData.education.description }}</div>
          <div class="tip-note">{{ footData.education.tip }}</div>
        </section>

        <!-- ✅ 新增：foot symptom image -->
        <section class="foot-image-card">
          <img
              src="/FOOT_SYMPTOMS.jpg"
              alt="Foot symptom warning signs"
              class="foot-image"
          />
        </section>
        <!-- completed notice -->
        <section v-if="footData.todayCompleted" class="foot-card notice-card">
          You have already completed today's foot check. You can still update
          and submit again if needed.
        </section>

        <!-- questions -->
        <section class="question-list">
          <div
              v-for="question in footData.questions"
              :key="question.id"
              class="question-card"
          >
            <div class="question-text">
              {{ question.question }}
            </div>

            <div class="question-actions">
              <button
                  type="button"
                  class="answer-btn yes-btn"
                  :class="{ active: answers[question.id] === true }"
                  @click="answers[question.id] = true"
              >
                Yes
              </button>

              <button
                  type="button"
                  class="answer-btn no-btn"
                  :class="{ active: answers[question.id] === false }"
                  @click="answers[question.id] = false"
              >
                No
              </button>
            </div>
          </div>
        </section>

        <!-- analyze button -->
        <button
            class="analyze-btn"
            type="button"
            :disabled="submitting"
            @click="submitFootCheck"
        >
          {{ submitting ? "Analyzing..." : "🔎 Analyze Results" }}
        </button>

        <!-- result -->
        <section
            v-if="footData.latestResult"
            class="foot-card result-card"
            :class="resultCardClass(footData.latestResult.riskLevel)"
        >
          <div class="result-title">
            {{ footData.latestResult.title }}
          </div>

          <div class="result-message">
            {{ footData.latestResult.message }}
          </div>
        </section>

        <!-- back -->
        <button class="back-btn" type="button" @click="goBack">
          ← Back to Dashboard
        </button>
      </template>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import http from "../api/http";

const router = useRouter();

const loading = ref(true);
const error = ref("");
const submitting = ref(false);

const footData = reactive({
  organ: "foot",
  education: null,
  questions: [],
  todayCompleted: false,
  todayAnswers: [],
  latestResult: null,
});

const answers = reactive({});

function goBack() {
  router.push("/dashboard");
}

function resultCardClass(riskLevel) {
  if (riskLevel === "URGENT") return "result-urgent";
  if (riskLevel === "WARNING") return "result-warning";
  return "result-normal";
}

function fillAnswersFromToday(todayAnswers = []) {
  Object.keys(answers).forEach((key) => {
    delete answers[key];
  });

  todayAnswers.forEach((item) => {
    answers[item.questionId] = item.answer;
  });
}

async function fetchFootData() {
  loading.value = true;
  error.value = "";

  try {
    const res = await http.get("/organs/foot");

    if (res.code !== 0) {
      error.value = res.message || "Failed to load foot check.";
      return;
    }

    const data = res.data || {};

    footData.organ = data.organ || "foot";
    footData.education = data.education || null;
    footData.questions = data.questions || [];
    footData.todayCompleted = !!data.todayCompleted;
    footData.todayAnswers = data.todayAnswers || [];
    footData.latestResult = data.latestResult || null;

    fillAnswersFromToday(footData.todayAnswers);
  } catch (e) {
    error.value = "Failed to load foot symptom check.";
  } finally {
    loading.value = false;
  }
}

async function submitFootCheck() {
  if (submitting.value) return;

  submitting.value = true;

  try {
    const payload = {
      answers: footData.questions.map((q) => ({
        questionId: q.id,
        answer: answers[q.id] ?? false,
      })),
    };

    const res = await http.post("/organs/foot/check", payload);

    if (res.code !== 0) {
      alert(res.message || "Failed to analyze results.");
      return;
    }

    footData.latestResult = res.data;
    footData.todayCompleted = true;
  } catch (e) {
    alert("Failed to submit foot symptom check.");
  } finally {
    submitting.value = false;
  }
}

onMounted(() => {
  fetchFootData();
});
</script>

<style scoped>
.foot-page {
  min-height: 100vh;
  padding: 18px;
  background: radial-gradient(900px 500px at 20% 10%, rgba(20, 184, 166, 0.08), transparent),
  radial-gradient(900px 500px at 80% 20%, rgba(99, 102, 241, 0.1), transparent),
  #f8fafc;
}

.foot-shell {
  max-width: 620px;
  margin: 0 auto;
}

.state-card {
  padding: 18px;
  border-radius: 20px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
}

.error-card {
  color: #b91c1c;
  background: #fef2f2;
  border-color: #fecaca;
}

.foot-hero {
  padding: 22px 22px 20px;
  border-radius: 24px 24px 0 0;
  background: linear-gradient(135deg, #6d43a8, #5a338f);
  color: #ffffff;
  box-shadow: 0 14px 34px rgba(91, 51, 143, 0.22);
}

.foot-hero-top {
  display: inline-block;
  padding: 4px 10px;
  margin-bottom: 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  font-size: 13px;
  font-weight: 700;
}

.foot-title {
  margin: 0;
  font-size: 36px;
  line-height: 1.15;
}

.foot-subtitle {
  margin: 10px 0 0;
  font-size: 17px;
  color: rgba(255, 255, 255, 0.9);
}

.foot-card {
  margin-top: 16px;
  padding: 18px;
  border-radius: 18px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.06);
}

.tip-card {
  background: #fffaf0;
  border-color: #fde7b2;
}

.tip-title {
  font-size: 16px;
  font-weight: 800;
  color: #7c4a03;
  margin-bottom: 8px;
}

.tip-desc {
  font-size: 15px;
  line-height: 1.7;
  color: #7c4a03;
}

.tip-note {
  margin-top: 10px;
  font-size: 14px;
  line-height: 1.6;
  color: #9a6700;
}

.notice-card {
  background: #f5f3ff;
  border-color: #d8b4fe;
  color: #5b21b6;
  font-weight: 600;
}

.question-list {
  margin-top: 16px;
}

.question-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 18px;
  margin-top: 12px;
  border-radius: 18px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.05);
}

.question-text {
  flex: 1;
  font-size: 17px;
  line-height: 1.5;
  color: #0f172a;
}

.question-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.answer-btn {
  min-width: 68px;
  padding: 10px 16px;
  border-radius: 999px;
  border: 1px solid #cbd5e1;
  background: #ffffff;
  font-size: 15px;
  font-weight: 700;
  color: #334155;
  cursor: pointer;
  transition: background-color 0.18s ease,
  border-color 0.18s ease,
  color 0.18s ease,
  transform 0.18s ease,
  box-shadow 0.18s ease;
}

.answer-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
}

.yes-btn:hover {
  border-color: #fda4af;
  background: #fff1f2;
  color: #e11d48;
}

.no-btn:hover {
  border-color: #86efac;
  background: #f0fdf4;
  color: #16a34a;
}

.answer-btn.active.yes-btn {
  border-color: #f87171;
  background: #fef2f2;
  color: #dc2626;
  box-shadow: 0 8px 18px rgba(248, 113, 113, 0.16);
}

.answer-btn.active.no-btn {
  border-color: #4ade80;
  background: #f0fdf4;
  color: #16a34a;
  box-shadow: 0 8px 18px rgba(74, 222, 128, 0.16);
}

.analyze-btn {
  width: 100%;
  margin-top: 18px;
  padding: 16px;
  border: 0;
  border-radius: 18px;
  background: linear-gradient(135deg, #6d43a8, #4c2c7a);
  color: #ffffff;
  font-size: 18px;
  font-weight: 800;
  box-shadow: 0 12px 26px rgba(91, 51, 143, 0.22);
  cursor: pointer;
  transition: transform 0.18s ease,
  box-shadow 0.18s ease,
  opacity 0.18s ease;
}

.analyze-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 14px 30px rgba(91, 51, 143, 0.28);
}

.analyze-btn:disabled {
  opacity: 0.72;
  cursor: not-allowed;
}

.result-card {
  margin-top: 20px;
}

.result-title {
  font-size: 24px;
  font-weight: 800;
  line-height: 1.3;
  margin-bottom: 10px;
}

.result-message {
  font-size: 17px;
  line-height: 1.7;
}

.result-urgent {
  background: #fef2f2;
  border-color: #fca5a5;
  color: #b91c1c;
}

.result-warning {
  background: #fff7ed;
  border-color: #fdba74;
  color: #c2410c;
}

.result-normal {
  background: #f0fdf4;
  border-color: #86efac;
  color: #166534;
}

.back-btn {
  width: 100%;
  margin-top: 18px;
  padding: 14px 16px;
  border-radius: 16px;
  border: 1px solid #d1d5db;
  background: #f8fafc;
  color: #334155;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.18s ease,
  border-color 0.18s ease,
  transform 0.18s ease,
  box-shadow 0.18s ease;
}

.back-btn:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  transform: translateY(-1px);
  box-shadow: 0 10px 22px rgba(15, 23, 42, 0.06);
}

.foot-image-card {
  margin-top: 16px;
  border-radius: 18px;
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.06);
}

.foot-image {
  width: 100%;
  display: block;
}

@media (max-width: 640px) {
  .foot-title {
    font-size: 30px;
  }

  .question-card {
    flex-direction: column;
    align-items: stretch;
  }

  .question-actions {
    justify-content: flex-end;
  }
}
</style>