<template>
    <div class="container">
        <div class="page-header text-center" style="margin-top: 20px">
            <h1>회원 가입</h1>
        </div>
        <form @submit.prevent="memberCreate">
            <div class="form-group">
                <label>
                    이름
                </label>
                <input class="form-control" type="text" v-model="name">
            </div>

            <div class="form-group">
                <label>
                    이메일
                </label>
                <input class="form-control" type="text" v-model="email">

            </div>
            <div class="form-group">
                <label>
                    비밀번호
                </label>
                <input class="form-control" type="text" v-model="password">
            </div>
            <div class="form-group">
                <label>
                    도시
                </label>
                <input class="form-control" type="text" v-model="city">
            </div>
            <div class="form-group">
                <label>
                    상세주소
                </label>
                <input class="form-control" type="text" v-model="street">
            </div>
            <div class="form-group">
                <label>
                    우편번호
                </label>
                <input class="form-control" type="text" v-model="zipcode">
            </div>
            <button class="btn btn-secondary mt-3" type="submit">가입하기</button>
        </form>
    </div>
</template>

<script>
import axios from 'axios';
export default {
    data() {
        return {
            name: "",
            email: "",
            password: "",
            city: "",
            street: "",
            zipcode: "",
        }
    },
    methods: {
        async memberCreate() {
            try {
                // json 인코딩 방식 => MemberCreate 참조
                // multipart-formdata 방식 => ItemCreate 참조
                const registerData = {
                    name: this.name, email: this.email, password: this.password,
                    city: this.city, street: this.street, zipcode: this.zipcode
                };
                await axios.post(`${process.env.VUE_APP_API_BASE_URL}/member/create`, registerData);
                // window.location.href = "/login";
                this.$router.push({ name: "Login" }); //URL도 가능하지만 name으로도 가능!
            }
            catch (error) {
                const error_message = error.response.data.error_message;
                if (error_message) {
                    console.log(error_message);
                    alert(error_message);
                }
                else {
                    console.log(error);
                    alert("입력값 확인 필요");
                }
            }
        }
    },
}

</script>