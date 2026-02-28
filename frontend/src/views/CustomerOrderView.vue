<script setup>
import { computed, ref } from 'vue';
import { addItemToCart, createCart, placeOrder, searchProducts } from '../api/retailApi';

const searchTerm = ref('');
const isSearching = ref(false);
const searchError = ref('');
const products = ref([]);

const activeOrder = ref(null);
const checkout = ref({
  customerName: '',
  mobileNumber: '',
  email: ''
});

const placingOrder = ref(false);
const orderError = ref('');
const lastOrder = ref(null);

const cartItems = computed(() => {
  return activeOrder.value?.items || [];
});

const cartTotal = computed(() => {
  const backendTotal = Number(activeOrder.value?.totalAmount);
  if (Number.isFinite(backendTotal) && backendTotal > 0) {
    return backendTotal;
  }

  return cartItems.value.reduce((sum, item) => {
    const price = Number(item.salePrice || item.price || 0);
    return sum + price * Number(item.quantity || 0);
  }, 0);
});

async function runProductSearch() {
  searchError.value = '';
  products.value = [];

  if (!searchTerm.value.trim()) {
    searchError.value = 'Please enter a product name or keyword to search.';
    return;
  }

  isSearching.value = true;
  try {
    const result = await searchProducts(searchTerm.value.trim());
    products.value = Array.isArray(result) ? result : result?.items || [];
  } catch (error) {
    searchError.value = error.message;
  } finally {
    isSearching.value = false;
  }
}

async function addToCart(product) {
  orderError.value = '';

  try {
    if (!activeOrder.value?.id) {
      activeOrder.value = await createCart();
    }

    activeOrder.value = await addItemToCart(activeOrder.value.id, product.id, 1);
  } catch (error) {
    orderError.value = error.message;
  }
}

async function submitOrder() {
  orderError.value = '';
  lastOrder.value = null;

  if (!checkout.value.customerName.trim() || !checkout.value.mobileNumber.trim()) {
    orderError.value = 'Customer name and mobile number are mandatory.';
    return;
  }

  if (!activeOrder.value?.id || !cartItems.value.length) {
    orderError.value = 'Please add at least one product before placing an order.';
    return;
  }

  placingOrder.value = true;
  try {
    const payload = {
      customerName: checkout.value.customerName.trim(),
      mobileNumber: checkout.value.mobileNumber.trim(),
      email: checkout.value.email.trim() || null
    };

    const response = await placeOrder(activeOrder.value.id, payload);
    lastOrder.value = {
      orderId: response?.id,
      amount: response?.totalAmount || cartTotal.value,
      status: response?.status
    };

    activeOrder.value = null;
    checkout.value.customerName = '';
    checkout.value.mobileNumber = '';
    checkout.value.email = '';
  } catch (error) {
    orderError.value = error.message;
  } finally {
    placingOrder.value = false;
  }
}
</script>

<template>
  <section>
    <h2>Search Products</h2>
    <form @submit.prevent="runProductSearch" class="stack">
      <label for="customer-product-search">Product name / keyword</label>
      <input
        id="customer-product-search"
        v-model="searchTerm"
        type="search"
        placeholder="e.g. Lemon, Apple, Banana"
      />
      <button :aria-busy="isSearching" :disabled="isSearching" type="submit">Search</button>
    </form>

    <p v-if="searchError" role="alert">{{ searchError }}</p>

    <table v-if="products.length">
      <thead>
        <tr>
          <th>Product</th>
          <th>Price</th>
          <th>Available Qty</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="product in products" :key="product.id">
          <td>{{ product.name }}</td>
          <td>{{ Number(product.price || 0).toFixed(2) }}</td>
          <td>{{ product.inventory?.quantity ?? product.quantityAvailable ?? product.availableQuantity ?? '-' }}</td>
          <td>
            <button @click="addToCart(product)">Add to cart</button>
          </td>
        </tr>
      </tbody>
    </table>
  </section>

  <section>
    <h2>Cart</h2>
    <p v-if="activeOrder?.id" class="subtle">Draft Order ID: {{ activeOrder.id }}</p>
    <p v-if="!cartItems.length" class="subtle">Cart is empty.</p>

    <table v-if="cartItems.length">
      <thead>
        <tr>
          <th>Product</th>
          <th>Price</th>
          <th>Qty</th>
          <th>Subtotal</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in cartItems" :key="item.productId || item.id">
          <td>{{ item.productName || item.name }}</td>
          <td>{{ Number(item.salePrice || item.price || 0).toFixed(2) }}</td>
          <td>{{ item.quantity }}</td>
          <td>{{ (Number(item.salePrice || item.price || 0) * Number(item.quantity || 0)).toFixed(2) }}</td>
        </tr>
      </tbody>
      <tfoot>
        <tr>
          <th colspan="3" scope="row">Total</th>
          <th>{{ cartTotal.toFixed(2) }}</th>
        </tr>
      </tfoot>
    </table>

    <p v-if="cartItems.length" class="subtle">
      Quantity reduction is not shown because current API supports add-only quantities.
    </p>
  </section>

  <section>
    <h2>Place Order</h2>
    <form @submit.prevent="submitOrder" class="stack">
      <div class="grid-actions">
        <label>
          Customer Name
          <input v-model="checkout.customerName" type="text" autocomplete="name" required />
        </label>
        <label>
          Mobile Number
          <input v-model="checkout.mobileNumber" type="tel" autocomplete="tel" required />
        </label>
        <label>
          Email (Optional)
          <input v-model="checkout.email" type="email" autocomplete="email" />
        </label>
      </div>
      <button :aria-busy="placingOrder" :disabled="placingOrder" type="submit">Place Order</button>
    </form>

    <p v-if="orderError" role="alert">{{ orderError }}</p>

    <article v-if="lastOrder" class="result-box">
      <h3>Order placed successfully</h3>
      <p><strong>Order ID:</strong> {{ lastOrder.orderId }}</p>
      <p><strong>Status:</strong> {{ lastOrder.status || 'PLACED' }}</p>
      <p><strong>Amount to Pay:</strong> {{ Number(lastOrder.amount || 0).toFixed(2) }}</p>
      <p class="subtle">Optional SMS/Email notifications are expected to be handled by backend integration.</p>
    </article>
  </section>
</template>