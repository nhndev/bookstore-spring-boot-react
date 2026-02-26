import httpClent from './httpClient';

const BOOK_API = '/api/v1/books';

const bookService = {
  search: (params) => {
    return httpClent.get(BOOK_API, { params });
  },
  findById: (id) => {
    return httpClent.get(`${BOOK_API}/${id}`);
  },
  create: (formData, config) => {
    return httpClent.post(BOOK_API, formData, config);
  },
  update: (id, formData, config) => {
    return httpClent.put(`${BOOK_API}/${id}`, formData, config);
  },
  delete: (id) => {
    return httpClent.delete(`${BOOK_API}/${id}`);
  }
};

export default bookService;
