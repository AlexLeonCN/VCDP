/**
 * API 服务层 - 统一管理所有 API 请求
 */

const API_BASE_URL = '/api';

async function request(url, options = {}) {
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    ...options
  };

  try {
    const response = await fetch(`${API_BASE_URL}${url}`, config);

    if (!response.ok) {
      throw new Error(`请求失败: ${response.status} ${response.statusText}`);
    }

    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
      throw new Error('服务器返回了非 JSON 格式的数据');
    }

    const data = await response.json();
    if (process.env.NODE_ENV === 'development') {
      console.log('API 响应:', url, data);
    }
    return data;
  } catch (error) {
    if (error.name === 'TypeError' && error.message.includes('fetch')) {
      throw new Error('网络连接失败，请检查本地服务是否启动');
    }
    if (process.env.NODE_ENV === 'development') {
      console.error('API 请求错误:', url, error);
    }
    throw error;
  }
}

function unwrapResult(result) {
  if (result.success === false) {
    throw new Error(result.message || '操作失败');
  }
  return result.data;
}

export async function fetchProjects({ page = 1, size = 12 } = {}) {
  const result = await request(`/projects?page=${page}&size=${size}`);
  return unwrapResult(result);
}

export async function fetchProject(id) {
  const result = await request(`/projects/${id}`);
  return unwrapResult(result);
}

export async function fetchEcus(projectId, { page = 1, size = 12 } = {}) {
  const result = await request(`/projects/${projectId}/ecus?page=${page}&size=${size}`);
  return unwrapResult(result);
}

export async function fetchEcu(projectId, ecuId) {
  const result = await request(`/projects/${projectId}/ecus/${ecuId}`);
  return unwrapResult(result);
}

export async function createProject(project) {
  const result = await request('/projects', {
    method: 'POST',
    body: JSON.stringify(project)
  });
  return unwrapResult(result);
}

export async function updateProject(id, project) {
  const result = await request(`/projects/${id}`, {
    method: 'PUT',
    body: JSON.stringify(project)
  });
  return unwrapResult(result);
}

export async function createEcu(projectId, payload) {
  const result = await request(`/projects/${projectId}/ecus`, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
  return unwrapResult(result);
}

export async function updateEcu(projectId, ecuId, payload) {
  const result = await request(`/projects/${projectId}/ecus/${ecuId}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
  return unwrapResult(result);
}

export async function deleteProject(id) {
  const result = await request(`/projects/${id}`, {
    method: 'DELETE'
  });
  return unwrapResult(result);
}

export async function deleteEcu(projectId, ecuId) {
  const result = await request(`/projects/${projectId}/ecus/${ecuId}`, {
    method: 'DELETE'
  });
  return unwrapResult(result);
}

export async function batchDeleteProjects(ids) {
  const result = await request('/projects/batch-delete', {
    method: 'POST',
    body: JSON.stringify({ ids })
  });
  return unwrapResult(result);
}

export async function batchDeleteEcus(projectId, ids) {
  const result = await request(`/projects/${projectId}/ecus/batch-delete`, {
    method: 'POST',
    body: JSON.stringify({ ids })
  });
  return unwrapResult(result);
}

export async function fetchCanInterfaceTypes() {
  const result = await request('/enums/can-interface-types');
  return unwrapResult(result);
}

export async function fetchCanConnTypes() {
  const result = await request('/enums/can-conn-types');
  return unwrapResult(result);
}

export async function fetchEthInterfaceTypes() {
  const result = await request('/enums/eth-interface-types');
  return unwrapResult(result);
}

export { request };
