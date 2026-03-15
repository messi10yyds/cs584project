<template>
  <svg
      :width="size"
      :height="size"
      viewBox="0 0 64 64"
      xmlns="http://www.w3.org/2000/svg"
  >
    <!-- 左肾 -->
    <path
        d="M22 10C16 10 12 16 12 24C12 34 18 40 24 40C28 40 32 36 32 30V18C32 13 28 10 22 10Z"
        :fill="color"
    />

    <!-- 右肾 -->
    <path
        d="M42 10C36 10 32 13 32 18V30C32 36 36 40 40 40C46 40 52 34 52 24C52 16 48 10 42 10Z"
        :fill="color"
    />

    <!-- 上面的小圆 -->
    <circle cx="26" cy="8" r="5" :fill="color" />
    <circle cx="38" cy="8" r="5" :fill="color" />
  </svg>
</template>

<script>
export default {
  name: "KidneyIcon",
  props: {
    size: {
      type: Number,
      default: 64
    },
    color: {
      type: String,
      default: "#D85C73"
    }
  }
};
</script>