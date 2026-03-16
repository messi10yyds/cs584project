<template>
  <div class="organ-page">
    <div class="organ-shell">
      <div v-if="loading" class="state-card">
        Loading...
      </div>

      <div v-else-if="error" class="state-card error-card">
        {{ error }}
      </div>

      <template v-else>
        <!-- top hero -->
        <section class="hero-panel">
          <div class="hero-banner">
            <button class="back-btn" @click="goBack">← Back</button>

            <div class="hero-tag">{{ organMeta.tag }}</div>

            <div class="hero-main">
              <div class="hero-icon"><span v-if="typeof organMeta.icon === 'string'">{{ organMeta.icon }}</span>

                <component
                    v-else
                    :is="organMeta.icon"
                    :size="44"
                    style="display:block; transform:translateY(5px);"
                />
              </div>

              <div>
                <h1 class="hero-title">{{ organMeta.title }}</h1>
                <!--                <p class="hero-subtitle">-->
                <!--                  Educational content · Screening status-->
                <!--                </p>-->
              </div>
            </div>
          </div>
        </section>

        <!-- screening -->
        <section class="section-card">
          <div class="screening-section-title">
            <!--            <span class="screening-warning">⚠</span>-->
            SCREENING STATUS
          </div>

          <div v-if="!detail.screening" class="empty-box">
            No screening information available.
          </div>

          <div
              v-else
              class="screening-card"
              :class="screeningCardClass(detail.screening.status)"
          >
            <div
                class="screening-head"
                :class="screeningHeadClass(detail.screening.status)"
            >
              <div>Status</div>
              <div>Next Due</div>
              <div>Appointment</div>
              <div>Action</div>
            </div>

            <div class="screening-row">
              <div class="screening-cell">
                <span
                    class="status-pill"
                    :class="screeningStatusClass(detail.screening.status)"
                >
                  {{ screeningStatusText(detail.screening.status) }}
                </span>
              </div>

              <div class="screening-cell">
                {{ detail.screening.dueDate || "—" }}
              </div>

              <div class="screening-cell">
                {{
                  detail.screening.appointmentDate
                      ? detail.screening.appointmentDate
                      : "Not booked"
                }}
              </div>

              <div class="screening-cell">
                <button
                    class="screening-btn"
                    :class="screeningButtonClass(detail.screening.status)"
                    type="button"
                    @click="goToAppointment('organ-screening')"
                >
                  {{ detail.screening.appointmentDate ? "Reschedule" : "Book now" }}
                </button>
              </div>
            </div>

            <div class="screening-note">
              {{ detail.screening.displayName }}
            </div>
          </div>
        </section>

        <!-- education -->
        <section class="section-card">
          <div class="section-title">📘 Education</div>

          <div class="edu-tabs">
            <button
                class="edu-tab-btn"
                :class="{ active: activeTab === 'what' }"
                @click="activeTab = 'what'"
                type="button"
            >
              What is it?
            </button>

            <button
                class="edu-tab-btn"
                :class="{ active: activeTab === 'risk' }"
                @click="activeTab = 'risk'"
                type="button"
            >
              Risk Factors
            </button>

            <button
                class="edu-tab-btn"
                :class="{ active: activeTab === 'checkups' }"
                @click="activeTab = 'checkups'"
                type="button"
            >
              Checkups
            </button>
          </div>

          <!-- what is it -->
          <div v-if="activeTab === 'what'" class="edu-panel">
            <div
                v-for="(item, index) in normalizedWhatIsIt"
                :key="index"
                class="what-card"
            >
              <div class="what-card-top">
                <div class="what-card-icon">
                  <span v-if="typeof organWhatIcon === 'string'">{{ organWhatIcon }}</span>

                  <component
                      v-else
                      :is="organWhatIcon"
                      :size="34"
                      style="display:block; transform:translateY(5px);"
                  />

                </div>

                <div>
                  <div class="what-card-title">{{ item.title }}</div>
                  <div class="what-card-desc multiline-text">
                    {{ item.description }}
                  </div>
                </div>
              </div>
            </div>

            <div
                v-if="whatDetail.heading || whatDetail.body || whatDetail.warning"
                class="what-detail-card"
            >
              <div v-if="whatDetail.heading" class="what-detail-heading">
                {{ whatDetail.heading }}
              </div>

              <div v-if="whatDetail.body" class="what-detail-body">
                {{ whatDetail.body }}
              </div>

              <div v-if="whatDetail.warning" class="what-detail-warning">
                ⚠ {{ whatDetail.warning }}
              </div>
            </div>
          </div>

          <!-- risk factors -->
          <div v-else-if="activeTab === 'risk'" class="edu-panel">
            <div
                v-for="(item, index) in normalizedRiskFactors"
                :key="index"
                class="risk-factor-card"
            >
              <div class="risk-factor-title">
                {{ item.title }}
              </div>
              <div class="risk-factor-desc">
                {{ item.description }}
              </div>
            </div>
          </div>

          <!-- checkups -->
          <div v-else class="edu-panel">
            <template v-if="checkupFrequencies.length > 0">
              <div class="edu-subtitle">
                How often do you need this checkup?
              </div>

              <div
                  v-for="(item, index) in checkupFrequencies"
                  :key="`freq-${index}`"
                  class="checkup-card"
              >
                <div class="checkup-left">
                  <div class="checkup-calendar-icon">📅</div>
                  <div>
                    <div class="checkup-title">{{ item.title }}</div>
                    <div class="checkup-desc">{{ item.description }}</div>
                  </div>
                </div>

                <div
                    class="checkup-pill"
                    :class="item.badgeType === 'follow' ? 'follow' : 'standard'"
                >
                  {{ item.badge }}
                </div>
              </div>
            </template>

            <div class="edu-subtitle section-gap">
              {{ checkupSectionTitle }}
            </div>

            <div
                v-for="(item, index) in detail.education?.checkups || []"
                :key="index"
                class="exam-step"
            >
              <div class="exam-step-dot">{{ index + 1 }}</div>
              <div class="exam-step-text">{{ item }}</div>
            </div>

            <button
                v-if="detail.screening"
                class="exam-book-btn"
                type="button"
                @click="goToAppointment('organ-checkups')"
            >
              📅
              {{
                detail.screening.appointmentDate
                    ? `Reschedule ${detail.screening.displayName}`
                    : `Book My ${detail.screening.displayName} Now`
              }}
            </button>
          </div>
        </section>
      </template>
    </div>
  </div>
</template>

<script setup>
import {computed, onMounted, reactive, ref, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import KidneyIcon from "@/components/icons/KidneyIcon.vue";
import http from "../api/http";

const route = useRoute();
const router = useRouter();

const loading = ref(true);
const error = ref("");
const activeTab = ref("what");

const detail = reactive({
  organ: "",
  screening: null,
  education: null,
});

const organConfigMap = {
  eye: {
    tag: "EYE HEALTH",
    title: "Retinopathy & Eye Health",
    icon: "👁️",
  },
  kidney: {
    tag: "KIDNEY HEALTH",
    title: "Kidney Health",
    icon: KidneyIcon,
  },
  heart: {
    tag: "Heart HEALTH",
    title: "Heart & A1C Health",
    icon: "❤️",
  },
};

const organCheckupFrequencyMap = {
  eye: [
    {
      title: "Every year",
      description: "If no retinopathy is detected",
      badge: "Standard",
      badgeType: "standard",
    },
    {
      title: "Every 6 months",
      description: "If diabetes-related eye problems are present",
      badge: "Closer Follow-up",
      badgeType: "follow",
    },
  ],
  heart: [
    {
      title: "Every 3 months",
      description:
          "If your treatment has changed or you're having trouble meeting your blood sugar goals",
      badge: "Closer Follow-up",
      badgeType: "follow",
    },
    {
      title: "Every 6 months",
      description:
          "If you're meeting your treatment and blood sugar goals",
      badge: "Standard",
      badgeType: "standard",
    },
  ],
};

const organWhatDetailFallbackMap = {
  eye: {
    heading: "What is Diabetic Retinopathy?",
    body:
        "High blood sugar damages the small blood vessels inside your retina — the light-sensitive layer at the back of your eye. Over time these vessels can leak, swell, or grow abnormally, threatening your vision.",
    warning:
        "Retinopathy is one of the leading causes of preventable blindness in people with diabetes — but it can be stopped when caught early.",
  },
  kidney: {
    heading: "What is diabetic kidney disease?",
    body:
        "Diabetes can damage the tiny blood vessels in the kidneys, making it harder for them to filter waste and extra fluid from the blood.",
    warning:
        "Kidney damage often develops silently, so regular screening is important even when you feel well.",
  },
  heart: {
    heading: "Why does heart health matter in diabetes?",
    body:
        "Diabetes increases the risk of blood vessel damage, high blood pressure, and other problems that can strain the heart over time.",
    warning:
        "Managing blood sugar, blood pressure, and lifestyle risks can help lower long-term heart complications.",
  },
};

const organKey = computed(() => route.params.organ);

const organMeta = computed(() => {
  return organConfigMap[organKey.value] || {
    tag: "ORGAN HEALTH",
    title: "Organ Detail",
    icon: "🩺",
  };
});

const normalizedWhatIsIt = computed(() => {
  const list = detail.education?.whatIsIt || [];

  return list.map((item, index) => {
    if (typeof item === "string") {
      return {
        title: `Subtitle ${index + 1}`,
        description: item,
      };
    }

    return {
      title: item.title || `Subtitle ${index + 1}`,
      description: item.description || "",
    };
  });
});

const normalizedRiskFactors = computed(() => {
  const list = detail.education?.riskFactors || [];

  return list.map((item, index) => {
    if (typeof item === "string") {
      return {
        title: `Subtitle ${index + 1}`,
        description: item,
      };
    }

    return {
      title: item.title || `Subtitle ${index + 1}`,
      description: item.description || "",
    };
  });
});

const checkupFrequencies = computed(() => {
  return organCheckupFrequencyMap[detail.organ] || [];
});

const checkupSectionTitle = computed(() => {
  if (detail.organ === "eye") return "What happens during the exam?";
  if (detail.organ === "heart") return "What happens during the exam?";
  return "What happens during the exam?";
});

const organWhatIcon = computed(() => {
  if (detail.organ === "eye") return "👁️";
  if (detail.organ === "kidney") return KidneyIcon;
  if (detail.organ === "heart") return "❤️";
  return "🩺";
});

const whatDetail = computed(() => {
  return (
      organWhatDetailFallbackMap[detail.organ] || {
        heading: "",
        body: "",
        warning: "",
      }
  );
});

function screeningStatusText(status) {
  if (status === "OVERDUE") return "Overdue";
  if (status === "DUE_SOON") return "Due soon";
  if (status === "ON_TRACK") return "On track";
  if (status === "COMPLETED") return "Completed";
  return "Unknown";
}

function screeningStatusClass(status) {
  if (status === "OVERDUE") return "status-overdue";
  if (status === "DUE_SOON") return "status-due-soon";
  if (status === "ON_TRACK") return "status-on-track";
  if (status === "COMPLETED") return "status-completed";
  return "status-default";
}

function screeningCardClass(status) {
  if (status === "OVERDUE") return "screening-card-overdue";
  if (status === "DUE_SOON") return "screening-card-due-soon";
  if (status === "ON_TRACK") return "screening-card-on-track";
  return "";
}

function screeningHeadClass(status) {
  if (status === "OVERDUE") return "screening-head-overdue";
  if (status === "DUE_SOON") return "screening-head-due-soon";
  if (status === "ON_TRACK") return "screening-head-on-track";
  return "";
}

function screeningButtonClass(status) {
  if (status === "OVERDUE") return "screening-btn-red";
  if (status === "DUE_SOON") return "screening-btn-yellow";
  if (status === "ON_TRACK") return "screening-btn-green";
  return "";
}

async function fetchOrganDetail() {
  const organ = route.params.organ;

  if (!["eye", "kidney", "heart"].includes(organ)) {
    error.value = "Unsupported organ page.";
    loading.value = false;
    return;
  }

  loading.value = true;
  error.value = "";

  try {
    const res = await http.get(`/organs/${organ}`);

    if (res.code !== 0) {
      error.value = res.message || "Failed to load organ detail.";
      return;
    }

    const data = res.data || {};
    detail.organ = data.organ || organ;
    detail.screening = data.screening || null;
    detail.education = data.education || null;
    activeTab.value = "what";
  } catch (e) {
    error.value = "Failed to load organ detail.";
  } finally {
    loading.value = false;
  }
}

function goBack() {
  router.push("/dashboard");
}

onMounted(() => {
  fetchOrganDetail();
});

watch(
    () => route.params.organ,
    () => {
      fetchOrganDetail();
    }
);

function goToAppointment(source = "organ-detail") {
  if (!detail.screening?.screeningTypeId) {
    alert("Missing screeningTypeId");
    return;
  }

  router.push({
    path: `/screenings/${detail.screening.screeningTypeId}/appointment`,
    query: {
      organ: detail.organ,
      source,
    },
  });
}
</script>

<style scoped>
.organ-page {
  min-height: 100vh;
  padding: 18px;
  background: radial-gradient(900px 500px at 20% 10%, rgba(20, 184, 166, 0.1), transparent),
  radial-gradient(900px 500px at 80% 20%, rgba(59, 130, 246, 0.1), transparent),
  #f8fafc;
}

.organ-shell {
  max-width: 680px;
  margin: 0 auto;
}

.state-card,
.section-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 18px;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.08);
}

.state-card {
  padding: 20px;
  color: #334155;
}

.error-card {
  background: #fef2f2;
  border-color: #fecaca;
  color: #b91c1c;
}

.hero-panel {
  margin-bottom: 18px;
}

.hero-banner {
  background: linear-gradient(135deg, #0f8f86, #127c86);
  color: #ffffff;
  border-radius: 20px;
  padding: 18px;
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.1);
}

.back-btn {
  border: none;
  background: rgba(255, 255, 255, 0.16);
  color: #ffffff;
  border-radius: 999px;
  padding: 8px 14px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  margin-bottom: 16px;
}

.hero-tag {
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.12em;
  opacity: 0.9;
  margin-bottom: 14px;
}

.hero-main {
  display: flex;
  align-items: center;
  gap: 16px;
}

.hero-icon {
  width: 72px;
  height: 72px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.14);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34px;
  flex-shrink: 0;
}

.hero-title {
  margin: 0 0 6px;
  font-size: 38px;
  line-height: 1.12;
  font-weight: 800;
}

.hero-subtitle {
  margin: 0;
  font-size: 15px;
  opacity: 0.94;
}

.section-card {
  padding: 18px;
  margin-bottom: 18px;
}

.section-title {
  margin-bottom: 14px;
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: #7c8aa5;
}

.empty-box {
  padding: 14px;
  border: 1px dashed #cbd5e1;
  border-radius: 14px;
  color: #64748b;
  background: #f8fafc;
  font-size: 14px;
}

.screening-section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0.08em;
  color: #7c8aa5;
}

.screening-warning {
  font-size: 16px;
}

.screening-card {
  border-radius: 16px;
  padding: 16px;
}

.screening-card-overdue {
  border: 1px solid #f3b2b2;
  background: #fff4f4;
}

.screening-card-due-soon {
  border: 1px solid #f6d37c;
  background: #fffbeb;
}

.screening-card-on-track {
  border: 1px solid #bbf7d0;
  background: #f0fdf4;
}

.screening-head,
.screening-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1.1fr 0.95fr;
  gap: 12px;
  align-items: center;
}

.screening-head {
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: 10px;
}

.screening-head-overdue {
  color: #b91c1c;
}

.screening-head-due-soon {
  color: #b45309;
}

.screening-head-on-track {
  color: #166534;
}

.screening-row {
  padding-bottom: 12px;
  border-bottom: 1px solid #f1b9b9;
}

.screening-cell {
  font-size: 15px;
  color: #334155;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 6px 12px;
  font-size: 13px;
  font-weight: 700;
  white-space: nowrap;
}

.status-overdue {
  background: #fee2e2;
  color: #dc2626;
}

.status-due-soon {
  background: #fef3c7;
  color: #d97706;
}

.status-on-track,
.status-completed {
  background: #dcfce7;
  color: #15803d;
}

.status-default {
  background: #e5e7eb;
  color: #475569;
}

.screening-btn {
  border: none;
  border-radius: 999px;
  padding: 9px 14px;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
}

.screening-btn-red {
  background: #ef4444;
  color: #ffffff;
}

.screening-btn-yellow {
  background: #f59e0b;
  color: #ffffff;
}

.screening-btn-green {
  background: #dff4e7;
  color: #15803d;
  border: 1px solid #bbf7d0;
}

.screening-btn-green:hover {
  background: #c8ecd8;
}

.screening-btn-red:hover {
  background: #dc2626;
}

.screening-btn-yellow:hover {
  background: #d97706;
}

.screening-note {
  padding-top: 12px;
  font-size: 14px;
  line-height: 1.5;
  color: #7c2d12;
}

.screening-card-on-track .screening-note {
  color: #166534;
}

.screening-card-due-soon .screening-note {
  color: #92400e;
}

.edu-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.edu-tab-btn {
  border: 1px solid #dbe3ef;
  background: #f8fafc;
  color: #7c8aa5;
  border-radius: 999px;
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.edu-tab-btn.active {
  background: #0f8f86;
  color: #ffffff;
  border-color: #0f8f86;
}

.edu-panel {
  display: grid;
  gap: 12px;
}

.what-card {
  background: linear-gradient(135deg, #1e7f84, #1b8b7e);
  color: #ffffff;
  border-radius: 16px;
  padding: 18px;
}

.what-card-top {
  display: flex;
  align-items: center;
  gap: 16px;
}

.what-card-icon {
  width: 68px;
  height: 68px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34px;
  flex-shrink: 0;
}

.what-card-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 6px;
}

.what-card-desc {
  font-size: 14px;
  line-height: 1.6;
  opacity: 0.95;
}

.multiline-text {
  white-space: pre-line;
}

.what-detail-card {
  background: #f6f9fc;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 18px;
}

.what-detail-heading {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 10px;
}

.what-detail-body {
  font-size: 15px;
  line-height: 1.8;
  color: #334155;
}

.what-detail-warning {
  margin-top: 14px;
  background: #fef3c7;
  border-radius: 14px;
  padding: 14px;
  font-size: 14px;
  line-height: 1.6;
  color: #92400e;
}

.risk-factor-card {
  background: #f8fafc;
  border-left: 4px solid #14b8a6;
  border-radius: 14px;
  padding: 14px 16px;
}

.risk-factor-title {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 6px;
}

.risk-factor-desc {
  font-size: 14px;
  line-height: 1.6;
  color: #64748b;
}

.edu-subtitle {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 10px;
}

.section-gap {
  margin-top: 18px;
}

.checkup-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 14px;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 14px 16px;
  margin-bottom: 10px;
}

.checkup-left {
  display: flex;
  gap: 12px;
  align-items: center;
  min-width: 0;
}

.checkup-calendar-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.checkup-title {
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
}

.checkup-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.5;
  margin-top: 3px;
}

.checkup-pill {
  border-radius: 999px;
  padding: 7px 12px;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
  white-space: nowrap;
}

.checkup-pill.standard {
  background: #dcfce7;
  color: #15803d;
}

.checkup-pill.follow {
  background: #fef3c7;
  color: #d97706;
}

.exam-step {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #e5e7eb;
}

.exam-step:last-of-type {
  border-bottom: none;
}

.exam-step-dot {
  width: 28px;
  height: 28px;
  border-radius: 999px;
  background: #0f8f86;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
}

.exam-step-text {
  font-size: 15px;
  line-height: 1.6;
  color: #334155;
}

.exam-book-btn {
  width: 100%;
  margin-top: 18px;
  border: none;
  border-radius: 14px;
  padding: 14px 16px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  background: #1e7f84;
  color: #ffffff;
}

.exam-book-btn:hover {
  opacity: 0.95;
}

@media (max-width: 640px) {
  .organ-page {
    padding: 16px;
  }

  .hero-title {
    font-size: 30px;
  }

  .hero-main {
    align-items: flex-start;
  }

  .screening-head {
    display: none;
  }

  .screening-row {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .checkup-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .checkup-pill {
    align-self: flex-start;
  }
}
</style>