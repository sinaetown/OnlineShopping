import OrderList from "@/views/OrderList.vue";
import OrderCart from "@/views/OrderCart.vue";

export const orderRoutes = [
  {
    path: "/orders",
    name: "OrderList",
    component: OrderList,
  },
  {
    path: "/ordercart",
    name: "OrderCart",
    component: OrderCart,
  },
];
