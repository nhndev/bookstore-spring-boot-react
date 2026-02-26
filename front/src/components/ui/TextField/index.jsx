import React from 'react';
import PropTypes from 'prop-types';
import Feedback from 'react-bootstrap/esm/Feedback';

TextField.propTypes = {
  className: PropTypes.string,
  label: PropTypes.string,
  placeholder: PropTypes.string,
  name: PropTypes.string.isRequired,
  maxLength: PropTypes.number,
  value: PropTypes.node.isRequired,
  handleChange: PropTypes.func.isRequired,
  handleBlur: PropTypes.func.isRequired,
  error: PropTypes.string,
  touched: PropTypes.bool,
  required: PropTypes.bool,
};

function TextField({
  className,
  label,
  placeholder,
  name,
  maxLength,
  value,
  handleChange,
  handleBlur,
  error,
  touched,
  required,
}) {
  const showError = error && touched;
  return (
    <div className={`form-group ${className || ''}`}>
      {label && (
        <label>
          {label}
          {required && <span className="text-danger ms-1">*</span>}
        </label>
      )}
      <input
        type="text"
        placeholder={placeholder}
        name={name}
        maxLength={maxLength}
        className={`form-control ${showError ? 'is-invalid' : ''}`}
        value={value}
        onChange={handleChange}
        onBlur={handleBlur}
      />
      {showError && (
        <Feedback type="invalid" className="feedback">
          {error}
        </Feedback>
      )}
    </div>
  );
}

export default TextField;
