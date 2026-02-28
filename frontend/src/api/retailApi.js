import * as mockApi from './mockRetailApi';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL?.trim() || 'http://localhost:8090';
const USE_MOCK_API = import.meta.env.VITE_USE_MOCK_API === 'true';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    ...options
  });

  const contentType = response.headers.get('content-type') || '';
  const payload = contentType.includes('application/json')
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message =
      typeof payload === 'string'
        ? payload
        : payload?.message || 'Request failed. Please check backend logs.';
    throw new Error(message);
  }

  return payload;
}

export function getApiBaseUrl() {
  return API_BASE_URL;
}

export function isMockApiMode() {
  return USE_MOCK_API;
}

export function searchProducts(term) {
  if (USE_MOCK_API) {
    return mockApi.searchProducts(term);
  }

  const query = new URLSearchParams({ q: term });
  return request(`/api/products/search?${query.toString()}`);
}

export function createCart() {
  if (USE_MOCK_API) {
    return mockApi.createCart();
  }

  return request('/api/orders', {
    method: 'POST'
  });
}

export function addItemToCart(orderId, productId, quantity) {
  if (USE_MOCK_API) {
    return mockApi.addItemToCart(orderId, productId, quantity);
  }

  return request(`/api/orders/${encodeURIComponent(orderId)}/items`, {
    method: 'POST',
    body: JSON.stringify({ productId, quantity })
  });
}

export function placeOrder(orderId, order) {
  if (USE_MOCK_API) {
    return mockApi.placeOrder(orderId, order);
  }

  return request(`/api/orders/${encodeURIComponent(orderId)}/place`, {
    method: 'POST',
    body: JSON.stringify(order)
  });
}

export function searchOrderById(orderId) {
  if (USE_MOCK_API) {
    return mockApi.searchOrderById(orderId);
  }

  return request(`/api/employee/orders/${encodeURIComponent(orderId)}`);
}

export function getTopSellingProductsOfTheDay(date) {
  if (USE_MOCK_API) {
    return mockApi.getTopSellingProductsOfTheDay(date);
  }

  const query = new URLSearchParams();
  if (date) {
    query.set('date', date);
  }
  const suffix = query.toString() ? `?${query.toString()}` : '';

  return request(`/api/management/reports/top-selling${suffix}`);
}

export function getLeastSellingProductsOfTheMonth(month) {
  if (USE_MOCK_API) {
    return mockApi.getLeastSellingProductsOfTheMonth(month);
  }

  const query = new URLSearchParams();
  if (month) {
    query.set('month', month);
  }
  const suffix = query.toString() ? `?${query.toString()}` : '';

  return request(`/api/management/reports/least-selling${suffix}`);
}

export function getSalesAmountByDateRange(fromDate, toDate) {
  if (USE_MOCK_API) {
    return mockApi.getSalesAmountByDateRange(fromDate, toDate);
  }

  const query = new URLSearchParams({ start: fromDate, end: toDate });
  return request(`/api/management/reports/sales?${query.toString()}`);
}