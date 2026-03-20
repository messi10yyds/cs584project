<template>
  <div class="appointment-page">
    <div class="appointment-shell">
      <!-- loading -->
      <div v-if="loading" class="state-card">
        Loading appointment...
      </div>

      <!-- error -->
      <div v-else-if="error" class="state-card error-card">
        {{ error }}
      </div>

      <!-- content -->
      <template v-else>
        <section class="hero-panel">
          <div class="hero-banner">
            <button class="back-btn" @click="goBack">← Back</button>

            <div class="hero-tag-row">
              <div class="hero-tag">{{ appointmentTag }}</div>
            </div>

            <div v-if="successMessage" class="success-banner">
              {{ successMessage }}
            </div>

            <div class="hero-main">
              <div class="hero-icon">
                <span v-if="typeof appointmentIcon === 'string'">
                  {{ appointmentIcon }}
                </span>
                <component
                    v-else
                    :is="appointmentIcon"
                    :size="46"
                    style="display: block; transform: translateY(8px);"
                />
              </div>

              <div>
                <h1 class="hero-title">{{ displayScreeningName }}</h1>
                <p class="hero-subtitle">Appointment management</p>
              </div>
            </div>
          </div>
        </section>

        <section class="section-card">
          <div class="section-title">Appointment Details</div>

          <div class="info-list">
            <div class="info-row">
              <div class="info-label">Screening</div>
              <div class="info-value">{{ displayScreeningName }}</div>
            </div>

            <div class="info-row">
              <div class="info-label">Last completed</div>
              <div class="info-value">{{ formatDate(detail.lastCompletedDate) }}</div>
            </div>

            <div class="info-row">
              <div class="info-label">Next due</div>
              <div class="info-value">{{ formatDate(detail.nextDueDate) }}</div>
            </div>

            <div class="info-row">
              <div class="info-label">Appointment</div>
              <div class="info-value">
                {{ detail.appointmentBooked ? (detail.scheduledFor || "Booked") : "Not booked" }}
              </div>
            </div>

            <div class="info-row" v-if="detail.appointmentBooked">
              <div class="info-label">Status</div>
              <div class="info-value">
                <span class="status-badge" :class="appointmentStatusClass(detail.appointmentStatus)">
                  {{ appointmentStatusText(detail.appointmentStatus) }}
                </span>
              </div>
            </div>
          </div>

          <div class="appointment-form">
            <div class="section-subtitle">
              {{ detail.appointmentBooked ? "Reschedule appointment" : "Book appointment" }}
            </div>

            <label class="date-label" for="scheduled-date">Select date</label>
            <input
                id="scheduled-date"
                v-model="selectedDate"
                class="date-input"
                type="date"
                :min="minDate"
            />

            <div v-if="dateError" class="inline-error">
              {{ dateError }}
            </div>

            <button
                class="primary-btn"
                type="button"
                :disabled="submitting || !selectedDate || !!dateError"
                @click="submitAppointment"
            >
              {{
                submitting
                    ? "Saving..."
                    : detail.appointmentBooked
                        ? "Reschedule"
                        : "Book"
              }}
            </button>
          </div>
        </section>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import http from "../api/http";
import KidneyIcon from "@/components/icons/KidneyIcon.vue";
import kidneyIcon from "@/components/icons/KidneyIcon.vue";
const route = useRoute();
const router = useRouter();

const loading = ref(true);
const error = ref("");
const submitting = ref(false);
const selectedDate = ref("");
const successMessage = ref("");

const detail = reactive({
  screeningTypeId: null,
  screeningName: "",
  lastCompletedDate: "",
  nextDueDate: "",
  appointmentBooked: false,
  appointmentId: null,
  scheduledFor: "",
  appointmentStatus: "",
});

const screeningTypeId = computed(() => route.params.screeningTypeId);
const organ = computed(() => route.query.organ || "");

const appointmentTag = computed(() => {
  if (organ.value === "eye") return "EYE SCREENING";
  if (organ.value === "kidney") return "KIDNEY SCREENING";
  if (organ.value === "heart") return "HEART SCREENING";
  return "SCREENING";
});

const appointmentIcon = computed(() => {
  if (organ.value === "eye") return "👁️";
  if (organ.value === "kidney") return kidneyIcon;
  if (organ.value === "heart") return "💗";
  return "🩺";
});

const displayScreeningName = computed(() => {
  const raw = detail.screeningName || "";

  if (raw === "EYE_12M") return "Eye Exam";
  if (raw === "EYE_6M") return "Eye Exam";
  if (raw === "KIDNEY_12M") return "Kidney Test";
  if (raw === "A1C_6M") return "A1C Test";
  if (raw === "A1C_12M") return "A1C Test";

  return raw || "Screening Appointment";
});

const minDate = computed(() => {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0");
  const day = String(today.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
});

const dateError = computed(() => {
  if (!selectedDate.value) return "";

  if (selectedDate.value < minDate.value) {
    return "Please select today or a future date.";
  }

  return "";
});

function appointmentStatusText(status) {
  if (status === "SCHEDULED") return "Scheduled";
  if (status === "COMPLETED") return "Completed";
  if (status === "CANCELLED") return "Cancelled";
  return status || "Unknown";
}

function appointmentStatusClass(status) {
  if (status === "SCHEDULED") return "status-scheduled";
  if (status === "COMPLETED") return "status-completed";
  if (status === "CANCELLED") return "status-cancelled";
  return "status-default";
}

async function fetchAppointmentDetail() {
  if (!screeningTypeId.value) {
    error.value = "Missing screeningTypeId.";
    loading.value = false;
    return;
  }

  loading.value = true;
  error.value = "";

  try {
    const res = await http.get(`/screenings/${screeningTypeId.value}/appointment`);

    if (res.code !== 0) {
      error.value = res.message || "Failed to load appointment detail.";
      return;
    }

    const data = res.data || {};
    detail.screeningTypeId = data.screeningTypeId ?? null;
    detail.screeningName = data.screeningName || "";
    detail.lastCompletedDate = data.lastCompletedDate || "";
    detail.nextDueDate = data.nextDueDate || "";
    detail.appointmentBooked = !!data.appointmentBooked;
    detail.appointmentId = data.appointmentId ?? null;
    detail.scheduledFor = data.scheduledFor || "";
    detail.appointmentStatus = data.appointmentStatus || "";
    selectedDate.value = data.scheduledFor || "";
  } catch (e) {
    error.value = "Failed to load appointment detail.";
  } finally {
    loading.value = false;
  }
}

async function submitAppointment() {
  if (!screeningTypeId.value) {
    alert("Missing screeningTypeId.");
    return;
  }

  if (!selectedDate.value) {
    alert("Please select a date.");
    return;
  }

  if (dateError.value) {
    alert(dateError.value);
    return;
  }

  submitting.value = true;
  successMessage.value = "";

  try {
    let res;

    if (detail.appointmentBooked) {
      res = await http.put(
          `/screenings/${screeningTypeId.value}/appointment/reschedule`,
          {
            scheduledFor: selectedDate.value,
          }
      );
    } else {
      res = await http.post(
          `/screenings/${screeningTypeId.value}/appointment`,
          {
            scheduledFor: selectedDate.value,
          }
      );
    }

    if (res.code !== 0) {
      alert(res.message || "Failed to save appointment.");
      return;
    }

    await fetchAppointmentDetail();
    successMessage.value = detail.appointmentBooked
        ? "Appointment updated successfully."
        : "Appointment booked successfully.";

    setTimeout(() => {
      successMessage.value = "";
    }, 2500);
  } catch (e) {
    alert("Failed to save appointment.");
  } finally {
    submitting.value = false;
  }
}

function goBack() {
  const source = route.query.source;
  const organValue = route.query.organ;

  if (source === "dashboard") {
    router.push("/dashboard");
    return;
  }

  if (organValue) {
    router.push(`/organs/${organValue}`);
    return;
  }

  router.push("/dashboard");
}

function formatDate(dateStr) {
  if (!dateStr) return "—"

  if (dateStr === "2000-01-01") {
    return "— —"
  }

  if (dateStr === "2000-12-31") {
    return "— —"
  }
  if (dateStr === "2000-06-29") {
    return "— —"
  }
  return dateStr
}

onMounted(() => {
  fetchAppointmentDetail();
});

watch(
    () => route.params.screeningTypeId,
    () => {
      fetchAppointmentDetail();
    }
);
</script>

<style scoped>
.appointment-page {
  min-height: 100vh;
  padding: 18px;
  background:
      radial-gradient(900px 500px at 20% 10%, rgba(20, 184, 166, 0.10), transparent),
      radial-gradient(900px 500px at 80% 20%, rgba(59, 130, 246, 0.10), transparent),
      #f8fafc;
}

.appointment-shell {
  max-width: 680px;
  margin: 0 auto;
}

.state-card,
.section-card,
.hero-banner {
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 24px;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.06);
}

.state-card,
.section-card {
  padding: 20px;
}

.error-card {
  color: #b91c1c;
}

.hero-panel {
  margin-bottom: 18px;
}

.hero-banner {
  padding: 20px 24px 22px;
}

.back-btn {
  border: none;
  background: #eef2ff;
  color: #334155;
  padding: 8px 14px;
  border-radius: 999px;
  font-size: 13px;
  cursor: pointer;
  margin-bottom: 14px;
}

.hero-tag-row {
  margin-bottom: 14px;
}

.hero-tag {
  display: inline-block;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: #0f766e;
  background: #ccfbf1;
  padding: 8px 14px;
  border-radius: 999px;
}

.success-banner {
  margin-bottom: 14px;
  padding: 12px 14px;
  border-radius: 14px;
  background: #ecfdf5;
  border: 1px solid #bbf7d0;
  color: #166534;
  font-size: 14px;
  font-weight: 600;
}

.hero-main {
  display: flex;
  align-items: center;
  gap: 16px;
}

.hero-icon {
  width: 74px;
  height: 74px;
  border-radius: 20px;
  background: linear-gradient(135deg, #ecfeff, #eef2ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 38px;
  flex-shrink: 0;
}

.hero-title {
  margin: 0 0 8px;
  font-size: 27px;
  font-weight: 800;
  color: #0f172a;
}

.hero-subtitle {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.section-title {
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 18px;
}

.section-subtitle {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 12px;
}

.info-list {
  display: grid;
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 12px 18px;
  border-radius: 18px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.info-label {
  font-size: 14px;
  color: #64748b;
}

.info-value {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
  text-align: right;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 34px;
  padding: 0 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.01em;
}

.status-scheduled {
  background: #dcfce7;
  color: #15803d;
}

.status-completed {
  background: #dbeafe;
  color: #1d4ed8;
}

.status-cancelled {
  background: #fee2e2;
  color: #dc2626;
}

.status-default {
  background: #e2e8f0;
  color: #475569;
}

.appointment-form {
  margin-top: 24px;
  padding-top: 18px;
  border-top: 1px solid #e2e8f0;
}

.date-label {
  display: block;
  margin-bottom: 8px;
  font-size: 13px;
  color: #475569;
  font-weight: 600;
}

.date-input {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid #cbd5e1;
  border-radius: 14px;
  padding: 12px 14px;
  font-size: 14px;
  background: #fff;
  color: #0f172a;
  margin-bottom: 10px;
}

.date-input:focus {
  outline: none;
  border-color: #14b8a6;
  box-shadow: 0 0 0 4px rgba(20, 184, 166, 0.12);
}

.inline-error {
  margin-bottom: 12px;
  font-size: 13px;
  color: #dc2626;
  font-weight: 600;
}

.primary-btn {
  width: 100%;
  border: none;
  border-radius: 16px;
  padding: 14px 16px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  background: linear-gradient(135deg, #14b8a6, #0ea5e9);
  color: white;
  box-shadow: 0 10px 24px rgba(20, 184, 166, 0.22);
}

.primary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}
</style>