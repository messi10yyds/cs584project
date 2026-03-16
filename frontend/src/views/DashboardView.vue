<template>
  <div class="dashboard-page">
    <div class="dashboard-shell">
      <!-- Top bar -->
      <div class="topbar">
        <div>
          <div class="brand-row">
            <div class="brand-dot"></div>
            <div class="brand-text">Diacare Egypt</div>
          </div>
          <h1 class="page-title">Dashboard</h1>
        </div>

        <button class="logout-btn" @click="logout">Logout</button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="state-card">
        Loading dashboard...
      </div>

      <!-- Error -->
      <div v-else-if="error" class="state-card error-card">
        {{ error }}
      </div>

      <!-- Content -->
      <template v-else>
        <!-- Greeting -->
        <!-- Greeting + organ tabs -->
        <section class="hero-panel">
          <div class="hero-banner">
            <p class="hero-greeting">{{ overview.greeting?.greetingText }}</p>
            <h2 class="hero-name">{{ overview.greeting?.userName }}</h2>
            <p class="hero-date">{{ overview.greeting?.dateText }}</p>
          </div>

          <div class="organ-tabs">
            <div
                v-for="organ in overview.organs || []"
                :key="organ.key"
                class="organ-tab"
                @click="goToOrganDetail(organ.key)"
            >
              <div class="organ-tab-icon">

                <!-- 如果是 emoji -->
                <span v-if="typeof organIconMap[organ.key] === 'string'">
        {{ organIconMap[organ.key] || "🩺" }}
      </span>

                <!-- 如果是 Vue 组件 -->
                <component
                    v-else
                    :is="organIconMap[organ.key]"
                    :size="34"
                    style="display: block; transform: translateY(10px);"
                />

              </div>

              <div class="organ-tab-label">{{ organ.label }}</div>
            </div>
          </div>
        </section>

        <!-- Risk alerts -->
        <!-- Risk alerts -->
        <section class="section-card">
          <div class="dashboard-section-title">⚠️ Risk Alerts</div>

          <div
              v-if="!overview.riskAlerts || overview.riskAlerts.length === 0"
              class="empty-box"
          >
            No alerts for now.
          </div>

          <div v-else class="alert-list">
            <div
                v-for="(alert, index) in overview.riskAlerts"
                :key="index"
                class="risk-card"
                :class="alertClass(alert.level)"
            >
              <div class="risk-card-inner">
                <div class="risk-icon-wrap">
                  <div class="risk-dot"></div>
                </div>

                <div class="risk-content">
                  <div class="risk-title">{{ alert.title }}</div>
                  <div class="risk-message">{{ alert.message }}</div>

                  <button
                      class="risk-link-btn"
                      type="button"
                      @click="handleAlertAction(alert)"
                  >
                    → {{ alertActionText(alert) }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Medication -->
        <!-- Medication -->
        <section class="section-card">
          <div class="section-head">
            <h3>💊 Today's Medications</h3>
            <span class="section-note">
      {{ (overview.todayMedications || []).length }} medication(s)
    </span>
          </div>

          <div
              v-if="!overview.todayMedications || overview.todayMedications.length === 0"
              class="empty-box"
          >
            No medication information available.
          </div>

          <div v-else class="med-log-card">
            <div class="med-log-title">Daily Dose Log</div>

            <div
                v-for="med in overview.todayMedications"
                :key="med.medicationId"
                class="med-log-row"
            >
              <div class="med-info">
                <div class="med-title-line">
                  <div class="med-name">{{ med.name }}</div>
                </div>

                <div class="med-sub">
                  {{ med.dosageText || "Dosage not specified" }} ·
                  {{ med.expectedDosesPerDay }}× daily
                </div>

                <div class="med-progress-text">
                  {{ med.takenToday }} / {{ med.expectedDosesPerDay }} dose(s) logged today
                </div>
              </div>

              <div class="med-actions">
                <div
                    class="med-progress-pill"
                    :class="medicationProgressClass(med)"
                >
                  {{ medicationProgressText(med) }}
                </div>

                <button
                    class="med-action-btn"
                    :class="{
          completed: med.status === 'COMPLETED',
          disabled: submittingMedId === med.medicationId
        }"
                    :disabled="med.status === 'COMPLETED' || submittingMedId === med.medicationId"
                    @click="logMedicationDose(med.medicationId)"
                >
                  {{
                    submittingMedId === med.medicationId
                        ? "Saving..."
                        : medicationActionText(med)
                  }}
                </button>
              </div>
            </div>
          </div>
        </section>

        <!-- Screenings -->
        <!-- Screenings -->
        <section class="section-card">
          <div class="dashboard-section-title">📋 Screening Status</div>

          <div
              v-if="!overview.screenings || overview.screenings.length === 0"
              class="empty-box"
          >
            No screening information available.
          </div>

          <div v-else class="screening-table-card">
            <div class="screening-table-head">
              <div>Test</div>
              <div>Status</div>
              <div>Next Due</div>
              <div>Appointment</div>
              <div>Action</div>
            </div>

            <div
                v-for="screening in overview.screenings"
                :key="screening.userScreeningId"
                class="screening-table-row"
            >
              <div class="screening-cell screening-test-cell">
                <span class="screening-test-icon">
                  <!-- 字符串图标 -->
                  <span v-if="typeof screeningIcon(screening.screeningType) === 'string'">
                    {{ screeningIcon(screening.screeningType) }}
                  </span>
                  <!-- Vue 组件图标 -->
                  <component
                      v-else
                      :is="screeningIcon(screening.screeningType)"
                      :size="22"
                      class="screening-svg-icon"
                      style="display: block; transform: translateY(3px);"
                  />
                </span>

                <span class="screening-test-name">{{ screening.displayName }}</span>
              </div>

              <div class="screening-cell">
    <span
        class="screening-status-pill"
        :class="screeningTableStatusClass(screening.status)"
    >
      {{ screeningTableStatusText(screening.status) }}
    </span>
              </div>

              <div class="screening-cell screening-date-cell">
                {{ screening.dueDate || "—" }}
              </div>

              <div class="screening-cell screening-date-cell">
                {{ screening.appointmentDate ? `Booked · ${screening.appointmentDate}` : "Not booked" }}
              </div>

              <div class="screening-cell">
                <button
                    class="screening-action-btn"
                    type="button"
                    @click="goToAppointment(screening, 'dashboard')"
                >
                  {{ screening.appointmentDate ? "Reschedule" : "Book" }}
                </button>
              </div>
            </div>
          </div>
        </section>
      </template>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import KidneyIcon from "@/components/icons/KidneyIcon.vue";
import http from "../api/http";

const router = useRouter();

const loading = ref(true);
const error = ref("");
const submittingMedId = ref(null);

const overview = reactive({
  greeting: null,
  organs: [],
  riskAlerts: [],
  todayMedications: [],
  screenings: [],
});

const organIconMap = {
  eye: "👁️",
  foot: "🦶",
  kidney: KidneyIcon,
  heart: "❤️",
};

function alertClass(level) {
  if (level === "HIGH") return "alert-high";
  if (level === "MEDIUM") return "alert-medium";
  return "alert-low";
}

function alertActionText(alert) {
  if (alert.actionType === "VIEW") {
    if (alert.relatedKey === "foot") return "View foot check results";
    if (alert.relatedKey === "eye") return "View screening status";
    if (alert.relatedKey === "kidney") return "View screening status";
    return "View details";
  }

  return "View details";
}

function medStatusClass(status) {
  if (status === "COMPLETED") return "pill-green";
  if (status === "PARTIAL") return "pill-yellow";
  return "pill-gray";
}

function medicationActionText(med) {
  if (med.status === "COMPLETED") return "Taken";
  return "Log dose";
}

function medicationProgressText(med) {
  if (med.status === "COMPLETED") {
    return "Completed";
  }

  if ((med.takenToday || 0) > 0) {
    return "Partial";
  }

  return "Not started";
}

function medicationProgressClass(med) {
  if (med.status === "COMPLETED") {
    return "progress-completed";
  }

  if ((med.takenToday || 0) > 0) {
    return "progress-partial";
  }

  return "progress-not-started";
}

function screeningStatusClass(status) {
  if (status === "OVERDUE") return "pill-red";
  if (status === "ON_TRACK") return "pill-blue";
  if (status === "COMPLETED") return "pill-green";
  return "pill-gray";
}

function screeningIcon(screeningType) {
  if (screeningType?.startsWith("EYE")) return "👁️";
  if (screeningType?.startsWith("KIDNEY")) return KidneyIcon;
  if (screeningType?.startsWith("A1C")) return "🩸";
  return "📄";
}

function screeningTableStatusText(status) {
  if (status === "OVERDUE") return "Overdue";
  if (status === "DUE_SOON") return "Due soon";
  if (status === "ON_TRACK") return "On track";
  if (status === "COMPLETED") return "Completed";
  return "Unknown";
}

function screeningTableStatusClass(status) {
  if (status === "OVERDUE") return "screening-pill-overdue";
  if (status === "DUE_SOON") return "screening-pill-due-soon";
  if (status === "ON_TRACK") return "screening-pill-on-track";
  if (status === "COMPLETED") return "screening-pill-completed";
  return "screening-pill-default";
}

function screeningActionText(screening) {
  if (screening.status === "OVERDUE" || screening.status === "ON_TRACK") {
    return "Book";
  }
  return "View";
}

async function fetchOverview() {
  loading.value = true;
  error.value = "";

  try {
    const res = await http.get("/dashboard/overview");

    if (res.code !== 0) {
      error.value = res.message || "Failed to load dashboard";
      return;
    }

    const data = res.data || {};

    overview.greeting = data.greeting || null;
    overview.organs = data.organs || [];
    overview.riskAlerts = [...(data.riskAlerts || [])].sort((a, b) => {
      const order = { foot: 0, eye: 1, kidney: 2, heart: 3 };
      return (order[a.relatedKey] ?? 99) - (order[b.relatedKey] ?? 99);
    });
    overview.todayMedications = data.todayMedications || [];
    overview.screenings = data.screenings || [];
  } catch (e) {
    error.value = "Failed to load dashboard overview.";
  } finally {
    loading.value = false;
  }
}

async function logMedicationDose(medicationId) {
  if (submittingMedId.value) return;

  submittingMedId.value = medicationId;

  try {
    const res = await http.post("/dashboard/medication/log", {
      medicationId,
    });

    if (res.code !== 0) {
      alert(res.message || "Failed to log medication dose");
      return;
    }

    const updatedMed = res.data;
    const index = overview.todayMedications.findIndex(
        (item) => item.medicationId === medicationId
    );

    if (index !== -1) {
      overview.todayMedications[index] = {
        ...overview.todayMedications[index],
        ...updatedMed,
      };
    }
  } catch (e) {
    alert("Failed to log medication dose.");
  } finally {
    submittingMedId.value = null;
  }
}

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userId");
  localStorage.removeItem("tokenType");
  router.push("/login");
}

function handleAlertAction(alert) {
  if (alert.relatedKey === "foot") {
    router.push("/foot");
    return;
  }
}

onMounted(() => {
  fetchOverview();
});

function goToOrganDetail(organKey) {
  if (organKey === "eye" || organKey === "kidney" || organKey === "heart") {
    router.push(`/organs/${organKey}`);
  }
  if (organKey === "foot") {
    router.push("/foot");
    return;
  }
}

function screeningTypeToOrgan(screeningType) {
  if (screeningType?.startsWith("EYE")) return "eye";
  if (screeningType?.startsWith("KIDNEY")) return "kidney";
  if (screeningType?.startsWith("A1C")) return "heart";
  return "";
}

function goToAppointment(screening, source = "dashboard") {
  if (!screening?.screeningTypeId) {
    alert("Missing screeningTypeId");
    return;
  }

  const organ = screeningTypeToOrgan(screening.screeningType);

  router.push({
    path: `/screenings/${screening.screeningTypeId}/appointment`,
    query: {
      organ,
      source,
    },
  });
}
</script>

<style scoped>
.dashboard-page {
  min-height: 100vh;
  padding: 18px;
  background:
      radial-gradient(900px 500px at 20% 10%, rgba(20, 184, 166, 0.1), transparent),
      radial-gradient(900px 500px at 80% 20%, rgba(59, 130, 246, 0.1), transparent),
      #f8fafc;
}

.dashboard-shell {
  max-width: 680px;
  margin: 0 auto;
}

.topbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 20px;
}

.brand-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.brand-dot {
  width: 12px;
  height: 12px;
  border-radius: 999px;
  background: #14b8a6;
  box-shadow: 0 0 0 4px rgba(20, 184, 166, 0.15);
}

.brand-text {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
}

.page-title {
  margin: 0;
  font-size: 30px;
  color: #0f172a;
}

.logout-btn {
  border: 1px solid #dbe3ef;
  background: #ffffff;
  color: #334155;
  border-radius: 12px;
  padding: 10px 14px;
  cursor: pointer;
  font-weight: 600;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.05);
}

.section-card,
.state-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 18px;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.08);
}

.section-card {
  padding: 18px;
  margin-bottom: 18px;
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

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}

.section-head h3 {
  margin: 0;
  font-size: 18px;
  color: #0f172a;
}

.section-note {
  font-size: 13px;
  color: #64748b;
}

.empty-box {
  padding: 14px;
  border: 1px dashed #cbd5e1;
  border-radius: 14px;
  color: #64748b;
  background: #f8fafc;
  font-size: 14px;
}

.hero-panel {
  margin-bottom: 18px;
}

.hero-banner {
  background: linear-gradient(135deg, #0f8f86, #127c86);
  color: #ffffff;
  border-radius: 20px 20px 0 0;
  padding: 18px 18px 60px;
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.1);
}

.hero-greeting {
  margin: 0 0 6px;
  font-size: 15px;
  font-weight: 600;
  opacity: 0.95;
}

.hero-name {
  margin: 0;
  font-size: 30px;
  line-height: 1.15;
  font-weight: 800;
}

.hero-date {
  margin: 8px 0 0;
  font-size: 14px;
  opacity: 0.92;
}

.organ-tabs {
  margin: -38px auto 0;
  width: calc(100% - 28px);
  background: #ffffff;
  border-radius: 0 0 22px 22px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  overflow: hidden;
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.1);
  border: 1px solid #e5e7eb;
  border-top: none;
}

.organ-tab {
  min-height: 84px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #f8fafc;
  border-right: 1px solid #e5e7eb;
  cursor: pointer;
  transition: background 0.2s ease, transform 0.2s ease;
}

.organ-tab:hover {
  background: #eef6f7;
  transform: translateY(-1px);
}

.organ-tab:last-child {
  border-right: none;
}

.organ-tab-icon {
  font-size: 24px;
}

.organ-tab-label {
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #64748b;
}

.med-name {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
}

.med-sub {
  font-size: 14px;
  color: #475569;
}

.progress-not-started {
  background: #e5e7eb;
  color: #475569;
}

.progress-partial {
  background: #fef3c7;
  color: #b45309;
}

.progress-completed {
  background: #dcfce7;
  color: #15803d;
}

.dashboard-section-title {
  margin-bottom: 14px;
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: #7c8aa5;
}

.med-log-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 14px 16px;
}

.med-log-title {
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 8px;
}

.med-log-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-top: 1px solid #e5e7eb;
}

.med-log-row:first-of-type {
  border-top: none;
}

.med-info {
  flex: 1;
  min-width: 0;
}

.med-title-line {
  margin-bottom: 6px;
}

.med-progress-text {
  margin-top: 6px;
  font-size: 13px;
  color: #64748b;
}

.med-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.med-progress-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 108px;
  border-radius: 999px;
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 700;
  white-space: nowrap;
}

.med-action-btn {
  border: none;
  border-radius: 999px;
  padding: 10px 16px;
  min-width: 108px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  background: #dff4ef;
  color: #0f8f86;
  transition: transform 0.15s ease, opacity 0.15s ease;
}

.med-action-btn:hover:not(:disabled) {
  transform: translateY(-1px);
}

.med-action-btn.completed {
  background: #dff4e7;
  color: #15803d;
  cursor: not-allowed;
}

.med-action-btn.disabled {
  opacity: 0.7;
  cursor: wait;
}

.alert-list {
  display: grid;
  gap: 14px;
}

.risk-card {
  border-radius: 16px;
  padding: 0;
  overflow: hidden;
}

.risk-card-inner {
  display: flex;
  gap: 14px;
  padding: 16px 16px 16px 0;
}

.risk-icon-wrap {
  width: 24px;
  display: flex;
  justify-content: center;
  padding-top: 6px;
}

.risk-dot {
  width: 16px;
  height: 16px;
  border-radius: 999px;
  box-shadow: inset 0 2px 4px rgba(255, 255, 255, 0.35);
}

.risk-content {
  flex: 1;
}

.risk-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 6px;
}

.risk-message {
  font-size: 15px;
  line-height: 1.5;
}

.risk-link-btn {
  margin-top: 10px;
  padding: 0;
  border: none;
  background: transparent;
  font-size: 14px;
  font-weight: 700;
  text-decoration: underline;
  cursor: pointer;
}

.risk-card.alert-high {
  background: #fff1f2;
  border: 1px solid #f8b4b4;
  box-shadow: inset 4px 0 0 #ef4444;
}

.risk-card.alert-high .risk-dot {
  background: linear-gradient(135deg, #fb7185, #dc2626);
}

.risk-card.alert-high .risk-title {
  color: #991b1b;
}

.risk-card.alert-high .risk-message {
  color: #9f1239;
}

.risk-card.alert-high .risk-link-btn {
  color: #b91c1c;
}

.risk-card.alert-medium {
  background: #fffbeb;
  border: 1px solid #f6d37c;
  box-shadow: inset 4px 0 0 #f59e0b;
}

.risk-card.alert-medium .risk-dot {
  background: linear-gradient(135deg, #fcd34d, #d97706);
}

.risk-card.alert-medium .risk-title {
  color: #92400e;
}

.risk-card.alert-medium .risk-message {
  color: #a16207;
}

.risk-card.alert-medium .risk-link-btn {
  color: #b45309;
}

.risk-card.alert-low {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  box-shadow: inset 4px 0 0 #22c55e;
}

.risk-card.alert-low .risk-dot {
  background: linear-gradient(135deg, #4ade80, #16a34a);
}

.risk-card.alert-low .risk-title {
  color: #166534;
}

.risk-card.alert-low .risk-message {
  color: #15803d;
}

.risk-card.alert-low .risk-link-btn {
  color: #15803d;
}

.screening-table-card {
  border: 1px solid #d8dee8;
  border-radius: 16px;
  overflow: hidden;
  background: #ffffff;
}

/* 这里是重新调过的关键比例 */
.screening-table-head,
.screening-table-row {
  display: grid;
  grid-template-columns: 1.35fr 0.9fr 0.95fr 1.05fr 0.95fr;
  column-gap: 10px;
  align-items: center;
}

.screening-table-head {
  padding: 14px 16px;
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #7c8aa5;
}

.screening-table-row {
  padding: 16px;
  border-bottom: 1px solid #edf2f7;
}

.screening-table-row:last-child {
  border-bottom: none;
}

.screening-cell {
  min-width: 0;
  text-align: left;
}

.screening-test-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.screening-test-icon {
  font-size: 16px;
  line-height: 1;
  flex-shrink: 0;
}

.screening-test-name {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
}

.screening-date-cell {
  font-size: 14px;
  color: #64748b;
}

/* 状态列明确左贴齐 */
.screening-table-row .screening-cell:nth-child(2) {
  display: flex;
  justify-content: flex-start;
}

/* 预约列居中 */
.screening-table-row .screening-cell:nth-child(4) {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}

/* 操作列居中 */
.screening-table-row .screening-cell:nth-child(5) {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.screening-status-pill {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 5px 10px;
  font-size: 12px;
  font-weight: 700;
  white-space: nowrap;
}

.screening-pill-overdue {
  background: #fee2e2;
  color: #dc2626;
}

.screening-pill-due-soon {
  background: #fef3c7;
  color: #d97706;
}

.screening-pill-on-track {
  background: #dcfce7;
  color: #15803d;
}

.screening-pill-completed {
  background: #dcfce7;
  color: #15803d;
}

.screening-pill-default {
  background: #e5e7eb;
  color: #475569;
}

.screening-action-btn {
  border: none;
  border-radius: 999px;
  padding: 7px 16px;
  min-width: 0;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  background: #dff4e7;
  color: #15803d;
  transition: transform 0.15s ease, opacity 0.15s ease;
  white-space: nowrap;
}

.screening-action-btn:hover {
  transform: translateY(-1px);
}

@media (max-width: 640px) {
  .dashboard-page {
    padding: 16px;
  }

  .topbar {
    flex-direction: column;
    align-items: stretch;
  }

  .section-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-name {
    font-size: 28px;
  }

  .organ-tabs {
    grid-template-columns: repeat(2, 1fr);
  }

  .med-log-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .med-actions {
    width: 100%;
    justify-content: space-between;
  }

  .med-action-btn,
  .med-progress-pill {
    min-width: 96px;
  }

  .risk-card-inner {
    padding: 14px 14px 14px 0;
  }

  .risk-title {
    font-size: 17px;
  }

  .screening-table-head {
    display: none;
  }

  .screening-table-row {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .screening-cell {
    display: flex;
    justify-content: space-between;
    gap: 10px;
    text-align: left;
  }

  .screening-test-cell {
    justify-content: flex-start;
  }

  .screening-table-row .screening-cell:nth-child(2),
  .screening-table-row .screening-cell:nth-child(4),
  .screening-table-row .screening-cell:nth-child(5) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    text-align: left;
  }
}
</style>