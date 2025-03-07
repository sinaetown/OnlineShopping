<template>
    <div class="container">
        <div class="page-header text-center" style="margin-top: 20px">
            <h1>로그인</h1>
        </div>
        <!-- submit은 기본적으로 form 제출 시, 브라우저가 페이지를 새로고침하므로 해당 동작을 막기 위해 prevent를 사용한다 -->
        <form @submit.prevent="doLogin">
            <div class="form-group">
                <label for="email">
                    Email
                </label>
                <input v-model="email" class="form-control" type="email">
            </div>
            <div class="form-group">
                <label for="pw">
                    Password
                </label>
                <input v-model="password" class="form-control" type="password">
            </div>
            <button class="btn btn-secondary mt-3" type="submit">로그인</button>
        </form>
    </div>
</template>

<script>
import axios from 'axios';
// jwt-decode에서 export시에 중괄호 넣어서 export했기에 import할 때에도 {} 넣어서 import
import { jwtDecode } from 'jwt-decode';

export default {
    data() {
        return {
            email: "",
            password: "",
        }
    },
    methods: {
        async doLogin() {
            // 두 가지 예외 가능성 : 
            // 1) 200번대 상태값이지만 token이 비어있는 경우
            // 2) 200번대 상태값이 아닌 경우
            try { //RequestHeader가 200번대가 아니면 다 catch로 빠짐
                const loginData = { email: this.email, password: this.password };
                const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/doLogin`, loginData);
                const token = response.data.result.token;
                if (token) {
                    const decoded = jwtDecode(token);
                    localStorage.setItem("token", token);
                    localStorage.setItem("role", decoded.role);
                    // created 함수는 reload될 때 1번만 실행되는 hook 함수
                    // 그런데, router.push를 통한 화면전환은 reload를 실행시키지 않기에, created 함수는 호출이 되지 않는다
                    alert("Welcome, " + decoded.sub + "!");
                    // this.$router.push("/");
                    window.location.href="/";
                }
                else {
                    console.log("200 ok but not a valid token");
                    alert("Login failed!");
                }
            }
            catch (error) {
                const error_message = error.response.data.error_message;
                if (error_message) {
                    console.log(error_message);
                    alert(error_message);
                }
                else {
                    console.log(error);
                    alert("Login failed!");
                }
            }
        }
    },
}
</script>

<style lang="scss" scoped></style>