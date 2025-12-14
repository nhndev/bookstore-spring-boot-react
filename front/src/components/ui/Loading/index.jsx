import React from 'react';
import './Loading.scss';
import { useSelector } from 'react-redux';

function Loading() {
  const isLoading = useSelector((state) => state.app.isLoading);

  return (
    <div className={`loading-wrapper ${isLoading && "active"}`}>
      <div className="lds-ring">
        <div></div>
        <div></div>
        <div></div>
        <div></div>
      </div>
    </div>
  );
}

export default Loading;
