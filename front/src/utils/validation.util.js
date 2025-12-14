const validationUtil = {
  requiredMsg: (name) => {
    return `${name} là trường bắt buộc!`;
  },
  maxLength: (name, length) => {
    return `${name} không được vượt quá ${length} ký tự!`;
  },
  formatFile: () => {
    return `File không đúng định dạng!`;
  },
  sizeFile: () => {
    return `Kích thước file quá lớn! (Tối đa 2MB)`;
  },
};

export default validationUtil;
