import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import '@picocss/pico/css/pico.min.css';
import './styles.css';
import App from './App.vue';
import CustomerOrderView from './views/CustomerOrderView.vue';
import ManagementView from './views/ManagementView.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/customer'
    },
    {
      path: '/customer',
      name: 'customer',
      component: CustomerOrderView
    },
    {
      path: '/management',
      name: 'management',
      component: ManagementView
    }
  ]
});

createApp(App).use(router).mount('#app');