<template>
  <div id="app">
    <Login />
  </div>
</template>

<script>
import Login from './components/Login.vue';
export default {
  components: { Login }
};
</script>

<style>
#app {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f5f7fa;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
}
</style>