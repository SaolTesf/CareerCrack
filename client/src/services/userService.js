import { apiClient } from './apiClient';
import { buildQuery } from './buildQuery';

export const userService = {
  getById(id) {
    return apiClient.get(`/users/${id}`);
  },

  getByUsernameAndEmail(username, email) {
    return apiClient.get(`/users/${encodeURIComponent(username)}/${encodeURIComponent(email)}`);
  },

  search(username) {
    return apiClient.get(`/users${buildQuery({ username })}`);
  },

  update(id, user) {
    return apiClient.put(`/users/${id}`, user);
  },

  remove(id) {
    return apiClient.delete(`/users/${id}`);
  },

  // useful for live validation on the signup form -> returns boolean
  usernameExists(username) {
    return apiClient.get(`/users/username-check/${encodeURIComponent(username)}`);
  },

  emailExists(email) {
    return apiClient.get(`/users/email-check/${encodeURIComponent(email)}`);
  },
};
