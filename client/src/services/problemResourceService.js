import { apiClient } from './apiClient';

export const problemResourceService = {
  // { problemId, url, resourceType?, description? }
  create(resource) {
    return apiClient.post('/problem-resources', resource);
  },
};
