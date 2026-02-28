const products = [
  {
    id: '6b6fe8f1-1f2f-4c1e-9b9e-4a7f4f8a2001',
    name: 'Basmati Rice 5kg',
    description: 'Premium long grain rice',
    price: 13.5,
    inventory: { quantity: 24, lowStock: false },
    soldToday: 18,
    soldMonth: 120
  },
  {
    id: '6b6fe8f1-1f2f-4c1e-9b9e-4a7f4f8a2002',
    name: 'Whole Milk 1L',
    description: 'Fresh whole milk',
    price: 1.95,
    inventory: { quantity: 8, lowStock: true },
    soldToday: 34,
    soldMonth: 220
  },
  {
    id: '6b6fe8f1-1f2f-4c1e-9b9e-4a7f4f8a2003',
    name: 'Brown Bread',
    description: 'Soft sandwich bread',
    price: 2.1,
    inventory: { quantity: 12, lowStock: false },
    soldToday: 21,
    soldMonth: 176
  },
  {
    id: '6b6fe8f1-1f2f-4c1e-9b9e-4a7f4f8a2004',
    name: 'Eggs 12 Pack',
    description: 'Farm eggs',
    price: 3.75,
    inventory: { quantity: 9, lowStock: true },
    soldToday: 27,
    soldMonth: 198
  },
  {
    id: '6b6fe8f1-1f2f-4c1e-9b9e-4a7f4f8a2005',
    name: 'Olive Oil 1L',
    description: 'Cold-pressed olive oil',
    price: 9.4,
    inventory: { quantity: 14, lowStock: false },
    soldToday: 11,
    soldMonth: 67
  },
  {
    id: '6b6fe8f1-1f2f-4c1e-9b9e-4a7f4f8a2006',
    name: 'Chocolate Cookies',
    description: 'Crunchy cookies',
    price: 2.65,
    inventory: { quantity: 30, lowStock: false },
    soldToday: 5,
    soldMonth: 32
  },
  {
    id: '6b6fe8f1-1f2f-4c1e-9b9e-4a7f4f8a2007',
    name: 'Green Tea 50 Bags',
    description: 'Refreshing green tea',
    price: 4.2,
    inventory: { quantity: 6, lowStock: true },
    soldToday: 3,
    soldMonth: 26
  },
  {
    id: '6b6fe8f1-1f2f-4c1e-9b9e-4a7f4f8a2008',
    name: 'Pasta Penne 500g',
    description: 'Durum wheat pasta',
    price: 1.8,
    inventory: { quantity: 16, lowStock: false },
    soldToday: 7,
    soldMonth: 55
  }
];

const salesByDate = [
  { date: '2026-02-20', salesAmount: 421.55 },
  { date: '2026-02-21', salesAmount: 389.1 },
  { date: '2026-02-22', salesAmount: 456.9 },
  { date: '2026-02-23', salesAmount: 501.2 },
  { date: '2026-02-24', salesAmount: 478.4 },
  { date: '2026-02-25', salesAmount: 530.15 },
  { date: '2026-02-26', salesAmount: 562.7 }
];

const orders = [
  {
    id: '3d38782e-9dbf-42f0-8d6a-a69fd44f9001',
    status: 'PLACED',
    totalAmount: 23.2,
    customer: {
      id: '52c5db63-2d35-4610-bab4-d398420f5001',
      name: 'Demo Customer',
      mobileNumber: '+10000000001',
      email: 'demo@example.com'
    },
    items: [
      {
        productId: '6b6fe8f1-1f2f-4c1e-9b9e-4a7f4f8a2001',
        productName: 'Basmati Rice 5kg',
        quantity: 1,
        salePrice: 13.5
      },
      {
        productId: '6b6fe8f1-1f2f-4c1e-9b9e-4a7f4f8a2002',
        productName: 'Whole Milk 1L',
        quantity: 2,
        salePrice: 1.95
      },
      {
        productId: '6b6fe8f1-1f2f-4c1e-9b9e-4a7f4f8a2006',
        productName: 'Chocolate Cookies',
        quantity: 2,
        salePrice: 2.65
      }
    ]
  }
];

function delay(result, ms = 180) {
  return new Promise((resolve) => {
    setTimeout(() => resolve(result), ms);
  });
}

function toNumber(value, fallback = 0) {
  const nextValue = Number(value);
  return Number.isFinite(nextValue) ? nextValue : fallback;
}

function byNameMatch(term) {
  const normalizedTerm = term.trim().toLowerCase();
  return products.filter((item) => item.name.toLowerCase().includes(normalizedTerm));
}

function calculateOrderAmount(items) {
  return items.reduce((sum, item) => {
    const product = products.find((entry) => String(entry.id) === String(item.productId));
    if (!product) {
      return sum;
    }
    return sum + product.price * toNumber(item.quantity, 0);
  }, 0);
}

function buildOrderId() {
  const stamp = new Date().toISOString().replace(/[-:T.Z]/g, '').slice(0, 14);
  const serial = orders.length + 1001;
  return `3d38782e-9dbf-42f0-8d6a-a69fd44f${String(serial).padStart(4, '0')}`;
}

function buildCustomerId() {
  const serial = orders.length + 5001;
  return `52c5db63-2d35-4610-bab4-d398420f${String(serial).padStart(4, '0')}`;
}

function toOrderItem(productId, quantity) {
  const product = products.find((entry) => String(entry.id) === String(productId));
  if (!product) {
    return null;
  }

  return {
    productId: product.id,
    productName: product.name,
    quantity,
    salePrice: product.price
  };
}

function calculateOrderTotal(order) {
  return Number(
    (order.items || [])
      .reduce((sum, item) => sum + Number(item.salePrice || 0) * Number(item.quantity || 0), 0)
      .toFixed(2)
  );
}

export async function searchProducts(term) {
  if (!term?.trim()) {
    return delay([]);
  }

  return delay(byNameMatch(term));
}

export async function createCart() {
  const draftOrder = {
    id: buildOrderId(),
    status: 'CREATED',
    totalAmount: 0,
    customer: null,
    items: []
  };

  orders.unshift(draftOrder);
  return delay(draftOrder);
}

export async function addItemToCart(orderId, productId, quantity) {
  const order = orders.find((entry) => String(entry.id) === String(orderId));
  if (!order) {
    throw new Error('Order/cart not found in mock data.');
  }

  const normalizedQuantity = Math.max(toNumber(quantity, 1), 1);
  const nextItem = toOrderItem(productId, normalizedQuantity);

  if (!nextItem) {
    throw new Error('Product not found in mock data.');
  }

  const existing = order.items.find((entry) => String(entry.productId) === String(productId));
  if (existing) {
    existing.quantity += normalizedQuantity;
    existing.salePrice = nextItem.salePrice;
    existing.productName = nextItem.productName;
  } else {
    order.items.push(nextItem);
  }

  order.totalAmount = calculateOrderTotal(order);
  return delay(order);
}

export async function placeOrder(orderId, customerData) {
  const order = orders.find((entry) => String(entry.id) === String(orderId));
  if (!order) {
    throw new Error('Order/cart not found in mock data.');
  }

  if (!order.items?.length) {
    throw new Error('Please add at least one item before placing order.');
  }

  order.status = 'PLACED';
  order.customer = {
    id: buildCustomerId(),
    name: customerData.customerName,
    mobileNumber: customerData.mobileNumber,
    email: customerData.email || null
  };
  order.totalAmount = calculateOrderTotal(order);

  return delay(order);
}

export async function searchOrderById(orderId) {
  const foundOrder = orders.find((entry) => String(entry.id) === String(orderId));
  if (!foundOrder) {
    throw new Error(
      'Order not found in mock data. Try 3d38782e-9dbf-42f0-8d6a-a69fd44f9001 or place a new order.'
    );
  }

  return delay(foundOrder);
}

export async function getTopSellingProductsOfTheDay() {
  const result = [...products].sort((a, b) => b.soldToday - a.soldToday).slice(0, 5);
  return delay(result);
}

export async function getLeastSellingProductsOfTheMonth() {
  const result = [...products].sort((a, b) => a.soldMonth - b.soldMonth).slice(0, 5);
  return delay(result);
}

export async function getSalesAmountByDateRange(fromDate, toDate) {
  if (!fromDate || !toDate) {
    throw new Error('From Date and To Date are required.');
  }

  const from = new Date(fromDate);
  const to = new Date(toDate);

  const rangeData = salesByDate.filter((entry) => {
    const current = new Date(entry.date);
    return current >= from && current <= to;
  });

  return delay(rangeData);
}