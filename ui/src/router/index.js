import { createRouter, createWebHistory } from 'vue-router';
import Home from '../components/Home.vue';
import ProjectWorkspace from '../components/ProjectWorkspace.vue';

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: Home
  },
  {
    path: '/projects/:id',
    name: 'ProjectWorkspace',
    component: ProjectWorkspace,
    props: true
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/home'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
