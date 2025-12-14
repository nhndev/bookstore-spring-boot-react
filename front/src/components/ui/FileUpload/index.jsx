import React, { forwardRef } from 'react';
import PropTypes from 'prop-types';
import Feedback from 'react-bootstrap/esm/Feedback';
import { Col } from 'react-bootstrap';
import PreviewImage from '../PreviewImage';

const FileUpload = forwardRef(function FileUpload(
  {
    className,
    label,
    placeholder,
    accept,
    name,
    file,
    handleChange,
    handleBlur,
    handleClickImage,
    error,
    hidden = false,
    currentImage,
  },
  ref
) {
  return (
    <div className={`form-group ${className}`}>
      <label>{label}</label>
      <input
        ref={ref}
        type="file"
        placeholder={placeholder}
        accept={accept}
        name={name}
        className={`form-control ${error ? 'is-invalid' : ''}`}
        onChange={handleChange}
        onBlur={handleBlur}
        hidden={hidden}
      />
      {!error && (file || currentImage) && (
        <Col xl={12}>
          {accept && accept.includes('image') && (
            <PreviewImage
              src={file ? null : currentImage}
              file={file}
              handleClick={handleClickImage}
            />
          )}
        </Col>
      )}
      {error && (
        <Feedback type="invalid" className="feedback">
          {error}
        </Feedback>
      )}
    </div>
  );
});

FileUpload.propTypes = {
  className: PropTypes.string,
  label: PropTypes.string.isRequired,
  placeholder: PropTypes.string,
  accept: PropTypes.string,
  name: PropTypes.string.isRequired,
  value: PropTypes.any,
  handleChange: PropTypes.func.isRequired,
  handleBlur: PropTypes.func,
  handleClickImage: PropTypes.func,
  error: PropTypes.string,
  file: PropTypes.object,
  touched: PropTypes.bool,
  hidden: PropTypes.bool,
  currentImage: PropTypes.string,
};

export default FileUpload;
