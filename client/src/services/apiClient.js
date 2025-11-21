const API_BASE_URL = process.env.REACT_APP_API_URL;

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
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,        
      },
      ...options,

    };

    // Add auth token if available
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token;
    }
    try {
      const response = await fetch(API_BASE_URL + endpoint, config);
      const data = await response.json()

      if (!response.ok) {
        //resetSession();
        throw new Error(data.error || 'Something went wrong');
      }

      return data;
    }
    catch (error) {
      //resetSession();
      throw error;
    }

  },

  get(endpoint) {
    return this.request(endpoint, { method: 'GET'});
  },

  post(endpoint, body) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(body),
    });
  },

  put(endpoint, body) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(body),
    });
  },

  delete(endpoint) {
    return this.request(endpoint, {method: 'DELETE'});
  },

};