import { apiClient } from './apiClient';

export const tagService = {
  // [{ id, name }]
  getAll() {
    return apiClient.get('/tags');
  },
};
