import { apiClient } from './apiClient';
import { authService } from './authService';
import { buildQuery } from './buildQuery';

// params: { page, size, sortBy, ascending } -> all optional
// Returns a Spring Page: { content, totalPages, totalElements, number, size, ... }
export const problemService = {
  getAll(params) {
    return apiClient.get(`/problems${buildQuery(params)}`);
  },

  getByUser(userId, params) {
    return apiClient.get(`/users/${userId}/problems${buildQuery(params)}`);
  },

  // convenience: the logged-in user's problems
  getMine(params) {
    const user = authService.getCurrentUser();
    if (!user) throw new Error('Not logged in');
    return this.getByUser(user.id, params);
  },

  getById(id) {
    return apiClient.get(`/problems/${id}`);
  },

  // { userId, categoryId, title, externalLink?, difficulty?, status?, description?, solution?, tags?: string[] }
  create(problem) {
    return apiClient.post('/problems', problem);
  },

  // { category, title, externalLink, difficulty, status, description, solution, tags }
  update(id, problem) {
    return apiClient.put(`/problems/${id}`, problem);
  },

  remove(id) {
    return apiClient.delete(`/problems/${id}`);
  },

  deleteProblemTag(problemId, tagName) {
    return apiClient.delete(`/problems/${problemId}/tags${buildQuery({ name: tagName })}`);
  },
};
