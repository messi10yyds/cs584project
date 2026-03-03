import { createRouter, createWebHistory } from "vue-router";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue";
import DashboardView from "../views/DashboardView.vue";
import http from "../api/http";
import { loadInitDraft } from "../utils/initDraft";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: "/", redirect: "/login" },
        { path: "/login", component: LoginView },
        { path: "/register", component: RegisterView },
        { path: "/dashboard", component: DashboardView },

        { path: "/init", redirect: "/init/profile" },
        { path: "/init/profile", component: () => import("../views/init/InitProfileView.vue") },
        { path: "/init/screenings", component: () => import("../views/init/InitScreeningsView.vue") },
        { path: "/init/medications", component: () => import("../views/init/InitMedicationsView.vue") },
    ],
});

const publicPaths = ["/login", "/register"];

router.beforeEach(async (to) => {
    const token = localStorage.getItem("token");

    // ===============================
    // 1) 未登录只能访问 login/register
    // ===============================
    if (!token && !publicPaths.includes(to.path)) {
        return "/login";
    }

    // ===============================
    // 2) 已登录访问 login/register
    //    → 根据 init 状态决定去哪里
    // ===============================
    if (token && publicPaths.includes(to.path)) {
        try {
            const res = await http.get("/api/init/status");
            if (res.code === 0 && res.data?.initialized === false) {
                return "/init";
            }
            return "/dashboard";
        } catch (e) {
            return "/login";
        }
    }

    // ===============================
    // 3) 已登录：访问非 /init 页面
    //    → 必须已初始化
    // ===============================
    if (token && !to.path.startsWith("/init")) {
        try {
            const res = await http.get("/api/init/status");
            if (res.code === 0 && res.data?.initialized === false) {
                return "/init";
            }
        } catch (e) {
            // 401 会被 http.js 自动跳 login
        }
    }

    // ===============================
    // 4) 已登录访问 /init/* 页面
    //    → 若已初始化，不允许再进入 init
    // ===============================
    if (token && to.path.startsWith("/init")) {
        try {
            const res = await http.get("/api/init/status");
            if (res.code === 0 && res.data?.initialized === true) {
                return "/dashboard";
            }
        } catch (e) {}

        // ===============================
        // 5) 阻止跳过 init 步骤
        // ===============================
        const draft = loadInitDraft();

        // profile 永远允许
        if (to.path === "/init/profile") {
            return;
        }

        // screenings 需要 profile
        if (to.path === "/init/screenings") {
            if (!draft.profile) {
                return "/init/profile";
            }
            return;
        }

        // medications 需要 profile + screenings(3条)
        if (to.path === "/init/medications") {
            const hasProfile = !!draft.profile;
            const hasScreenings =
                Array.isArray(draft.screenings) && draft.screenings.length === 3;

            if (!hasProfile) return "/init/profile";
            if (!hasScreenings) return "/init/screenings";
            return;
        }
    }
});

export default router;