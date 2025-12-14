import httpClent from './httpClient';

const AUTHOR_API = '/api/v1/book-authors';

const authorService = {
  search: (params) => {
    const url = AUTHOR_API;
    return httpClent.get(url + '', { params });
  },
  findById: (id) => {
    const url = `${AUTHOR_API}/${id}`;
    return httpClent.get(url);
  },
  create: (data, config) => {
    const url = AUTHOR_API;
    return httpClent.post(url, data, config);
  },
  update: (id, data, config) => {
    const url = `${AUTHOR_API}/${id}`;
    return httpClent.put(url, data, config);
  },
  delete: (id) => {
    const url = `${AUTHOR_API}/${id}`;
    return httpClent.delete(url);
  }
};

export default authorService;
