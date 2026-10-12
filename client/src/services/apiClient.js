const API_BASE_URL = import.meta.env.VITE_API_KEY;

/*const readBody = async (response) => {
  const raw = await response.text();
  if (!raw) return null;
  const isJson = response.headers.get()
}
  */

// called when jwt token expires
const resetSession = () => {
  localStorage.clear();
  // redirect to login page
}

export const apiClient = {
  async request(endpoint, options = {}) {
    const { skipAuth, ...rest } = options; // extract skipAuth from options and rest contains the remaining options
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...rest.headers,        
      },
      ...rest,

    };

    // Add auth token if available
    const token = localStorage.getItem('authToken')
    if (token && !skipAuth) {
      config.headers['Authorization'] = 'Bearer ' + token;
    }

    const response = await fetch(API_BASE_URL + endpoint, config);
    const data = await response.json().catch(() => null);

    if (!response.ok) {
      const error = new Error(data?.message || 'Something went wrong');
      error.status = response.status;
      error.fieldErrors = data?.fieldErrors || null;
      throw error;
    }

    return data;

  },

  get(endpoint, options) {
    return this.request(endpoint, { method: 'GET', ...options});
  },

  post(endpoint, body, options) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(body),
      ...options,
    });
  },

  put(endpoint, body, options) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(body),
      ...options,
    });
  },

  delete(endpoint, options) {
    return this.request(endpoint, {method: 'DELETE', ...options});
  },

};