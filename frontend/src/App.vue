<script setup>
import { ref, onMounted } from 'vue'

const message = ref('Loading...')
const error = ref('')

onMounted(async () => {
  try {
    const response = await fetch('/api/hello')
    const data = await response.json()
    message.value = data.message
  } catch (e) {
    error.value = 'Failed to connect to backend'
  }
})
</script>

<template>
  <div style="padding: 40px; font-family: Arial;">
    <h1>Vue + Spring Boot Demo</h1>

    <p v-if="error" style="color: red;">{{ error }}</p>
    <p v-else>{{ message }}</p>
  </div>
</template>