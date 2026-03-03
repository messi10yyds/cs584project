import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      // 前端请求 /api/... 会被转发到后端 8080
      '/api': 'http://localhost:8080'
    }
  }
})
