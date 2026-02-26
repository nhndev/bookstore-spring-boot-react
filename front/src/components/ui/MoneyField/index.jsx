import React, { useCallback } from 'react';
import PropTypes from 'prop-types';
import { Form } from 'react-bootstrap';
import Feedback from 'react-bootstrap/esm/Feedback';
import formatUtil from '../../../utils/format.util';
import validationUtil from '../../../utils/validation.util';

MoneyField.propTypes = {
  className: PropTypes.string,
  label: PropTypes.string,
  placeholder: PropTypes.string,
  name: PropTypes.string.isRequired,
  value: PropTypes.string.isRequired,
  handleChange: PropTypes.func.isRequired,
  handleBlur: PropTypes.func.isRequired,
  error: PropTypes.string,
  touched: PropTypes.bool,
  required: PropTypes.bool,
  min: PropTypes.number,
  max: PropTypes.number,
  minLength: PropTypes.number,
  maxLength: PropTypes.number,
  setFieldError: PropTypes.func,
};

function MoneyField({
  className,
  label,
  placeholder,
  name,
  value,
  handleChange,
  handleBlur,
  error,
  touched,
  required,
  min,
  max,
  minLength,
  maxLength,
  setFieldError,
}) {
  const showError = error && touched;
  const displayValue = formatUtil.formatNumberWithCommas(value);

  const runValidation = useCallback(
    (raw) => {
      if (!setFieldError) return;
      const msg = validationUtil.validateMoney(raw, {
        required,
        min,
        max,
        minLength,
        maxLength,
        label: label || 'Giá',
      });
      setFieldError(name, msg || undefined);
    },
    [setFieldError, name, required, min, max, minLength, maxLength, label],
  );

  const onChange = (e) => {
    let raw = e.target.value.replace(/,/g, '').replace(/\./g, '').replace(/\D/g, '');
    if (maxLength != null && raw.length > maxLength) {
      raw = raw.slice(0, maxLength);
    }
    handleChange({ target: { name, value: raw } });
    runValidation(raw);
  };

  const onBlur = (e) => {
    handleBlur(e);
    runValidation(value);
  };

  return (
    <div className={`form-group ${className || ''}`}>
      {label && (
        <Form.Label>
          {label}
          {required && <span className="text-danger ms-1">*</span>}
        </Form.Label>
      )}
      <Form.Control
        type="text"
        inputMode="numeric"
        name={name}
        placeholder={placeholder}
        value={displayValue}
        onChange={onChange}
        onBlur={onBlur}
        className={showError ? 'is-invalid' : ''}
      />
      {showError && (
        <Feedback type="invalid" className="feedback">
          {error}
        </Feedback>
      )}
    </div>
  );
}

export default MoneyField;
