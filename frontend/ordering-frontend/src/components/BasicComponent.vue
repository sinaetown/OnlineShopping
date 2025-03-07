<template>
    <h1>
        Hello World {{ myLang }}
    </h1>
    <input type="text" value="myvalue" />
    <!-- :value는 v-bind:value= 의 축약어 -->
    <!-- v-bind는 input박스에서 많이 사용하는 단방향 데이터 바인딩 방식 -->
    <!-- v-model은 양방향 데이터 바인딩 모델이라는 차이점 -->
    <input type="text" :value="inputvalue1" />
    <input type="text" v-model="inputvalue2" />
    <!-- onClick() == @click -->
    <button class="btn btn-secondary" style="width:auto" @click="showValue">Variable Check</button>
    <div>
        <h2>{{ count }}</h2>
        <button class="btn btn-warning hover:red" @click="increment">Increment</button>
    </div>
    <div>
        <h2>{{ doubleCount }}</h2>
        <button class="btn btn-warning" @click="increment">Double Increment</button>
    </div>
</template>
  
<script>
export default {
    data() {
        return {
            myLang: "Python",
            inputvalue1: "Python2",
            inputvalue2: "Python3",
            count: 0,
        }
    },
    // vue 생명주기에서 인스턴스가 생성되는 시점을 created라고 한다, 화면이 열리기 전에 실행되는 함수
    // Webhook: 특정 이벤트가 발생했을 때 서버로 알림을 보냄
    created() {
        this.myLang = "Java";
    },

    // computed는 종속된 반응형 데이터가 변경될 때만 함수를 다시 실행하여 값을 계산하는 계산함수 
    computed: {
        doubleCount() {
            return this.count * 2;
        }
    },
    methods: {
        showValue() {
            alert(this.inputvalue2);
        },
        increment() {
            this.count++;
        },
    }
}
</script>
  