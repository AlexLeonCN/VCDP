import { createRouter, createWebHistory } from 'vue-router';
import Home from '../components/Home.vue';
import ProjectWorkspace from '../components/ProjectWorkspace.vue';
import ProjectOverview from '../components/ProjectOverview.vue';
import EcuList from '../components/EcuList.vue';

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
    props: true,
    children: [
      {
        path: '',
        name: 'ProjectOverview',
        component: ProjectOverview,
        props: true
      },
      {
        path: 'ecu',
        name: 'EcuList',
        component: EcuList,
        props: true
      }
    ]
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
