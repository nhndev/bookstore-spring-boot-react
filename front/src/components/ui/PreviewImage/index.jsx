import React, { useState } from 'react';
import PropTypes from 'prop-types';

PreviewImage.propTypes = {
  file: PropTypes.object,
  src: PropTypes.string,
  handleClick: PropTypes.func,
};

function PreviewImage({ file, src, handleClick }) {
  const [preview, setPreview] = useState({});

  if (file) {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      setPreview(reader.result);
    };
  }
  return (
    <div className="current-image" style={{ margin: 10 }} onClick={handleClick}>
      <img src={src ? src : preview} alt="" />
      <div className="overlay">Thay đổi</div>
    </div>
  );
}

export default PreviewImage;
