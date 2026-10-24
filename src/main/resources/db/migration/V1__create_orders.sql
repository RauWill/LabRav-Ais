CREATE TABLE orders (
  id            uuid PRIMARY KEY,
  order_number  text NOT NULL UNIQUE,
  status        text NOT NULL,
  title         text NOT NULL,
  created_at    timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT orders_status_known
    CHECK (status IN ('NEW', 'PAID', 'SHIPPED', 'CANCELLED'))
);
