<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { isMockApiMode } from './api/retailApi';

const route = useRoute();
const usingMockApi = isMockApiMode();

const pageTitle = computed(() => {
  if (route.name === 'management') {
    return 'Management & Store Reporting';
  }

  return 'Customer Online Ordering';
});
</script>

<template>
  <main class="container">
    <header>
      <h1>XYZ Retail Portal</h1>
      <p>{{ pageTitle }}</p>
      <p v-if="usingMockApi" class="pill">Mock API Mode Enabled</p>
      <nav>
        <ul>
          <li><RouterLink to="/customer">Customer</RouterLink></li>
          <li><RouterLink to="/management">Management</RouterLink></li>
        </ul>
      </nav>
    </header>

    <RouterView />
  </main>
</template>