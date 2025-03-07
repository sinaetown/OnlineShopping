// vue cli 는 vue를 컨트롤하기 위한 명령어 툴
// vue cli 구성 옵션을 지정하는 파일
// transpileDependencies 옵션을 통해 의존성을 ES5로 transform한다.

const { defineConfig } = require("@vue/cli-service");
module.exports = defineConfig({
  transpileDependencies: true,
});
