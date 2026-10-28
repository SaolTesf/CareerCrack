// Turns { page: 0, size: 10, sortBy: undefined } into "?page=0&size=10"
// (skips undefined/null/empty values so backend defaults apply)
export const buildQuery = (params = {}) => {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      query.append(key, value);
    }
  });
  const str = query.toString();
  return str ? `?${str}` : '';
};
