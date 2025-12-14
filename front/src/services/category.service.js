import httpClent from './httpClient';

const CATEGORY_API = '/api/v1/book-categories';

const categoryService = {
  search: (params) => {
    const url = CATEGORY_API;
    return httpClent.get(url + '/search', { params });
  },
  findById: (id) => {
    const url = `${CATEGORY_API}/${id}`;
    return httpClent.get(url);
  },
  findAllBasicInfo: () => {
    const url = CATEGORY_API;
    return httpClent.get(url);
  },
  create: (data, config) => {
    const url = CATEGORY_API;
    return httpClent.post(url, data, config);
  },
  update: (id, data, config) => {
    const url = `${CATEGORY_API}/${id}`;
    return httpClent.put(url, data, config);
  },
  delete: (id) => {
    const url = `${CATEGORY_API}/${id}`;
    return httpClent.delete(url);
  }
};

export default categoryService;
