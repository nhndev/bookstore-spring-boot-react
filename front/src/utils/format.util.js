function formatPrice(value) {
  if (value == null) return '-';
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  }).format(value);
}

/**
 * Format number with comma separators for display (e.g. 1000000 -> "1,000,000").
 * @param {string|number} value - Raw digits or number
 * @returns {string} Formatted string with commas, or empty string
 */
function formatNumberWithCommas(value) {
  if (value == null || value === '') return '';
  const str = String(value).replace(/,/g, '');
  if (!/^\d*$/.test(str)) return str;
  return str.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

export default {
  formatPrice,
  formatNumberWithCommas,
};

