import { apiClient } from './apiClient';
import { buildQuery } from './buildQuery';

// There is no "list categories" endpoint, and create needs a categoryId,
// so these mirror the seed data in the SQL script. Keep in sync with the DB.
export const CATEGORIES = [
  { id: 1, name: 'LEETCODE', label: 'LeetCode' },
  { id: 2, name: 'BEHAVIORAL', label: 'Behavioral' },
  { id: 3, name: 'SYSTEM_DESIGN', label: 'System Design' },
];

export const problemCategoryService = {
  // categoryName is one of LEETCODE | BEHAVIORAL | SYSTEM_DESIGN
  getProblems(categoryName, params) {
    return apiClient.get(`/problem-categories${buildQuery({ categoryName, ...params })}`);
  },
};
