const API_BASE_URL = process.env.REACT_APP_API_URL;

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
        throw new Error(data.message || 'Something went wrong');
      }

      return data;
    }
    catch (error) {
      throw error;
    }

  },
};