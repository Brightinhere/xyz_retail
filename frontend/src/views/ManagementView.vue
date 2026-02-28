<script setup>
import { ref } from 'vue';
import {
  getLeastSellingProductsOfTheMonth,
  getSalesAmountByDateRange,
  getTopSellingProductsOfTheDay,
  searchOrderById
} from '../api/retailApi';

const orderId = ref('');
const orderLoading = ref(false);
const orderResult = ref(null);
const orderError = ref('');

const topSelling = ref([]);
const leastSelling = ref([]);
const reportError = ref('');
const reportLoading = ref({
  top: false,
  least: false,
  salesRange: false
});

const salesRange = ref([]);
const fromDate = ref('');
const toDate = ref('');
const reportDate = ref('');
const reportMonth = ref('');

function normalizeProducts(result) {
  return Array.isArray(result) ? result : result?.items || [];
}

async function runOrderSearch() {
  orderError.value = '';
  orderResult.value = null;

  if (!orderId.value.trim()) {
    orderError.value = 'Please provide an order ID.';
    return;
  }

  orderLoading.value = true;
  try {
    orderResult.value = await searchOrderById(orderId.value.trim());
  } catch (error) {
    orderError.value = error.message;
  } finally {
    orderLoading.value = false;
  }
}

async function loadTopSelling() {
  reportError.value = '';
  reportLoading.value.top = true;
  try {
    topSelling.value = normalizeProducts(await getTopSellingProductsOfTheDay(reportDate.value || undefined));
  } catch (error) {
    reportError.value = error.message;
  } finally {
    reportLoading.value.top = false;
  }
}

async function loadLeastSelling() {
  reportError.value = '';
  reportLoading.value.least = true;
  try {
    leastSelling.value = normalizeProducts(await getLeastSellingProductsOfTheMonth(reportMonth.value || undefined));
  } catch (error) {
    reportError.value = error.message;
  } finally {
    reportLoading.value.least = false;
  }
}

async function loadSalesByRange() {
  reportError.value = '';
  salesRange.value = [];

  if (!fromDate.value || !toDate.value) {
    reportError.value = 'From Date and To Date are required.';
    return;
  }

  reportLoading.value.salesRange = true;
  try {
    const result = await getSalesAmountByDateRange(fromDate.value, toDate.value);
    salesRange.value = Array.isArray(result) ? result : result?.items || [];
  } catch (error) {
    reportError.value = error.message;
  } finally {
    reportLoading.value.salesRange = false;
  }
}

function isLowInventory(product) {
  return Boolean(product.inventory?.lowStock);
}

function inventoryQuantity(product) {
  return product.inventory?.quantity ?? product.quantityAvailable ?? product.availableQuantity ?? '-';
}
</script>

<template>
  <section>
    <h2>Store Employee - Search Order</h2>
    <form @submit.prevent="runOrderSearch" class="stack">
      <label for="order-id">Order ID</label>
      <input id="order-id" v-model="orderId" placeholder="Enter order ID" />
      <button :aria-busy="orderLoading" :disabled="orderLoading" type="submit">Search Order</button>
    </form>

    <p v-if="orderError" role="alert">{{ orderError }}</p>

    <article v-if="orderResult" class="result-box">
      <h3>Order Found</h3>
      <p><strong>Order ID:</strong> {{ orderResult.orderId || orderResult.id }}</p>
      <p><strong>Status:</strong> {{ orderResult.status || '-' }}</p>
      <p><strong>Customer:</strong> {{ orderResult.customer?.name || orderResult.customerName || '-' }}</p>
      <p><strong>Mobile:</strong> {{ orderResult.customer?.mobileNumber || orderResult.mobileNumber || '-' }}</p>
      <p><strong>Total:</strong> {{ Number(orderResult.totalAmount || orderResult.amount || 0).toFixed(2) }}</p>
    </article>
  </section>

  <section>
    <h2>Management Reports</h2>
    <div class="grid-actions">
      <label>
        Date (optional)
        <input v-model="reportDate" type="date" />
      </label>
      <button @click="loadTopSelling" :aria-busy="reportLoading.top" :disabled="reportLoading.top">
        Top 5 Selling (Today)
      </button>
      <label>
        Month (optional)
        <input v-model="reportMonth" type="month" />
      </label>
      <button @click="loadLeastSelling" :aria-busy="reportLoading.least" :disabled="reportLoading.least">
        Least Selling (Month)
      </button>
    </div>

    <form @submit.prevent="loadSalesByRange" class="stack">
      <div class="grid-actions">
        <label>
          From Date
          <input v-model="fromDate" type="date" required />
        </label>
        <label>
          To Date
          <input v-model="toDate" type="date" required />
        </label>
      </div>
      <button
        type="submit"
        :aria-busy="reportLoading.salesRange"
        :disabled="reportLoading.salesRange"
      >
        Sales Amount per Day
      </button>
    </form>

    <p v-if="reportError" role="alert">{{ reportError }}</p>

    <article>
      <h3>Top 5 Selling Products of the Day</h3>
      <table v-if="topSelling.length">
        <thead>
          <tr>
            <th>Name</th>
            <th>Qty Available</th>
            <th>Price</th>
            <th>Low Inventory</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in topSelling" :key="product.id">
            <td>{{ product.name }}</td>
            <td>{{ inventoryQuantity(product) }}</td>
            <td>{{ Number(product.price || 0).toFixed(2) }}</td>
            <td>
              <span :class="['pill', { danger: isLowInventory(product) }]">
                {{ isLowInventory(product) ? 'LOW (<10)' : 'In Stock' }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="subtle">No top-selling data loaded yet.</p>
    </article>

    <article>
      <h3>Least Selling Products of the Month</h3>
      <table v-if="leastSelling.length">
        <thead>
          <tr>
            <th>Name</th>
            <th>Qty Available</th>
            <th>Price</th>
            <th>Low Inventory</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in leastSelling" :key="product.id">
            <td>{{ product.name }}</td>
            <td>{{ inventoryQuantity(product) }}</td>
            <td>{{ Number(product.price || 0).toFixed(2) }}</td>
            <td>
              <span :class="['pill', { danger: isLowInventory(product) }]">
                {{ isLowInventory(product) ? 'LOW (<10)' : 'In Stock' }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="subtle">No least-selling data loaded yet.</p>
    </article>

    <article>
      <h3>Sales Amount per Day (Custom Date Range)</h3>
      <table v-if="salesRange.length">
        <thead>
          <tr>
            <th>Date</th>
            <th>Sales Amount</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="entry in salesRange" :key="entry.date">
            <td>{{ entry.day }}</td>
            <td>{{ Number(entry.totalSales || 0).toFixed(2) }}</td>
          </tr>
        </tbody>
      </table>
      <p v-else class="subtle">No sales range data loaded yet.</p>
    </article>
  </section>
</template>