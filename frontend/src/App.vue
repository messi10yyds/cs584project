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
  <router-view />
</template>