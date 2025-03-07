import { createRouter, createWebHistory } from "vue-router";
import ItemList from "@/views/ItemList.vue";
import LoginComponent from "@/views/LoginComponent.vue";
// export default인 경우에는 {} 필요 없음
// 여러 요소가 있을 경우, {} 필요함
import { memberRoutes } from "./memberRouter.js";
import { orderRoutes } from "./orderRouter.js";
import { itemRoutes } from "./itemRouter.js";
import BasicComponent from "@/components/BasicComponent.vue";

const routes = [
  {
    // URL 경로 지정
    path: "/",
    // 이 라우터의 이름
    name: "HOME",
    component: ItemList,
  },
  {
    path: "/basic",
    name: "BasicComponent",
    component: BasicComponent,
  },

  {
    path: "/login",
    name: "Login",
    component: LoginComponent,
  },
  // ...은 스프레드 연산자로 불리고
  // 주로, 배열 요소를 다른 배열 요소에 합할 때 사용된다
  ...memberRoutes,
  ...orderRoutes,
  ...itemRoutes,
];
const router = createRouter({
  // vue-router는 내부적으로 두 가지 방식의 history 관리를 제공한다
  // 1) createWebHistory -> 빈번하게 사용
  // 2) createHashHistory
  history: createWebHistory(),
  routes,
});
export default router;
