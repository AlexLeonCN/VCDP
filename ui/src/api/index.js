/**
 * API 服务层 - 统一管理所有 API 请求
 */

const API_BASE_URL = '/api';

/**
 * 统一的请求方法
 */
async function request(url, options = {}) {
  const token = localStorage.getItem('token');
  
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    ...options
  };

  // 如果有 token，添加到请求头
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }

  try {
    const response = await fetch(`${API_BASE_URL}${url}`, config);
    
    // 处理 HTTP 错误状态
    if (!response.ok) {
      if (response.status === 401) {
        // token 过期或无效，清除并跳转到登录页
        localStorage.removeItem('token');
        if (window.location.pathname !== '/login') {
          window.location.href = '/login';
        }
        throw new Error('登录已过期，请重新登录');
      }
      throw new Error(`请求失败: ${response.status} ${response.statusText}`);
    }

    let data;
    try {
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        data = await response.json();
      } else {
        const text = await response.text();
        console.warn('API 返回非 JSON 数据:', text);
        // 尝试解析为 JSON
        try {
          data = JSON.parse(text);
        } catch {
          throw new Error('服务器返回了非 JSON 格式的数据');
        }
      }
    } catch (parseError) {
      console.error('JSON 解析错误:', parseError);
      throw new Error('服务器响应格式错误');
    }
    
    // 开发环境下打印响应数据，方便调试
    if (process.env.NODE_ENV === 'development') {
      console.log('API 响应:', url, data);
    }
    return data;
  } catch (error) {
    // 网络错误处理
    if (error.name === 'TypeError' && error.message.includes('fetch')) {
      throw new Error('网络连接失败，请检查网络设置');
    }
    // 开发环境下打印错误信息
    if (process.env.NODE_ENV === 'development') {
      console.error('API 请求错误:', url, error);
    }
    throw error;
  }
}

/**
 * 登录接口
 */
export async function login(username, password) {
  return request('/login', {
    method: 'POST',
    body: JSON.stringify({ username, password })
  });
}

/**
 * 注册接口
 */
export async function register(username, password, email, nickname) {
  return request('/register', {
    method: 'POST',
    body: JSON.stringify({ username, password, email, nickname })
  });
}

/**
 * 退出登录接口
 */
export async function logout() {
  return request('/logout', {
    method: 'POST'
  });
}

