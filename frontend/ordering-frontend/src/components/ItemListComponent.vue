<template>
    <div class="container">
        <div class="page-header text-center" style="margin-top: 20px">
            <h1>{{ pageTitle }}</h1>
        </div>
        <div class="d-flex justify-content-between" style="margin-top: 20px;">
            <form @submit.prevent="onSearch()">
                <select v-model="searchType" class="form-control"
                    style="display:inline-block; width:auto; margin-right:5px">
                    <option value="optional">선택</option>
                    <option value="name">이름</option>
                    <option value="category">카테고리</option>
                </select>
                <input v-model="searchValue" type="text" class="form-control"
                    style="display:inline-block; width:auto; margin-right:5px" />
                <button type="submit" class="form-control"
                    style="display:inline-block; width:auto; margin-right:5px">검색</button>
            </form>
            <div v-if="!isAdmin"><button @click="addCart" class="btn btn-secondary" style="margin-right: 10px">장바구니</button>
                <button @click="placeOrder" class="btn btn-success">주문하기</button>
            </div>
            <div v-if="isAdmin">
                <a href="/item/create"><button class="btn btn-success" style="margin-right: 10px">상품 등록</button></a>
            </div>
        </div>
        <table class="table">
            <thead>
                <tr>
                    <th v-if="!isAdmin"></th>
                    <th>제품 사진</th>
                    <th>제품명</th>
                    <th>가격</th>
                    <th>재고수량</th>
                    <th v-if="!isAdmin">주문수량</th>
                    <th v-if="isAdmin">Action</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="item in itemList" :key="item.id">
                    <!-- checkbox를 선택하면 value가 true가 담기게 됨 -->
                    <td v-if="!isAdmin"><input type="checkbox" v-model="selectedItems[item.id]" /></td>
                    <td><img :src="getImage(item.id)" style="height: 100px; width:auto" /></td>
                    <td>{{ item.name }}</td>
                    <td>{{ item.price }}</td>
                    <td>{{ item.stockQuantity }}</td>
                    <td v-if="!isAdmin"><input v-model="item.quantity" type="number" min="1" style="width: 50px" /></td>
                    <td v-if="isAdmin"><button class="btn btn-warning" @click.stop="deleteItem(item.id)">상품 삭제</button></td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
import axios from 'axios';
import { mapActions } from 'vuex';


export default {
    props: ['isAdmin', 'pageTitle'],
    data() {
        return {
            itemList: [],
            pageSize: 10,
            currentPage: 0,
            searchType: 'optional',
            searchValue: '',
            isLoading: false,
            isLastPage: false,
            selectedItems: {},
        };
    },
    created() { // 화면이 그려지면서 실행됨
        this.loadItems();
    },
    // mounted : Window DOM 객체가 생성된 이후에 실행되는 hook 함수 (화면이 다 그려진 후 호출됨)
    mounted() {
        // scroll 동작이 발생할 때마다 scrolPagination 함수가 호출이 된다는 뜻!
        window.addEventListener('scroll', this.scrollPagination);
    },
    methods: {
        ...mapActions(['addtoCart']),
        addCart() {
            if (!confirm("장바구니에 담으시겠어요?")) return;
            const orderItems = Object.keys(this.selectedItems)
                .filter(key => this.selectedItems[key] === true)
                .map(key => {
                    const item = this.itemList.find(i => i.id == key);
                    // 아래 코드는 장바구니 담는 순간 화면 단에서 재고수량 줄이기
                    // item.stockQuantity -= item.quantity; 
                    return { itemId: item.id, name: item.name, count: item.quantity };
                });
            // 바로 아래 코드는 mutation 직접 호출 방식
            // orderItems.forEach(item => this.$store.commit('addtoCart', item));

            // 바로 아래 코드는 actions 호출 방식
            orderItems.forEach(item => this.$store.dispatch('addtoCart', item));
            alert("장바구니에 담겼어요!");
        },

        scrollPagination() {
            // innerHeight : 브라우저 창의 내부 높이를 픽셀 단위로 변환 (viewport height, 사용자의 눈에 보이는 창)
            // offsetHeight : 전체 브라우저 창의 높이
            const nearBottom = window.innerHeight + window.scrollY >= document.body.offsetHeight - 200;
            if (nearBottom && !this.isLastPage && !this.isLoading) {
                this.currentPage++;
                this.loadItems();
            }
        },
        getImage(itemId) {
            return `${process.env.VUE_APP_API_BASE_URL}/item/${itemId}/image`;
        },
        async loadItems() {
            this.isLoading = true;
            try {
                // params 키워드 사용해야 파라미터 방식으로 axios 요청 가능
                const params = {
                    page: this.currentPage,
                    size: this.pageSize,
                    [this.searchType]: this.searchValue,
                };
                // if (this.searchType === "name") {
                //     params.name = this.searchValue;
                // }
                // else if (this.searchType === "category") {
                //     params.name = this.serachValue;
                // }
                console.log("params: " + params);
                // response에는 전체 item list를 담고 있음
                const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/items`, { params });
                // ...은 배열을 풀어서 쓸 때 (합칠 때) 사용함
                const addItemList = response.data.map(item => ({ ...item, quantity: 1 }));
                if (addItemList.length < this.pageSize) {
                    this.isLastPage = true;
                }
                this.itemList = [...this.itemList, ...addItemList];
                this.isLoading = false;
            }
            catch (error) {
                console.log(error);
            }
        },
        onSearch() {
            this.itemList = [];
            this.currentPage = 0;
            this.isLastPage = false;
            this.loadItems();
            this.selectedItems = [];
        },
        async placeOrder() {
            // {"1":true, "2": false, "3":true ..} 형식
            console.log(this, this.selectedItems);
            // Obejct.keys : 위 데이터 구조에서 1, 2 등 key 값만 추출하는 메서드

            const orderItems = Object.keys(this.selectedItems)
                .filter(key => this.selectedItems[key] === true)
                .map(key => {
                    const item = this.itemList.find(i => i.id == key);
                    return { itemId: item.id, count: item.quantity };
                });
            if (orderItems.length < 1) {
                alert("선택된 상품이 없어요!");
                return;
            }

            if (!confirm(`${orderItems.length}개의 상품을 주문하시겠어요?`)) {
                alert("주문이 취소됐어요!");
                return;
            }

            const token = localStorage.getItem('token');
            const headers = token ? { Authorization: `Bearer ${token}` } : {};
            try {
                await axios.post(`${process.env.VUE_APP_API_BASE_URL}/order/create`, orderItems, { headers });
                console.log(orderItems);
                alert("주문완료! :)");
                window.location.reload();
            }
            catch (error) {
                console.log(error);
                alert("주문실패! :(");
            }



        },
        async deleteItem(itemId) {
            if (confirm("삭제하시겠습니까?")) {
                try {
                    const token = localStorage.getItem('token');
                    const headers = token ? { Authorization: `Bearer ${token}` } : {};
                    await axios.delete(`${process.env.VUE_APP_API_BASE_URL}/item/${itemId}/delete`, { headers });
                    alert("상품 삭제 성공! :)");
                    window.location.reload();

                }
                catch (error) {
                    console.log(error);
                    alert("상품 삭제 실패! :(")
                }
            }

        }
    },

}
</script>
<style lang="scss" scoped></style>