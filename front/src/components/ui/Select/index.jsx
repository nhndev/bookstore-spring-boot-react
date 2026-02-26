import React from 'react';
import PropTypes from 'prop-types';
import Feedback from 'react-bootstrap/esm/Feedback';

Select.propTypes = {
  className: PropTypes.string,
  label: PropTypes.string.isRequired,
  placeholder: PropTypes.string,
  name: PropTypes.string.isRequired,
  value: PropTypes.node.isRequired,
  handleChange: PropTypes.func.isRequired,
  handleBlur: PropTypes.func.isRequired,
  error: PropTypes.string,
  touched: PropTypes.bool,
  required: PropTypes.bool,
  options: PropTypes.array.isRequired,
};
function Select({
  className,
  label,
  name,
  value,
  handleChange,
  handleBlur,
  error,
  touched,
  required,
  options,
}) {
  const showError = error && touched;
  return (
    <div className={`form-group ${className}`}>
      <label>
        {label}
        {required && <span className="text-danger ms-1">*</span>}
      </label>
      <select
        className={`form-select ${showError ? 'is-invalid' : ''}`}
        name={name}
        value={value}
        onChange={handleChange}
        onBlur={handleBlur}
      >
        {options &&
          options.length > 0 &&
          options.map((option) => (
            <option key={option?.key} value={option.key}>
              {option.value}
            </option>
          ))}
      </select>
      {showError && (
        <Feedback type="invalid" className="feedback">
          {error}
        </Feedback>
      )}
    </div>
  );
}

export default Select;
