import formatUtil from "./format.util";

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
  /**
   * Validate money/price value. Returns error message or null.
   * Strips commas and dots before validating.
   * @param {string} value - Input string (may contain , or .)
   * @param {Object} options - { required, min, max, minLength, maxLength, label }
   */
  validateMoney(value, options = {}) {
    const {
      required = false,
      min = null,
      max = null,
      minLength = null,
      maxLength = null,
      label = 'Giá',
    } = options;
    const withSeparators = value == null ? '' : String(value).trim();
    const raw = withSeparators.replace(/,/g, '').replace(/\./g, '');
    if (required && raw === '') {
      return this.requiredMsg(label);
    }
    if (raw === '') return null;
    if (minLength != null && raw.length < minLength) {
      return `${label} phải có ít nhất ${minLength} ký tự số`;
    }
    if (maxLength != null && raw.length > maxLength) {
      return `${label} không được vượt quá ${maxLength} ký tự số`;
    }
    if (!/^\d+$/.test(raw)) return `${label} phải là số`;
    const num = Number(raw);
    if (min != null && num < min) {
      return `${label} phải lớn hơn hoặc bằng ${min}`;
    }
    if (max != null && num > max) {
      return `${label} vượt quá giới hạn cho phép (tối đa ${formatUtil.formatNumberWithCommas(max)})`;
    }
    return null;
  },
};

export default validationUtil;
