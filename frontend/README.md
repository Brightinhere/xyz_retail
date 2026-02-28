# XYZ Retail Frontend (Vue 3 + PicoCSS)

Single-page frontend for the assignment requirements in `Backend Engineer Assignment.md`.

## What this frontend covers

- Customer product search.
- Add product to cart with quantity behavior.
- Place order with mandatory customer name and mobile number.
- Show returned order ID and amount after successful order placement.
- Store employee order lookup by order ID.
- Management reporting for:
  - Top 5 selling products of the day.
  - Least selling products of the month.
  - Sale amount per day for custom date ranges.
- Reporting tables include:
  - Product name
  - Quantity available
  - Product price
  - Low inventory indicator when quantity is below 10

## Tech Stack

- Vue 3
- Vue Router
- Vite
- PicoCSS

## Run locally

1. Install dependencies:

   ```bash
   npm install
   ```

2. Configure environment:

   ```bash
   copy .env.example .env
   ```

  Edit `.env` values:

  - `VITE_API_BASE_URL=http://localhost:8090` for real backend
  - `VITE_USE_MOCK_API=false` to use real backend

## Lightweight Mock API mode

Use this mode when backend is unavailable and you still want to demo the assignment flows.

1. In `.env`, set:

  ```bash
  VITE_USE_MOCK_API=true
  ```

2. Run app:

  ```bash
  npm run dev
  ```

3. In mock mode you can:

  - Search products and add them to cart.
  - Place orders and receive generated mock order IDs.
  - Search existing order `3d38782e-9dbf-42f0-8d6a-a69fd44f9001` or newly placed orders.
  - Load top/least selling reports and date-range sales report.

The header shows `Mock API Mode Enabled` when this mode is active.

3. Start dev server:

   ```bash
   npm run dev
   ```

4. Build for production:

   ```bash
   npm run build
   npm run preview
   ```

## Backend API contract expected by frontend

The frontend calls these endpoints by default (prepend `VITE_API_BASE_URL`):

- `GET /api/products/search?q={term}`
- `POST /api/orders` (create cart/draft order)
- `POST /api/orders/{orderId}/items` with `{ "productId": "uuid", "quantity": number }` (increment/add quantity)
- `POST /api/orders/{orderId}/place` with

  ```json
  {
    "customerName": "string",
    "mobileNumber": "string",
    "email": "string|null"
  }
  ```

- `GET /api/employee/orders/{orderId}`
- `GET /api/management/reports/top-selling?date=YYYY-MM-DD` (optional date)
- `GET /api/management/reports/least-selling?month=YYYY-MM` (optional month)
- `GET /api/management/reports/sales?start=YYYY-MM-DD&end=YYYY-MM-DD`

For list responses, frontend accepts either:

- plain arrays (`[]`), or
- object wrapper with `items` array (`{ "items": [] }`)

For product report rows, frontend expects DTO shape with nested inventory:

```json
{
  "id": "uuid",
  "name": "string",
  "price": 10.5,
  "inventory": {
    "quantity": 8,
    "lowStock": true
  }
}
```

## Notes

- Bonus email confirmation is assumed to be backend-driven.
- CORS should be enabled on backend for frontend origin during development.
- Cart quantity decrease/remove is not exposed in the provided backend controller set; frontend only supports add/increment.