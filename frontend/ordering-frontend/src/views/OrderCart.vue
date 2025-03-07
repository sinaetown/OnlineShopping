<template>
    <div class="container">
        <div class="page-header text-center" style="margin-top: 20px">
            <h1>장바구니</h1>
        </div>
        <button @click="clearCart" class="btn btn-secondary" style="display:inline-block; width:auto; margin-right:5px">
            장바구니 비우기</button>
        <button @click="placeOrder" class="btn btn-warning"
            style="display:inline-block; width:auto; margin-right:5px">주문하기</button>
        <table class="table">
            <thead>
                <tr>
                    <th>제품ID</th>
                    <th>제품명</th>
                    <th>주문수량</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="item in getCartItems" :key="item.itemId">
                    <td>{{ item.itemId }}</td>
                    <td>{{ item.name }}</td>
                    <td>{{ item.count }}</td>
                </tr>
            </tbody>

        </table>
    </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import axios from 'axios';

export default {
    computed: {
        ...mapGetters(['getCartItems', 'getTotalQuantity'])
    },
    methods: {
        ...mapActions(['clearCart']),
        clearCart() {
            // 바로 아래 코드는 mutation 직접 호출 방식
            // this.$store.commit('clearCart');

            // 바로 아래 코드는 actions 호출 방식
            if (!confirm("장바구니를 비우시겠어요?")) {
                alert("장바구니 비우기를 취소했어요!");
                return;
            }
            this.$store.dispatch('clearCart');
            alert("장바구니가 비워졌어요!");
        },
        async placeOrder() {
            const orderItems = this.getCartItems.map(item => {
                return { itemId: item.itemId, count: item.count }
            })
            if (!confirm(`${this.getTotalQuantity}개의 상품을 주문하시겠어요?`)) {
                alert("주문이 취소됐어요!");
                return;
            }
            if (this.getTotalQuantity < 1) {
                alert("장바구니가 비어있어요!");
                return;
            }
            const token = localStorage.getItem('token');
            const headers = token ? { Authorization: `Bearer ${token}` } : {};
            try {
                await axios.post(`${process.env.VUE_APP_API_BASE_URL}/order/create`, orderItems, { headers });
                console.log(orderItems);
                alert("주문완료! :)");
                this.$store.commit('clearCart');
            }
            catch (error) {
                console.log(error);
                alert("주문실패! :(");
            }
        },
    },
}
</script>
